package GUI;

import components.FOptionPane;
import door.MainClass;
import fox.FoxCursor;
import fox.FoxFontBuilder;
import fox.InputAction;
import fox.Out;
import images.FoxSpritesCombiner;
import interfaces.Cached;
import logic.ScenarioEngine;
import lombok.*;
import render.FoxRender;
import secondGUI.SaveGame;
import tools.Cursors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static fox.Out.LEVEL;
import static fox.Out.Print;
import static registry.Registry.*;

@Data
public class GamePlay extends JFrame implements MouseListener, MouseMotionListener, Cached {
    private ScenarioEngine scenario;

    private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private final Double FULLSCREEN_WIDTH = screen.getWidth();
    private final Double FULLSCREEN_HEIGHT = screen.getHeight();
    private Double WINDOWED_WIDTH = screen.getWidth() * 0.75D;
    private Double WINDOWED_HEIGHT = screen.getHeight() * 0.9D;

    private DefaultListModel<String> dlm = new DefaultListModel<>();
    private JList<String> answerList;

    private Thread textAnimateThread;
    private long dialogDelaySpeed = 48;

    private BufferedImage heroAvatar, picSceneImage, gamepaneImage;
    private BufferedImage[] backButtons;

    private JPanel basePane;
    private boolean isStoryPlayed, backButOver, backButPressed, isDialogAnimated, isPaused;

    private String lastText;

    private int n = 0;
    private int refDelay;
    private long was = System.currentTimeMillis();
    private String curFps;
    private float fpsIterCount = 0;
    private double charWidth = 12.2D;
    private char[] dialogChars;

    private Rectangle dialogTextRect, choseVariantRect;
    private Rectangle heroAvatarRect, backButtonRect, centerPicRect;
    private Point mouseNow, frameWas, mouseWasOnScreen;


    public GamePlay(GraphicsConfiguration gConfig) {
        super("GamePlayParent", gConfig);
        refDelay = 1000 / gConfig.getDevice().getDisplayMode().getRefreshRate();

        setName("GamePlay");
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        getContentPane().setBackground(new Color(0,0,0,0));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setCursor(Cursors.PinkCursor.get());

        loadResources();
        loadScenario();
        setInAc();

        basePane = new JPanel(null) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;
                FoxRender.setRender(g2D, userConf.getQuality());

                drawBase(g2D);
                drawAvatar(g2D);
                drawBackButton(g2D);
                drawAutoDialog(g2D); // поместить в FoxLib

                g2D.setColor(Color.GRAY);
                g2D.drawRoundRect(dialogTextRect.x, dialogTextRect.y, dialogTextRect.width, dialogTextRect.height, 16, 16);
                g2D.setColor(Color.GRAY);
                g2D.drawRoundRect(choseVariantRect.x, choseVariantRect.y, choseVariantRect.width, choseVariantRect.height, 8, 8);

//                g2D.setColor(Color.YELLOW);
//                g2D.drawRect(backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height);

                drawFPS(g2D);
            }

            private void drawBase(Graphics2D g2D) {
                if (picSceneImage == null) {
                    picSceneImage = (BufferedImage) cache.get("blackpane");
                }
                try {
                    g2D.drawImage(picSceneImage, GamePlay.this.centerPicRect.x, GamePlay.this.centerPicRect.y, GamePlay.this.centerPicRect.width, GamePlay.this.centerPicRect.height, this);
                } catch (Exception e) {
                    g2D.setColor(Color.DARK_GRAY);
                    g2D.fillRect(16, 16, getWidth() - 32, getHeight() - 32);
                    g2D.setColor(Color.RED);
                    g2D.drawString("NO IMAGE", (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g2D, "NO IMAGE").getWidth() / 2), getHeight() / 2 - 96);
                }

                drawNPC(g2D);

