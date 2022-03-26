package GUI;

import components.FOptionPane;
import components.FoxConsole;
import components.FoxTipsEngine;
import door.Exit;
import fox.FoxFontBuilder;
import fox.InputAction;
import images.FoxSpritesCombiner;
import interfaces.Cached;
import registry.Registry;
import render.FoxRender;
import secondGUI.*;
import tools.Cursors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import static fox.Out.LEVEL;
import static fox.Out.Print;
import static registry.Registry.*;

public class GameMenu extends JFrame implements MouseListener, MouseMotionListener, ActionListener, Cached {
    private static GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
    private static GraphicsConfiguration gc = gDevice.getDefaultConfiguration();

    private static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private static BufferedImage centerImage, picMenuImage;
    private static BufferedImage[] exitImages, startImages, menuImages;

    private static Point2D mouseWasOnScreen, frameWas;

    private static String downText;

    private static JPanel basePane;
    private static JButton optionsButton, galleryButton, saveLoadButton, exitButton, aboutButton;
    private static JLabel downTextLabel;

    private static float wPercent, hPercent;
    private final FoxConsole cons;
    private FoxTipsEngine cd;

    private Integer curFps = 0;
    private int refDelay;
    private float fpsCounter = 0;
    private long was = System.currentTimeMillis();

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        FoxRender.setRender(g2D, userConf.getQuality());

