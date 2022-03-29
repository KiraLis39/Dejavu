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
import lombok.Data;
import registry.Registry;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static fox.Out.LEVEL;
import static fox.Out.Print;
import static registry.Registry.*;

@Data
public class GamePlay extends iGamePlay implements MouseListener, MouseMotionListener, WindowListener, Cached {
    private static GamePlay gamePlay;
    private ScenarioEngine scenario = new ScenarioEngine();

    private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private Double WINDOWED_WIDTH = screen.getWidth() * 0.75D;
    private Double WINDOWED_HEIGHT = screen.getHeight() * 0.9D;

    private DefaultListModel<String> dlm = new DefaultListModel<>();
    private JList<String> answerList;

    private Thread textAnimateThread;
    private long dialogDelaySpeed = 48, defaultDialogDefaultDelay = 48;

    private BufferedImage nullSceneImage, currentSceneImage;
    private BufferedImage currentNpcImage;
    private BufferedImage nullAvatar, currentHeroAvatar;
    private BufferedImage gameImageUp, gameImageDL, gameImageDC, gameImageDR;
    private BufferedImage[] backButtons;

    private JPanel basePane, downCenterPane;
    private boolean isStoryPlayed, backButOver, backButPressed, isDialogAnimated, isPaused;

    private int n = 0;
    private int refDelay;
    private long was = System.currentTimeMillis();
    private String dialogOwner;
    private String curFps;
    private float fpsIterCount = 0;
    private Double charWidth = 12.2D;
    private char[] dialogChars;
    private Point mouseNow, frameWas, mouseWasOnScreen;
    private Shape backBtnShape, dialogTextRext;


    // FRAME BUILD:
    public GamePlay(GraphicsConfiguration gConfig) {
        super("GamePlayParent", gConfig);
        gamePlay = this;
        refDelay = 1000 / gConfig.getDevice().getDisplayMode().getRefreshRate();

        setName("GamePlay");
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(Cursors.PinkCursor.get());
        setAutoRequestFocus(true);

        addWindowListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        loadResources();
        setInAc();
        checkFullscreen();

        JPanel upPane = new JPanel() {
            {
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;
                FoxRender.setRender(g2D, userConf.getQuality());

                try {
                    g2D.drawImage(currentSceneImage,
                            Double.valueOf(GamePlay.this.getWidth() * 0.01d).intValue(),
                            Double.valueOf(GamePlay.this.getHeight() * 0.01d).intValue(),
                            getWidth() - Double.valueOf(GamePlay.this.getWidth() * 0.02d).intValue(),
                            getHeight() - Double.valueOf(GamePlay.this.getHeight() * 0.01d).intValue(),
                            this);
                } catch (Exception e) {
                    g2D.setColor(Color.DARK_GRAY);
                    g2D.fillRect(16, 16, getWidth() - 32, getHeight() - 32);
                    g2D.setColor(Color.RED);
                    g2D.drawString("NO IMAGE", (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g2D, "NO IMAGE").getWidth() / 2), getHeight() / 2 - 96);
                }

                drawNPC(g2D);

                g2D.drawImage(gameImageUp, 0, 0,
                        getWidth(), getHeight(),
                        this);

//                g2D.setColor(Color.GREEN);
//                g2D.drawRect(1,1,getWidth()-2,getHeight()-2);
//                g2D.setColor(Color.BLUE);
//                g2D.drawRect(
//                        Double.valueOf(GamePlay.this.getWidth() * 0.01d).intValue(),
//                        Double.valueOf(GamePlay.this.getHeight() * 0.01d).intValue(),
//                        getWidth() - Double.valueOf(GamePlay.this.getWidth() * 0.02d).intValue(),
//                        getHeight() - Double.valueOf(GamePlay.this.getHeight() * 0.02d).intValue());
            }

            private void drawNPC(Graphics2D g2D) {
                if (currentNpcImage == null) {
                    return;
                }
                g2D.drawImage(currentNpcImage,
                        10, 10,
                        getWidth() - 20, getHeight() - 20,
                        this);
            }
        };

