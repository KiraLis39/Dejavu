package GUI;

import components.FOptionPane;
import components.FoxConsole;
import components.FoxTipsEngine;
import door.Exit;
import fox.FoxCursor;
import fox.FoxFontBuilder;
import fox.InputAction;
import images.FoxSpritesCombiner;
import interfaces.Cached;
import tools.Media;
import render.FoxRender;
import registry.Registry;
import secondGUI.*;

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
import static registry.Registry.userConf;

public class MainMenu extends JFrame implements MouseListener, MouseMotionListener, ActionListener, Cached {
    private static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    private static BufferedImage centerImage, picMenuImage;
    private static BufferedImage[] exitImages, startImages, menuImages;

    private static Point2D mouseWasOnScreen, frameWas;

    private static String downText;

    private static JPanel basePane;
    private static JButton optionsButton, galleryButton, saveLoadButton, exitButton, aboutButton;
    private static JLabel downTextLabel;

    private static float wPercent, hPercent;
    private Graphics2D g2D;

    private final FoxConsole cons;
    private FoxTipsEngine cd;

    public MainMenu() {
        Thread.currentThread().setName("=== MAIN THREAD ===");

        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curSimpleCursor"), "simpleCursor"));

        preLoading();

        add(buildBasePane());

        addMouseListener(this);
        addMouseMotionListener(this);

        pack();
        setLocationRelativeTo(null);

        testNewbie();

        Print(MainMenu.class, LEVEL.INFO, "MainMenu setts visible...");
        setVisible(true);
        checkFullscreen();

        setStatusText(null);

        Media.playBackg("fonKricket");
        Media.playMusic("musMainMenu", true);

        cons = new FoxConsole(this);
    }

    private void preLoading() {
        Print(MainMenu.class, LEVEL.INFO, "MainMenu preloading...");

        try {
            Print(MainMenu.class, LEVEL.DEBUG, "Preparing sprites...");
            startImages = FoxSpritesCombiner.addSpritelist("PlayButtonSprite", (BufferedImage) cache.get("picPlayButtonSprite"), 1, 3);
            menuImages = FoxSpritesCombiner.addSpritelist("MenuButtonSprite", (BufferedImage) cache.get("picMenuButtonSprite"), 1, 3);
            exitImages = FoxSpritesCombiner.addSpritelist("ExitButtonSprite", (BufferedImage) cache.get("picExitButtonSprite"), 1, 3);
        } catch (Exception e) {
            Print(MainMenu.class, LEVEL.WARN, "Can`t load sprites: " + e.getMessage());
        }

        try {
            Print(MainMenu.class, LEVEL.DEBUG, "Preparing images...");
            centerImage = (BufferedImage) cache.get("picMenuBase");
            picMenuImage = (BufferedImage) cache.get("picMenupane");
        } catch (Exception e) {
            Print(MainMenu.class, LEVEL.WARN, "Can`t load images: " + e.getMessage());
        }

        downText = "\u266B " + userConf.getUserName() + " \u266B";

        Print(MainMenu.class, LEVEL.DEBUG, "Sets inAc...");
        InputAction.add("MainMenu", this);
        InputAction.set("MainMenu", "Ctrl+F4", KeyEvent.VK_F4, 512, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Exit.exit(0, "Exit by users 'Ctrl+F4'");
            }
        });
        Print(MainMenu.class, LEVEL.INFO, "MainMenu preloading done.");
    }

    private static void testNewbie() {
        Print(MainMenu.class, LEVEL.INFO, "A newbie test...");

        if (userConf.getUserName().equals("newEmptyUser")) {
            Print(MainMenu.class, LEVEL.ACCENT, "Open NewUserForm to change name " + userConf.getUserName());
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

    private void checkFullscreen() {
        Print(MainMenu.class, LEVEL.INFO, "\nMainMenu fullscreen switch...");

        Double sw = screen.getWidth();
        Double sh = screen.getHeight();

        MainMenu.this.remove(basePane);

        if (userConf.isFullScreen()) {
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(sw.intValue(), sh.intValue()));
            setSize(new Dimension(sw.intValue(), sh.intValue()));
            setState(MAXIMIZED_BOTH);
            sw = screen.getWidth();
            sh = screen.getHeight();
        } else {
            setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));
            setPreferredSize(new Dimension((int) (sw * 0.75d), (int) (sh * 0.75d)));
            setSize(new Dimension((int) (sw * 0.75d), (int) (sh * 0.75d)));
            setState(NORMAL);
            sw = getWidth() * 1D;
            sh = getHeight() * 1D;
        }
