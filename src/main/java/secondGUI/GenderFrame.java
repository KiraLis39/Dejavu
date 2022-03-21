package secondGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import GUI.GameFrame;
import adds.IOM;
import adds.Out;
import adds.Out.LEVEL;
import builders.FoxFontBuilder;
import builders.ResManager;
import games.FoxCursor;
import mods.ModsLoader;
import resourses.IOMs;
import resourses.Registry;


@SuppressWarnings("serial")
public class GenderFrame extends JFrame implements ListSelectionListener, MouseListener, MouseMotionListener {
	private final int WIDTH = 600, HEIGHT = 400;
	
	private BufferedImage baseBuffer;	
	private Rectangle okButtonRect, avatarRect;
	private Boolean okButtonOver = false, okButtonPressed = false;
	private JList<String> avatarList;
	private Point mouseNow;
	
	
	@Override
 	public void paint(Graphics g) {
		if (baseBuffer == null) {reloadBuffer();}
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(baseBuffer, 0, 0, GenderFrame.this);
		g2D.dispose();
		
		if (avatarList != null) {avatarList.repaint();}
	}
	
	private void reloadBuffer() {
		baseBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2D = (Graphics2D) baseBuffer.getGraphics();
		
		g2D.drawImage(ResManager.getBImage("picGender"), 0, 0, WIDTH, HEIGHT, GenderFrame.this);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
//		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

		g2D.setColor(Color.ORANGE);
		g2D.setFont(Registry.f5);
		g2D.drawString("Настройка персонажа:", (int) (WIDTH / 2 - FoxFontBuilder.getStringBounds(g2D, "Настройка персонажа:").getWidth() / 2D), (int) (HEIGHT * 0.12D));
		
		g2D.setFont(Registry.f4);
		
		if (okButtonOver) {g2D.setColor(Color.DARK_GRAY);} else {g2D.setColor(Color.GRAY);}
		g2D.fillRoundRect(okButtonRect.x, okButtonRect.y, okButtonRect.width, okButtonRect.height, 25, 25);
		g2D.setColor(Color.BLUE.darker());
		g2D.fillRoundRect(okButtonRect.x + 5, okButtonRect.y + 5, okButtonRect.width - 10, okButtonRect.height - 10, 25, 25);
		if (okButtonOver) {g2D.setColor(Color.WHITE);} else {g2D.setColor(Color.GRAY);}
		g2D.drawString("OK", (int) (okButtonRect.getCenterX() - FoxFontBuilder.getStringBounds(g2D, "OK").getWidth() / 2D - 1.5D), (int) (okButtonRect.getCenterY() + 6.5D));
		g2D.setColor(Color.WHITE);
		g2D.drawString("OK", (int) (okButtonRect.getCenterX() - FoxFontBuilder.getStringBounds(g2D, "OK").getWidth() / 2D), (int) (okButtonRect.getCenterY() + 6D));
		
		if (IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.AVATAR_INDEX) == -1) {IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.AVATAR_INDEX, 0);}
		g2D.drawImage(ResManager.getBImage(IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.AVATAR_INDEX)), 
				avatarRect.x, avatarRect.y, 
				avatarRect.width, avatarRect.height,
				GenderFrame.this);
		
		g2D.dispose();
		
		repaint();
	}

	public GenderFrame() {
		prepareSizes();
		
		setTitle("Выбор аватара:");
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setCursor(FoxCursor.createCursor("curAnyCursor"));
		setLayout(null);

		avatarList = new JList<String>(new String[] {"Аватар 1", "Аватар 2", "Аватар 3", "Аватар 4"}) {
			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(ResManager.getBImage("picGameMenu"), 0, 0, getWidth(), getHeight(), this);
				super.paintComponent(g);
			}
			
			{
				setFont(Registry.f2);
				setBackground(new Color(0,0,0,0));
				setOpaque(false);
				setForeground(Color.WHITE);
				setSelectionBackground(Color.BLUE.darker());
				setSelectionForeground(Color.ORANGE.brighter());
				addListSelectionListener(GenderFrame.this);
			}
		};
		
		avatarList.setBounds(
				(int) (WIDTH * 0.048D), (int) (HEIGHT * 0.255D), 
				(int) (WIDTH * 0.49D), (int) (HEIGHT * 0.5D));
		
		add(avatarList);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		pack();
		setLocationRelativeTo(null);
		
		setVisible(true);
	}

	private void prepareSizes() {
		okButtonRect = new Rectangle((int) (WIDTH * 0.30D), (int) (HEIGHT * 0.88D), (int) (WIDTH * 0.40D), (int) (HEIGHT * 0.1D));
		avatarRect = new Rectangle((int) (WIDTH * 0.614D), (int) (HEIGHT * 0.30D), (int) (WIDTH * 0.3D), (int) (HEIGHT * 0.40D));
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.AVATAR_INDEX, IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_SEX).equals("fema") ? avatarList.getSelectedIndex() + 1 : avatarList.getSelectedIndex() + 5);
		reloadBuffer();
	}


	@Override
	public void mousePressed(MouseEvent e) {
		if (okButtonOver) {okButtonPressed = true;} else {okButtonPressed = false;}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (okButtonPressed) {
			try {
				if (IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.AVATAR_INDEX) == 0) {
					JOptionPane.showMessageDialog(null, "Не выбран аватар.", "Внимание!", JOptionPane.OK_OPTION);
				} else {
					Out.Print(GenderFrame.class, LEVEL.ACCENT, "\nПроверка разрешения на использование модов...");
					if (IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.USE_MODS)) {new ModsLoader(new File("./mods/"));
					} else {Out.Print(GenderFrame.class, LEVEL.ACCENT, "Моды отключены в опциях. Продолжаем без них...\n");}

					IOM.save(IOM.HEADERS.CONFIG.name());
					
					dispose();
					
					Out.Print(GenderFrame.class, LEVEL.INFO, "Запуск NewGameFrame()...");
					new GameFrame();
				}
			} catch (Exception e2) {e2.printStackTrace();}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseNow = e.getPoint();		
		if (!okButtonRect.contains(mouseNow) && okButtonOver) {okButtonOver = false; reloadBuffer();}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseNow = e.getPoint();		
		if (okButtonRect.contains(mouseNow) && !okButtonOver) {okButtonOver = true; reloadBuffer();}
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
}