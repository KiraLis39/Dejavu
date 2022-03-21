package secondGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;

import adds.IOM;
import adds.InputAction;
import adds.Out;
import adds.Out.LEVEL;
import builders.FoxFontBuilder;
import builders.ResManager;
import games.FoxCursor;
import resourses.IOMs;


@SuppressWarnings("serial")
public class GalleryFrame extends JDialog implements MouseListener, MouseMotionListener {
	private Dimension toolk = Toolkit.getDefaultToolkit().getScreenSize();

	private int linePicturesCount, colPicturesCount, picWidth, picHeight;
	private int shiftRightLeft = 5, shiftDownUp = 5, shiftHorizontal, shiftVertical;
	private Point2D mouseWasOnScreen, frameWas;
	private boolean isFullscreen = false;
	
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(ResManager.getBImage("picGallery"), 0, 0, GalleryFrame.this.getWidth(), GalleryFrame.this.getHeight(), GalleryFrame.this);
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
//		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		
		g2D = (Graphics2D) g;
		g2D.setFont(FoxFontBuilder.setFoxFont(6, 16, false));
		g2D.setColor(Color.ORANGE);
		g2D.drawString("ESC для возврата", (int) (GalleryFrame.this.getWidth() * 0.03D), (int) (GalleryFrame.this.getHeight() * 0.97D));
		g2D.drawString("F для переключения вида", (int) (GalleryFrame.this.getWidth() * 0.75D), (int) (GalleryFrame.this.getHeight() * 0.97D));
		
		if (linePicturesCount > 1) {
			for (int i = 0; i < linePicturesCount; i++) {
				for (int j = 0; j < colPicturesCount; j++) {
					g2D.setColor(Color.GRAY);
					g2D.fillRect(
							shiftHorizontal + shiftRightLeft * (j + 1) + picWidth * j, shiftVertical + shiftDownUp * (i + 1) + picHeight * i, 
							picWidth - colPicturesCount, picHeight - linePicturesCount);
					
					g2D.setColor(Color.LIGHT_GRAY);
					g2D.drawRect(
							shiftHorizontal + shiftRightLeft * (j + 1) + picWidth * j, shiftVertical + shiftDownUp * (i + 1) + picHeight * i, 
							picWidth - colPicturesCount, picHeight - linePicturesCount);
				}
			}
		}
		
		g2D.dispose();
	}

	public GalleryFrame(JFrame parent) {
		super(parent, true);
		Out.Print(GalleryFrame.class, LEVEL.INFO, "Вход в Галерею.");

		setUndecorated(true);
		setLayout(new BorderLayout());
		setCursor(FoxCursor.createCursor("curGaleryCursor"));
		if (IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.FULLSCREEN)) {setPreferredSize(toolk.getSize());
		} else {setPreferredSize(new Dimension(950, 600));}
//		Media.stopBackg();
//		Media.playMusic("musGalleryTheme");

		addMouseListener(this);
		addMouseMotionListener(this);
		
		pack();
		setLocationRelativeTo(null);
		
		buildMiniatures();
		
		InputAction.add("galery", this);
		InputAction.set("galery", "close", KeyEvent.VK_ESCAPE	, 0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
//				new MainMenu();
			}
		});
		InputAction.set("galery", "fullscreen", KeyEvent.VK_F	, 0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GalleryFrame.this.getSize().getWidth() == toolk.getSize().getWidth()) {setSize(new Dimension(950, 600));
				} else {setSize(toolk.getSize());}
				buildMiniatures();
				GalleryFrame.this.setLocationRelativeTo(null);
				isFullscreen = !isFullscreen;
			}
		});
	
		setVisible(true);
	}

	private void buildMiniatures() {
		picWidth = (GalleryFrame.this.getWidth() - shiftRightLeft) / 4 - 10;
		picHeight = (GalleryFrame.this.getHeight() - shiftDownUp) / 3 - 25;
		
		colPicturesCount = (GalleryFrame.this.getWidth() - shiftRightLeft * 4) / picWidth;
		linePicturesCount = (GalleryFrame.this.getHeight() - shiftDownUp * 3) / picHeight;
		
		shiftHorizontal = (GalleryFrame.this.getWidth() - ((picWidth + shiftRightLeft) * 4)) / 2;
		shiftVertical = (GalleryFrame.this.getHeight() - ((picHeight + shiftDownUp) * 3)) / 2 - 10;
		
		Out.Print(GalleryFrame.class, LEVEL.INFO, "Galery may has " + (linePicturesCount * colPicturesCount) + " miniatures.");
	}


	@Override
	public void mousePressed(MouseEvent e) {
		mouseWasOnScreen = new Point(e.getXOnScreen(), e.getYOnScreen());
		frameWas = getLocation();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (isFullscreen) {return;}
		try {
			setLocation(
				(int) (frameWas.getX() - (mouseWasOnScreen.getX() - e.getXOnScreen())), 
				(int) (frameWas.getY() - (mouseWasOnScreen.getY() - e.getYOnScreen())));
		} catch (Exception e2) {/* IGNORE MOVING */}
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {}
}