//        pack();
        setLocationRelativeTo(null);

        wPercent = sw.floatValue() / 100f;
        hPercent = sh.floatValue() / 100f;
        MainMenu.this.add(buildBasePane());
        MainMenu.this.revalidate();

        Print(MainMenu.class, LEVEL.INFO, "MainMenu fullscreen checked. Thread: " + Thread.currentThread().getName());
    }


    private JComponent buildBasePane() {
        Print(MainMenu.class, LEVEL.INFO, "Building the BasePane...");
        basePane = new JPanel(new BorderLayout((int) (wPercent * 2.6f), (int) (hPercent * 2.0f))) {
            @Override
            protected void paintComponent(Graphics g) {
                g2D = (Graphics2D) g;
                FoxRender.setMedRender(g2D);
                g2D.drawImage(picMenuImage, 0, 0, getWidth(), getHeight(), this);
            }

            {
                setBorder(new EmptyBorder((int) (hPercent * 3f), (int) (wPercent * 2f), (int) (wPercent * 1.6f), (int) (hPercent * 4.2f)));

                JPanel upPlayPane = new JPanel(new BorderLayout()) {
                    {
                        setOpaque(false);

                        JButton playButton = new JButton() {
                            BufferedImage bImage = startImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (startImages != null) {
                                    g2D = (Graphics2D) g;
//                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
                                    if (bImage == startImages[1]) {
                                        g2D.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D),getHeight() / 2 + 6);
                                    }
//									g2D.dispose();
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
                                setOpaque(false);

                                setActionCommand("play");
                                addActionListener(MainMenu.this);
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
                                    g2D = (Graphics2D) g;
//                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    if (bImage == menuImages[1]) {
                                        g2D.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D),getHeight() / 2 + 6);
                                    }
//									g2D.dispose();
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
                                setOpaque(false);

                                setActionCommand("options");
                                addActionListener(MainMenu.this);
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
                                    g2D = (Graphics2D) g;
//                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    if (bImage == menuImages[1]) {
                                        g2D.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D),getHeight() / 2 + 6);
                                    }
//									g2D.dispose();
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
                                setOpaque(false);

                                setActionCommand("saveLoad");
                                addActionListener(MainMenu.this);
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
                                    g2D = (Graphics2D) g;
//                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    if (bImage == menuImages[1]) {
                                        g2D.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D),getHeight() / 2 + 6);
                                    }
//									g2D.dispose();
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
                                setOpaque(false);

                                setActionCommand("gallery");
                                addActionListener(MainMenu.this);
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
                                    g2D = (Graphics2D) g;
//                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);

                                    if (bImage == menuImages[1]) {
                                        g2D.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D),getHeight() / 2 + 6);
                                    }

//									g2D.dispose();
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
                                setOpaque(false);

                                setActionCommand("about");
                                addActionListener(MainMenu.this);
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
                            g2D = (Graphics2D) g;
//                            FoxRender.setMedRender(g2D);

                            g2D.drawImage(centerImage, 0, 0, getWidth(), getHeight(), this);

                            g2D.setColor(Color.BLACK);
                            g2D.drawString("v." + Registry.version, 7, 18);
                            g2D.setColor(Color.ORANGE);
                            g2D.drawString("v." + Registry.version, 8, 16);
//							g2D.dispose();
                        } else {
                            super.paintComponent(g);
                        }
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
                                    g2D = (Graphics2D) g;
                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
                                    if (bImage == exitImages[1]) {
                                        g2D.drawString(getName(),
                                                (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D) - 2,
                                                getHeight() / 2 + 6 + 2);
                                    } else {
                                        g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D),getHeight() / 2 + 6);
                                    }

//                                    g2D.dispose();
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
                                setOpaque(false);

                                setActionCommand("exit");
                                addActionListener(MainMenu.this);
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
        frameWas = MainMenu.this.getLocation();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "play" -> {
                dispose();
                if (userConf.getAvatarIndex() == 0) {
                    new GenderFrame();
                } else {
                    new GameFrame();
                }
            }
            case "exit" -> {
                int exit = new FOptionPane(
                        "Подтверждение:", "Точно закрыть игру и выйти?",
                        FOptionPane.TYPE.YES_NO_TYPE, null, true).get();
                if (exit == 0) {
                    Exit.exit(0);
                }
            }
            case "gallery" -> new GalleryFrame(MainMenu.this);
            case "saveLoad" -> new SaveGame();
            case "options" -> {
                new OptMenuFrame();
                checkFullscreen();
            }
            case "about" -> new AuthorsFrame();
            default -> {
            }
        }
    }
}