                g2D.drawImage(gamepaneImage, 0, 0, getWidth(), getHeight(), this);
            }

            private void drawNPC(Graphics2D g2D) {
                if (scenario == null || scenario.getCurrentNpcImage() == null) {
                    return;
                }
                // draw NPC:
                g2D.drawImage(scenario.getCurrentNpcImage(), 0, 0, scenario.getCurrentNpcImage().getWidth(), scenario.getCurrentNpcImage().getHeight(), this);
            }

            private void drawAvatar(Graphics2D g2D) {
                // draw hero avatar:
                g2D.drawImage(heroAvatar, heroAvatarRect.x, heroAvatarRect.y, heroAvatarRect.width, heroAvatarRect.height, this);
            }

            private void drawBackButton(Graphics2D g2D) {
                if (backButOver) {
                    if (backButPressed) {
                        g2D.drawImage(backButtons[1], backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height, this);
                    } else {
                        g2D.drawImage(backButtons[2], backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height, this);
                    }
                } else {
                    g2D.drawImage(backButtons[0], backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height, this);
                }
            }

            private void drawAutoDialog(Graphics2D g2D) {
                if (dialogChars != null) {
                    // hero name:
                    g2D.setFont(fontName);
                    g2D.setColor(Color.BLACK);
                    g2D.drawString(scenario.getCurrentHeroName(), dialogTextRect.x + 4, dialogTextRect.y + 21);
                    g2D.setColor(Color.ORANGE);
                    g2D.drawString(scenario.getCurrentHeroName(), dialogTextRect.x + 5, dialogTextRect.y + 20);

                    // draw hero dialog:
                    g2D.setFont(fontDialog);
                    charWidth = g2D.getFontMetrics().getMaxCharBounds(g2D).getWidth();

                    g2D.setColor(Color.GREEN);
                    int mem = 0, line = 1;
                    W:
                    while (true) {
                        int shift = 0;
                        line++;

                        for (int i = mem; i < dialogChars.length; i++) {
                            if (dialogChars[i] == 10) {
                                mem = i + 1;
                                break;
                            } // next line marker detector (\n)

                            g2D.drawString(String.valueOf(dialogChars[i]),
                                    (int) (dialogTextRect.x + 5 + (charWidth * shift)),
                                    (dialogTextRect.y + 18) + 25 * (line - 1));

                            shift++;
                            if (i >= dialogChars.length - 1) {
                                break W;
                            }
                        }
                    }
                }
            }

            private void drawFPS(Graphics2D g2D) {
                if (configuration.isFpsShowed()) {
                    fpsIterCount++;
                    if (System.currentTimeMillis() - was > 1000) {
                        curFps = Double.valueOf(Math.floor(fpsIterCount)).toString();
                        was = System.currentTimeMillis();
                        fpsIterCount = 0;
                    }

                    g2D.setColor(Color.GRAY);
                    g2D.drawString(curFps, 10, 25);
                }
            }

            {
                setOpaque(false);
                setFocusable(true);
                setSize(GamePlay.this.getWidth(), GamePlay.this.getHeight());

                addMouseListener(GamePlay.this);
                addMouseMotionListener(GamePlay.this);

                answerList = new JList<>(dlm) {
                    @Override
                    public int locationToIndex(Point location) {
                        int index = super.locationToIndex(location);
                        if (index != -1 && !getCellBounds(index, index).contains(location)) {
                            return -1;
                        } else {
                            return index;
                        }
                    }

                    {
                        setFocusable(false);
                        setForeground(Color.WHITE);
                        setBackground(new Color(0.5f, 0.5f, 1.0f, 0.1f));
                        setBorder(new EmptyBorder(3, 3, 3, 3));
                        setFont(fontDialog);
                        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        setSelectionBackground(new Color(1.0f, 1.0f, 1.0f, 0.2f));
                        setSelectionForeground(new Color(1.0f, 1.0f, 0.0f, 1.0f));
                        setVisibleRowCount(5);
                        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curTextCursor"), "textCursor"));
                        addMouseListener(GamePlay.this);
                    }
                };

                add(answerList);
            }
        };

        add(basePane);

        setVisible(true);
        reloadRectangles();

        new Thread(new StoryPlayThread()) {
            {
                setDaemon(true);
                setName("StoryPlayed-thread (GameFrame repaint thread)");
            }
        }.start();
    }

    private void loadResources() {
        // npc images:
        try {
            for (Path path : Files.list(npcAvatarsDir).toList()) {
                cache.add(path.toFile().getName().replace(picExtension, ""),
                        toBImage(path.toString().replace(picExtension, "")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // scenes images:
        try {
            for (Path path : Files.list(scenesDir).toList()) {
                cache.add(path.toFile().getName().replace(picExtension, ""),
                        toBImage(path.toString().replace(picExtension, "")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // other images:
        try {
            backButtons = FoxSpritesCombiner.addSpritelist("picBackButBig", (BufferedImage) cache.get("picBackButBig"), 3, 1);
            gamepaneImage = (BufferedImage) cache.get("picGamepane");
            if (gamepaneImage == null) {
                throw new NullPointerException("gamepaneImage is NULL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCenterImage(null);
    }

    private BufferedImage toBImage(String path) {
        try {
            return ImageIO.read(new File(path + picExtension));
        } catch (Exception e) {
            Out.Print(MainClass.class, Out.LEVEL.WARN, "Ошибка чтения медиа '" + Paths.get(path) + "': " + e.getMessage());
        }
        return null;
    }

    private void loadScenario() {
        scenario = new ScenarioEngine();
        scenario.load(new File(blockPath + "/00NewStart" + sBlockExtension)); // loading First block:
    }

    private void setInAc() {
        InputAction.add("game", GamePlay.this); // SwingUtilities.getWindowAncestor(basePane));

        InputAction.set(InputAction.FOCUS_TYPE.WHEN_FOCUSED, "game", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showExitRequest();
            }
        });
        InputAction.set("game", "fullscreen", KeyEvent.VK_F, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPaused = true;
                userConf.setFullScreen(!userConf.isFullScreen());
                reloadRectangles();
                isPaused = false;
            }
        });
        InputAction.set("game", "switchFPS", KeyEvent.VK_F11, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configuration.setFpsShowed(!configuration.isFpsShowed());
            }
        });
        InputAction.set("game", "switchQuality", KeyEvent.VK_F3, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userConf.nextQuality();
                System.out.println("Quality: " + userConf.getQuality());
            }
        });

        InputAction.set("game", "next", KeyEvent.VK_SPACE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isDialogAnimated) {
                    dialogDelaySpeed = 0;
                    return;
                }
                scenario.choice(-1);
                answerList.clearSelection();
            }
        });
        InputAction.set("game", "answer_1", KeyEvent.VK_1, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 1) {
                    return;
                }
                answerList.setSelectedIndex(0);
                scenario.choice(0);
                resetVariantsList();
            }
        });
        InputAction.set("game", "answer_2", KeyEvent.VK_2, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 2) {
                    return;
                }
                answerList.setSelectedIndex(1);
                scenario.choice(1);
                resetVariantsList();
            }
        });
        InputAction.set("game", "answer_3", KeyEvent.VK_3, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 3) {
                    return;
                }
                answerList.setSelectedIndex(2);
                scenario.choice(2);
                resetVariantsList();
            }
        });
        InputAction.set("game", "answer_4", KeyEvent.VK_4, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 4) {
                    return;
                }
                answerList.setSelectedIndex(3);
                scenario.choice(3);
                resetVariantsList();
            }
        });
        InputAction.set("game", "answer_5", KeyEvent.VK_5, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 5) {
                    return;
                }
                answerList.setSelectedIndex(4);
                scenario.choice(4);
                resetVariantsList();
            }
        });

        InputAction.set("game", "keyLeft", KeyEvent.VK_LEFT, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        InputAction.set("game", "keyRight", KeyEvent.VK_RIGHT, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
    }

    private void reloadRectangles() {
        if (userConf.isFullScreen()) {
            centerPicRect = new Rectangle(0, 0, FULLSCREEN_WIDTH.intValue(), (int) (FULLSCREEN_HEIGHT * 0.75D));
            heroAvatarRect = new Rectangle((int) (FULLSCREEN_WIDTH * 0.01D), (int) (FULLSCREEN_HEIGHT * 0.74D),
                    (int) (FULLSCREEN_WIDTH * 0.148D), (int) (FULLSCREEN_HEIGHT * 0.23D));
            backButtonRect = new Rectangle((int) (FULLSCREEN_WIDTH * 0.895D), (int) (FULLSCREEN_HEIGHT * 0.734D),
                    (int) (FULLSCREEN_WIDTH * 0.0832D), (int) (FULLSCREEN_HEIGHT * 0.19D));

            dialogTextRect = new Rectangle((int) (FULLSCREEN_WIDTH * 0.167D), (int) (FULLSCREEN_HEIGHT * 0.74D),
                    (int) (FULLSCREEN_WIDTH * 0.565D), (int) (FULLSCREEN_HEIGHT * 0.225D));
            choseVariantRect = new Rectangle((int) (FULLSCREEN_WIDTH * 0.735D), (int) (FULLSCREEN_HEIGHT * 0.74D),
                    (int) (FULLSCREEN_WIDTH * 0.1485D), (int) (FULLSCREEN_HEIGHT * 0.225D));

            setBackground(new Color(0, 0, 0, 0));
            setSize(FULLSCREEN_WIDTH.intValue(), FULLSCREEN_HEIGHT.intValue());
            basePane.setSize(getSize());
        } else {
            centerPicRect = new Rectangle(0, 0, WINDOWED_WIDTH.intValue(), (int) (WINDOWED_HEIGHT * 0.75D));
            heroAvatarRect = new Rectangle((int) (WINDOWED_WIDTH * 0.01D), (int) (WINDOWED_HEIGHT * 0.74D),
                    (int) (WINDOWED_WIDTH * 0.148D), (int) (WINDOWED_HEIGHT * 0.23D));
            backButtonRect = new Rectangle((int) (WINDOWED_WIDTH * 0.895D), (int) (WINDOWED_HEIGHT * 0.734D),
                    (int) (WINDOWED_WIDTH * 0.0832D), (int) (WINDOWED_HEIGHT * 0.19D));

            dialogTextRect = new Rectangle((int) (WINDOWED_WIDTH * 0.167D), (int) (WINDOWED_HEIGHT * 0.74D),
                    (int) (WINDOWED_WIDTH * 0.565D), (int) (WINDOWED_HEIGHT * 0.225D));
            choseVariantRect = new Rectangle((int) (WINDOWED_WIDTH * 0.735D), (int) (WINDOWED_HEIGHT * 0.74D),
                    (int) (WINDOWED_WIDTH * 0.1485D), (int) (WINDOWED_HEIGHT * 0.225D));

            setBackground(Color.BLACK);
            setSize(WINDOWED_WIDTH.intValue(), WINDOWED_HEIGHT.intValue());
            basePane.setSize(getSize());
        }

        answerList.setSize(choseVariantRect.width, choseVariantRect.height);
        answerList.setLocation(choseVariantRect.x, choseVariantRect.y);

        setLocationRelativeTo(null);
    }


    public void setCenterImage(String sceneName) {
        if (sceneName == null) {
            picSceneImage = (BufferedImage) cache.get("blackpane");
        }
    }

    public void updateDialogText() {
        stopAnimation();

        n++;
        System.out.println("Income data #" + n + ":\nTEXT:\n\t" + scenario.getCurrentText() + "\nANSWERS:\n\t" + scenario.getAnswers());

        textAnimateThread = new Thread(() -> {
            animateText(scenario.getCurrentText());
            updateAnswerVariants(scenario.getAnswers());
            isDialogAnimated = false;
        });
        textAnimateThread.start();
    }

    private void animateText(String text) {
        if (text != null && !text.equals(lastText)) {
            lastText = text;

            isDialogAnimated = true;
            dialogDelaySpeed = 64;

            StringBuilder sb = new StringBuilder(text);
            dialogChars = new char[text.length()];

            int shift = 0;
            for (int i = 0; i < text.length(); i++) {
                shift++;
                if (charWidth * shift > dialogTextRect.getWidth() - charWidth * 2 + 4) {
                    for (int k = i; k > 0; k--) {
                        if ((int) dialogChars[k] == 32) {
                            sb.setCharAt(k, (char) 10);
                            break;
                        }
                    }
                    shift = 0;
                }
                try {
                    sb.getChars(0, i + 1, dialogChars, 0);
                } catch (Exception e) {
                    /* IGNORE */
                }

                try {
                    Thread.sleep(dialogDelaySpeed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void updateAnswerVariants(ArrayList<String> answers) {
        if (answers != null) {
            dlm.clear();
            for (String a : answers) {
                addAnswer(a);
            }
        }
    }

    public void addAnswer(String answer) {
        if (answer.split("R").length > 1) {
            dlm.addElement(dlm.size() + 1 + ": " + answer.split("R")[1]);
        } else {
            dlm.addElement(answer);
        }
    }

    private void resetVariantsList() {
        dlm.clear();
        if (isDialogAnimated) {
            dialogDelaySpeed = 0;
        }
        addAnswer("Далее...");
    }

    private void showExitRequest() {
        backButPressed = false;
        isPaused = true;

        int closeQ = new FOptionPane(
                "Подтверждение:",
                "Да: Прервать игру и вернуться в меню.\nНет: Сохранение/загрузка.",
                FOptionPane.TYPE.YES_NO_TYPE,
                null,
                true).get();

        switch (closeQ) {
            case 0 -> stopGame();
            case 1 -> {
                new SaveGame(GamePlay.this, getGraphicsConfiguration());
                isPaused = false;
            }
            default -> {
                System.out.println("Response = " + closeQ);
                setVisible(true);
                isStoryPlayed = true;
                isPaused = false;
            }
        }
    }

    private void stopGame() {
        isPaused = false;
        isStoryPlayed = false;
        stopAnimation();

        dispose();
        new GameMenu();
    }

    private void stopAnimation() {
        if (textAnimateThread != null) {
            dialogDelaySpeed = 0;
            textAnimateThread.interrupt();
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {
        mouseWasOnScreen = new Point(e.getXOnScreen(), e.getYOnScreen());
        frameWas = getLocation();

        backButPressed = backButOver;
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if (backButOver) {
            showExitRequest();
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!userConf.isFullScreen()) {
            GamePlay.this.setLocation(
                    (int) (frameWas.getX() - (mouseWasOnScreen.getX() - e.getXOnScreen())),
                    (int) (frameWas.getY() - (mouseWasOnScreen.getY() - e.getYOnScreen())));
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseNow = e.getPoint();

        backButOver = backButtonRect.contains(mouseNow);
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if (answerList.locationToIndex(e.getPoint()) != -1) {
            if (scenario == null) {
                Print(getClass(), LEVEL.ACCENT, "Variant has click, but scenario is NULL");
                return;
            }

            Print(GamePlay.class, LEVEL.DEBUG, "Был выбран вариант " + answerList.getSelectedValue());
            if (isDialogAnimated) {
                dialogDelaySpeed = 0;
            }
            scenario.choice(answerList.getSelectedIndex());
            resetVariantsList();
        }
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }


    private class StoryPlayThread implements Runnable {
        @Override
        public void run() {
            musicPlayer.stop();
            backgPlayer.stop();
            isStoryPlayed = true;
            addAnswer("Далее...");

            Print(GamePlay.class, LEVEL.INFO, "GameFrame.StoryPlayedThread: Start now.");
            while (isStoryPlayed) {
                if (isPaused) {
                    Thread.yield();
                    continue;
                }
                basePane.repaint();
                try {Thread.currentThread().sleep(refDelay);
                } catch (InterruptedException e) {
                    System.out.println("Ошибка в потоке отрисовки: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
            Print(GamePlay.class, LEVEL.INFO, "GameFrame.StoryPlayedThread: Stop!");
        }
    }
}
//Choice choice = new Choice();
//choice.addItem("First");
//choice.addItem("Second");
//choice.addItem("Third");

////		Полезные методы класса Choice:
//countItems() - считать количество пунктов в списке; 
//	getItem(int) - возвратить строку с определенным номером в списке; 
//	select(int) - выбрать строку с определенным номером; 
//	select(String) - выбрать определенную строку текста из списка. 

//add(choice);