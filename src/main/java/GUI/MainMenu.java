package GUI;

import components.FoxTipsEngine;
import door.Exit;
import fox.FoxConsole;
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
    private static JButton optionsButton, galleryButton, saveLoadButton, playButton, aboutButton;
    private static JLabel downTextLabel;

    private static float wPercent, hPercent;

    private final FoxConsole cons;
    private FoxTipsEngine cd;


    public MainMenu() {
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curSimpleCursor"), "simpleCursor"));

        preLoading();
        switchFullscreen();

        add(buildBasePane());

        addMouseListener(this);
        addMouseMotionListener(this);

        pack();
        setLocationRelativeTo(null);

        testNewbie();

        Print(MainMenu.class, LEVEL.INFO, "MainMenu setts visible...");
        setVisible(true);

        setStatusText(null);

        Media.playBackg("fonKricket");
        Media.playMusic("musMainMenu", true);

        cons = new FoxConsole(this);
//        connectFoxConsole(cons);
    }

    private void preLoading() {
        Print(MainMenu.class, LEVEL.INFO, "MainMenu preloading...");

        startImages = FoxSpritesCombiner.addSpritelist("PlayButtonSprite", (BufferedImage) cache.get("picPlayButtonSprite"), 1, 3);
        menuImages = FoxSpritesCombiner.addSpritelist("MenuButtonSprite", (BufferedImage) cache.get("picMenuButtonSprite"), 1, 3);
        exitImages = FoxSpritesCombiner.addSpritelist("ExitButtonSprite", (BufferedImage) cache.get("picExitButtonSprite"), 1, 3);

        centerImage = (BufferedImage) cache.get("picMenuBase");
        picMenuImage = (BufferedImage) cache.get("picMenupane");

        downText = "\u266B " + userConf.getUserName() + " \u266B";

        InputAction.add("MainMenu", this);
        InputAction.set("MainMenu", "Ctrl+F4", KeyEvent.VK_F4, 512, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Exit.exit(0, "Exit by users 'Ctrl+F4'");
            }
        });
    }

    private static void testNewbie() {
        if (userConf.getUserName().equals("newEmptyUser")) {
            Print(MainMenu.class, LEVEL.ACCENT, "Open NewUserForm to change name by " + userConf.getUserName());
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

    private void switchFullscreen() {
        Print(MainMenu.class, LEVEL.INFO, "MainMenu fullscreen test...");

        if (userConf.isFullScreen()) {
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension((int) (screen.getWidth()), (int) (screen.getHeight())));
            setSize(new Dimension((int) (screen.getWidth()), (int) (screen.getHeight())));
        } else {
            setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));
            setPreferredSize(new Dimension((int) (screen.width * 0.75f), (int) (screen.height * 0.75f)));
            setSize(new Dimension((int) (screen.width * 0.75f), (int) (screen.height * 0.75f)));
        }
        setLocationRelativeTo(null);

        wPercent = getWidth() / 100f;
        hPercent = getHeight() / 100f;

        if (userConf.isFullScreen()) {
            userConf.setFullScreen(false);
            if (basePane != null) {
                MainMenu.this.remove(basePane);
                basePane = null;

                MainMenu.this.add(buildBasePane());
            }
        }

        userConf.setFullScreen(!userConf.isFullScreen());
    }


    private JComponent buildBasePane() {
        basePane = new JPanel(new BorderLayout((int) (wPercent * 2.6f), (int) (hPercent * 2.0f))) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;
                FoxRender.setMedRender(g2D);
                g2D.drawImage(picMenuImage, 0, 0, getWidth(), getHeight(), this);
            }

            {
                setBorder(new EmptyBorder((int) (hPercent * 3f), (int) (wPercent * 2f), (int) (wPercent * 1.6f), (int) (hPercent * 4.2f)));

                JPanel upPlayPane = new JPanel(new BorderLayout()) {
                    {
                        setOpaque(false);

                        JButton playButton = new JButton("play game") {
                            BufferedImage bImage = startImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (startImages != null) {
                                    Graphics2D g2D = (Graphics2D) g;
                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
                                    g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("playButton");
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
//						setBorder(new EmptyBorder(0, 0, 0, 6));

                        optionsButton = new JButton("game options") {
                            BufferedImage bImage = menuImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (menuImages != null) {
                                    Graphics2D g2D = (Graphics2D) g;
                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("optionsButton");
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

                        saveLoadButton = new JButton("save/load") {
                            BufferedImage bImage = menuImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (menuImages != null) {
                                    Graphics2D g2D = (Graphics2D) g;
                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("saveLoadButton");
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

                        galleryButton = new JButton("gallery") {
                            BufferedImage bImage = menuImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (menuImages != null) {
                                    Graphics2D g2D = (Graphics2D) g;
                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("galleryButton");
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

                        aboutButton = new JButton("about") {
                            BufferedImage bImage = menuImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (menuImages != null) {
                                    Graphics2D g2D = (Graphics2D) g;
                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
                                    g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("aboutButton");
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
                            Graphics2D g2D = (Graphics2D) g;
                            FoxRender.setMedRender(g2D);

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

                    {

                    }
                };

                JPanel downExitPane = new JPanel(new BorderLayout((int) (wPercent * 3f), (int) (hPercent * 2.0f))) {
                    {
                        setOpaque(false);

                        playButton = new JButton("exit game") {
                            BufferedImage bImage = exitImages[0];

                            @Override
                            public void paintComponent(Graphics g) {
                                if (exitImages != null) {
                                    Graphics2D g2D = (Graphics2D) g;
                                    FoxRender.setMedRender(g2D);

                                    g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
                                    g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
                                    g2D.dispose();
                                } else {
                                    super.paintComponent(g);
                                }
                            }

                            {
                                setName("exitButton");
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

                        try {
                            cd = new FoxTipsEngine(this, FoxTipsEngine.TYPE.INFO, ImageIO.read(new File("./resources/tipIco.png")),
                                    "Заголовок подсказки:",
                                    "Это текст сообщения. Его необходимо правильно<br>переносить на следующую строку и вообще...<br>всё в таком духе. Вот.",
                                    "Тем более, если сообщение окажется черезчур длинным."
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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

                        add(playButton, BorderLayout.EAST);
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
            case "play":
                dispose();
                if (userConf.getAvatarIndex() == 0) {
                    new GenderFrame();
                } else {
                    new GameFrame();
                }
                break;

            case "exit":
                int exit = JOptionPane.showConfirmDialog(null, "Точно закрыть игру и выйти?", "Подтверждение:", JOptionPane.YES_NO_OPTION);
                if (exit == 0) {
                    Exit.exit(0);
                }
                break;

            case "gallery":
                new GalleryFrame(MainMenu.this);
                break;

            case "saveLoad":
                new SaveGame();
                break;

            case "options":
                new OptMenuFrame();
                switchFullscreen();
                break;

            case "about":
                new AuthorsFrame();
                break;

            default:
        }
    }
}