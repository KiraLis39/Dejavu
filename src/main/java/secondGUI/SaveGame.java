package secondGUI;

import GUI.GamePlay;
import components.FOptionPane;
import configurations.UserConf;
import configurations.UserSave;
import door.Exit;
import door.MainClass;
import images.FoxCursor;
import iom.JIOM;
import logic.SaveLoad;
import registry.Registry;
import utils.InputAction;
import interfaces.Cached;
import render.FoxRender;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static registry.Registry.*;

public class SaveGame extends JDialog implements Cached, ActionListener, ListSelectionListener {
    private final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private final int WIDTH = (int) (screen.getWidth() * 0.5d);
    private final int HEIGHT = (int) (screen.getHeight() * 0.75d);
    private final BufferedImage backImage;
    private final JFrame parent;
    private JList<UserConfPanel> playersList;
    private JTextArea infoArea;
    private BufferedImage infoImage;
    private final ArrayList<UserConfPanel> userSaveList = new ArrayList<>();

    public SaveGame(JFrame parent, GraphicsConfiguration gConfig) {
        super(parent, "SaveLoadFrame", true, gConfig);
        this.parent = parent;
        backImage = (BufferedImage) cache.get("picSaveLoad");

        setSize(WIDTH, HEIGHT);
        setFocusable(true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curGalleryCursor"), "galleryCursor"));
        setAutoRequestFocus(true);

        JPanel centerSaveInfoPane = new JPanel(new BorderLayout(0,0)) {
            @Override
            public void paintComponent(Graphics g) {
//                super.paintComponent(g);
                Graphics2D g2D = (Graphics2D) g;
                FoxRender.setRender(g2D, FoxRender.RENDER.HIGH);

                g2D.drawImage(backImage,
                        0,0,
                        getWidth(), getHeight(),

                        0, 3,
                        (int) (backImage.getWidth() * 0.66f),
                        backImage.getHeight(),

                        this);

                if (infoImage != null) {
                    g2D.drawImage(infoImage,
                            28, 33,
                            getWidth() - 43, getHeight() - 310,
                            this);
                }

//                g2D.setColor(Color.GREEN);
//                g2D.drawRect(0,0,getWidth() - 1,getHeight() - 1);
            }

            {
                setOpaque(false);
                setBorder(new EmptyBorder(0,36,36,20));

                infoArea = new JTextArea() {
                    {
                        setOpaque(false);
                        setWrapStyleWord(true);
                        setLineWrap(true);
                        setFont(fontName2);
                        setForeground(Color.WHITE);
                        getContentPane().setForeground(Color.WHITE);
                        setPreferredSize(new Dimension(0, 210));
                    }
                };

                add(infoArea, BorderLayout.SOUTH);
            }
        };

        JPanel leftSavesPane = new JPanel(new BorderLayout(0,0)) {
            @Override
            public void paintComponent(Graphics g) {
//                super.paintComponent(g);
                Graphics2D g2D = (Graphics2D) g;
                FoxRender.setRender(g2D, FoxRender.RENDER.HIGH);

                g2D.drawImage(backImage,
                        0,0,
                        getWidth(), getHeight(),

                        (int) (backImage.getWidth() * 0.66f),
                        0,
                        backImage.getWidth(),
                        backImage.getHeight(),

                        this);
            }

            {
                setOpaque(false);
                setBorder(new EmptyBorder(36,9,30,22));
                setPreferredSize(new Dimension(300, 0));

                reloadSaves();
                ListModel<UserConfPanel> ucModel = new AbstractListModel<>() {
                    @Override
                    public int getSize() {
                        return userSaveList.size();
                    }

                    @Override
                    public UserConfPanel getElementAt(int index) {
                        return userSaveList.get(index);
                    }
                };
                playersList = new JList<>(ucModel) {
                    {
                        setOpaque(false);
                        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//                        setVisibleRowCount(-1);
//                        setFixedCellHeight(40);
//                        setSelectedIndex(-1);
                        setCellRenderer(new FoxCellRenderer(96));
                        addListSelectionListener(SaveGame.this);
                    }
                };

                JPanel btnsPane = new JPanel(new GridLayout(2,0,0,0)) {
                    {
                        setOpaque(false);

                        JButton confirmBtn = new JButton("Загрузить это сохранение") {
                            {
                                setBackground(Color.BLACK);
                                setForeground(Color.WHITE);
                                setActionCommand("choseSelected");
                                addActionListener(SaveGame.this);
                                setFocusPainted(false);
                            }
                        };

                        JButton createBtn = new JButton("Создать новое сохранение") {
                            {
                                setBackground(Color.BLACK);
                                setForeground(Color.WHITE);
                                setActionCommand("createNew");
                                addActionListener(SaveGame.this);
                                setFocusPainted(false);
                            }
                        };

                        add(confirmBtn);
                        add(createBtn);
                    }
                };


                add(playersList, BorderLayout.CENTER);
                add(btnsPane, BorderLayout.SOUTH);
            }
        };

        add(centerSaveInfoPane, BorderLayout.CENTER);
        add(leftSavesPane, BorderLayout.EAST);

        inAc();

        setLocationRelativeTo(null);
        setModal(true);
        setVisible(true);
    }

    private void reloadSaves() {
        userSaveList.clear();
        try {
            List<Path> users = Files.list(Registry.usersSaveDir).toList();
            for (Path user : users) {
                if (user.toString().contains("save")) {
                    userSaveList.add(new UserConfPanel(
                            JIOM.fileToDto(Paths.get(user.getParent() + "\\uconf.dto"), UserConf.class),
                            JIOM.fileToDto(user, UserSave.class)
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inAc() {
        InputAction.add("save", this);
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_IN_FOCUSED_WINDOW, "save", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("choseSelected")) {
            if (playersList.getSelectedValue() == null) {
                new FOptionPane("Ошибка:", "Нужно выбрать сохранение\nили нажать ESC");
            } else {
                UserSave selectedSave = playersList.getSelectedValue().getSave();
                parent.dispose();
                musicPlayer.stop();
                backgPlayer.stop();
                new GamePlay(parent.getGraphicsConfiguration(), selectedSave, -1);
                dispose();
            }
        }

        if (e.getActionCommand().equals("createNew")) {
            String name = (String) new FOptionPane("Запись сохранения:", "Введи имя сохранения:", FOptionPane.TYPE.INPUT).get();
            if (name != null && !name.isBlank()) {
                SaveLoad.handleSave(Paths.get(usersSaveDir + "\\save_" + name), name);
                reloadSaves();
                playersList.setModel(new AbstractListModel<>() {
                    @Override
                    public int getSize() {
                        return userSaveList.size();
                    }

                    @Override
                    public UserConfPanel getElementAt(int index) {
                        return userSaveList.get(index);
                    }
                });
                revalidate();
            } else {
                new FOptionPane("Запись не сохранена:", "Неудачное имя сохранения!");
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            UserConf up = playersList.getSelectedValue().getConfig();
            UserSave us = playersList.getSelectedValue().getSave();
            infoArea.setText(String.format(
                    "%s\nИгрок: %s\n" +
                    "Часть: %s\tДата: %d %s\n" +
                    "Круг: %d", us.getName() == null ? "auto" : us.getName(), up.getUserName(), us.getChapter(), us.getToday(), us.getMonth(), us.getCycleCount()
            ));
            try {
                infoImage = ImageIO.read(new File(scenesDir + "\\" + us.getScreen() + picExtension));
            } catch (IOException ex) {
                infoImage = (BufferedImage) cache.get("0");
            }
        }
    }
}