        JPanel downPane = new JPanel(new BorderLayout(0, 0)) {
            {
                setOpaque(false);
                setPreferredSize(new Dimension(0, Double.valueOf(GamePlay.this.getHeight() * 0.25d).intValue()));

                JPanel downLeftPane = new JPanel() {
                    BufferedImage avatar;

                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2D = (Graphics2D) g;
                        FoxRender.setRender(g2D, userConf.getQuality());

                        g2D.drawImage(gameImageDL, 0, 0, getWidth(), getHeight(), this);
                        drawAvatar(g2D);

//                        g2D.setColor(Color.RED);
//                        g2D.drawRect(1,1,getWidth()-2,getHeight()-2);
                    }

                    private void drawAvatar(Graphics2D g2D) {
                        if (currentHeroAvatar == null) {
//                            if (nullAvatar == null) {
//                                nullAvatar = (BufferedImage) cache.get("0");
//                            }
                            avatar = nullAvatar;
                        } else {
                            avatar = currentHeroAvatar;
                        }

                        g2D.drawImage(avatar,
                                Double.valueOf(getWidth() * 0.1d).intValue(),
                                Double.valueOf(getHeight() * 0.04d).intValue(),
                                getWidth() - Double.valueOf(getWidth() * 0.19d).intValue(),
                                getHeight() - Double.valueOf(getHeight() * 0.16d).intValue(),
                                this);
                    }

                    {
                        setOpaque(false);
                        setPreferredSize(new Dimension(Double.valueOf(GamePlay.this.getWidth() * 0.17d).intValue(), 0));
                    }
                };

                downCenterPane = new JPanel(new BorderLayout(0, 0)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2D = (Graphics2D) g;
                        FoxRender.setRender(g2D, userConf.getQuality());

                        g2D.drawImage(gameImageDC, 0, 0, getWidth(), getHeight(), this);

                        drawAutoDialog(g2D);

//                        g2D.setColor(Color.YELLOW);
//                        g2D.drawRect(0,0,getWidth()-1,getHeight()-1);
//                        g2D.setColor(Color.GREEN.darker());
//                        g2D.draw(answerList.getBounds());
                        if (dialogTextRext == null) {
                            dialogTextRext = new Rectangle(
                                    Double.valueOf(getWidth() * 0.0025d).intValue(),
                                    Double.valueOf(getHeight() * 0.04d).intValue(),
                                    getWidth() - answerList.getWidth() - Double.valueOf(getWidth() * 0.005d).intValue(),
                                    getHeight() - Double.valueOf(getHeight() * 0.08d).intValue()
                            );
                        }
                        g2D.setColor(Color.GREEN.darker());
                        g2D.draw(dialogTextRext);
                    }

                    private void drawAutoDialog(Graphics2D g2D) {
                        // owner name:
                        if (dialogOwner != null) {
                            g2D.setFont(fontName);
                            g2D.setColor(Color.BLACK);
                            g2D.drawString(dialogOwner, dialogTextRext.getBounds().x - 3, dialogTextRext.getBounds().y + 6);
                            g2D.setColor(Color.ORANGE);
                            g2D.drawString(dialogOwner, dialogTextRext.getBounds().x - 4, dialogTextRext.getBounds().y + 5);
                        }

                        // dialog:
                        if (dialogChars != null) {
                            g2D.setFont(fontDialog);
                            charWidth = g2D.getFontMetrics().getMaxCharBounds(g2D).getWidth();

                            g2D.setColor(Color.GREEN);
                            int mem = 0, line = 1;
                            W:
                            while (true) {
                                float shift = 0f;
                                line++;

                                for (int i = mem; i < dialogChars.length; i++) {
                                    if (dialogChars[i] == 10) {
                                        mem = i + 1;
                                        break;
                                    } // next line marker detector (\n)

                                    g2D.drawString(String.valueOf(dialogChars[i]),
                                            dialogTextRext.getBounds().x * (shift * 5),
                                            dialogTextRext.getBounds().y * line * 2
                                    );

                                    shift++;
                                    if (i >= dialogChars.length - 1) {
                                        break W;
                                    }
                                }
                            }
                        }
                    }

                    {
                        setOpaque(false);
                        setBorder(new EmptyBorder(6, 0, 32, 0));

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

                                setPreferredSize(new Dimension(210, 0));
                            }
                        };

