package GUI;

import components.FOptionPane;
import configurations.UserSave;
import door.MainClass;
import images.FoxCursor;
import logic.SaveLoad;
import registry.Registry;
import utils.FoxFontBuilder;
import utils.InputAction;
import fox.Out;
import images.FoxSpritesCombiner;
import interfaces.Cached;
import logic.ScenarioEngine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import render.FoxRender;
import secondGUI.SaveGame;
import tools.Cursors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.synth.Region;
import java.awt.*;
import java.awt.event.*;
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

@EqualsAndHashCode(callSuper = true)
@Data
public class GamePlay extends JFrame implements MouseListener, MouseMotionListener, WindowListener, Cached {
    public enum MONTH {июнь,июль,август}
    private static DefaultListModel<String> dlm = new DefaultListModel<>();
    private static Thread textAnimateThread;
    private static long dialogDelaySpeed = 48, defaultDialogDefaultDelay = 48;
    private static BufferedImage currentSceneImage, currentNpcImage, currentHeroAvatar;
    private static boolean isDialogAnimated, isChapterUpdate;
    private static char[] dialogChars;
    private static Double charWidth;
    private static String dialogOwner;
    private static Shape dialogTextRext;

    private Double WINDOWED_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.75D;
    private Double WINDOWED_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.9D;
    private static JList<String> answerList;
    private long was = System.currentTimeMillis(), autoSaveSeconds = 15_000;
    private BufferedImage nullAvatar, gameImageUp, gameImageDL, gameImageDC, gameImageDR;
    private BufferedImage[] backButtons;
    private JPanel basePane, downCenterPane;
    private boolean isStoryPlayed;
    private boolean backButOver;
    private boolean backButPressed;
    private boolean isPaused;
    private static boolean needsUpdateRectangles, showQualityChanged;
    private int refDelay, infoShowedCycles = 100;
    private float fpsIterCount = 0;
    private double curFps;
    private static String lastText;
    private Point mouseNow, frameWas, mouseWasOnScreen;
    private Shape backBtnShape;
    private ScenarioEngine scenario = new ScenarioEngine();
    private Color chapterColor = new Color(0.0f, 0.0f, 0.0f, 0.5f);
    private Polygon chapterPolygon;

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

        drawOther(g);
    }

    private void drawFPS(Graphics g) {
        if (configuration.isFpsShowed()) {
            fpsIterCount++;
            if (System.currentTimeMillis() - was > 1000) {
                curFps = Double.valueOf(Math.floor(fpsIterCount));
                was = System.currentTimeMillis();
                fpsIterCount = 0;
            }

            g.setColor(Color.GRAY);
            g.drawString(String.format("%.0f", curFps), 10, 25);
        }
    }

    private void drawOther(Graphics g) {
        if (showQualityChanged && infoShowedCycles > 0) {
            g.setColor(Color.ORANGE);
            g.setFont(fontDialog);
            g.drawString("Качество установлено: " + userConf.getQuality().name(), 100 - (infoShowedCycles / 10), 60);
            infoShowedCycles--;
        } else if (infoShowedCycles == 0) {
            showQualityChanged = false;
            infoShowedCycles = 100;
        }
    }

    // FRAME BUILD:
    public GamePlay(GraphicsConfiguration gConfig, UserSave loader, int linerMod) {
        super("GamePlayParent", gConfig);
        refDelay = 1000 / gConfig.getDevice().getDisplayMode().getRefreshRate();
        userSave = loader;

        preInit();
        loadResources();
        setInAc();

        lastText = "";
        needsUpdateRectangles = false;
        currentNpcImage = null;
        dlm.clear();

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
                    if (currentSceneImage == null) {
                        g2D.setColor(Color.BLACK);
                        g2D.fillRect(0, 0, getWidth(), getHeight());
                    } else {
                        g2D.drawImage(currentSceneImage,
                                Double.valueOf(GamePlay.this.getWidth() * 0.01d).intValue(),
                                Double.valueOf(GamePlay.this.getHeight() * 0.01d).intValue(),
                                getWidth() - Double.valueOf(GamePlay.this.getWidth() * 0.02d).intValue(),
                                getHeight() - Double.valueOf(GamePlay.this.getHeight() * 0.01d).intValue(),
                                this);
                    }
                } catch (Exception e) {
                    g2D.setColor(Color.DARK_GRAY);
                    g2D.fillRect(16, 16, getWidth() - 32, getHeight() - 32);
                    g2D.setColor(Color.RED);
                    g2D.drawString("NO IMAGE", (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g2D, "NO IMAGE").getWidth() / 2), getHeight() / 2 - 96);
                }

                drawNPC(g2D);

                drawChapterAndDay(g2D);

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

            private void drawChapterAndDay(Graphics2D g2D) {
                if (userSave.getChapter() != null && !userSave.getChapter().isBlank()) {
                    if (chapterPolygon == null || needsUpdateRectangles) {
                        chapterPolygon = new Polygon(
                                new int[] {(int) (getWidth() * 0.75f), getWidth(), getWidth(), (int) (getWidth() * 0.85f)},
                                new int[] {(int) (getHeight() * 0.01f), (int) (getHeight() * 0.01f), (int) (getHeight() * 0.2f), (int) (getHeight() * 0.2f)},
                                4);
                    }
                    g2D.setColor(chapterColor);
                    g2D.fill(chapterPolygon);

                    g2D.setFont(f9);
                    g2D.setColor(Color.BLACK);
                    String secLine = userSave.getMonth() + ": " + userSave.getToday();

                    g2D.drawString(userSave.getChapter(), getWidth() * 0.852f, getHeight() * 0.1075f);
                    g2D.drawString(secLine, (float) (getWidth() - FoxFontBuilder.getStringBounds(g2D, secLine).getWidth()) - 59f, getHeight() * 0.1475f);
                    if (isChapterUpdate) {
                        g2D.setColor(Color.YELLOW);
                        isChapterUpdate = false;
                    } else {
                        g2D.setColor(Color.WHITE);
                    }
                    g2D.drawString(userSave.getChapter(), getWidth() * 0.85f, getHeight() * 0.11f);
                    g2D.drawString(secLine, (float) (getWidth() - FoxFontBuilder.getStringBounds(g2D, secLine).getWidth()) - 60f, getHeight() * 0.15f);
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

                        this);
            }
        };

        JPanel downPane = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;
                FoxRender.setRender(g2D, userConf.getQuality());

                super.paintComponents(g2D);

