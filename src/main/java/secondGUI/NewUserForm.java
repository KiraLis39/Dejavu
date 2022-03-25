package secondGUI;

import components.FOptionPane;
import configurations.UserConf;
import door.MainClass;
import fox.FoxFontBuilder;
import fox.InputAction;
import interfaces.Cached;
import registry.Registry;
import tools.Cursors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static registry.Registry.userConf;

public class NewUserForm extends JDialog implements Cached, ListSelectionListener, ActionListener, ChangeListener {
    private final int WIDTH = 500, HEIGHT = 480;
    private JTextField nameField, ageField;
    private JCheckBox maleBox, femaBox;

    private JList<String> avatarList;
    private JPanel avatarPicPane;

    public NewUserForm() {
        preInit();

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
                                g2D.drawImage((BufferedImage) cache.get(userConf.getAvatarIndex() + ""),
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
                                        setText(userConf.getUserName());
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
                                        setText(userConf.getUserAge() + "");
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
                                        setSelected(userConf.getUserSex() == UserConf.USER_SEX.MALE);
                                        addChangeListener(NewUserForm.this);
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
                                        setSelected(userConf.getUserSex() == UserConf.USER_SEX.FEMALE);
                                        addChangeListener(NewUserForm.this);
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

        UserConf.USER_SEX gender = userConf.getUserSex();
        avatarList.setSelectedIndex(gender == UserConf.USER_SEX.MALE ? userConf.getAvatarIndex() + 1 : userConf.getAvatarIndex() + 5);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setColor(Color.DARK_GRAY);
        g2D.fillRoundRect(0, 0, WIDTH, HEIGHT, 30, 30);

        g2D.setStroke(new BasicStroke(1.2f));
        g2D.setColor(Color.BLACK);
        g2D.drawRoundRect(4, 6, WIDTH - 11, HEIGHT - 9, 25, 25);
        g2D.setColor(Color.GRAY);
        g2D.drawRoundRect(5, 5, WIDTH - 10, HEIGHT - 10, 25, 25);

        g2D.setFont(Registry.f0);
        g2D.setColor(Color.BLACK);
        g2D.drawString("Настройка персонажа:", (int) (WIDTH / 2 - FoxFontBuilder.getStringBounds(g2D, "Настройка персонажа:").getWidth() / 2D) - 1, 41);
        g2D.setColor(Color.ORANGE);
        g2D.drawString("Настройка персонажа:", (int) (WIDTH / 2 - FoxFontBuilder.getStringBounds(g2D, "Настройка персонажа:").getWidth() / 2D), 40);

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

    private void inAc() {
        InputAction.add("form", NewUserForm.this);
        InputAction.set("form", "cancel", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        UserConf.USER_SEX sex = userConf.getUserSex();
        userConf.setAvatarIndex(sex == UserConf.USER_SEX.FEMALE ? avatarList.getSelectedIndex() + 1 : avatarList.getSelectedIndex() + 5);
        avatarPicPane.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("finish")) {
            if (nameField.getText().equals("")) {
                new FOptionPane("Ошибка:", "Не введен ник персонажа!");
            } else if (!maleBox.isSelected() && !femaBox.isSelected()) {
                new FOptionPane("Ошибка:", "Не выбран пол персонажа!");
            } else if (userConf.getAvatarIndex() == 0) {
                new FOptionPane("Ошибка:", "Не выбран аватар!");
            } else {
                if (userConf.getUserName() == null || !userConf.getUserName().equals(nameField.getText().trim())) {
                    try {
                        MainClass.createNewUser(nameField.getText().trim(),
                                maleBox.isSelected() ? UserConf.USER_SEX.MALE : UserConf.USER_SEX.FEMALE,
                                Integer.parseInt(ageField.getText()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                dispose();
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JCheckBox) {
            if (((JCheckBox) e.getSource()).getName().equals("mailCBox")) {
                userConf.setUserSex(UserConf.USER_SEX.MALE);
            } else {
                userConf.setUserSex(UserConf.USER_SEX.FEMALE);
            }
        }
    }
}