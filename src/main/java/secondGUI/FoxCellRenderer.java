package secondGUI;

import configurations.UserConf;
import interfaces.Cached;
import registry.Registry;
import render.FoxRender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FoxCellRenderer implements ListCellRenderer<UserConfPanel>, Cached {
    private int cellHeight;

    public FoxCellRenderer(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends UserConfPanel> list, UserConfPanel ucPane, int index, boolean isSelected, boolean cellHasFocus) {
        ucPane.setEnabled(list.isEnabled());
        ucPane.setFont(list.getFont());
        ucPane.setLayout(new BorderLayout(0,0));
        ucPane.setBackground(Color.DARK_GRAY);

        JPanel base = new JPanel(new BorderLayout(0,0)) {
            {
                setOpaque(false);
                setBorder(BorderFactory.createRaisedSoftBevelBorder());

                add(new JPanel() {
                    BufferedImage ico;

                    @Override
                    protected void paintComponent(Graphics g) {
                        if (ico == null) {return;}
                        g.drawImage(ico, 0, 0, getWidth(), getHeight(), null);
                    }

                    {
                        setOpaque(false);
                        setPreferredSize(new Dimension(cellHeight, cellHeight));

                        ico = (BufferedImage) cache.get(String.valueOf(ucPane.getConfig().getAvatarIndex() + 1));
                        if (ico.getWidth() >= cellHeight || ico.getHeight() >= cellHeight) {
                            BufferedImage tmp = new BufferedImage(cellHeight - 3, cellHeight - 3, BufferedImage.TYPE_INT_ARGB);
                            Graphics g = tmp.getGraphics();
                            g.drawImage(ico, 0,0,tmp.getWidth(), tmp.getHeight(), null);
                            g.dispose();
                            ico = tmp;
                        }
                    }
                }, BorderLayout.WEST);

                add(new JPanel(new BorderLayout(0,0)) {
                    {
                        setOpaque(false);
                        setBorder(new EmptyBorder(3, 6, 3, 0));

                        add(new JLabel() {
                            {
                                setText("<html><b>" + ucPane.getConfig().getUserName() + "</b> " + (ucPane.getConfig().getUserSex() == UserConf.USER_SEX.MALE ? "(муж.)" : "(жен.)"));
                                setFont(Registry.f4);
                            }
                        }, BorderLayout.NORTH);
                        add(new JLabel() {
                            {
                                setText("<html>Возраст: " + ucPane.getConfig().getUserAge());
                                setFont(Registry.f7);
                            }
                        }, BorderLayout.CENTER);
                        add(new JLabel() {
                            {
                                setText("<html>Цикл: " + ucPane.getConfig().getCycleCount());
                                setFont(Registry.f8);
                            }
                        }, BorderLayout.SOUTH);
                    }
                }, BorderLayout.CENTER);
            }
        };

        if (isSelected) {
            ucPane.setBackground(Color.GRAY);
            base.setForeground(Color.WHITE);
        } else {
            ucPane.setBackground(list.getBackground());
            base.setForeground(Color.BLACK);
        }

        ucPane.add(base, BorderLayout.CENTER);

        return ucPane;
    }
}