//                g2D.setColor(Color.ORANGE);
//                g2D.drawRect(0,0,getWidth()-1,getHeight()-1);
            }

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
                        if (dialogTextRext == null || needsUpdateRectangles) {
                            dialogTextRext = new Rectangle(
                                    Double.valueOf(getWidth() * 0.0025d).intValue(),
                                    Double.valueOf(getHeight() * 0.095d).intValue(),
                                    Double.valueOf((getWidth() - answerList.getWidth()) * 0.985d).intValue(),
                                    Double.valueOf(getHeight() * 0.75d).intValue()
                            );
                            needsUpdateRectangles = false;
                        }
//                        g2D.setColor(Color.GREEN.darker());
//                        g2D.draw(dialogTextRext);
                    }

                    private void drawAutoDialog(Graphics2D g2D) {
                        // owner name:
                        if (dialogOwner != null && dialogTextRext != null) {
                            g2D.setFont(fontName);

                            int dx = (int) (getWidth() * 0.09d - FoxFontBuilder.getStringBounds(g2D, dialogOwner).getWidth() / 2);
                            int dy = dialogTextRext.getBounds().y - 3;

                            g2D.setColor(Color.BLACK);
                            g2D.drawString(dialogOwner,
                                    dx,
                                    dy);

                            g2D.setColor(Color.ORANGE);
                            g2D.drawString(dialogOwner,
                                    dx - 1,
                                    dy + 1);
                        }

                        g2D.setFont(fontDialog);
                        if (charWidth == null) {
                            charWidth = g2D.getFontMetrics().getMaxCharBounds(g2D).getWidth();
                        }

                        // dialog:
                        if (dialogChars != null && dialogTextRext != null) {
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
                                    g2D.setColor(Color.GRAY.brighter());
                                    g2D.drawString(String.valueOf(dialogChars[i]),
                                            Double.valueOf(dialogTextRext.getBounds().x + (shift * charWidth) + 0.35d).floatValue(),
                                            Double.valueOf(dialogTextRext.getBounds().y * line + 0.55d).floatValue()
                                    );

                                    g2D.setColor(Color.BLACK);
                                    g2D.drawString(String.valueOf(dialogChars[i]),
                                            Double.valueOf(dialogTextRext.getBounds().x + (shift * charWidth)).floatValue(),
                                            dialogTextRext.getBounds().y * line
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
                                setFont(fontAnswers);
                                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                setSelectionBackground(new Color(1.0f, 1.0f, 1.0f, 0.2f));
                                setSelectionForeground(new Color(1.0f, 1.0f, 0.0f, 1.0f));
                                setVisibleRowCount(6);

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
                            g2D.drawImage(backButtons[1],
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
        };

        add(upPane, BorderLayout.CENTER);
        add(downPane, BorderLayout.SOUTH);

        try {
            scenario.load(userSave.getScript(), linerMod);
            if (linerMod == 0) {
                setAnswers(new ArrayList<>() {{
                    add("(нажми пробел)");
                }});
            }
        } catch (IOException e) {
            System.err.println("Script load exception: " + e.getMessage());
            SwingUtilities.invokeLater(() -> stopGame());
        }

        new Thread(new StoryPlayThread()) {
            {
                setName("StoryPlayed_thread");
            }
        }.start();
    }

    // GAME CONTROLS:
    public static void setScene(String sceneName, String npcImage) {
        // scene:
        if (sceneName != null) {
            userSave.setScreen(sceneName);
            currentSceneImage = (BufferedImage) cache.get(sceneName);
        }

        // npc:
        if (npcImage == null) {
            return;
        } else if (npcImage.equals("CLEAR")) {
            currentNpcImage = null;
        } else {
            currentNpcImage = (BufferedImage) cache.get(npcImage);
        }
    }

    public static void setDialog(String _dialogOwner, String dialogText, int carma) {
        stopAnimation();
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
            setAvatar(convertRussianNpcNameToSourceImageName(_dialogOwner));
        }

        // text:
        textAnimateThread = new Thread(() -> animateText(dialogText));
        textAnimateThread.start();

        setAnswers(null);
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
            default -> {
                System.err.println("GamePlay.changeCarma: Странное имя в корректоре кармы: " + dialogOwner);
            }
        }
    }

    private static String convertRussianNpcNameToSourceImageName(String dialogOwner) {
        switch (dialogOwner) {
            case "Аня" -> {
                return "Ann";
            }

            default -> {
                return "NA";
            }
        }
    }

    public static void setAvatar(String avatar) {
        currentHeroAvatar = (BufferedImage) cache.get(Objects.requireNonNullElse(avatar, "0"));
    }

    private static void animateText(String text) {
        isDialogAnimated = false;

        if (text != null) {
            lastText = text;

            isDialogAnimated = true;
            dialogDelaySpeed = defaultDialogDefaultDelay;

            StringBuilder sb = new StringBuilder(text);
            dialogChars = new char[text.length()];

            if (charWidth == null) {return;}
            int shift = 0;
            for (int i = 0; i < text.length(); i++) {
                try {
                    shift++;
                    if (charWidth * shift > dialogTextRext.getBounds().width - charWidth * 3) {
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
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }

            isDialogAnimated = false;
        }
    }

    public static void setAnswers(ArrayList<String> answers) {
        dlm.clear();
        answerList.setForeground(Color.WHITE);
        if (answers == null) {
            dlm.addElement("Далее...");
            return;
        }

        if (answers.get(0).equals("Начать игру") || answers.get(0).equals("(нажми пробел)")) {
            dlm.addElement("Начать игру");
            return;
        }

        answerList.setForeground(Color.GREEN);
        for (String answer : answers) {
            dlm.addElement(getCountUnicodeChar(dlm.size() + 1) + " " + answer.split("R")[0]);
        }
        needsUpdateRectangles = true;
    }

    private static char getCountUnicodeChar(int numberToUnicode) {
        switch (numberToUnicode) {
            case 1: return '1';
            case 2: return '2';
            case 3: return '3';
            case 4: return '4';
            case 5: return '5';
            case 6: return '6';
            default: return '?';
        }
    }

    private static void stopAnimation() {
        if (textAnimateThread != null) {
            dialogDelaySpeed = 0;
            textAnimateThread.interrupt();
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

    private void preInit() {
        setName("GamePlay");
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(Cursors.PinkCursor.get());
        setAutoRequestFocus(true);

        addWindowListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
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
            backButtons = FoxSpritesCombiner.getSprites("picBackButBig",
                    (BufferedImage) cache.get("picBackButBig"), 1, 3);
            gameImageUp = (BufferedImage) cache.get("picGamepaneUp");
            gameImageDL = (BufferedImage) cache.get("picGamepaneDL");
            gameImageDC = (BufferedImage) cache.get("picGamepaneDC");
            gameImageDR = (BufferedImage) cache.get("picGamepaneDR");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setScene(userSave.getScreen(), null);
    }

    private BufferedImage toBImage(@NonNull String path) {
        try {
            return ImageIO.read(new File(path + picExtension));
        } catch (Exception e) {
            Out.Print(MainClass.class, Out.LEVEL.WARN, "Ошибка чтения медиа '" + Paths.get(path) + "': " + e.getMessage());
        }
        return null;
    }

    private void setInAc() {
        InputAction.add("game", GamePlay.this); // SwingUtilities.getWindowAncestor(basePane));

        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "Ctrl+F4", KeyEvent.VK_F4, 512, new AbstractAction() {
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
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "fullscreen", KeyEvent.VK_F, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPaused = true;
                userConf.setFullScreen(!userConf.isFullScreen());
                checkFullscreen();
                isPaused = false;
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "switchFPS", KeyEvent.VK_F11, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configuration.setFpsShowed(!configuration.isFpsShowed());
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "switchQuality", KeyEvent.VK_F3, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userConf.nextQuality();
                System.out.println("Quality: " + userConf.getQuality());
                showQualityChanged = true;
            }
        });

        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "next", KeyEvent.VK_SPACE, 0, new AbstractAction() {
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
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_1", KeyEvent.VK_1, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 1) {
                    return;
                }
                dialogDelaySpeed = 0;
                answerList.setSelectedIndex(0);
                scenario.choice(0);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_2", KeyEvent.VK_2, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 2) {
                    return;
                }
                dialogDelaySpeed = 0;
                answerList.setSelectedIndex(1);
                scenario.choice(1);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_3", KeyEvent.VK_3, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 3) {
                    return;
                }
                dialogDelaySpeed = 0;
                answerList.setSelectedIndex(2);
                scenario.choice(2);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_4", KeyEvent.VK_4, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 4) {
                    return;
                }
                dialogDelaySpeed = 0;
                answerList.setSelectedIndex(3);
                scenario.choice(3);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_5", KeyEvent.VK_5, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 5) {
                    return;
                }
                dialogDelaySpeed = 0;
                answerList.setSelectedIndex(4);
                scenario.choice(4);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_6", KeyEvent.VK_6, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlm.size() < 6) {
                    return;
                }
                dialogDelaySpeed = 0;
                answerList.setSelectedIndex(5);
                scenario.choice(5);
            }
        });

        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "keyLeft", KeyEvent.VK_LEFT, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Key left...");
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "keyRight", KeyEvent.VK_RIGHT, 0, new AbstractAction() {
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

        needsUpdateRectangles = true;

        Print(GamePlay.class, LEVEL.INFO, "GamePlay fullscreen checked. Thread: " + Thread.currentThread().getName());
    }

    // EXIT:
    private void showExitRequest() {
        backButPressed = false;
        isPaused = true;

        int closeQ = (int) new FOptionPane(
                "Подтверждение:",
                "Что нужно сделать?",
                FOptionPane.TYPE.VARIANTS,
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
        try {
            scenario.close();
            userSave.setLineIndex(userSave.getLineIndex() - 1);
            SaveLoad.save();
            isPaused = false;
            isStoryPlayed = false;
            stopAnimation();
            dialogChars = null;

            GameMenu.setVisible();
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
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
            if (answerList.getSelectedValue().equals("Далее...")) {
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


    // THREAD:
    private class StoryPlayThread implements Runnable {
        @Override
        public void run() {
            musicPlayer.stop();
            backgPlayer.stop();
            isStoryPlayed = true;

            Print(GamePlay.class, LEVEL.INFO, "GameFrame.StoryPlayedThread: Start now.");
            while (isStoryPlayed) {
                try {
                    if (isPaused) {
                        Thread.yield();
                        continue;
                    }
                    repaint();
                    Thread.sleep(refDelay);
                } catch (InterruptedException e) {
                    System.out.println("Ошибка в потоке отрисовки: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
            Print(GamePlay.class, LEVEL.INFO, "GameFrame.StoryPlayedThread: Stop!");
        }
    }
}

////		Полезные методы класса Choice:

//Choice choice = new Choice();
//choice.addItem("First");
//choice.addItem("Second");
//choice.addItem("Third");

//countItems() - считать количество пунктов в списке; 
//	getItem(int) - возвратить строку с определенным номером в списке; 
//	select(int) - выбрать строку с определенным номером; 
//	select(String) - выбрать определенную строку текста из списка.
//add(choice);

////        Альтернативный способ создания потока:
//        java.util.Timer t = new java.util.Timer();
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        }, autoSaveSeconds);