        super.paintComponents(g2D);
        if (configuration.isFpsShowed()) {
            fpsCounter++;
            if (System.currentTimeMillis() - was > 1000) {
                curFps = Double.valueOf(Math.floor(fpsCounter)).intValue();
                fpsCounter = 0;
                was = System.currentTimeMillis();
            }
            drawFPS(g2D);
        }
        g2D.dispose();
    }

    private void drawFPS(Graphics2D g2D) {
        g2D.setColor(Color.GRAY);
        g2D.drawString(curFps.toString(), 10, 25);
    }

    public GameMenu() {
        super("GameMenuParent", gc);
        refDelay = 1000 / gDevice.getDisplayMode().getRefreshRate();

        setName("GameMenu");
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setCursor(Cursors.SimpleCursor.get());

        preLoading();
        inAc();

        addMouseListener(this);
        addMouseMotionListener(this);

        pack();
        setAutoRequestFocus(true);
        setLocationRelativeTo(null);

        testNewbie();

        Print(GameMenu.class, LEVEL.INFO, "MainMenu setts visible...");
        setVisible(true);
        checkFullscreen();

        setStatusText(null);

        backgPlayer.play("fonKricket");
        musicPlayer.play("musMainMenu");

        cons = new FoxConsole(this);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                repaint();
                try {Thread.currentThread().sleep(refDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private static void testNewbie() {
        Print(GameMenu.class, LEVEL.INFO, "A newbie test...");

        if (userConf.getUserName() == null || userConf.getUserName().equals("newEmptyUser")) {
            Print(GameMenu.class, LEVEL.ACCENT, "Open NewUserForm to change name " + userConf.getUserName());
            new NewUserForm();
        }

//		Out.Print("\nДанная программа использует " +
//				(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 +
//				"мб из " + Runtime.getRuntime().totalMemory() / 1048576 +
//				"мб выделенных под неё. \nСпасибо за использование утилиты компании MultyVerse39 Group!");
    }

    private static void setStatusText(String newText) {
        if (newText == null) {
            downTextLabel.setText("\u266B " + userConf.getUserName() + " \u266B");
        } else {
            downTextLabel.setText("\u266B " + newText + " \u266B");
        }
    }

    private void preLoading() {
        Thread.currentThread().setName("=== MAIN THREAD ===");

        Print(GameMenu.class, LEVEL.INFO, "MainMenu preloading...");

        try {
            Print(GameMenu.class, LEVEL.DEBUG, "Preparing sprites...");
            startImages = FoxSpritesCombiner
                    .addSpritelist("PlayButtonSprite", (BufferedImage) cache.get("picPlayButtonSprite"),1, 3);
            menuImages = FoxSpritesCombiner
                    .addSpritelist("MenuButtonSprite", (BufferedImage) cache.get("picMenuButtonSprite"),1, 3);
            exitImages = FoxSpritesCombiner
                    .addSpritelist("ExitButtonSprite", (BufferedImage) cache.get("picExitButtonSprite"),1, 3);
        } catch (Exception e) {
            Print(GameMenu.class, LEVEL.WARN, "Can`t load sprites: " + e.getMessage());
        }

        try {
            Print(GameMenu.class, LEVEL.DEBUG, "Preparing images...");
            centerImage = (BufferedImage) cache.get("picMenuBase");
            picMenuImage = (BufferedImage) cache.get("picMenupane");
        } catch (Exception e) {
            Print(GameMenu.class, LEVEL.WARN, "Can`t load images: " + e.getMessage());
        }

        downText = "\u266B " + userConf.getUserName() + " \u266B";

        Print(GameMenu.class, LEVEL.INFO, "MainMenu preloading done.");
    }

    private void inAc() {
        Print(GameMenu.class, LEVEL.DEBUG, "Sets inAc...");

        InputAction.add("MainMenu", GameMenu.this);
        InputAction.set("MainMenu", "Ctrl+F4", KeyEvent.VK_F4, 512, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Exit.exit(0, "Exit by users 'Ctrl+F4'");
            }
        });
        InputAction.set("MainMenu", "switchQuality", KeyEvent.VK_F3, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userConf.nextQuality();
                System.out.println("Quality: " + userConf.getQuality());
            }
        });
        InputAction.set("MainMenu", "switchFullscreen", KeyEvent.VK_F, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userConf.setFullScreen(!userConf.isFullScreen());
                checkFullscreen();
            }
        });
    }

    private void checkFullscreen() {
        Print(GameMenu.class, LEVEL.INFO, "\nMainMenu fullscreen switch...");

        Double sw = screen.getWidth();
        Double sh = screen.getHeight();
        if (basePane != null) {
            remove(basePane);
        }

        if (userConf.isFullScreen()) {
            setState(MAXIMIZED_BOTH);

            setBackground(Color.BLACK);
            getContentPane().setBackground(Color.BLACK);
            setPreferredSize(new Dimension(sw.intValue(), sh.intValue()));
            setSize(new Dimension(sw.intValue(), sh.intValue()));
        } else {
            setState(NORMAL);

            getRootPane().setOpaque(false);
            getLayeredPane().setOpaque(false);
            setBackground(new Color(0, 0, 0, 0));
            setPreferredSize(new Dimension((int) (sw * 0.75d), (int) (sh * 0.75d)));
            setSize(new Dimension((int) (sw * 0.75d), (int) (sh * 0.75d)));

            sw = Double.valueOf(getWidth());
            sh = Double.valueOf(getHeight());
        }

        wPercent = (float) (sw / 100d);
        hPercent = (float) (sh / 100d);
        add(buildBasePane());

        setLocationRelativeTo(null);
        revalidate();

        Print(GameMenu.class, LEVEL.INFO, "MainMenu fullscreen checked. Thread: " + Thread.currentThread().getName());
    }


    private JPanel buildBasePane() {
        Print(GameMenu.class, LEVEL.INFO, "Building the BasePane...");

        basePane = new JPanel(new BorderLayout((int) (wPercent * 2.6f), (int) (hPercent * 2.0f))) {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(picMenuImage, 0, 0, getWidth(), getHeight(), this);
            }

            {
                setName("basePane");
                setOpaque(false);
                setBorder(new EmptyBorder((int) (hPercent * 3f), (int) (wPercent * 2f), (int) (wPercent * 1.6f), (int) (hPercent * 4.2f)));

                JPanel upPlayPane = new JPanel(new BorderLayout()) {
                    {
                        setOpaque(false);

                        JButton playButton = new JButton() {
                            BufferedImage bImage = startImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (startImages != null) {
                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
                                    if (bImage == startImages[1]) {
                                        g.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
                                    }
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("Играть");
                                setPreferredSize(new Dimension(0, (int) (hPercent * 6.5f)));
                                setFont(Registry.f5);
                                setForeground(Color.BLACK);
                                setBorderPainted(false);
                                setFocusPainted(false);
                                setFocusable(false);
                                setOpaque(false);

                                setActionCommand("play");
                                addActionListener(GameMenu.this);
                                addMouseListener(new MouseAdapter() {
                                    public void mouseEntered(MouseEvent me) {
                                        setStatusText("Начать/продолжить игру");
                                        bImage = startImages[1];
                                        repaint();
                                    }

                                    public void mouseExited(MouseEvent me) {
                                        setStatusText(null);
                                        bImage = startImages[0];
                                        repaint();
                                    }

                                    public void mousePressed(MouseEvent me) {
                                        bImage = startImages[2];
                                        repaint();
                                    }

                                    public void mouseReleased(MouseEvent me) {
                                        bImage = startImages[0];
                                        repaint();
                                    }
                                });
                            }
                        };

                        add(playButton);
                    }
                };

                JPanel rightButPane = new JPanel(new GridLayout(10, 0, 3, 3)) {
                    {
                        setOpaque(false);

                        optionsButton = new JButton("Настройки") {
                            BufferedImage bImage = menuImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (menuImages != null) {
                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    if (bImage == menuImages[1]) {
                                        g.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
                                    }
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("Настройки");
                                setPreferredSize(new Dimension((int) (wPercent * 28.55f), 50));
                                setFont(Registry.f5);
                                setForeground(Color.BLACK);
                                setBorderPainted(false);
                                setFocusPainted(false);
                                setFocusable(false);
                                setOpaque(false);

                                setActionCommand("options");
                                addActionListener(GameMenu.this);
                                addMouseListener(new MouseAdapter() {
                                    public void mouseEntered(MouseEvent me) {
                                        setStatusText("Настройки игры");
                                        bImage = menuImages[1];
                                        repaint();
                                    }

                                    public void mouseExited(MouseEvent me) {
                                        setStatusText(null);
                                        bImage = menuImages[0];
                                        repaint();
                                    }

                                    public void mousePressed(MouseEvent me) {
                                        bImage = menuImages[2];
                                        repaint();
                                    }

                                    public void mouseReleased(MouseEvent me) {
                                        bImage = menuImages[0];
                                        repaint();
                                    }
                                });
                            }
                        };

                        saveLoadButton = new JButton("Сохранение/загрузка") {
                            BufferedImage bImage = menuImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (menuImages != null) {
                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    if (bImage == menuImages[1]) {
                                        g.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
                                    }
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("Сохранение/загрузка");
//								setPreferredSize(new Dimension(410, 50));
                                setFont(Registry.f5);
                                setForeground(Color.BLACK);
                                setBorderPainted(false);
                                setFocusPainted(false);
                                setFocusable(false);
                                setOpaque(false);

                                setActionCommand("saveLoad");
                                addActionListener(GameMenu.this);
                                addMouseListener(new MouseAdapter() {
                                    public void mouseEntered(MouseEvent me) {
                                        setStatusText("Сохранить и загрузить");
                                        bImage = menuImages[1];
                                        repaint();
                                    }

                                    public void mouseExited(MouseEvent me) {
                                        setStatusText(null);
                                        bImage = menuImages[0];
                                        repaint();
                                    }

                                    public void mousePressed(MouseEvent me) {
                                        bImage = menuImages[2];
                                        repaint();
                                    }

                                    public void mouseReleased(MouseEvent me) {
                                        bImage = menuImages[0];
                                        repaint();
                                    }
                                });
                            }
                        };

                        galleryButton = new JButton("Галерея") {
                            BufferedImage bImage = menuImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (menuImages != null) {
                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    if (bImage == menuImages[1]) {
                                        g.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
                                    }
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("Галерея");
//								setPreferredSize(new Dimension(410, 50));
                                setFont(Registry.f5);
                                setForeground(Color.BLACK);
                                setBorderPainted(false);
                                setFocusPainted(false);
                                setFocusable(false);
                                setOpaque(false);

                                setActionCommand("gallery");
                                addActionListener(GameMenu.this);
                                addMouseListener(new MouseAdapter() {
                                    public void mouseEntered(MouseEvent me) {
                                        setStatusText("Галерея воспоминаний");
                                        bImage = menuImages[1];
                                        repaint();
                                    }

                                    public void mouseExited(MouseEvent me) {
                                        setStatusText(null);
                                        bImage = menuImages[0];
                                        repaint();
                                    }

                                    public void mousePressed(MouseEvent me) {
                                        bImage = menuImages[2];
                                        repaint();
                                    }

                                    public void mouseReleased(MouseEvent me) {
                                        bImage = menuImages[0];
                                        repaint();
                                    }
                                });
                            }
                        };

                        aboutButton = new JButton("Об игре") {
                            BufferedImage bImage = menuImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (menuImages != null) {
                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);

                                    if (bImage == menuImages[1]) {
                                        g.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
                                    }
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("Об игре");
//								setPreferredSize(new Dimension(410, 50));
                                setFont(Registry.f5);
                                setForeground(Color.BLACK);
                                setBorderPainted(false);
                                setFocusPainted(false);
                                setFocusable(false);
                                setOpaque(false);

                                setActionCommand("about");
                                addActionListener(GameMenu.this);
                                addMouseListener(new MouseAdapter() {
                                    public void mouseEntered(MouseEvent me) {
                                        setStatusText("Об игре и создателях");
                                        bImage = menuImages[1];
                                        repaint();
                                    }

                                    public void mouseExited(MouseEvent me) {
                                        setStatusText(null);
                                        bImage = menuImages[0];
                                        repaint();
                                    }

                                    public void mousePressed(MouseEvent me) {
                                        bImage = menuImages[2];
                                        repaint();
                                    }

                                    public void mouseReleased(MouseEvent me) {
                                        bImage = menuImages[0];
                                        repaint();
                                    }
                                });
                            }
                        };

                        add(optionsButton);
                        add(saveLoadButton);
                        add(galleryButton);
                        add(aboutButton);
                    }
                };

                JPanel midImagePane = new JPanel() {
                    @Override
                    public void paintComponent(Graphics g) {
                        if (centerImage != null) {
                            g.drawImage(centerImage, 0, 0, getWidth(), getHeight(), this);

                            g.setColor(Color.BLACK);
                            g.drawString("v." + Registry.version, 7, 18);
                            g.setColor(Color.ORANGE);
                            g.drawString("v." + Registry.version, 8, 16);
                        } else {
                            super.paintComponent(g);
                        }
                    }

                    {
                        setOpaque(false);
                    }
                };

                JPanel downExitPane = new JPanel(new BorderLayout((int) (wPercent * 3f), (int) (hPercent * 2.0f))) {
                    {
                        setOpaque(false);

                        exitButton = new JButton() {
                            BufferedImage bImage = exitImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (exitImages != null) {
                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
                                    if (bImage == exitImages[1]) {
                                        g.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
                                    }
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("Выйти");
                                setPreferredSize(new Dimension((int) (wPercent * 28.55f), (int) (hPercent * 6.5f)));
                                setFont(Registry.f5);
                                setForeground(Color.BLACK);
                                setBorderPainted(false);
                                setFocusPainted(false);
                                setFocusable(false);
                                setOpaque(false);

                                setActionCommand("exit");
                                addActionListener(GameMenu.this);
                                addMouseListener(new MouseAdapter() {
                                    public void mouseEntered(MouseEvent me) {
                                        setStatusText("Завершить игру и выйти");
                                        bImage = exitImages[1];
                                        repaint();
                                    }

                                    public void mouseExited(MouseEvent me) {
                                        setStatusText(null);
                                        bImage = exitImages[0];
                                        repaint();
                                    }

                                    public void mousePressed(MouseEvent me) {
                                        bImage = exitImages[2];
                                        repaint();
                                    }

                                    public void mouseReleased(MouseEvent me) {
                                        bImage = exitImages[0];
                                        repaint();
                                    }
                                });
                            }
                        };

                        BufferedImage tipIco = null;
                        try {
                            tipIco = ImageIO.read(new File("./resources/tipIco.png"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        cd = new FoxTipsEngine(this, FoxTipsEngine.TYPE.INFO, tipIco,
                                "Заголовок подсказки:",
                                "Это текст сообщения. Его необходимо правильно<br>переносить на следующую строку и вообще...<br>всё в таком духе. Вот.",
                                "Тем более, если сообщение окажется черезчур длинным."
                        );

                        downTextLabel = new JLabel() {
                            {
//								setBorder(new EmptyBorder(0, 0, 0, 128));
                                setText(downText);
                                setFont(Registry.f6);
                                setForeground(Color.WHITE);
                                setHorizontalAlignment(0);

                                addMouseListener(new MouseAdapter() {
                                    public void mouseEntered(MouseEvent me) {
                                        setForeground(Color.ORANGE);
                                        setStatusText("Выбрать другого игрока (2x click)");
                                        cd.show();
                                    }

                                    public void mouseExited(MouseEvent me) {
                                        setForeground(Color.WHITE);
                                        setStatusText(null);
//							        	 cd.close();
                                    }

                                    public void mousePressed(MouseEvent me) {
                                        if (me.getClickCount() >= 2) {
                                            new NewUserForm();
                                        }
                                    }
                                });
                            }
                        };

                        add(exitButton, BorderLayout.EAST);
                        add(downTextLabel, BorderLayout.CENTER);
                    }
                };

                add(upPlayPane, BorderLayout.NORTH);
                add(rightButPane, BorderLayout.EAST);
                add(midImagePane, BorderLayout.CENTER);
                add(downExitPane, BorderLayout.SOUTH);
            }
        };

        return basePane;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (userConf.isFullScreen()) {
            return;
        }

        setLocation(
                (int) (frameWas.getX() - (mouseWasOnScreen.getX() - e.getXOnScreen())),
                (int) (frameWas.getY() - (mouseWasOnScreen.getY() - e.getYOnScreen())));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseWasOnScreen = new Point(e.getXOnScreen(), e.getYOnScreen());
        frameWas = GameMenu.this.getLocation();
    }

    public void mouseMoved(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "play" -> {
                new GamePlay(gc);
                dispose();
            }
            case "exit" -> {
                int exit = new FOptionPane(
                        "Подтверждение:", "Точно закрыть игру и выйти?",
                        FOptionPane.TYPE.YES_NO_TYPE, null, true).get();
                if (exit == 0) {
                    Exit.exit(0);
                }
            }
            case "gallery" -> new GalleryFrame(GameMenu.this, getGraphicsConfiguration());
            case "saveLoad" -> new SaveGame(GameMenu.this, getGraphicsConfiguration());
            case "options" -> {
                new OptMenuFrame(GameMenu.this, getGraphicsConfiguration());
                checkFullscreen();
            }
            case "about" -> new AuthorsFrame(GameMenu.this, getGraphicsConfiguration());
            default -> {
            }
        }
    }
}