                        add(answerList, BorderLayout.EAST);
                    }
                };

                JPanel downRightPane = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2D = (Graphics2D) g;
                        FoxRender.setRender(g2D, userConf.getQuality());

                        g2D.drawImage(gameImageDR, 0, 0, getWidth(), getHeight(), this);

//                        g2D.setColor(Color.CYAN);
//                        g2D.drawRect(1,1,getWidth()-2,getHeight()-2);

                        if (backBtnShape == null) {
                            backBtnShape = new Polygon() {
                                {
                                    addPoint(24, 6);
                                    addPoint(getWidth() - 18, 6);
                                    addPoint(getWidth() - 18, getHeight() - 48);

                                    addPoint((int) (getWidth() * 0.475f), (int) (getHeight() * 0.6f));
                                    addPoint((int) (getWidth() * 0.25f), (int) (getHeight() * 0.3f));
                                }
                            };
                        }

                        drawBackButton(g2D);
//                        g2D.setColor(Color.PINK);
//                        g2D.draw(backBtnShape);
                    }

                    private void drawBackButton(Graphics2D g2D) {
                        if (backButOver) {
                            g2D.drawImage(backButPressed ? backButtons[1] : backButtons[1],
                                    backBtnShape.getBounds().x + 6, backBtnShape.getBounds().y - 3,
                                    backBtnShape.getBounds().width, backBtnShape.getBounds().height,
                                    this);
                        } else {
                            g2D.drawImage(backButtons[0],
                                    backBtnShape.getBounds().x, backBtnShape.getBounds().y,
                                    backBtnShape.getBounds().width, backBtnShape.getBounds().height,
                                    this);
                        }
                    }

                    {
                        setName("btnPane");
//                        setOpaque(false);
                        setPreferredSize(new Dimension(Double.valueOf(GamePlay.this.getWidth() * 0.1d).intValue(), 0));
                        addMouseMotionListener(GamePlay.this);
                        addMouseListener(GamePlay.this);
                    }
                };

                add(downLeftPane, BorderLayout.WEST);
                add(downCenterPane, BorderLayout.CENTER);
                add(downRightPane, BorderLayout.EAST);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;
                FoxRender.setRender(g2D, userConf.getQuality());

                super.paintComponents(g2D);

