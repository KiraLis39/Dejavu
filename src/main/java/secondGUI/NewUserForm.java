package secondGUI;

import components.FOptionPane;
import configurations.UserConf;
import door.MainClass;
import fox.FoxFontBuilder;
import fox.InputAction;
import fox.Out;
import interfaces.Cached;
import registry.Registry;
import render.FoxRender;
import tools.Cursors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class NewUserForm extends JDialog implements Cached, ListSelectionListener, ActionListener, ItemListener {
    private final int WIDTH = 500, HEIGHT = 480;
    private JTextField nameField, ageField;
    private JCheckBox maleBox, femaBox;
    private JList<String> avatarList;
    private JPanel avatarPicPane;
    private UserConf newUserConf;

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        // TODO: где рендер?
        g2D.setColor(Color.DARK_GRAY);
        g2D.fillRoundRect(0, 0, WIDTH, HEIGHT, 30, 30);

        g2D.setStroke(new BasicStroke(1.2f));
        g2D.setColor(Color.BLACK);
        g2D.drawRoundRect(4, 6, WIDTH - 11, HEIGHT - 9, 25, 25);
        g2D.setColor(Color.GRAY);
        g2D.drawRoundRect(5, 5, WIDTH - 10, HEIGHT - 10, 25, 25);

        g2D.setFont(Registry.f0);
        g2D.setColor(Color.BLACK);
        g2D.drawString("Настрой своего персонажа:", (int) (WIDTH / 2 - FoxFontBuilder.getStringBounds(g2D, "Настрой своего персонажа:").getWidth() / 2D) - 1, 41);
        g2D.setColor(Color.ORANGE);
        g2D.drawString("Настрой своего персонажа:", (int) (WIDTH / 2 - FoxFontBuilder.getStringBounds(g2D, "Настрой своего персонажа:").getWidth() / 2D), 40);

        g2D.setFont(Registry.f0);
        if (nameField.hasFocus()) {
            g2D.setColor(Color.GREEN);
        } else {
            g2D.setColor(Color.WHITE);
        }
        if (ageField.hasFocus()) {
            g2D.setColor(Color.GREEN);
        } else {
            g2D.setColor(Color.WHITE);
        }

        super.paintComponents(g2D);
    }

    public NewUserForm() {
        preInit();
        prepareNewUserConfig();

        JPanel midAreasPane = new JPanel(new BorderLayout(3, 3)) {
            {
                setOpaque(false);
                setBorder(new EmptyBorder(9, 0, 6, 0));

                JPanel avatarPane = new JPanel(new BorderLayout(3, 3)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        g.drawImage((BufferedImage) cache.get("picGameMenu"), 0, 0, getWidth(), getHeight(), this);
                    }

                    {
                        setOpaque(false);

                        avatarList = new JList<>(new String[]{"Аватар 1", "Аватар 2", "Аватар 3", "Аватар 4"}) {
                            {
                                setFont(Registry.f2);
                                setBackground(new Color(0, 0, 0, 0));
                                setBorder(new EmptyBorder(3, 6, 0, 0));
                                setOpaque(false);
                                setForeground(Color.WHITE);
                                setSelectionBackground(Color.BLUE.darker());
                                setSelectionForeground(Color.ORANGE.brighter());
                                addListSelectionListener(NewUserForm.this);
                            }
                        };

                        avatarPicPane = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                Graphics2D g2D = (Graphics2D) g;

                                g2D.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                                g2D.fillRect(0, 0, getWidth(), getHeight());

                                g2D.setColor(new Color(0.0f, 0.0f, 0.0f, 0.25f));
                                g2D.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 9, 9);

                                g2D.setColor(Color.GRAY);
                                g2D.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 9, 9);
                                g2D.drawImage((BufferedImage) cache.get(String.valueOf(newUserConf.getAvatarIndex() + 1)),
                                        6, 6,
                                        getWidth() - 12, getHeight() - 12,
                                        NewUserForm.this);
                            }

                            {
                                setOpaque(false);
                                setPreferredSize(new Dimension(256, 256)); //cache.get("0")
                            }
                        };

                        add(avatarList, BorderLayout.CENTER);
                        add(avatarPicPane, BorderLayout.EAST);
                    }
                };

                JPanel nameAgeSexPane = new JPanel(new BorderLayout(3, 3)) {
                    {
                        setOpaque(false);

                        JPanel nameAgePane = new JPanel(new BorderLayout(3, 3)) {
                            {
                                setOpaque(false);

                                nameField = new JTextField() {
                                    {
                                        setFont(Registry.f0);
                                        setOpaque(false);
                                        setForeground(Color.WHITE);
                                        setHorizontalAlignment(CENTER);
                                        setText(newUserConf.getUserName());
                                        setBorder(BorderFactory.createCompoundBorder(
                                                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), "Имя:", 1, 0, Registry.f2, Color.BLACK),
                                                new EmptyBorder(-3, 0, 3, 0)
                                        ));
                                    }
                                };

                                ageField = new JTextField(6) {
                                    {
                                        setFont(Registry.f0);
                                        setOpaque(false);
                                        setForeground(Color.WHITE);
                                        setHorizontalAlignment(CENTER);
                                        setText(newUserConf.getUserAge() + "");
                                        setBorder(BorderFactory.createCompoundBorder(
                                                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), "Возраст:", 1, 0, Registry.f2, Color.BLACK),
                                                new EmptyBorder(-3, 0, 3, 0)
                                        ));
                                    }
                                };

                                add(nameField, BorderLayout.CENTER);
                                add(ageField, BorderLayout.EAST);
                            }
                        };

                        JPanel midSexPane = new JPanel(new GridLayout(1, 2)) {
                            {
                                setOpaque(false);
                                setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true),
                                                "Пол:", 1, 0, Registry.f2, Color.BLACK),
                                        new EmptyBorder(-3, 0, 3, 0)
                                ));

                                ButtonGroup bg = new ButtonGroup();

                                maleBox = new JCheckBox("Парень") {
                                    {
                                        setName("mailCBox");
                                        setOpaque(false);
                                        setFont(Registry.f0);
                                        setHorizontalAlignment(0);
                                        setFocusPainted(false);
                                        setForeground(Color.CYAN);
                                        setSelected(newUserConf.getUserSex() == UserConf.USER_SEX.MALE);
                                        addItemListener(NewUserForm.this);
                                    }
                                };
                                femaBox = new JCheckBox("Девушка") {
                                    {
                                        setName("femaCBox");
                                        setOpaque(false);
                                        setFont(Registry.f0);
                                        setHorizontalAlignment(0);
                                        setFocusPainted(false);
                                        setForeground(Color.MAGENTA.brighter());
                                        setSelected(newUserConf.getUserSex() == UserConf.USER_SEX.FEMALE);
                                        addItemListener(NewUserForm.this);
                                    }
                                };

                                bg.add(maleBox);
                                bg.add(femaBox);

                                add(maleBox);
                                add(femaBox);
                            }
                        };

                        add(nameAgePane, BorderLayout.CENTER);
                        add(midSexPane, BorderLayout.SOUTH);
                    }
                };

                add(avatarPane, BorderLayout.CENTER);
                add(nameAgeSexPane, BorderLayout.SOUTH);
            }
        };

        JPanel downOkPane = new JPanel(new BorderLayout()) {
            {
                setOpaque(false);
                setBorder(new EmptyBorder(0, 270, 3, 3));

                JButton okButton = new JButton("Готово") {
                    {
                        setFocusPainted(false);
                        setBackground(Color.BLACK);
                        setForeground(Color.ORANGE);
                        setFont(Registry.f1);
                        setPreferredSize(new Dimension(0, 42));
                        setActionCommand("finish");
                        addActionListener(NewUserForm.this);
                    }
                };

                add(okButton);
            }
        };

        add(midAreasPane, BorderLayout.CENTER);
        add(downOkPane, BorderLayout.SOUTH);

        inAc();

        pack();
        setLocationRelativeTo(null);

        setModalityType(ModalityType.APPLICATION_MODAL);
        setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
        setModal(true);
        setVisible(true);
    }

    private void preInit() {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setPreferredSize(new Dimension(this.WIDTH, this.HEIGHT));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(Cursors.OtherCursor.get());
        setIgnoreRepaint(true);
        getRootPane().setBorder(new EmptyBorder(45, 12, 15, 12));
        getContentPane().setLayout(new BorderLayout(6, 6));
    }

    private void prepareNewUserConfig() {
        newUserConf = new UserConf() {
            {
                setUserName("newEmptyUser");
                setUserSex(UserConf.USER_SEX.MALE);
                setAvatarIndex(0);
                setUserAge(14);
                setQuality(FoxRender.RENDER.MED);
                setFullScreen(false);
                setAutoSaveOn(true);
                setAutoSkipping(false);
                setVoiceMuted(false);
                setBackgMuted(false);
                setSoundMuted(false);
                setMusicMuted(false);
                setBackgVolume(100);
                setMusicVolume(100);
                setSoundVolume(100);
                setVoiceVolume(100);
                setCycleCount(0);
            }
        };
    }

    private void inAc() {
        InputAction.add("form", NewUserForm.this);
        InputAction.set("form", "cancel", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateUser()) {
                    updateUserConfigAndClose();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("finish") && validateUser()) {
            updateUserConfigAndClose();
        }
    }

    private boolean validateUser() {
        if (nameField.getText().isBlank()) {
            new FOptionPane("Ошибка:", "Не введен ник персонажа!");
            return false;
        } else if (Registry.configuration.getLastUserHash() == nameField.getText().hashCode()) {
            new FOptionPane("Ошибка:", "Данное имя не доступно!");
            return false;
        } else if (!maleBox.isSelected() && !femaBox.isSelected()) {
            new FOptionPane("Ошибка:", "Не выбран пол персонажа!");
            return false;
        } else if (ageField.getText().isBlank()) {
            new FOptionPane("Ошибка:", "Не указан возраст!");
            return false;
        } else if (Integer.parseInt(ageField.getText().trim()) <= 0 || Integer.parseInt(ageField.getText().trim()) > 120) {
            ageField.setText("14");
        }
        if (Integer.parseInt(ageField.getText().trim()) < 18) {
            new FOptionPane("Внимание:", "Для данного возраста будет скрыта часть контента.");
        }
        return true;
    }

    private void updateUserConfigAndClose() {
        newUserConf.setUserName(nameField.getText().trim());
        newUserConf.setUserAge(Integer.parseInt(ageField.getText().trim()));
        dispose();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            int memIndex = avatarList.getSelectedIndex(); // 0-3 male; 4-7 fema
            if (((JCheckBox) e.getItem()).getName().equals("mailCBox")) {
                newUserConf.setUserSex(UserConf.USER_SEX.MALE);
                newUserConf.setAvatarIndex(memIndex);
            } else if (((JCheckBox) e.getItem()).getName().equals("femaCBox")) {
                newUserConf.setUserSex(UserConf.USER_SEX.FEMALE);
                newUserConf.setAvatarIndex(memIndex + 4);
            }
            avatarPicPane.repaint();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {return;}

        newUserConf.setAvatarIndex(newUserConf.getUserSex() == UserConf.USER_SEX.MALE ? avatarList.getSelectedIndex() : avatarList.getSelectedIndex() + 4);
        avatarPicPane.repaint();
    }

    public UserConf get() {
        Out.Print(MainClass.class, Out.LEVEL.INFO, "Приветствуем игрока " + newUserConf.getUserName() + "!");
        return newUserConf;
    }
}