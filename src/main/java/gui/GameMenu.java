package gui;

import components.FOptionPane;
import components.FoxConsole;
import door.Exit;
import images.FoxSpritesCombiner;
import interfaces.Cached;
import registry.Registry;
import render.FoxRender;
import secondGUI.AuthorsFrame;
import secondGUI.GalleryFrame;
import secondGUI.OptMenuFrame;
import secondGUI.PlayersListDialog;
import tools.Cursors;
import utils.FoxFontBuilder;
import utils.InputAction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static fox.Out.LEVEL;
import static fox.Out.Print;
import static registry.Registry.*;

public class GameMenu extends JFrame implements MouseListener, MouseMotionListener, ActionListener, Cached {
    private final GameMenu menuFrame;
    private static final GraphicsDevice gDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private static final GraphicsConfiguration gc = gDevice.getDefaultConfiguration();
    private BufferedImage[] exitImages, startImages;
    private BufferedImage centerImage;
    private BufferedImage botRightImage;
    private BufferedImage botLeftImage;
    private Point2D mouseWasOnScreen, frameWas;
    private JPanel basePane;
    private static JPanel downPane;
    private JButton optionsButton, galleryButton, exitButton, aboutButton, newGameBtn;
    private static JButton continueBtn;
    private JLabel downTextLabel;
//    private FoxTip cd, cd2;
    private String downText;
    private Integer curFps = 0;
    private final int refDelay;
    private float fpsCounter = 0;
    private long was = System.currentTimeMillis();
    private final double BORDER_RATIO = 0.75d;
    private final double WINDOW_RATIO = 0.75d;

