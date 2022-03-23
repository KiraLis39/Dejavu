package GUI;

import door.MainClass;
import fox.FoxCursor;
import fox.FoxFontBuilder;
import fox.InputAction;
import fox.Out;
import images.FoxSpritesCombiner;
import interfaces.Cached;
import logic.Scenario;
import lombok.Data;
import lombok.EqualsAndHashCode;
import registry.Registry;
import render.FoxRender;
import secondGUI.SaveGame;
import tools.Media;

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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fox.Out.LEVEL;
import static fox.Out.Print;
import static registry.Registry.userConf;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameFrame extends JFrame implements MouseListener, MouseMotionListener, Cached {
    private static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private static int FRAME_WIDTH = (int) (screen.getWidth() * 0.8D);
    private static int FRAME_HEIGHT = (int) (screen.getHeight() * 0.9D);

    private static Map<String, File> answerBlocksMap = new HashMap<>();
    private static DefaultListModel<String> dlm;
    private static JList<String> answerList;
    private static Thread textDynamicThread;
    private static BufferedImage npcImage, heroAvatar, picSceneImage;
    private static Rectangle dialogTextRect, choseVariantRect;
    private static JPanel basePane;
    private static Boolean isStoryPlayed = false, backButOver = false, backButPressed = false;
    private static Boolean isDialogAnimated = false, isPaused = false, bicubic = false;
    private static String blockExt = ".json", lastText, imageExt = ".png";
    private static String heroName;
    private static int n = 0, shift = 0;
    private static double charWidth = 12.2D;
    private static char[] dialogChars;
    private static long dialogDelaySpeed = 64;
    private BufferedImage picGamepane;
    private BufferedImage[] backButton;
    private Rectangle heroAvatarRect, backButtonRect, centerPicRect;
    private Point mouseNow, frameWas, mouseWasOnScreen;

    public GameFrame() {
        initialization();

        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curAnyCursor"), "anyCursor"));
        setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));

        basePane = new JPanel() {
            {
                setOpaque(false);
                setFocusable(true);
                setSize(GameFrame.this.getWidth(), GameFrame.this.getHeight());
                setLayout(null);

                addMouseListener(GameFrame.this);
                addMouseMotionListener(GameFrame.this);

                dlm = new DefaultListModel<>();
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
                        setForeground(Color.WHITE);
                        setBackground(new Color(0.5f, 0.5f, 1.0f, 0.1f));
                        setBorder(new EmptyBorder(3, 3, 3, 3));
                        setFont(Registry.fontDialog);
                        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        setSelectionBackground(new Color(1.0f, 1.0f, 1.0f, 0.2f));
                        setSelectionForeground(new Color(1.0f, 1.0f, 0.0f, 1.0f));
                        setVisibleRowCount(5);

                        addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseReleased(MouseEvent e) {
//								System.out.println("locationToIndex(): " + locationToIndex(e.getPoint()));
                                if (locationToIndex(e.getPoint()) != -1) {
                                    Print(GameFrame.class, LEVEL.INFO, "Был выбран вариант " + answerList.getSelectedValue());
                                    if (isDialogAnimated) {
                                        dialogDelaySpeed = 0;
                                    }
                                    Scenario.step(answerList.getSelectedIndex());
                                    resetVariantsList();
                                }
                            }
                        });

                        setFocusable(false);
                        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curTextCursor"), "textCursor"));
                    }
                };

                add(answerList);
            }
        };

        add(basePane);

        setInAc();

        setFullscreen();

        // loading First block:
        Scenario.load(new File(Registry.blockPath + "/00NewStart" + blockExt));
        Scenario.step(-1);

        setVisible(true);

        answerList.setSize(choseVariantRect.width, choseVariantRect.height);
        answerList.setLocation(choseVariantRect.x, choseVariantRect.y);

        Media.stopMusic();
        Media.stopBackg();

        isStoryPlayed = true;
        new Thread(() -> {
            while (isStoryPlayed) {
                if (isPaused) {
                    Thread.yield();
                    continue;
                }

                repaint();
                try {
                    Thread.sleep(64);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        addAnswer("Далее...");
    }

    private static BufferedImage toBImage(String path) {
        try {
            return ImageIO.read(new File(path + Registry.picExtension));
        } catch (Exception e) {
            Out.Print(MainClass.class, Out.LEVEL.WARN, "Ошибка чтения медиа '" + Paths.get(path) + "': " + e.getMessage());
        }
        return null;
    }

    // changing center background image:
    public static void setCenterImage(String sceneName) {
        if (sceneName == null) {
            picSceneImage = (BufferedImage) cache.get("blackpane");
            return;
        }

        System.out.println("Try to read the image: '" + Registry.scenesDir + "/" + sceneName + imageExt + "'...");
        picSceneImage = (BufferedImage) cache.get(sceneName.replace(imageExt, ""));
        if (picSceneImage == null) {
            picSceneImage = (BufferedImage) cache.get("blackpane");
        }
    }

    // changing down left hero avatar:
    public static void setHeroAvatar(int avatarIndex) {
        Print(GameFrame.class, LEVEL.INFO, "setHeroAvatar: Try to set hero`s avatar #" + avatarIndex);
        if (avatarIndex == 0) {
            heroAvatar = (BufferedImage) cache.get("0");
            return;
        }
        heroAvatar = (BufferedImage) cache.get(avatarIndex + "");
    }

    public static void setHeroName(String name) {
        heroName = name;
    }

    public static void setNpcImage(BufferedImage npcImage) {
        throw new RuntimeException("GameFrame.setNpcImage is empty method");
    }

    // changing dialog text:
    public static void setDialogText(String text, ArrayList<String> answers) {
        if (textDynamicThread != null) {
            dialogDelaySpeed = 0;
            textDynamicThread.interrupt();
        }
        if (text == null && answers == null) {
            System.out.println("Waiting for choise!..");
            return;
        }

        System.out.println("Income data " + ++n + ": " + text + "; " + answers);
        textDynamicThread = new Thread(() -> {
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
                    } catch (Exception e) {/* IGNORE */
                    }

                    try {
                        Thread.sleep(dialogDelaySpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (answers != null) {
                dlm.clear();
                for (String a : answers) {
                    addAnswer(a);
                }
            }
            isDialogAnimated = false;
        });
        textDynamicThread.start();
    }

    // handle add new answers (use setDialogText(dialogText, answers[])):
    private static void addAnswer(String answer) {
        System.out.println("#: addAnswer: " + answer);
        dlm.addElement(dlm.size() + 1 + ": " + answer.split("%")[0]);
        if (answer.split("%").length > 1) {
            fileMapAdd(answer);
        }
    }

    private static void resetVariantsList() {
        dlm.clear();
        if (isDialogAnimated) {
            dialogDelaySpeed = 0;
        }
        addAnswer("Далее...");
    }

    private static void fileMapAdd(String answer) {
//		System.out.println("#: fileMapAdd: " + answer);
        answerBlocksMap.put(
                answer.split("%")[0],
                new File(Registry.blockPath + "/" + answer.split("%")[1] + blockExt)
        );

//		System.out.println("\nAnswers map now: " + Arrays.asList(answerBlocksMap.entrySet()));
    }

    public static File getScenario(int answerIndex) {
//		System.out.println("\nЗапрашиваем индекс #" + answerIndex);
        return getScenario(answerList.getSelectedValue());
    }

    public static File getScenario(String fileName) {
        if (fileName == null) {
            return null;
        }
        fileName = fileName.split(":")[1].trim();
//		System.out.println("\nВозвращаем файл '" + fileName + "'.");
        return answerBlocksMap.get(fileName);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        FoxRender.setMedRender(g2D);

        drawBase(g2D);
        drawAvatar(g2D);
        drawBackButton(g2D);
        drawAutoDialog(g2D); // поместить в FoxLib

        g2D.setColor(Color.GRAY);
        g2D.drawRoundRect(dialogTextRect.x, dialogTextRect.y, dialogTextRect.width, dialogTextRect.height, 16, 16);
        g2D.setColor(Color.GRAY);
        g2D.drawRoundRect(choseVariantRect.x, choseVariantRect.y, choseVariantRect.width, choseVariantRect.height, 8, 8);

//		g2D.setColor(Color.YELLOW);g2D.drawRect(backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height);

        super.paintComponents(g2D);
    }

    private void drawBase(Graphics2D g2D) {
        if (picSceneImage == null) {
            picSceneImage = (BufferedImage) cache.get("blackpane");
        }
        try {
            picSceneImage.getWidth();
            g2D.drawImage(picSceneImage, this.centerPicRect.x, this.centerPicRect.y, this.centerPicRect.width, this.centerPicRect.height, this);
        } catch (Exception e) {
            g2D.setColor(Color.DARK_GRAY);
            g2D.fillRect(16, 16, getWidth() - 32, getHeight() - 32);
            g2D.setColor(Color.RED);
            g2D.drawString("NO IMAGE", (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g2D, "NO IMAGE").getWidth() / 2), getHeight() / 2 - 96);
        }

        drawNPC(g2D);

        g2D.drawImage(this.picGamepane, 0, 0, getWidth(), getHeight(), this);
    }

    private void drawNPC(Graphics2D g2D) {
        if (npcImage == null) {
            return;
        }
        // draw NPC:
        g2D.drawImage(npcImage, 0, 0, npcImage.getWidth(), npcImage.getHeight(), this);
    }

    private void drawAvatar(Graphics2D g2D) {
        // draw hero avatar:
        g2D.drawImage(heroAvatar, heroAvatarRect.x, heroAvatarRect.y, heroAvatarRect.width, heroAvatarRect.height, this);
    }

    private void drawBackButton(Graphics2D g2D) {
        if (backButOver) {
            if (backButPressed) {
                g2D.drawImage(backButton[1], backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height, this);
            } else {
                g2D.drawImage(backButton[2], backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height, this);
            }
        } else {
            g2D.drawImage(backButton[0], backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height, this);
        }
    }

    private void drawAutoDialog(Graphics2D g2D) {
        if (dialogChars != null) {
            // hero name:
            g2D.setFont(Registry.fontName);
            g2D.setColor(Color.BLACK);
            g2D.drawString(heroName, dialogTextRect.x + 4, dialogTextRect.y + 21);
            g2D.setColor(Color.ORANGE);
            g2D.drawString(heroName, dialogTextRect.x + 5, dialogTextRect.y + 20);

            // draw hero dialog:
            g2D.setFont(Registry.fontDialog);
            charWidth = g2D.getFontMetrics().getMaxCharBounds(g2D).getWidth();

            g2D.setColor(Color.GREEN);
            int mem = 0, line = 1;
            W:
            while (true) {
                shift = 0;
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

    private void initialization() {
        // npc avatars:
        try {
            for (Path path : Files.list(Registry.npcAvatarsDir).collect(Collectors.toList())) {
                cache.add(path.toFile().getName().replace(Registry.picExtension, ""), toBImage(path.toString().replace(Registry.picExtension, "")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // scenes load:
        try {
            for (Path path : Files.list(Registry.scenesDir).collect(Collectors.toList())) {
                cache.add(path.toFile().getName().replace(Registry.picExtension, ""), toBImage(path.toString().replace(Registry.picExtension, "")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // other images:
        backButton = FoxSpritesCombiner.addSpritelist("picBackButBig", (BufferedImage) cache.get("picBackButBig"), 3, 1);
        picGamepane = (BufferedImage) cache.get("picGamepane");

        setCenterImage(null);
    }

    private void setInAc() {
        InputAction.add("game", this);

        InputAction.set("game", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int closeQ = JOptionPane.showConfirmDialog(null,
                        "Уверен? Прекратить текущую игру и вернуться в меню? Нет - сохранение и загрузка.",
                        "Подтверждение:",
                        JOptionPane.YES_NO_OPTION);

                switch (closeQ) {
                    case 0:
                        isStoryPlayed = false;
                        dispose();
                        new MainMenu();
                        break;

                    case 1:
                        new SaveGame();
                        isPaused = false;
                        break;

                    default:
                        setVisible(true);
                }
            }
        });
        InputAction.set("game", "fullscreen", KeyEvent.VK_F, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {/* setFullscreen() */}
        });

        InputAction.set("game", "next", KeyEvent.VK_SPACE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isDialogAnimated) {
                    dialogDelaySpeed = 0;
                    return;
                }
                Scenario.step(-1);
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
                Scenario.step(0);
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
                Scenario.step(1);
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
                Scenario.step(2);
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
                Scenario.step(3);
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
                Scenario.step(4);
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

    private void stopGame() {
        isStoryPlayed = false;
        if (textDynamicThread != null) {
            textDynamicThread.interrupt();
        }

        dispose();
        new MainMenu();
    }

    private void reloadRectangles() {
        this.centerPicRect = new Rectangle(0, 0, FRAME_WIDTH, (int) (FRAME_HEIGHT * 0.75D));
        this.heroAvatarRect = new Rectangle((int) (FRAME_WIDTH * 0.01D), (int) (FRAME_HEIGHT * 0.74D), (int) (FRAME_WIDTH * 0.148D), (int) (FRAME_HEIGHT * 0.23D));
        this.backButtonRect = new Rectangle((int) (FRAME_WIDTH * 0.895D), (int) (FRAME_HEIGHT * 0.734D), (int) (FRAME_WIDTH * 0.0832D), (int) (FRAME_HEIGHT * 0.19D));

        dialogTextRect = new Rectangle((int) (FRAME_WIDTH * 0.167D), (int) (FRAME_HEIGHT * 0.74D), (int) (FRAME_WIDTH * 0.565D), (int) (FRAME_HEIGHT * 0.225D));
        choseVariantRect = new Rectangle((int) (FRAME_WIDTH * 0.735D), (int) (FRAME_HEIGHT * 0.74D), (int) (FRAME_WIDTH * 0.1485D), (int) (FRAME_HEIGHT * 0.225D));

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        basePane.setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    private void setFullscreen() {
        if (userConf.isFullScreen()) {
            FRAME_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
            FRAME_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
        } else {
            FRAME_WIDTH = (int) (screen.getWidth() * 0.75D);
            FRAME_HEIGHT = (int) (screen.getHeight() * 1.0D);
        }
        reloadRectangles();
        GameFrame.this.setLocationRelativeTo(null);
//		userConf.setFullScreen(!userConf.isFullScreen());
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
            backButPressed = false;
            isPaused = true;

            int closeQ = JOptionPane.showConfirmDialog(null,
                    "Уверен? Прекратить текущую игру и вернуться в меню? Нет - сохранение и загрузка.",
                    "Подтверждение:",
                    JOptionPane.YES_NO_OPTION);

            switch (closeQ) {
                case 0 -> stopGame();
                case 1 -> {
                    new SaveGame();
                    isPaused = false;
                }
                default -> {
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!userConf.isFullScreen()) {
            GameFrame.this.setLocation(
                    (int) (frameWas.getX() - (mouseWasOnScreen.getX() - e.getXOnScreen())),
                    (int) (frameWas.getY() - (mouseWasOnScreen.getY() - e.getYOnScreen())));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseNow = e.getPoint();

        backButOver = backButtonRect.contains(mouseNow);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public enum modSides {UP, DOWN, LEFT, RIGHT}
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