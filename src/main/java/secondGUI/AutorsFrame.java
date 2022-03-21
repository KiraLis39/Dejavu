package secondGUI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import adds.IOM;
import adds.InputAction;
import adds.Out;
import adds.Out.LEVEL;
import builders.FoxFontBuilder;
import builders.ResManager;
import games.FoxCursor;
import resourses.IOMs;


@SuppressWarnings("serial")
public class AutorsFrame extends JDialog {
	private static Dimension toolk = Toolkit.getDefaultToolkit().getScreenSize();
	private static String aboutText = "\nИгра создана в 2018 г., "
			+ "является моей первой более-менее серьёзной игрой, написанной на языке Java!"
			+ "\n\"Дежавю\" полностью придумана, написана, протестирована и оптимизирована мной - KiraLis39."
			+ "\n\nПрошу прощения за возможные неудобства или недочеты!"
			+ "\nОб ошибках или с предложениями, пожалуйста, пишите на AngelicaLis39@mail.ru"
			+ "\nСПб, 2015-2019."
			+ "\n\nАвтор сценария (истории): KiraLis39"
			+ "\n"
			+ "\nЗвук, музыка, эффекты: KiraLis39"
			+ "\nКод, тест, оптимизация: KiraLis39"
			+ "\nТестировщик и прочее: KiraLis39";
	private static JTextArea textHelp;
	
	
	public AutorsFrame() {
		Out.Print(AutorsFrame.class, LEVEL.INFO, "Вход в AutorsFrame.");
//		Library.mEngineModule.startMusic(new File(Library.musAutorsTheme), true);
		
		setModal(true);
		setUndecorated(true);
		setCursor(FoxCursor.createCursor("curTextCursor"));
		if (IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.FULLSCREEN)) {setPreferredSize(toolk.getSize());
		} else {setPreferredSize(new Dimension(600, 500));}
		
		add(new JPanel(new BorderLayout()) {
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.drawImage(new ImageIcon(ResManager.getBImage("picAutrs")).getImage(), 0, 0, getWidth(), getHeight(), null);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
//				g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//				g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//				g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//				g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//				g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
				
				g2.setFont(FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 22));
				g2.setColor(Color.BLACK);
				g2.drawString("<О создании игры>", 
						(int) (AutorsFrame.this.getWidth() / 2 - FoxFontBuilder.getStringBounds(g2, "<О создании игры>").getWidth() / 2) - 2, 25 + 2);
				g2.setColor(Color.ORANGE);
				g2.drawString("<О создании игры>", 
						(int) (AutorsFrame.this.getWidth() / 2 - FoxFontBuilder.getStringBounds(g2, "<О создании игры>").getWidth() / 2), 25);
				
				g2.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
				g2.fillRoundRect(20, 30, AutorsFrame.this.getWidth() - 40, AutorsFrame.this.getHeight() - 60, 10, 10);
				
				float[] shtrich = {12, 6};
				g2.setColor(Color.YELLOW);
				g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3, shtrich, 3));
				g2.drawRoundRect(20, 30, AutorsFrame.this.getWidth() - 40, AutorsFrame.this.getHeight() - 60, 10, 10);
				
				if (textHelp != null) {
					g2 = (Graphics2D) textHelp.getGraphics();
					g2.setColor(Color.BLACK);
					g2.drawString("Спасибо! (ESC для возврата в меню)", 20 - 2, AutorsFrame.this.getHeight() - 70 + 2);
					g2.setColor(Color.ORANGE.darker());
					g2.drawString("Спасибо! (ESC для возврата в меню)", 20, AutorsFrame.this.getHeight() - 70);
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
						setBorder(new EmptyBorder(0, 20, 10, 10));
						setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
						setForeground(Color.ORANGE);
						setFont(FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.GEORGIA, 19, false));
						setWrapStyleWord(true);
						setLineWrap(true);
						setText(aboutText);
						setEditable(false);
					}
				};

				add(textHelp);
			}
		});
		
		pack();
		setLocationRelativeTo(null);
				
		InputAction.add("autors", this);
		InputAction.set("autors", "close", KeyEvent.VK_ESCAPE	, 0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {AutorsFrame.this.dispose();}
		});
	
		setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
		setVisible(true);
	}
}