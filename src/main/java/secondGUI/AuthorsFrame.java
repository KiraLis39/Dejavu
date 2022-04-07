package secondGUI;

import images.FoxCursor;
import interfaces.Cached;
import registry.Registry;
import utils.FoxFontBuilder;
import utils.InputAction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static fox.Out.LEVEL;
import static fox.Out.Print;
import static registry.Registry.userConf;

public class AuthorsFrame extends JDialog implements Cached {
    private static final Dimension toolk = Toolkit.getDefaultToolkit().getScreenSize();
    private static final String aboutText = """
            Игра создана в 2022 г., является моей первой более-менее серьёзной игрой, написанной на языке Java!
            "Дежавю" полностью придумана, написана, протестирована и оптимизирована мной - KiraLis39.

            Прошу прощения за возможные неудобства или недочеты!
            Об ошибках или с предложениями, пожалуйста, пишите на AngelicaLis39@mail.ru
            СПб, 2015-2022.

            Автор сценария (истории): KiraLis39

            Звук, музыка, эффекты: KiraLis39
            Код, тест, оптимизация: KiraLis39
            Тестировщик и прочее: KiraLis39""";
    private static JTextArea textHelp;

    public AuthorsFrame(JFrame parent, GraphicsConfiguration gConfig) {
        super(parent, "AuthorsFrame", true, gConfig);
        Print(AuthorsFrame.class, LEVEL.INFO, "Вход в AutorsFrame.");
//		Library.mEngineModule.startMusic(new File(Library.musAutorsTheme), true);

        setModal(true);
        setUndecorated(true);
        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curTextCursor"), "textCursor"));
        if (userConf.isFullScreen()) {
            setBackground(Color.BLACK);
            setPreferredSize(toolk.getSize());
        } else {
            setBackground(new Color(0,0,0,0));
            setPreferredSize(new Dimension(600, 500));
        }

        add(new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.drawImage((BufferedImage) cache.get("picAutrs"), 0, 0, getWidth(), getHeight(), null);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
//				g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//				g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//				g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//				g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//				g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

                g2.setFont(Registry.fontName);
                g2.setColor(Color.BLACK);
                g2.drawString("<О создании игры>",
                        (int) (AuthorsFrame.this.getWidth() / 2 - FoxFontBuilder.getStringBounds(g2, "<О создании игры>").getWidth() / 2) - 2, 25 + 2);
                g2.setColor(Color.ORANGE);
                g2.drawString("<О создании игры>",
                        (int) (AuthorsFrame.this.getWidth() / 2 - FoxFontBuilder.getStringBounds(g2, "<О создании игры>").getWidth() / 2), 25);

                g2.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
                g2.fillRoundRect(20, 30, AuthorsFrame.this.getWidth() - 40, AuthorsFrame.this.getHeight() - 60, 10, 10);

                float[] shtrich = {12, 6};
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3, shtrich, 3));
                g2.drawRoundRect(20, 30, AuthorsFrame.this.getWidth() - 40, AuthorsFrame.this.getHeight() - 60, 10, 10);

                if (textHelp != null) {
                    g2 = (Graphics2D) textHelp.getGraphics();
                    g2.setColor(Color.BLACK);
                    g2.drawString("Спасибо! (ESC для возврата в меню)", 20 - 2, AuthorsFrame.this.getHeight() - 70 + 2);
                    g2.setColor(Color.ORANGE.darker());
                    g2.drawString("Спасибо! (ESC для возврата в меню)", 20, AuthorsFrame.this.getHeight() - 70);
                }

//				TextLayout tLayout = new TextLayout("SBP", ffb.setFoxFont(1, 26, true), g2.getFontRenderContext());
//				AffineTransform affTrans = new AffineTransform();
//				affTrans.setToTranslation(270, 220);
//				g2.draw(tLayout.getOutline(affTrans));

                g2.dispose();
            }

            {
                setBorder(new EmptyBorder(30, 10, 30, 10));

                textHelp = new JTextArea() {
                    {
                        setBorder(new EmptyBorder(9, 26, 10, 10));
                        setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                        setForeground(Color.ORANGE);
                        setFont(Registry.f10);
                        setWrapStyleWord(true);
                        setLineWrap(true);
                        setText(aboutText);
                        setEditable(false);
                        setFocusable(false);
                    }
                };

                add(textHelp);
            }
        });

        pack();
        setLocationRelativeTo(null);

        InputAction.add("authors", this);
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_FOCUSED,"authors", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthorsFrame.this.dispose();
            }
        });

        setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        setVisible(true);
    }
}