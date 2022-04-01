package GUI;

import components.FOptionPane;
import components.FoxConsole;
import components.FoxTip;
import door.Exit;
import utils.FoxFontBuilder;
import utils.InputAction;
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
    private static GameMenu menuFrame;
    private static final GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
    private static final GraphicsConfiguration gc = gDevice.getDefaultConfiguration();
    private final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private BufferedImage[] exitImages, startImages, menuImages;
    private BufferedImage centerImage, botTopImage, botRightImage, botLeftImage;
    private Point2D mouseWasOnScreen, frameWas;
    private JPanel basePane;
    private JButton optionsButton, galleryButton, saveLoadButton, exitButton, aboutButton;
    private JLabel downTextLabel;
    private FoxTip cd;

    private String downText;
    private Integer curFps = 0;
    private final int refDelay;
    private float fpsCounter = 0;
    private long was = System.currentTimeMillis();


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

        preLoading();
        inAc();

        add(buildBasePane());

        addMouseListener(this);
        addMouseMotionListener(this);

        pack();
        checkFullscreen();
        Print(GameMenu.class, LEVEL.INFO, "MainMenu setts visible...");

        setStatusText(null);

        backgPlayer.play("fonKricket");
        musicPlayer.play("musMainMenu");

        FoxConsole cons = new FoxConsole(this);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                repaint();
                try {
                    Thread.sleep(refDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public static void setVisible() {
        menuFrame.setVisible(true);
        backgPlayer.play("fonKricket");
        musicPlayer.play("musMainMenu");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        FoxRender.setRender(g2D, userConf.getQuality() == null ? FoxRender.RENDER.MED : userConf.getQuality());

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

    private void preLoading() {
        Thread.currentThread().setName("=== MAIN THREAD ===");

        Print(GameMenu.class, LEVEL.INFO, "MainMenu preloading...");

        try {
            Print(GameMenu.class, LEVEL.DEBUG, "Preparing sprites...");
            startImages = FoxSpritesCombiner
                    .getSprites("PlayButtonSprite", (BufferedImage) cache.get("picPlayButtonSprite"), 3, 1);
            menuImages = FoxSpritesCombiner
                    .getSprites("MenuButtonSprite", (BufferedImage) cache.get("picMenuButtonSprite"), 3, 1);
            exitImages = FoxSpritesCombiner
                    .getSprites("ExitButtonSprite", (BufferedImage) cache.get("picExitButtonSprite"), 3, 1);
        } catch (Exception e) {
            Print(GameMenu.class, LEVEL.WARN, "Can`t load sprites: " + e.getMessage());
        }

        try {
            Print(GameMenu.class, LEVEL.DEBUG, "Preparing images...");
            centerImage = (BufferedImage) cache.get("picMenuBase");
            botTopImage = (BufferedImage) cache.get("picMenuTop");
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

        InputAction.add("MainMenu", GameMenu.this);
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
        InputAction.set("MainMenu", "switchFPS", KeyEvent.VK_F11, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configuration.setFpsShowed(!configuration.isFpsShowed());
            }
        });
    }

    private void checkFullscreen() {
        if (isVisible() && (userConf.isFullScreen() && getExtendedState() == MAXIMIZED_BOTH || !userConf.isFullScreen() && getExtendedState() == NORMAL)) {
            return;
        }

        Print(GameMenu.class, LEVEL.INFO, "\nGameMenu fullscreen switch...");
        dispose();

        if (basePane != null) {
            remove(basePane);
        }
        if (userConf.isFullScreen()) {
            getContentPane().setBackground(Color.BLACK);
            setExtendedState(MAXIMIZED_BOTH);
//            gDevice.setFullScreenWindow(GameMenu.this);
        } else {
//            gDevice.setFullScreenWindow(null);
            setExtendedState(NORMAL);
            setBackground(new Color(0, 0, 0, 0));
            setSize(new Dimension(Double.valueOf(screen.getWidth() * 0.75d).intValue(), Double.valueOf(screen.getHeight() * 0.75d).intValue()));
        }

        setVisible(true);
        setLocationRelativeTo(null);

        add(buildBasePane());
        Print(GameMenu.class, LEVEL.INFO, "GameMenu fullscreen checked. Thread: " + Thread.currentThread().getName());
    }

    private JPanel buildBasePane() {
        Print(GameMenu.class, LEVEL.INFO, "Building the BasePane...");

        basePane = new JPanel(new BorderLayout(0, 0)) {
            {
                setName("basePane");
                setOpaque(false);

                JPanel upPlayPane = new JPanel(new BorderLayout(0, 0)) {
                    @Override
                    public void paintComponent(Graphics g) {
                        g.drawImage(botTopImage, 0, 0, getWidth(), getHeight(), this);
                    }

                    {
                        setOpaque(false);
                        setPreferredSize(new Dimension(GameMenu.this.getWidth(), Double.valueOf(GameMenu.this.getHeight() * 0.10d).intValue()));
                        setBorder(new EmptyBorder(
                                Double.valueOf(GameMenu.this.getHeight() * 0.025d).intValue(),
                                Double.valueOf(GameMenu.this.getWidth() * 0.02d).intValue(),
                                Double.valueOf(GameMenu.this.getHeight() * 0.01d).intValue(),
                                Double.valueOf(GameMenu.this.getWidth() * 0.02d).intValue()
                        ));

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
                                Double.valueOf(GameMenu.this.getHeight() * 0.025d).intValue(),
                                Double.valueOf(GameMenu.this.getWidth() * 0.02425d).intValue()
                        ));

                        JPanel buttonsPane = new JPanel(new GridLayout(12, 0, 0, 2)) {
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

                        add(buttonsPane, BorderLayout.CENTER);
                        add(downExitPane, BorderLayout.SOUTH);
                    }
                };

                JPanel midLeftPane = new JPanel(new BorderLayout(0, 0)) {
                    @Override
                    public void paintComponent(Graphics g) {
                        if (centerImage != null) {
                            g.drawImage(botLeftImage, 0, 0, getWidth(), getHeight(), this);
                            g.drawImage(centerImage,
                                    Double.valueOf(GameMenu.this.getWidth() * 0.0268d).intValue(),
                                    Double.valueOf(GameMenu.this.getHeight() * 0.009d).intValue(),
                                    getWidth() - (Double.valueOf(GameMenu.this.getWidth() * 0.044d).intValue()),
                                    getHeight() - (Double.valueOf(GameMenu.this.getHeight() * 0.12d).intValue()),
                                    this);

                            g.setColor(Color.BLACK);
                            g.drawString("v." + Registry.version, Double.valueOf(GameMenu.this.getWidth() * 0.05d).intValue(),
                                    Double.valueOf(GameMenu.this.getHeight() * 0.05d).intValue());
                            g.setColor(Color.ORANGE);
                            g.drawString("v." + Registry.version, Double.valueOf(GameMenu.this.getWidth() * 0.049d).intValue(),
                                    Double.valueOf(GameMenu.this.getHeight() * 0.0475d).intValue());

//                            g.setColor(Color.PINK);
//                            g.drawRect(0,0,getWidth() - 1,getHeight() - 1);
                        } else {
                            super.paintComponent(g);
                        }
                    }

                    {
                        setOpaque(false);
                        setBorder(new EmptyBorder(0, 0, Double.valueOf(GameMenu.this.getWidth() * 0.0235d).intValue(), 0));

                        downTextLabel = new JLabel() {
                            {
                                setName("downName");
                                setText(downText);
                                setFont(Registry.f6);
                                setForeground(Color.WHITE);
                                setHorizontalAlignment(0);
//                                setVerticalAlignment(BOTTOM);
//                                setAlignmentY(BOTTOM_ALIGNMENT);

                                addMouseListener(GameMenu.this);
                            }
                        };

                        try {
                            cd = new FoxTip(FoxTip.TYPE.INFO, ImageIO.read(new File("./resources/tipIco.png")),
                                    "Смена или создание героя:",
                                    "Кликни сюда два раза для смены игрока<br>или создания нового профиля.", null
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        add(downTextLabel, BorderLayout.SOUTH);
                    }
                };

                add(upPlayPane, BorderLayout.NORTH);
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
        int exit = new FOptionPane(
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
                cd.showTip(downTextLabel);
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
            case "play" -> {
                new GamePlay(gc);
                dispose();
            }
            case "exit" -> showExitRequest();
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