    public GameMenu() {
        super("GameMenuParent", gc);
        menuFrame = this;
        refDelay = 1000 / gDevice.getDisplayMode().getRefreshRate();

        setName("GameMenu");
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setCursor(Cursors.SimpleCursor.get());
        setAutoRequestFocus(true);

        DisplayMode mode = gDevice.getDisplayMode();
        setLocation((int) (mode.getWidth() * BORDER_RATIO), (int) (mode.getHeight() * BORDER_RATIO));
        setPreferredSize(new Dimension((int) (mode.getWidth() * WINDOW_RATIO), (int) (mode.getHeight() * WINDOW_RATIO)));

        preLoading();
        inAc();

        addMouseListener(this);
        addMouseMotionListener(this);

        pack();
        checkFullscreen();
        Print(GameMenu.class, LEVEL.INFO, "MainMenu setts visible...");

        setStatusText(null);

        backgPlayer.play("fonKricket");
        musicPlayer.play("musMainMenu");

//        try {
//            cd = new FoxTip(FoxTip.TYPE.INFO, ImageIO.read(new File("./resources/tipIco.png")),
//                    "Смена или создание героя:",
//                    "Кликни сюда два раза для смены игрока<br>или создания нового профиля.", null, downTextLabel);
//            cd2 = new FoxTip(FoxTip.TYPE.INFO, ImageIO.read(new File("./resources/tipIco.png")),
//                    "Начать новую игру:",
//                    "Кликни сюда для начала новой игры<br>(!старая будет затёрта!)", null, newGameBtn);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        FoxConsole cons = new FoxConsole(this);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                basePane.repaint();
                try {
                    Thread.sleep(refDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void preLoading() {
        Thread.currentThread().setName("=== MAIN THREAD ===");

        Print(GameMenu.class, LEVEL.INFO, "MainMenu preloading...");

        try {
            Print(GameMenu.class, LEVEL.DEBUG, "Preparing sprites...");
            startImages = FoxSpritesCombiner
                    .getSprites("PlayButtonSprite", (BufferedImage) cache.get("picPlayButtonSprite"), 3, 1);
            exitImages = FoxSpritesCombiner
                    .getSprites("ExitButtonSprite", (BufferedImage) cache.get("picExitButtonSprite"), 3, 1);
        } catch (Exception e) {
            Print(GameMenu.class, LEVEL.WARN, "Can`t load sprites: " + e.getMessage());
        }

        try {
            Print(GameMenu.class, LEVEL.DEBUG, "Preparing images...");
            centerImage = (BufferedImage) cache.get("picMenuBase");
            BufferedImage botTopImage = (BufferedImage) cache.get("picMenuTop");
            botRightImage = (BufferedImage) cache.get("picMenuBotRight");
            botLeftImage = (BufferedImage) cache.get("picMenuBotLeft");
        } catch (Exception e) {
            Print(GameMenu.class, LEVEL.WARN, "Can`t load images: " + e.getMessage());
        }

        downText = "\u266B " + userConf.getUserName() + " \u266B";

        Print(GameMenu.class, LEVEL.INFO, "MainMenu preloading done.");
    }

    private void inAc() {
        Print(GameMenu.class, LEVEL.DEBUG, "Sets inAc...");

        InputAction.add("MainMenu", menuFrame);
        InputAction.set("MainMenu", "Ctrl+F4", KeyEvent.VK_F4, 512, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Exit.exit(0, "Exit by users 'Ctrl+F4'");
            }
        });
        InputAction.set("MainMenu", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showExitRequest();
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_IN_FOCUSED_WINDOW, "MainMenu", "changeQuality", KeyEvent.VK_F3, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userConf.nextQuality();
                System.err.println("Changed quality to " + userConf.getQuality());
            }
        });
        InputAction.set("MainMenu", "switchFullscreen", KeyEvent.VK_F, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userConf.setFullScreen(!userConf.isFullScreen());
                checkFullscreen();
            }
        });
        InputAction.set("MainMenu", "switchFPS", KeyEvent.VK_F11, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configuration.setFpsShowed(!configuration.isFpsShowed());
                System.out.println("FPS showed: " + configuration.isFpsShowed());
            }
        });
    }

    private void checkFullscreen() {
//        if (isVisible() && (userConf.isFullScreen() && getExtendedState() == MAXIMIZED_BOTH || !userConf.isFullScreen() && getExtendedState() == NORMAL)) {
//            return;
//        }

        Print(GameMenu.class, LEVEL.INFO, "\nGameMenu fullscreen switch...");
        dispose();
        if (basePane != null) {
            remove(basePane);
        }

        if (userConf.isFullScreen()) {
            getContentPane().setBackground(Color.BLACK);
            gDevice.setFullScreenWindow(GameMenu.this);
        } else {
            setBackground(new Color(0, 0, 0, 0));
            gDevice.setFullScreenWindow(null);
            pack();
        }

        setLocationRelativeTo(null);
        setVisible(true);

        add(buildBasePane());

//        if (downPane != null) {
//            downPane.setVisible(userSave != null && userSave.getToday() != 3);
//            continueBtn.setVisible(downPane.isVisible());
//            downPane.revalidate();
//        }

        revalidate();
        Print(GameMenu.class, LEVEL.INFO, "GameMenu fullscreen checked. Thread: " + Thread.currentThread().getName());
    }

    private JPanel buildBasePane() {
        Print(GameMenu.class, LEVEL.INFO, "Building the BasePane...");

        basePane = new JPanel(new BorderLayout(0, 0)) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;
                FoxRender.setRender(g2D, userConf.getQuality() == null ? FoxRender.RENDER.HIGH : userConf.getQuality());
                super.paintComponent(g2D);
            }

            {
                setName("basePane");
                setOpaque(false);

                JPanel rightBottomPane = new JPanel(new BorderLayout(0, 0)) {
                    @Override
                    public void paintComponent(Graphics g) {
                        g.drawImage(botRightImage, 0, 0, getWidth(), getHeight(), this);
                    }

                    {
                        setOpaque(false);
                        setBorder(new EmptyBorder(
                                Double.valueOf(GameMenu.this.getHeight() * 0.0175d).intValue(),
                                Double.valueOf(GameMenu.this.getWidth() * 0.01425d).intValue(),
                                Double.valueOf(GameMenu.this.getHeight() * 0.035d).intValue(),
                                Double.valueOf(GameMenu.this.getWidth() * 0.02425d).intValue()
                        ));

                        JPanel rightPane = new JPanel(new BorderLayout(0,0)) {
                            {
                                setOpaque(false);

                                JPanel buttonsPane = new JPanel(new GridLayout(10, 0, 0, 2)) {
                                    {
                                        setOpaque(false);

                                        optionsButton = new JButton("Настройки") {
                                            BufferedImage bImage = startImages[0];

                                            @Override
                                            public void paintComponent(Graphics g) {
                                                if (startImages != null) {
                                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
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
                                                setName("Настройки");
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

//                                saveLoadButton = new JButton("Сохранение/загрузка") {
//                                    BufferedImage bImage = menuImages[0];
//
//                                    @Override
//                                    public void paintComponent(Graphics g) {
//                                        if (menuImages != null) {
//                                            g.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
//                                            if (bImage == menuImages[1]) {
//                                                g.drawString(getName(),
//                                                        (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
//                                                        getHeight() / 2 + 6 + 2);
//                                            } else {
//                                                g.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//                                            }
//                                        } else {
//                                            super.paintComponent(g);
//                                        }
//                                    }
//
//                                    {
//                                        setName("Сохранение/загрузка");
////								setPreferredSize(new Dimension(410, 50));
//                                        setFont(Registry.f5);
//                                        setForeground(Color.BLACK);
//                                        setBorderPainted(false);
//                                        setFocusPainted(false);
//                                        setFocusable(false);
//                                        setOpaque(false);
//
//                                        setActionCommand("saveLoad");
//                                        addActionListener(GameMenu.this);
//                                        addMouseListener(new MouseAdapter() {
//                                            public void mouseEntered(MouseEvent me) {
//                                                setStatusText("Сохранить и загрузить");
//                                                bImage = menuImages[1];
//                                                repaint();
//                                            }
//
//                                            public void mouseExited(MouseEvent me) {
//                                                setStatusText(null);
//                                                bImage = menuImages[0];
//                                                repaint();
//                                            }
//
//                                            public void mousePressed(MouseEvent me) {
//                                                bImage = menuImages[2];
//                                                repaint();
//                                            }
//
//                                            public void mouseReleased(MouseEvent me) {
//                                                bImage = menuImages[0];
//                                                repaint();
//                                            }
//                                        });
//                                    }
//                                };

                                        galleryButton = new JButton("Галерея") {
                                            BufferedImage bImage = startImages[0];

                                            @Override
                                            public void paintComponent(Graphics g) {
                                                if (startImages != null) {
                                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
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

                                        aboutButton = new JButton("Об игре") {
                                            BufferedImage bImage = startImages[0];

                                            @Override
                                            public void paintComponent(Graphics g) {
                                                if (startImages != null) {
                                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);

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

                                        newGameBtn = new JButton("Начать новую игру") {
                                            BufferedImage bImage = startImages[0];

                                            @Override
                                            public void paintComponent(Graphics g) {
                                                if (startImages != null) {
                                                    g.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
                                                    if (bImage == startImages[1]) {
                                                        g.drawString(getName(),
                                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                                getHeight() / 2 + 10);
                                                    } else {
                                                        g.drawString(getName(),
                                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D),
                                                                getHeight() / 2 + 8);
                                                    }
                                                } else {
                                                    super.paintComponent(g);
                                                }
                                            }

                                            {
                                                setName("Новая игра");
                                                setFont(Registry.f5);
                                                setForeground(Color.BLACK);
                                                setBorderPainted(false);
                                                setFocusPainted(false);
                                                setFocusable(false);
                                                setOpaque(false);

                                                setActionCommand("playNew");
                                                addActionListener(GameMenu.this);
                                                addMouseListener(new MouseAdapter() {
                                                    public void mouseEntered(MouseEvent me) {
//                                                        cd2.showTip();
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


                                        add(optionsButton);
//                                add(saveLoadButton);
                                        add(galleryButton);
                                        add(aboutButton);
                                        add(newGameBtn);
                                    }
                                };

                                downPane = new JPanel(new BorderLayout(0,0)) {
                                    {
                                        setOpaque(false);
                                        setBorder(new EmptyBorder(0,0,20,0));

                                        continueBtn = new JButton("Продолжить") {
                                            BufferedImage bImage2 = startImages[0];

                                            @Override
                                            public void paintComponent(Graphics g) {
                                                if (startImages != null) {
                                                    g.drawImage(bImage2, 0, 0, getWidth(), getHeight(), null, null);
                                                    if (bImage2 == startImages[1]) {
                                                        g.drawString(getName(),
                                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                                getHeight() / 2 + 10);
                                                    } else {
                                                        g.drawString(getName(),
                                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D),
                                                                getHeight() / 2 + 8);
                                                    }
                                                } else {
                                                    super.paintComponent(g);
                                                }
                                            }

                                            {
                                                setName("Продолжить");
                                                setFont(Registry.f5);
                                                setForeground(Color.YELLOW);
                                                setBorderPainted(false);
                                                setFocusPainted(false);
                                                setFocusable(false);
                                                setOpaque(false);
                                                setPreferredSize(new Dimension(0, 60));

                                                setActionCommand("continue");
                                                addActionListener(GameMenu.this);
                                                addMouseListener(new MouseAdapter() {
                                                    public void mouseEntered(MouseEvent me) {
//                                                        cd2.showTip();
                                                        setStatusText("Продолжить игру");
                                                        bImage2 = startImages[1];
                                                        repaint();
                                                    }

                                                    public void mouseExited(MouseEvent me) {
                                                        setStatusText(null);
                                                        bImage2 = startImages[0];
                                                        repaint();
                                                    }

                                                    public void mousePressed(MouseEvent me) {
                                                        bImage2 = startImages[2];
                                                        repaint();
                                                    }

                                                    public void mouseReleased(MouseEvent me) {
                                                        bImage2 = startImages[0];
                                                        repaint();
                                                    }
                                                });
                                            }
                                        };

                                        add(continueBtn);
                                    }
                                };

                                add(buttonsPane, BorderLayout.CENTER);

                                if (userSave.getChapter() != null) {
                                    add(downPane, BorderLayout.SOUTH);
                                }
                            }
                        };

                        JPanel downExitPane = new JPanel(new BorderLayout(0, 0)) {
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
                                        setFont(Registry.f5);
                                        setForeground(Color.BLACK);
                                        setBorderPainted(false);
                                        setFocusPainted(false);
                                        setFocusable(false);
                                        setOpaque(false);

                                        setPreferredSize(new Dimension(
                                                Double.valueOf(GameMenu.this.getWidth() * 0.25d).intValue(),
                                                Double.valueOf(GameMenu.this.getHeight() * 0.07d).intValue()
                                        ));

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

                                add(exitButton, BorderLayout.CENTER);
                            }
                        };

                        add(rightPane, BorderLayout.CENTER);
                        add(downExitPane, BorderLayout.SOUTH);
                    }
                };

                JPanel midLeftPane = new JPanel(new BorderLayout(0, 0)) {
                    @Override
                    public void paintComponent(Graphics g) {
                        if (centerImage != null) {
                            g.drawImage(botLeftImage, 0, 0, getWidth(), getHeight(), this);
                            g.drawImage(centerImage,
                                    Double.valueOf(GameMenu.this.getWidth() * 0.0264d).intValue(),
                                    Double.valueOf(GameMenu.this.getHeight() * 0.009d).intValue(),
                                    getWidth() - (Double.valueOf(GameMenu.this.getWidth() * 0.040d).intValue()),
                                    getHeight() - (Double.valueOf(GameMenu.this.getHeight() * 0.13d).intValue()),
                                    this);

                            g.setColor(Color.BLACK);
                            g.drawString("v." + Registry.version, Double.valueOf(GameMenu.this.getWidth() * 0.04d).intValue(),
                                    Double.valueOf(GameMenu.this.getHeight() * 0.04d).intValue());
                            g.setColor(Color.ORANGE);
                            g.drawString("v." + Registry.version, Double.valueOf(GameMenu.this.getWidth() * 0.039d).intValue(),
                                    Double.valueOf(GameMenu.this.getHeight() * 0.0375d).intValue());

//                            g.setColor(Color.PINK);
//                            g.drawRect(0,0,getWidth() - 1,getHeight() - 1);
                        } else {
                            super.paintComponent(g);
                        }

                        if (configuration.isFpsShowed()) {
                            fpsCounter++;
                            if (System.currentTimeMillis() - was > 1000) {
                                curFps = Double.valueOf(Math.floor(fpsCounter)).intValue();
                                fpsCounter = 0;
                                was = System.currentTimeMillis();
                            }
                            drawFPS(g);
                        }
                    }

                    private void drawFPS(Graphics g) {
                        g.setFont(fontName2);

                        g.setColor(Color.WHITE);
                        g.drawString(curFps.toString(), 9, 39);

                        g.setColor(Color.BLACK);
                        g.drawString(curFps.toString(), 10, 40);
                    }

                    {
                        setOpaque(false);
                        setBorder(new EmptyBorder(0, 0, Double.valueOf(GameMenu.this.getHeight() * 0.05d).intValue(), 0));

                        downTextLabel = new JLabel() {
                            {
                                setName("downName");
                                setText(downText);
                                setFont(Registry.f6);
                                setForeground(Color.WHITE);
                                setHorizontalAlignment(0);
                                setVerticalAlignment(CENTER);
                                setAlignmentY(CENTER_ALIGNMENT);

                                addMouseListener(GameMenu.this);
                            }
                        };

                        add(downTextLabel, BorderLayout.SOUTH);
                    }
                };

                add(rightBottomPane, BorderLayout.EAST);
                add(midLeftPane, BorderLayout.CENTER);
            }
        };

        return basePane;
    }

    private void setStatusText(String newText) {
        if (newText == null) {
            downTextLabel.setText("\u266B " + userConf.getUserName() + " \u266B");
        } else {
            downTextLabel.setText("\u266B " + newText + " \u266B");
        }
    }

    private void showExitRequest() {
        int exit = (int) new FOptionPane(
                "Подтверждение:", "Точно закрыть игру и выйти?",
                FOptionPane.TYPE.YES_NO_TYPE, null, true).get();
        if (exit == 0) {
            Exit.exit(0);
        }
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
        if (e.getSource() instanceof JLabel) {
            if (((JLabel) e.getSource()).getName().equals("downName")) {
                if (e.getClickCount() >= 2) {
                    new PlayersListDialog(GameMenu.this);
                }
            }
        }

        mouseWasOnScreen = new Point(e.getXOnScreen(), e.getYOnScreen());
        frameWas = GameMenu.this.getLocation();
    }
    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() instanceof JLabel) {
            if (((JLabel) e.getSource()).getName().equals("downName")) {
                setForeground(Color.WHITE);
                setStatusText(null);
//				cd.close();
            }
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() instanceof JLabel) {
            if (((JLabel) e.getSource()).getName().equals("downName")) {
                setForeground(Color.ORANGE);
                setStatusText("Сменить/создать игрока (2x click)");
//                cd.showTip();
            }
        }
    }
    public void mouseClicked(MouseEvent e) {
    }
    public void mouseMoved(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "playNew" -> {
                // начинаем новую игру:
                userSave.reset();
                new GamePlay(getGraphicsConfiguration(), userSave, menuFrame);
                dispose();
            }
            case "continue" -> {
                new GamePlay(getGraphicsConfiguration(), userSave, menuFrame);
                dispose();
            }
            case "exit" -> showExitRequest();
            case "gallery" -> new GalleryFrame(GameMenu.this, getGraphicsConfiguration());
            case "options" -> {
                new OptMenuFrame(GameMenu.this, getGraphicsConfiguration());
                checkFullscreen();
            }
            case "about" -> new AuthorsFrame(GameMenu.this, getGraphicsConfiguration());
            default -> {
            }
        }
    }

    public void showFrame() {
        checkFullscreen();
    }
}