//                g2D.setColor(Color.ORANGE);
//                g2D.drawRect(0,0,getWidth()-1,getHeight()-1);
            }
        };

        add(upPane, BorderLayout.CENTER);
        add(downPane, BorderLayout.SOUTH);

        try {
            // loading First block:
            scenario.load("00NewStart");
        } catch (IOException e) {
            e.printStackTrace();
            dispose();
        }
        new Thread(new StoryPlayThread()) {
            {
                setDaemon(true);
                setName("StoryPlayed-thread (GameFrame repaint thread)");
            }
        }.start();
    }

    // GETTERS & SETTERS:
    public static GamePlay getGamePlay() {
        return gamePlay;
    }

    // FRAME DRAWING:
    @Override
    public void paint(Graphics g) {
        super.paint(g);

//        g.setColor(Color.GRAY);
//        g.drawRoundRect(dialogTextRect.x, dialogTextRect.y, dialogTextRect.width, dialogTextRect.height, 16, 16);
//        g.setColor(Color.GRAY);
//        g.drawRoundRect(choseVariantRect.x, choseVariantRect.y, choseVariantRect.width, choseVariantRect.height, 8, 8);
//        g.setColor(Color.YELLOW);
//        g.drawRect(backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height);

        drawFPS(g);
    }

    private void drawFPS(Graphics g) {
        if (configuration.isFpsShowed()) {
            fpsIterCount++;
            if (System.currentTimeMillis() - was > 1000) {
                curFps = Double.valueOf(Math.floor(fpsIterCount)).toString();
                was = System.currentTimeMillis();
                fpsIterCount = 0;
            }

            g.setColor(Color.GRAY);
            g.drawString(curFps, 10, 25);
        }
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
            backButtons = FoxSpritesCombiner.getSprites("picBackButBig",
                    (BufferedImage) cache.get("picBackButBig"), 1, 3);
            gameImageUp = (BufferedImage) cache.get("picGamepaneUp");
            gameImageDL = (BufferedImage) cache.get("picGamepaneDL");
            gameImageDC = (BufferedImage) cache.get("picGamepaneDC");
            gameImageDR = (BufferedImage) cache.get("picGamepaneDR");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setScene(null, null);
    }

    private BufferedImage toBImage(String path) {
        try {
            return ImageIO.read(new File(path + picExtension));
        } catch (Exception e) {
            Out.Print(MainClass.class, Out.LEVEL.WARN, "Ошибка чтения медиа '" + Paths.get(path) + "': " + e.getMessage());
        }
        return null;
    }

    private void setInAc() {
        InputAction.add("game", GamePlay.this); // SwingUtilities.getWindowAncestor(basePane));

        InputAction.set("game", "Ctrl+F4", KeyEvent.VK_F4, 512, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showExitRequest();
            }
        });
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
                checkFullscreen();
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
            }
        });
        InputAction.set("game", "answer_6", KeyEvent.VK_6, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 6) {
                    return;
                }
                answerList.setSelectedIndex(5);
                scenario.choice(5);
            }
        });

        InputAction.set("game", "keyLeft", KeyEvent.VK_LEFT, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Key left...");
            }
        });
        InputAction.set("game", "keyRight", KeyEvent.VK_RIGHT, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Key right...");
            }
        });
    }

    private void checkFullscreen() {
        if (isVisible() && (userConf.isFullScreen() && getExtendedState() == MAXIMIZED_BOTH || !userConf.isFullScreen() && getExtendedState() == NORMAL)) {
            return;
        }

        Print(GamePlay.class, LEVEL.INFO, "\nGamePlay fullscreen switch...");
        dispose();

        if (userConf.isFullScreen()) {
            getContentPane().setBackground(Color.BLACK);
            setExtendedState(MAXIMIZED_BOTH);
//            gDevice.setFullScreenWindow(GameMenu.this);
        } else {
//            gDevice.setFullScreenWindow(null);
            setExtendedState(NORMAL);
            setBackground(new Color(0, 0, 0, 0));
            setPreferredSize(new Dimension(WINDOWED_WIDTH.intValue(), WINDOWED_HEIGHT.intValue()));
            pack();
        }

        setVisible(true);
        setLocationRelativeTo(null);

        Print(GamePlay.class, LEVEL.INFO, "GamePlay fullscreen checked. Thread: " + Thread.currentThread().getName());
    }

    // GAME CONTROLS:
    @Override
    public void setScene(String sceneName, String npcName) {
        // scene:
        if (sceneName == null) {
            if (nullSceneImage == null) {
                nullSceneImage = (BufferedImage) cache.get("blackpane");
            }
            currentSceneImage = nullSceneImage;
        } else {
            currentSceneImage = (BufferedImage) cache.get(sceneName);
        }

        // npc:
        if (npcName == null) {
            return;
        } else {
            currentNpcImage = (BufferedImage) cache.get(npcName);
        }
    }

    @Override
    public void setDialog(String dialogOwner, String dialogText, ArrayList<String> answers) {
        stopAnimation();
        n++;
//        System.out.println("\nIncome data #" + n + ":\nTEXT: '[" + dialogOwner + "] " + dialogText + "'\nANSWERS: " + (answers == null ? "(next)" : Arrays.toString(answers.toArray())) + "\n");

        // owner:
        if (dialogOwner == null || dialogOwner.equals("NULL")) {
            setAvatar(null);
            this.dialogOwner = "Кто-то:";
        } else {
            setAvatar(dialogOwner);
            this.dialogOwner = dialogOwner;
        }

        // text:
        textAnimateThread = new Thread(() -> animateText(dialogText));
        textAnimateThread.start();

        // answers:
        if (answers != null) {
            setAnswers(answers);
        } else {
            setAnswers(new ArrayList<>() {{
                add("Далее...");
            }});
        }
    }

    public void setAvatar(String avatar) {
        if (avatar == null) {
            currentHeroAvatar = (BufferedImage) cache.get("0");
        } else {
            currentHeroAvatar = (BufferedImage) cache.get(avatar);
        }
    }

    private void animateText(String text) {
        isDialogAnimated = false;

        if (text != null) { //  && !text.equals(lastText)
//            lastText = text;

            isDialogAnimated = true;
            dialogDelaySpeed = defaultDialogDefaultDelay;

            StringBuilder sb = new StringBuilder(text);
            dialogChars = new char[text.length()];

            int shift = 0;
            for (int i = 0; i < text.length(); i++) {
                shift++;
                if (charWidth * shift > dialogTextRext.getBounds().width - charWidth * 2 + 4) {
                    for (int k = i; k > 0; k--) {
                        if ((int) dialogChars[k] == 32) {
                            sb.setCharAt(k, (char) 10);
                            break;
                        }
                    }
                    shift = 0;
                }
                try {sb.getChars(0, i + 1, dialogChars, 0);
                } catch (Exception e) {
                    /* IGNORE */
                }

                try {Thread.sleep(dialogDelaySpeed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            isDialogAnimated = false;
        }
    }

    public void setAnswers(ArrayList<String> answers) {
        dlm.clear();
        if (answers == null) {
            dlm.addElement("Далее...");
            return;
        }

        for (String answer : answers) {
            dlm.addElement(dlm.size() + 1 + ": " + answer);
        }
    }

    // EXIT:
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

        new GameMenu();
        if (isVisible()) {
            dispose();
        }
    }

    private void stopAnimation() {
        if (textAnimateThread != null) {
            dialogDelaySpeed = 0;
            textAnimateThread.interrupt();
            try {
                textAnimateThread.join(500);
            } catch (InterruptedException ignore) {
            }
        }
    }

    // LISTENERS:
    @Override
    public void mousePressed(MouseEvent e) {
        mouseWasOnScreen = e.getLocationOnScreen();
        frameWas = getLocation();

        backButPressed = backButOver;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (backButOver) {
            showExitRequest();
            backButPressed = backButOver = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!userConf.isFullScreen() && frameWas != null) {
            GamePlay.this.setLocation(
                    (int) (frameWas.getX() - (mouseWasOnScreen.getX() - e.getXOnScreen())),
                    (int) (frameWas.getY() - (mouseWasOnScreen.getY() - e.getYOnScreen())));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseNow = e.getPoint();

        if (backBtnShape == null) {
            return;
        }
        if (e.getSource() instanceof JPanel && ((JPanel) e.getSource()).getName().equals("btnPane")) {
            backButOver = backBtnShape.contains(e.getPoint());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (answerList.locationToIndex(e.getPoint()) != -1) {
            if (scenario == null) {
                return;
            }
            Print(GamePlay.class, LEVEL.DEBUG, "Был выбран вариант " + answerList.getSelectedValue());
            scenario.choice(answerList.getSelectedIndex());
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        showExitRequest();
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }


    // THREAD:
    private class StoryPlayThread implements Runnable {
        @Override
        public void run() {
            musicPlayer.stop();
            backgPlayer.stop();
            isStoryPlayed = true;
            setAnswers(new ArrayList<>() {{
                add("Начать игру");
            }});

            Print(GamePlay.class, LEVEL.INFO, "GameFrame.StoryPlayedThread: Start now.");
            while (isStoryPlayed) {
                if (isPaused) {
                    Thread.yield();
                    continue;
                }
                repaint();
                try {
                    Thread.currentThread().sleep(refDelay);
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