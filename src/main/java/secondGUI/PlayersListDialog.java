package secondGUI;

import configurations.UserConf;
import configurations.UserSave;
import door.Exit;
import door.MainClass;
import utils.InputAction;
import iom.JIOM;
import registry.Registry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static registry.Registry.backgPlayer;
import static registry.Registry.musicPlayer;

public class PlayersListDialog extends JDialog implements ActionListener {
    private final JFrame owner;
    private JList<UserConfPanel> playersList;

    public PlayersListDialog(JFrame owner) {
        super(owner);
        this.owner = owner;

        setTitle("Смена или создание персонажа:");
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(400, 600));

        JPanel basePane = new JPanel(new BorderLayout(0, 0)) {
            {
                setOpaque(false);

                JPanel centerListPane = new JPanel(new BorderLayout(0, 0)) {
                    {
                        setBackground(Color.PINK.darker());

                        ArrayList<UserConfPanel> players = new ArrayList<>();
                        try {
                            List<Path> users = Files.list(Registry.usersDir).toList();
                            for (Path user : users) {
                                players.add(new UserConfPanel(
                                        JIOM.fileToDto(Paths.get(user + "\\uconf.dto"), UserConf.class),
                                        JIOM.fileToDto(Paths.get(user + "\\save.dto"), UserSave.class)
                                ));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ListModel<UserConfPanel> ucModel = new AbstractListModel<>() {
                            @Override
                            public int getSize() {
                                return players.size();
                            }

                            @Override
                            public UserConfPanel getElementAt(int index) {
                                return players.get(index);
                            }
                        };
                        playersList = new JList<>(ucModel) {
                            {
                                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//                                setVisibleRowCount(-1);
//                                setFixedCellHeight(40);
//                                setSelectedIndex(-1);
                                setCellRenderer(new FoxCellRenderer(96));
                            }
                        };

                        add(playersList);
                    }
                };
                JScrollPane baseScroll = new JScrollPane(centerListPane) {
                    {
//                        doLayout();
                    }
                };

                JPanel downButtonsPane = new JPanel(new BorderLayout(12, 0)) {
                    {
                        setBorder(new EmptyBorder(3, 6, 3, 6));

                        JButton confirmBtn = new JButton("Выбрать") {
                            {
                                setBackground(Color.DARK_GRAY);
                                setForeground(Color.WHITE);
                                setActionCommand("choseSelected");
                                addActionListener(PlayersListDialog.this);
                                setFocusPainted(false);
                            }
                        };

                        JButton createBtn = new JButton("Создать") {
                            {
                                setBackground(Color.DARK_GRAY);
                                setForeground(Color.WHITE);
                                setActionCommand("confirm");
                                addActionListener(PlayersListDialog.this);
                                setFocusPainted(false);
                            }
                        };

                        add(confirmBtn, BorderLayout.CENTER);
                        add(createBtn, BorderLayout.EAST);
                    }
                };

                add(baseScroll, BorderLayout.CENTER);
                add(downButtonsPane, BorderLayout.SOUTH);
            }
        };

        add(basePane);

        inAc();

        pack();
        setModal(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void inAc() {
        InputAction.add("plDialog", PlayersListDialog.this);
        InputAction.set("plDialog", "cancel", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("confirm")) {
            dispose();
            owner.dispose();
            MainClass.regNewbie();
            MainClass.postPlayerInit();
        }
        if (e.getActionCommand().equals("choseSelected")) {
            UserConf userWas = Registry.userConf;
            UserConf selectedPlayer = playersList.getSelectedValue().getConfig();
            dispose();
            if (!selectedPlayer.equals(Registry.userConf)) {
                try {
                    owner.dispose();
                    musicPlayer.stop();
                    backgPlayer.stop();
                    MainClass.loadUser(selectedPlayer);
                    MainClass.postPlayerInit();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    try {
                        MainClass.loadUser(userWas);
                        MainClass.postPlayerInit();
                        owner.setVisible(true);
                    } catch (IOException exc) {
                        exc.printStackTrace();
                        Exit.exit(217, "PlayersListDialog.actionPerformed: Ошибка смены/перезагрузки игрока");
                    }
                }
            }
        }
    }
}
