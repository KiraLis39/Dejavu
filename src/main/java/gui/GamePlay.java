package gui;

import components.FOptionPane;
import configurations.UserSave;
import door.MainClass;
import fox.Out;
import images.FoxCursor;
import interfaces.Cached;
import iom.JIOM;
import logic.ScenarioEngine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import render.FoxRender;
import secondGUI.OptMenuFrame;
import tools.Cursors;
import utils.FoxFontBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

import static fox.Out.LEVEL;
import static fox.Out.Print;
import static registry.Registry.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class GamePlay extends JFrame implements MouseListener, MouseMotionListener, WindowListener, Cached {
    private static GamePlay instance;
    public enum MONTH {июнь, июль, август}
    private Double WINDOWED_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.75D;
    private Double WINDOWED_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.9D;

    static DefaultListModel<String> dlm;
    static JList<String> answerList;
    static long dialogDelaySpeed = 32;
    static volatile boolean isDialogAnimated;

    private static Thread textAnimateThread;
    private static long defaultDialogDefaultDelay = 48;
    private static volatile BufferedImage currentSceneImage, currentNpcImage, currentHeroAvatar;
    private static volatile boolean isChapterUpdate;
    private static volatile boolean needsUpdateRectangles;
    private static char[] dialogChars;
    private static Double charWidth, charHeight;
    private static String dialogOwner;
    private static Shape dialogTextRect, downArea, avatarRect, backBtnShape;
    private static String lastText;
    private long was = System.currentTimeMillis(), autoSaveSeconds = 15_000;
    private BufferedImage nullAvatar, gameImageUp, backButtons;
    private JPanel basePane, downCenterPane;
    public static boolean isStoryPlayed;
    private boolean showQualityChanged, backButOver, backButPressed, showDebugGraphic = false, isShowInfo;
    private int refDelay, infoShowedCycles = 100;
    private float fpsIterCount = 0;
    private double curFps;
    private Point mouseNow, frameWas, mouseWasOnScreen;
    private ScenarioEngine scenario = new ScenarioEngine();
    private Color chapterColor = new Color(0.0f, 0.0f, 0.0f, 0.5f);
    private Polygon chapterPolygon;
    private Canvas canvas;
    private ArrayList<String> infos;

    // DRAW CANVAS THREAD:
    private class StoryPlayThread implements Runnable {
        AffineTransform tr;
        BufferedImage avatar;

        @Override
        public void run() {
            if (userSave.getMusicPlayed() != null) {
                musicPlayer.play(userSave.getMusicPlayed());
            }
            if (userSave.getBackgPlayed() != null) {
                backgPlayer.play(userSave.getBackgPlayed());
            }
            if (userSave.getScreen() != null) {
                currentSceneImage = (BufferedImage) cache.get(userSave.getScreen());
            }

            needsUpdateRectangles = true;
            isStoryPlayed = true;
            BufferStrategy bs;

            Print(GamePlay.class, LEVEL.ACCENT, "GameFrame.StoryPlayedThread: Start now.");
            while (isStoryPlayed) {
                if (canvas.getBufferStrategy() == null || !canvas.isValid()) {
                    canvas.createBufferStrategy(2);
                }

                try {
                    bs = canvas.getBufferStrategy();

                    do {
                        do {
                            Graphics2D g2D = (Graphics2D) bs.getDrawGraphics();
                            FoxRender.setRender(g2D, userConf.getQuality());
                            tr = g2D.getTransform();

                            drawScene(g2D);
                            drawNPC(g2D);
                            drawChapterAndDay(g2D);
                            drawUI(g2D);
                            updateAndDrawDialogZones(g2D);

                            drawOther(g2D);
                            drawFPS(g2D);

                            g2D.dispose();

                            if (needsUpdateRectangles) {
                                needsUpdateRectangles = false;
                            }
                        } while (bs.contentsRestored());
                        bs.show();
                    } while (bs.contentsLost());

                    Thread.sleep(refDelay);
                } catch (Exception e) {
                    System.err.println("Ошибка в потоке отрисовки: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
            Print(GamePlay.class, LEVEL.ACCENT, "GameFrame.StoryPlayedThread: Stop!");
        }

        private void updateAndDrawDialogZones(Graphics2D g2D) {
            if (dialogTextRect == null || needsUpdateRectangles) {
                downArea = new RoundRectangle2D.Float(
                        0,0,
                        getWidth() * 0.98f,getHeight() * 0.25f,
                        12f, 12f
                );

                if (currentHeroAvatar == null) {
                    avatar = nullAvatar;
                } else {
                    avatar = currentHeroAvatar;
                }

                avatarRect = new Rectangle(3,3,downArea.getBounds().height - 6, downArea.getBounds().height - 6);

                dialogTextRect = new Rectangle(
                        avatarRect.getBounds().width + 6,
                        avatarRect.getBounds().y,
                        downArea.getBounds().width - avatarRect.getBounds().width - 9, // - right button width,
                        downArea.getBounds().height - 6
                );

                backBtnShape = new Ellipse2D.Float(
                        downArea.getBounds().width - 64 - 3,
                        downArea.getBounds().height - 64 - 3,
                        64,
                        64);

                answerList.setBounds(
                        (int) (getWidth() * 0.825f),
                        (int) (getHeight() * 0.575f),
                        (int) (getWidth() * 0.2f),
                        120);
            }

            g2D.translate(getWidth() * 0.012f, getHeight() * 0.73f);
            {
                drawDownArea(g2D);
                drawAvatar(g2D);
                drawOwnerText(g2D);
                drawBackButton(g2D);
            }
            g2D.setTransform(tr);
            drawAnswers(g2D);
            g2D.setTransform(tr);
        }

        private void drawDownArea(Graphics2D g2D) {
            g2D.setColor(new Color(0.25f, 0.35f, 0.5f, 0.25f));
            g2D.fill(downArea);

            if (showDebugGraphic) {
                g2D.setColor(Color.GREEN);
                g2D.draw(downArea);
            }
        }

        private void drawAvatar(Graphics2D g2D) {
            Rectangle avr = avatarRect.getBounds();
            g2D.drawImage(avatar, avr.x, avr.y, avr.width, avr.height, canvas);
            if (showDebugGraphic) {
                g2D.setColor(Color.RED.brighter());
                g2D.draw(avatarRect);
            }
        }

        private void drawOwnerText(Graphics2D g2D) {
            drawOwnerName(g2D);
            drawAutoDialog(g2D);
            if (showDebugGraphic) {
                g2D.setColor(Color.BLUE.brighter());
                g2D.draw(dialogTextRect);
            }
        }

        private void drawOwnerName(Graphics2D g2D) {
            if (dialogOwner != null) {
                Rectangle avr = avatarRect.getBounds();
                g2D.setFont(fontName);

                g2D.setColor(Color.BLACK);
                g2D.drawString(dialogOwner,
                        (int) (avr.width / 2 - FoxFontBuilder.getStringBounds(g2D, dialogOwner).getWidth() / 2) - 1,
                        avr.height - 13);

                g2D.setColor(Color.ORANGE);
                g2D.drawString(dialogOwner,
                        (int) (avr.width / 2 - FoxFontBuilder.getStringBounds(g2D, dialogOwner).getWidth() / 2),
                        avr.height - 12);
            }
        }

        private void drawAutoDialog(Graphics2D g2D) {
            g2D.setFont(fontDialog);
            if (charWidth == null || charHeight == null) {
                charWidth = g2D.getFontMetrics().getMaxCharBounds(g2D).getWidth();
                charHeight = g2D.getFontMetrics().getMaxCharBounds(g2D).getHeight();
            }

            if (dialogChars != null) {
                int mem = 0, line = 0;
                W:
                while (true) {
                    float shift = 0f;
                    line++;

                    for (int i = mem; i < dialogChars.length; i++) {
                        if (dialogChars[i] == 10) {
                            mem = i + 1;
                            break;
                        } // next line marker detector (\n)
                        g2D.setColor(Color.DARK_GRAY);
                        g2D.drawString(String.valueOf(dialogChars[i]),
                                Double.valueOf(dialogTextRect.getBounds().x + (shift * charWidth) + 1.0d).floatValue(),
                                Double.valueOf(dialogTextRect.getBounds().y + (line * charHeight) + 1.0f).floatValue()
                        );

                        g2D.setColor(Color.WHITE);
                        g2D.drawString(String.valueOf(dialogChars[i]),
                                Double.valueOf(dialogTextRect.getBounds().x + (shift * charWidth)).floatValue(),
                                Double.valueOf(dialogTextRect.getBounds().y + (line * charHeight)).floatValue()
                        );

                        shift++;
                        if (i >= dialogChars.length - 1) {
                            break W;
                        }
                    }
                }
            }
        }

        private void drawBackButton(Graphics2D g2D) {
            Rectangle bbe = backBtnShape.getBounds();
            if (backButOver) {
                g2D.drawImage(backButtons,
                        bbe.x + 3, bbe.y + 3,
                        bbe.width - 6, bbe.height - 6,
                        canvas);
            } else {
                g2D.drawImage(backButtons,
                        bbe.x, bbe.y,
                        bbe.width, bbe.height,
                        canvas);
            }

            if (showDebugGraphic) {
                g2D.setColor(Color.MAGENTA.darker());
                g2D.draw(backBtnShape);
            }
        }

        private void drawAnswers(Graphics2D g2D) {
            Rectangle alr = answerList.getBounds();
            g2D.translate(alr.getX(), alr.getY());
            if (showDebugGraphic) {
                g2D.setColor(Color.ORANGE);
                g2D.draw(alr);
            }
            answerList.paint(g2D);
        }

        private void drawScene(Graphics2D g2D) {
            try {
                if (currentSceneImage == null) {
                    g2D.setColor(Color.BLACK);
                    g2D.fillRect(10, 10, getWidth() - 20, getHeight() - 20);
                } else {
                    g2D.drawImage(currentSceneImage,
                            10, 10,
                            getWidth() - 20, getHeight() - 20,
                            canvas);
                }
            } catch (Exception e) {
                g2D.setColor(Color.DARK_GRAY);
                g2D.fillRect(10, 10, getWidth() - 20, getHeight() - 20);
                g2D.setColor(Color.RED);
                g2D.drawString("NO IMAGE",
                        (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g2D, "NO IMAGE").getWidth() / 2),
                        getHeight() / 2 - 96);
            }

            if (showDebugGraphic) {
                g2D.setColor(Color.CYAN);
                g2D.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
            }
        }

        private void drawNPC(Graphics2D g2D) {
            if (currentNpcImage == null) {
                return;
            }
            g2D.drawImage(currentNpcImage,
                getWidth() / 2 - currentNpcImage.getWidth() / 2,
                (int) (GamePlay.this.getHeight() * 0.1f),

                currentNpcImage.getWidth(),
                currentNpcImage.getHeight(),

                canvas);
        }

        private void drawUI(Graphics2D g2D) {
            g2D.drawImage(gameImageUp,
                    0, 0,
                    getWidth(), getHeight(),
                    canvas);
        }

        private void drawChapterAndDay(Graphics2D g2D) {
            if (userSave.getChapter() != null && !userSave.getChapter().isBlank()) {
                if (chapterPolygon == null || needsUpdateRectangles) {
                    chapterPolygon = new Polygon(
                            new int[]{(int) (getWidth() * 0.75f), getWidth(), getWidth(), (int) (getWidth() * 0.85f)},
                            new int[]{(int) (getHeight() * 0.01f), (int) (getHeight() * 0.01f), (int) (getHeight() * 0.125f), (int) (getHeight() * 0.125f)},
                            4);
                }
                g2D.setColor(chapterColor);
                g2D.fill(chapterPolygon);

                g2D.setFont(f9);
                g2D.setColor(Color.BLACK);
                String secLine = userSave.getMonth() + ": " + userSave.getToday();
                Rectangle2D chBW = FoxFontBuilder.getStringBounds(g2D, userSave.getChapter());

                g2D.drawString(userSave.getChapter(),
                        (int) (getWidth() - chapterPolygon.getBounds().width / 2 - chBW.getWidth() / 3) + 1,
                        chapterPolygon.getBounds().height / 2 - 1);

                g2D.drawString(secLine,
                        getWidth() - chapterPolygon.getBounds().width / 2 + 1,
                        (int) (chapterPolygon.getBounds().height / 2 + FoxFontBuilder.getStringBounds(g2D, secLine).getHeight()) - 1);

                g2D.setColor(isChapterUpdate ? Color.YELLOW : Color.WHITE);
                if (isChapterUpdate) {
                    isChapterUpdate = false;
                }

                g2D.drawString(userSave.getChapter(),
                        (int) (getWidth() - chapterPolygon.getBounds().width / 2 - chBW.getWidth() / 3),
                        chapterPolygon.getBounds().height / 2);

                g2D.drawString(secLine,
                        getWidth() - chapterPolygon.getBounds().width / 2,
                        (int) (chapterPolygon.getBounds().height / 2 + FoxFontBuilder.getStringBounds(g2D, secLine).getHeight()));

                if (showDebugGraphic) {
                    g2D.setColor(Color.RED);
                    g2D.draw(chapterPolygon);
                }
            }
        }

        private void drawOther(Graphics g) {
            if (showQualityChanged && infoShowedCycles > 0) {
                g.setColor(Color.ORANGE);
                g.setFont(fontDialog);
                g.drawString("Качество установлено: " + userConf.getQuality().name(),
                        120 - (infoShowedCycles / 10),
                        60);
                infoShowedCycles--;
            } else if (infoShowedCycles == 0) {
                showQualityChanged = false;
                infoShowedCycles = 100;
            }

            if (isShowInfo) {
                g.setFont(fontAnswers);
                g.setColor(Color.ORANGE);
                for (int i = 0; i < infos.size(); i++) {
                    g.drawString(infos.get(i), 60, 45 * (i + 1));
                }
            }
        }

        private void drawFPS(Graphics g) {
            if (configuration.isFpsShowed()) {
                fpsIterCount++;
                if (System.currentTimeMillis() - was > 1000) {
                    curFps = Math.floor(fpsIterCount);
                    was = System.currentTimeMillis();
                    fpsIterCount = 0;
                }

                g.setColor(Color.DARK_GRAY);
                g.fillRect(20, 20, 30, 30);

                g.setFont(fontAnswers);
                g.setColor(Color.WHITE);
                g.drawString(String.format("%.0f", curFps), 23, 36);
            }
        }
    }

    public GamePlay(GraphicsConfiguration gConfig, UserSave loader) {
        super("GamePlayParent", gConfig);
        instance = this;
        new PlayInAcSetter(this);
        refDelay = (int) (1000f / gConfig.getDevice().getDisplayMode().getRefreshRate() - 0.5f);
        userSave = loader;

        setName("GamePlay");
        setUndecorated(true);
        setPreferredSize(new Dimension(WINDOWED_WIDTH.intValue(), WINDOWED_HEIGHT.intValue()));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(Cursors.PinkCursor.get());
        setAutoRequestFocus(true);

        addWindowListener(this);

        loadResources();
        resetGame();
        setLayout(null);

        getContentPane().setBackground(Color.BLACK);
        setExtendedState(MAXIMIZED_BOTH);
        getGraphicsConfiguration().getDevice().setFullScreenWindow(GamePlay.this);
        canvas = new Canvas(getGraphicsConfiguration()) {
            @Override
            public void paint(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0,0,getWidth(),getHeight());
            }

            {
                setFocusable(false);
            }
        };
        canvas.setFocusable(false);
        canvas.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);

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
                setFocusable(false);
                setPreferredSize(new Dimension(0, 60));
                setBorder(new EmptyBorder(3, 3, 3, 3));
                setFont(fontAnswers);

                setForeground(Color.WHITE);
                setOpaque(false);
                setBackground(new Color(0, 0, 0, 0));

                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                setSelectionBackground(new Color(1.0f, 1.0f, 1.0f, 0.2f));
                setSelectionForeground(new Color(1.0f, 1.0f, 0.0f, 1.0f));
                setVisibleRowCount(6);

                setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curTextCursor"), "textCursor"));
                addMouseListener(GamePlay.this);
            }
        };

        add(canvas);
        add(answerList);

        setVisible(true);

        canvas.setBounds(0, 0, getWidth(), getHeight());

        new Thread(new StoryPlayThread()) {
            {
                setName("StoryPlayed_thread");
            }
        }.start();

        try {
            setAnswers(new ArrayList<>() {{
                add("(нажми пробел)");
            }});
            scenario.load(userSave.getScript());
        } catch (IOException e) {
            System.err.println("Script load exception: " + e.getMessage());
            SwingUtilities.invokeLater(this::stopGame);
        }

        checkFullscreen();
    }

    private void resetGame() {
        lastText = "";
        currentNpcImage = null;
        isDialogAnimated = isChapterUpdate = false;
        dialogChars = null;
        charWidth = null;
        currentHeroAvatar = null;
        dialogOwner = null;
        dialogTextRect = null;
        answerList = null;
    }

    private void loadResources() {
        new Thread(() -> {
            // add npc types and moods:
            try {
                for (Path path : Files.walk(personasDir).toList()) {
                    if (Files.isRegularFile(path)) {
                        cache.addIfAbsent(path.toFile().getName().replace(picExtension, ""),
                                toBImage(path.toString().replace(picExtension, "")));
                    }
                    Thread.yield();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // add npc images:
        try {
            for (Path path : Files.list(npcAvatarsDir).toList()) {
                cache.addIfAbsent(path.toFile().getName().replace(picExtension, ""),
                        toBImage(path.toString().replace(picExtension, "")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // add scenes images:
        try {
            for (Path path : Files.list(scenesDir).toList()) {
                cache.addIfAbsent(path.toFile().getName().replace(picExtension, ""),
                        toBImage(path.toString().replace(picExtension, "")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get other images:
        try {
            nullAvatar = (BufferedImage) cache.get("0");
            backButtons = (BufferedImage) cache.get("picBackButBig");
            gameImageUp = (BufferedImage) cache.get("picGamepane");
//            gameImageDL = (BufferedImage) cache.get("picGamepaneDL");
//            gameImageDC = (BufferedImage) cache.get("picGamepaneDC");
//            gameImageDR = (BufferedImage) cache.get("picGamepaneDR");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setScene(userSave.getScreen(), null);

        infos = new ArrayList<>() {
            {
                add("Помощь -> 'F1'");
                add("Качество -> 'F3'");
                add("FPS -> 'F11'");
                add("Выход -> 'Alt+F4' или 'ESC'");
                add("Выбор варианта -> '1'-'6'");
                add("Далее -> 'Пробел'");
                add("Полный экран -> 'F'");
            }
        };
    }

    private BufferedImage toBImage(@NonNull String path) {
        try {
            return ImageIO.read(new File(path + picExtension));
        } catch (Exception e) {
            Out.Print(MainClass.class, Out.LEVEL.WARN, "Ошибка чтения медиа '" + Paths.get(path) + "': " + e.getMessage());
        }
        return null;
    }

    void checkFullscreen() {
        if (isVisible() && (userConf.isFullScreen() && getExtendedState() == MAXIMIZED_BOTH || !userConf.isFullScreen() && getExtendedState() == NORMAL)) {
            return;
        }

        if (userConf.isFullScreen()) {
            getContentPane().setBackground(Color.BLACK);
            setExtendedState(MAXIMIZED_BOTH);
            getGraphicsConfiguration().getDevice().setFullScreenWindow(GamePlay.this);
        } else {
            getContentPane().setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
            setExtendedState(NORMAL);
            getGraphicsConfiguration().getDevice().setFullScreenWindow(null);
            pack();
        }
        setLocationRelativeTo(null);

        needsUpdateRectangles = true;
        Print(GamePlay.class, LEVEL.INFO, "GamePlay fullscreen checked. Thread: " + Thread.currentThread().getName());
    }


    // GAME CONTROLS:
    public static void setScene(String sceneName, String npcImage) {
        // scene:
        if (sceneName != null) {
            userSave.setScreen(sceneName);
            currentSceneImage = (BufferedImage) cache.get(sceneName);
        }

        // npc:
        if (npcImage != null) {
            if (npcImage.equals("CLEAR") || npcImage.equals("-")) {
                currentNpcImage = null;
            } else {
                currentNpcImage = (BufferedImage) cache.get(npcImage);
            }
        }
    }

    public static void setDialog(String _dialogOwner, String dialogText, int carma) {
        if (carma != 0) {
            changeCarma(_dialogOwner, carma);
        }

        // owner:
        if (_dialogOwner == null || _dialogOwner.equalsIgnoreCase("NULL")) {
            dialogOwner = "Кто-то:";
            setAvatar(null);
        } else if (_dialogOwner.equals(userConf.getUserName())) {
            dialogOwner = _dialogOwner;
            setAvatar(String.valueOf(userConf.getAvatarIndex() + 1));
        } else {
            dialogOwner = _dialogOwner;
            setAvatar(convertRussianNpcNameToSourceImageName(_dialogOwner));
        }

        // text:
        if (textAnimateThread != null) {
            textAnimateThread.interrupt();
        }
        textAnimateThread = new Thread(() -> {
            if (dialogText != null) {
                lastText = dialogText;
                dialogDelaySpeed = defaultDialogDefaultDelay;

                try {
                    StringBuilder sb = new StringBuilder(dialogText);
                    dialogChars = new char[dialogText.length()];

                    int shift = 0, i = 0;
                    isDialogAnimated = true;
                    while (isDialogAnimated && i < dialogText.length()) {
                        shift++;

                        if (charWidth * shift > dialogTextRect.getBounds().width - charWidth * 4) {
                            for (int k = i; k > 0; k--) {
                                if ((int) dialogChars[k] == 32) {
                                    sb.setCharAt(k, (char) 10);
                                    break;
                                }
                            }
                            shift = 0;
                        }
                        sb.getChars(0, i + 1, dialogChars, 0);
                        if (userConf.isTextAnimated()) {
                            Thread.sleep(dialogDelaySpeed);
                        }
                        i++;
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                } finally {
                    isDialogAnimated = false;
                }
            }
        });
        textAnimateThread.start();

        setAnswers(null);
    }

    public static void setAvatar(String avatar) {
        currentHeroAvatar = (BufferedImage) cache.get(Objects.requireNonNullElse(avatar, "0"));
    }

    public static void setAnswers(ArrayList<String> answers) {
        if (answerList == null) {
            return;
        }

        dlm.clear();
        answerList.setForeground(Color.WHITE);
        needsUpdateRectangles = true;
        if (answers == null) {
            dlm.addElement("Далее...");
            return;
        }

        if (answers.get(0).equals("(нажми пробел)")) {
            dlm.addElement("(нажми пробел)");
            return;
        }

        answerList.setForeground(Color.YELLOW);
        for (String answer : answers) {
            dlm.addElement(getCountUnicodeChar(dlm.size() + 1) + " " + answer.split("R")[0]);
        }
    }

    public static void setChapter(String _chapter) {
        userSave.setChapter(_chapter);
        isChapterUpdate = true;
    }

    public static void dayAdd() {
        userSave.setToday(userSave.getToday() + 1);
        if (userSave.getToday() > 30) {
            userSave.setToday(1);
            userSave.setMonth(MONTH.values()[userSave.getMonth().ordinal() + 1]);
        }
    }

    private static void changeCarma(String dialogOwner, int carma) {
        switch (dialogOwner) {
            case "Аня" -> userSave.setCarmaAnn(userSave.getCarmaAnn() + carma);
            case "Дмитрий" -> userSave.setCarmaDmi(userSave.getCarmaDmi() + carma);
            case "Куро" -> userSave.setCarmaKur(userSave.getCarmaKur() + carma);
            case "Ольга" -> userSave.setCarmaOlg(userSave.getCarmaOlg() + carma);
            case "Олег" -> userSave.setCarmaOle(userSave.getCarmaOle() + carma);
            case "Оксана" -> userSave.setCarmaOks(userSave.getCarmaOks() + carma);
            case "Мишка" -> userSave.setCarmaMsh(userSave.getCarmaMsh() + carma);
            case "Мари" -> userSave.setCarmaMar(userSave.getCarmaMar() + carma);
            case "Лисса" -> userSave.setCarmaLis(userSave.getCarmaLis() + carma);
            default -> System.err.println("GamePlay.changeCarma: Странное имя в корректоре кармы: " + dialogOwner);
        }
    }


    private static String convertRussianNpcNameToSourceImageName(String dialogOwner) {
        switch (dialogOwner) {
            case "Аня" -> {
                return "Ann";
            }
            case "Дмитрий" -> {
                return "Dmitrii";
            }
            case "Куро" -> {
                return "Kuro";
            }
            case "Ольга" -> {
                return "Olga";
            }
            case "Олег" -> {
                return "Oleg";
            }
            case "Оксана" -> {
                return "Oksana";
            }
            case "Мишка" -> {
                return "Mishka";
            }
            case "Мари" -> {
                return "Mary";
            }
            case "Лисса" -> {
                return "Lissa";
            }

            default -> {
                return "NA";
            }
        }
    }

    private static char getCountUnicodeChar(int numberToUnicode) {
        return switch (numberToUnicode) {
            case 1 -> '1';
            case 2 -> '2';
            case 3 -> '3';
            case 4 -> '4';
            case 5 -> '5';
            case 6 -> '6';
            default -> '?';
        };
    }


    // EXIT:
    void showExitRequest() {
        backButPressed = false;

        int closeQ = (int) new FOptionPane(
                "Подтверждение:",
                "Желаешь завершить игру?",
                FOptionPane.TYPE.YES_NO_TYPE,
                null,
                true).get();

        if (closeQ == 0) {
            stopGame();
        } else {
            System.out.println("Response = " + closeQ);
            setVisible(true);
            isStoryPlayed = true;
        }
    }

    private void stopGame() {
        try {
            stopAnimation();
            userSave.setLineIndex(userSave.getLineIndex() - 1);
            scenario.close();
            isStoryPlayed = false;
            dialogChars = null;
            int req = (int) new FOptionPane("Выход из игры:", "Сохранить игру перед выходом?", FOptionPane.TYPE.YES_NO_TYPE).get();
            if (req == 0) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        JIOM.dtoToFile(userSave);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            GameMenu.setVisible();
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void stopAnimation() {
        if (textAnimateThread != null) {
            textAnimateThread.interrupt();
        }
    }


    // GETTERS & SETTERS:
    public static boolean isStoryPlayed() {
        return isStoryPlayed;
    }

    public static int getCarma(String npcName) {
        switch (npcName) {
            case "Ann" -> {
                return userSave.getCarmaAnn();
            }
            case "Dmi" -> {
                return userSave.getCarmaDmi();
            }
            case "Kur" -> {
                return userSave.getCarmaKur();
            }
            case "Olg" -> {
                return userSave.getCarmaOlg();
            }
            case "Ole" -> {
                return userSave.getCarmaOle();
            }
            case "Oks" -> {
                return userSave.getCarmaOks();
            }
            case "Mar" -> {
                return userSave.getCarmaMar();
            }
            case "Msh" -> {
                return userSave.getCarmaMsh();
            }
            case "Lis" -> {
                return userSave.getCarmaLis();
            }
            default -> {return -1;}
        }
    }

    public static JFrame getInstance() {
        return instance;
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
            new OptMenuFrame(GamePlay.this, getGraphicsConfiguration());
            checkFullscreen();
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
        if (downArea == null) {return;}
        mouseNow = new Point(e.getX() - 32, e.getY() - (getHeight() - downArea.getBounds().height) + 32);
        backButOver = backBtnShape.contains(mouseNow);
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if (answerList.locationToIndex(e.getPoint()) != -1) {
            if (scenario == null) {
                return;
            }
            Print(GamePlay.class, LEVEL.DEBUG, "Был выбран вариант " + answerList.getSelectedValue());
            if (answerList.getSelectedValue() != null && answerList.getSelectedValue().equals("Далее...")) {
                scenario.choice(-1);
            } else {
                scenario.choice(answerList.getSelectedIndex());
            }
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
}
//// Полезные методы класса Choice:

//Choice choice = new Choice();
//choice.addItem("First");
//choice.addItem("Second");
//choice.addItem("Third");

//  countItems() - считать количество пунктов в списке;
//	getItem(int) - возвратить строку с определенным номером в списке; 
//	select(int) - выбрать строку с определенным номером; 
//	select(String) - выбрать определенную строку текста из списка.
//  add(choice);

//// Альтернативный способ создания потока:
// java.util.Timer t = new java.util.Timer();
// t.schedule(new TimerTask() {
//    @Override
//    public void run() {
//      // ...
//    }
// }, autoSaveSeconds);