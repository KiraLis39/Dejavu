package secondGUI;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adds.IOM;
import adds.InputAction;
import adds.Out;
import adds.Out.LEVEL;
import builders.FoxFontBuilder;
import games.FoxCursor;
import media.Media;


@SuppressWarnings("serial")
public class OptMenuFrame extends JDialog implements ChangeListener, MouseMotionListener, MouseListener {
	private final int WIDTH = 400, HEIGHT = 600;
	private final Double widthPercent = WIDTH / 100D, heightPercent = HEIGHT / 100D, horizontalCenter = WIDTH / 2D;

	private String stringValueSound = "Заглушить звук:", stringValueMusic = "Заглушить музыку:", stringValueBackg = "Заглушить эффекты:", stringValueVoice = "Заглушить голоса:";
	private String stringFullscreen = "Полный экран:", stringAutoSaving = "Автосохранение:", stringUseMods = "Искать моды:", stringAutoSkipping = "Автопрокрутка:";
		
	private BufferedImage baseBuffer;
	
	private Boolean isSoundMute = false, isSoundMuteOver = false, isMusicMute = false, isMusicMuteOver = false, isBackgMute = false, isBackgMuteOver = false, isVoiceMute = false, isVoiceMuteOver = false, 
			isFullscreen = false, isFullscreenOver = false, isModEnabled = false, isModEnabledOver = false, isAutoSave = false, isAutoSaveOver = false, isAutoSkipping = false, isAutoSkippingOver = false;
	private JSlider volumeOfMusicSlider, volumeOfSoundSlider, volumeOfBackgSlider, volumeOfVoiceSlider;
	
	private Point mouseNow, titlePoint, musTitlePoint, soundTitlePoint, backgTitlePoint, voiceTitlePoint, down0Point, down1Point, down2Point, down3Point, downChecker0, downChecker1, downChecker2, downChecker3;
	private Rectangle musicMuteRect, soundMuteRect, backgMuteRect, voiceMuteRect, downBackFonRect;
	
	private int[] polygonsDot;
	private int[] scrollsSize = new int[] {(int) (widthPercent * 90D), (int) (heightPercent * 10D)};

	private Font f0 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.ARIAL, 30, true);
	private Font f1 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CANDARA, 20, true);
	
	
	@Override
	public void paint(Graphics g) {
		if (baseBuffer == null) {
			baseBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			reloadBaseBuffer();
		}
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(baseBuffer, 0, 0, OptMenuFrame.this);
		g2D.dispose();
		
		if (volumeOfSoundSlider != null) {
			volumeOfSoundSlider.repaint();
			volumeOfMusicSlider.repaint();
			volumeOfBackgSlider.repaint();
			volumeOfVoiceSlider.repaint();
		}
	}

	private void reloadBaseBuffer() {
		Graphics2D g2D = baseBuffer.createGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
//		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2D.setFont(f0);
		
		if (titlePoint == null) {
			titlePoint = new Point((int) (horizontalCenter - FoxFontBuilder.getStringBounds(g2D, "Настройки игры:").getWidth() / 2), (int) (heightPercent * 6D));
			soundTitlePoint = new Point((int) (widthPercent * 5D), (int) (heightPercent * 14D));
			musTitlePoint = new Point((int) (widthPercent * 5D), (int) (heightPercent * 29D));
			backgTitlePoint = new Point((int) (widthPercent * 5D), (int) (heightPercent * 43D));
			voiceTitlePoint = new Point((int) (widthPercent * 5D), (int) (heightPercent * 57D));
		}
		
		g2D.setColor(Color.DARK_GRAY);
		g2D.fillRect(0, 0, getWidth(), getHeight());
		g2D.setColor(Color.BLACK);
		g2D.drawRect(5, 10, getWidth() - 10, getHeight() - 20);
//		g2D.drawImage(ResourceManager.getBufferedImage("picAurora"), 0, 0, getWidth(), getHeight(), OptMenuFrame.this);
		
		drawUpTitle(g2D);
		
		g2D.setFont(f1);
		drawDownMenu(g2D);
		drawCenterMenu(g2D);
		drawCheckBox(g2D);
		
		g2D.dispose();
	}

	private void drawUpTitle(Graphics2D g2D) {
//		g2D.setColor(Color.BLACK);
//		g2D.drawString("Настройки игры:", titlePoint.x - 2, titlePoint.y + 2);
		g2D.setColor(Color.WHITE);
//		g2D.drawString("Настройки игры:", titlePoint.x, titlePoint.y);
		
		TextLayout tLayout = new TextLayout("Настройки игры:", f0 , g2D.getFontRenderContext());
		AffineTransform affTrans = new AffineTransform();
		affTrans.setToTranslation(titlePoint.x, titlePoint.y);
		g2D.draw(tLayout.getOutline(affTrans));
	}

	private void drawCenterMenu(Graphics2D g2D) {
		g2D.setColor(Color.BLACK);
		g2D.drawString(stringValueSound, soundTitlePoint.x - 2, soundTitlePoint.y + 2);
		g2D.drawString(stringValueMusic, musTitlePoint.x - 2, musTitlePoint.y + 2);
		g2D.drawString(stringValueBackg, backgTitlePoint.x - 2, backgTitlePoint.y + 2);
		g2D.drawString(stringValueVoice, voiceTitlePoint.x - 2, voiceTitlePoint.y + 2);
		
		g2D.setColor(Color.WHITE);
		g2D.drawString(stringValueSound, soundTitlePoint.x, soundTitlePoint.y);
		g2D.drawString(stringValueMusic, musTitlePoint.x, musTitlePoint.y);
		g2D.drawString(stringValueBackg, backgTitlePoint.x, backgTitlePoint.y);
		g2D.drawString(stringValueVoice, voiceTitlePoint.x, voiceTitlePoint.y);
	}

	private void drawCheckBox(Graphics2D g2D) {
		g2D.setColor(Color.BLACK);
		g2D.drawRoundRect(soundMuteRect.x - 2, soundMuteRect.y + 2, soundMuteRect.width, soundMuteRect.height, 6, 6);
		g2D.drawRoundRect(musicMuteRect.x - 2, musicMuteRect.y + 2, musicMuteRect.width, musicMuteRect.height, 6, 6);
		g2D.drawRoundRect(backgMuteRect.x - 2, backgMuteRect.y + 2, backgMuteRect.width, backgMuteRect.height, 6, 6);
		g2D.drawRoundRect(voiceMuteRect.x - 2, voiceMuteRect.y + 2, voiceMuteRect.width, voiceMuteRect.height, 6, 6);
		
		if (isSoundMute) {
			if (isSoundMuteOver) {g2D.setColor(Color.orange);} else {g2D.setColor(Color.GREEN);}
			g2D.drawRoundRect(soundMuteRect.x, soundMuteRect.y, soundMuteRect.width, soundMuteRect.height, 6, 6);
			g2D.fillPolygon(new Polygon(polygonsDot, 
							new int[] {(int) (heightPercent * 12D), (int) (heightPercent * 14D), (int) (heightPercent * 10D), (int) (heightPercent * 13D)}, 4));
		} else {
			if (isSoundMuteOver) {g2D.setColor(Color.orange);} else {g2D.setColor(Color.WHITE);}
			g2D.drawRoundRect(soundMuteRect.x, soundMuteRect.y, soundMuteRect.width, soundMuteRect.height, 6, 6);
		}		
		
		if (isMusicMute) {
			if (isMusicMuteOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.GREEN);}
			g2D.drawRoundRect(musicMuteRect.x, musicMuteRect.y, musicMuteRect.width, musicMuteRect.height, 6, 6);
			g2D.fillPolygon(new Polygon(polygonsDot, 
							new int[] {(int) (heightPercent * 27D), (int) (heightPercent * 29D), (int) (heightPercent * 25D), (int) (heightPercent * 28D)}, 4));
		} else {
			if (isMusicMuteOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.WHITE);}
			g2D.drawRoundRect(musicMuteRect.x, musicMuteRect.y, musicMuteRect.width, musicMuteRect.height, 6, 6);
		}		
		
		if (isBackgMute) {
			if (isBackgMuteOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.GREEN);}
			g2D.drawRoundRect(backgMuteRect.x, backgMuteRect.y, backgMuteRect.width, backgMuteRect.height, 6, 6);
			g2D.fillPolygon(new Polygon(polygonsDot, 
							new int[] {(int) (heightPercent * 41D), (int) (heightPercent * 43D), (int) (heightPercent * 39D), (int) (heightPercent * 42D)}, 4));
		} else {
			if (isBackgMuteOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.WHITE);}
			g2D.drawRoundRect(backgMuteRect.x, backgMuteRect.y, backgMuteRect.width, backgMuteRect.height, 6, 6);
		}		
		
		if (isVoiceMute) {
			if (isVoiceMuteOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.GREEN);}
			g2D.drawRoundRect(voiceMuteRect.x, voiceMuteRect.y, voiceMuteRect.width, voiceMuteRect.height, 6, 6);
			g2D.fillPolygon(new Polygon(polygonsDot, 
							new int[] {(int) (heightPercent * 55D), (int) (heightPercent * 57D), (int) (heightPercent * 53D), (int) (heightPercent * 56D)}, 4));
		} else {
			if (isVoiceMuteOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.WHITE);}
			g2D.drawRoundRect(voiceMuteRect.x, voiceMuteRect.y, voiceMuteRect.width, voiceMuteRect.height, 6, 6);
		}
	}
	
	private void drawDownMenu(Graphics2D g2D) {
		downSettingsPrepare(g2D);
		
		if (isFullscreen) {
			if (isFullscreenOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.GREEN);}
			g2D.fillPolygon(new Polygon(new int[] {downChecker0.x, downChecker0.x + 6, downChecker0.x + 9, downChecker0.x + 5}, 
							new int[] {downChecker0.y, downChecker0.y + 9, downChecker0.y - 9, downChecker0.y + 3}, 4));
		}
		
		if (isModEnabled) {
			if (isModEnabledOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.GREEN);}
			g2D.fillPolygon(new Polygon(new int[] {downChecker1.x, downChecker1.x + 6, downChecker1.x + 9, downChecker1.x + 5}, 
					new int[] {downChecker1.y, downChecker1.y + 9, downChecker1.y - 9, downChecker1.y + 3}, 4));
		}
		
		if (isAutoSave) {
			if (isAutoSaveOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.GREEN);}
			g2D.fillPolygon(new Polygon(new int[] {downChecker2.x, downChecker2.x + 6, downChecker2.x + 9, downChecker2.x + 5}, 
					new int[] {downChecker2.y, downChecker2.y + 9, downChecker2.y - 9, downChecker2.y + 3}, 4));
		}
		
		if (isAutoSkipping) {
			if (isAutoSkippingOver) {g2D.setColor(Color.ORANGE);} else {g2D.setColor(Color.GREEN);}
			g2D.fillPolygon(new Polygon(new int[] {downChecker3.x, downChecker3.x + 6, downChecker3.x + 9, downChecker3.x + 5}, 
					new int[] {downChecker3.y, downChecker3.y + 9, downChecker3.y - 9, downChecker3.y + 3}, 4));
		}
	}

	private void downSettingsPrepare(Graphics2D g2D) {
		if (down0Point == null) {
			down0Point = new Point((int) (WIDTH / 4 - FoxFontBuilder.getStringBounds(g2D, stringFullscreen).getWidth() / 2), (int) (heightPercent * 77D));
			down2Point = new Point((int) (WIDTH / 4 * 3 - FoxFontBuilder.getStringBounds(g2D, stringUseMods).getWidth() / 2) - 5, (int) (heightPercent * 77D));
			
			down1Point = new Point((int) (WIDTH / 4 - FoxFontBuilder.getStringBounds(g2D, stringAutoSaving).getWidth() / 2), (int) (heightPercent * 89D));
			down3Point = new Point((int) (WIDTH / 4 * 3 - FoxFontBuilder.getStringBounds(g2D, stringAutoSkipping).getWidth() / 2) - 5, (int) (heightPercent * 89D));
			
			downChecker0 = new Point(WIDTH / 4 - 6, (int) (heightPercent * 80D));
			downChecker1 = new Point(WIDTH / 4 * 3 - 12, (int) (heightPercent * 80D));
			downChecker2 = new Point(WIDTH / 4 - 6, (int) (heightPercent * 92D));
			downChecker3 = new Point(WIDTH / 4 * 3 - 12, (int) (heightPercent * 92D));
			
			volumeOfSoundSlider.setSize(scrollsSize[0], scrollsSize[1]);
			volumeOfSoundSlider.setLocation(soundTitlePoint.x, soundTitlePoint.y + 5);
			
			volumeOfMusicSlider.setSize(scrollsSize[0], scrollsSize[1]);
			volumeOfMusicSlider.setLocation(musTitlePoint.x, musTitlePoint.y + 5);
			
			volumeOfBackgSlider.setSize(scrollsSize[0], scrollsSize[1]);
			volumeOfBackgSlider.setLocation(backgTitlePoint.x, backgTitlePoint.y + 5);
			
			volumeOfVoiceSlider.setSize(scrollsSize[0], scrollsSize[1]);
			volumeOfVoiceSlider.setLocation(voiceTitlePoint.x, voiceTitlePoint.y + 5);
		}
		
		g2D.setColor(new Color(0.75f, 0.75f, 1.0f, 0.065f));
		g2D.fillRoundRect(downBackFonRect.x, downBackFonRect.y, downBackFonRect.width, downBackFonRect.height, 9, 9);
		g2D.setColor(new Color(0.8f, 0.8f, 1.0f, 0.65f));
		g2D.drawRoundRect(downBackFonRect.x, downBackFonRect.y, downBackFonRect.width, downBackFonRect.height, 9, 9);
		
		g2D.setColor(Color.BLACK);
		g2D.drawString(stringFullscreen, 		down0Point.x - 2, down0Point.y + 2);
		g2D.drawString(stringAutoSaving, 	down1Point.x - 2, down1Point.y + 2);
		g2D.drawString(stringUseMods, 		down2Point.x - 2, down2Point.y + 2);
		g2D.drawString(stringAutoSkipping, down3Point.x - 2, down3Point.y + 2);
		
		g2D.drawRoundRect(downChecker0.x - 2, downChecker0.y + 2, soundMuteRect.width, soundMuteRect.height, 6, 6);
		g2D.drawRoundRect(downChecker1.x - 2, downChecker1.y + 2, musicMuteRect.width, musicMuteRect.height, 6, 6);
		g2D.drawRoundRect(downChecker2.x - 2, downChecker2.y + 2, backgMuteRect.width, backgMuteRect.height, 6, 6);
		g2D.drawRoundRect(downChecker3.x - 2, downChecker3.y + 2, voiceMuteRect.width, voiceMuteRect.height, 6, 6);
		
		g2D.setColor(Color.WHITE);
		g2D.drawString(stringFullscreen, 		down0Point.x, down0Point.y);
		g2D.drawString(stringAutoSaving, 	down1Point.x, down1Point.y);
		g2D.drawString(stringUseMods, 		down2Point.x, down2Point.y);
		g2D.drawString(stringAutoSkipping, down3Point.x, down3Point.y);
	}

	
	public OptMenuFrame() {
		this(0);
	}
	
	public OptMenuFrame(int code) {
		Out.Print(OptMenuFrame.class, LEVEL.INFO, "Вход в опции!");
		
		setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setCursor(FoxCursor.createCursor("curOtherCursor"));
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setLayout(null);

		polygonsDot = new int[] {(int) (widthPercent * 84D), (int) (widthPercent * 87D), (int) (widthPercent * 89D), (int) (widthPercent * 87D)};
		
		soundMuteRect = new Rectangle((int) (widthPercent * 85D), (int) (heightPercent * 12D), 15, 15);
		musicMuteRect = new Rectangle((int) (widthPercent * 85D), (int) (heightPercent * 27D), 15, 15);
		backgMuteRect = new Rectangle((int) (widthPercent * 85D), (int) (heightPercent * 41D), 15, 15);
		voiceMuteRect = new Rectangle((int) (widthPercent * 85D), (int) (heightPercent * 55D), 15, 15);
		
		downBackFonRect = new Rectangle((int) (widthPercent * 3D), (int) (heightPercent * 71D), (int) (widthPercent * 94D), (int) (heightPercent * 26D));
		
		buildVolumeSliders();		
		checkConditions();
		addInAction();
	
		add(volumeOfSoundSlider);
		add(volumeOfMusicSlider);
		add(volumeOfBackgSlider);
		add(volumeOfVoiceSlider);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		pack();
		setLocationRelativeTo(null);
		
		setModal(true);
		setVisible(true);
		repaint();

		Out.Print(OptMenuFrame.class, LEVEL.INFO, "Окно опций OptMenuFrame отображено успешно.");
	}
	
	private void addInAction() {
		InputAction.add("options", this);
		InputAction.set("options", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {saveAndClose();}
		});
	}

	private void buildVolumeSliders() {
		Out.Print(OptMenuFrame.class, LEVEL.INFO, "Построение слайдеров громкости....");
		
		volumeOfSoundSlider = getSlider("volumeOfSound");
		volumeOfMusicSlider = getSlider("volumeOfMusic");
		volumeOfBackgSlider = getSlider("volumeOfBackg");
		volumeOfVoiceSlider = getSlider("volumeOfVoice");
	}

	private JSlider getSlider(String name) {
		return new JSlider(0, 100) {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
			
			{
				setName(name);
				setForeground(Color.ORANGE.brighter());
				setPaintLabels(true);
				setPaintTicks(true);
				setMajorTickSpacing(50);
				setMinorTickSpacing(2);
				setSnapToTicks(true);
				setOpaque(false);
				setIgnoreRepaint(true);
				addChangeListener(OptMenuFrame.this);
			}			
		};
	}

	private void saveAndClose() {
		Out.Print(OptMenuFrame.class, LEVEL.ACCENT, "Сохранение и закрытие опций....");	
		
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUND_VOL, String.valueOf(volumeOfSoundSlider.getValue() / 100f));
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.MUSIC_VOL, String.valueOf(volumeOfMusicSlider.getValue() / 100f));
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.BACKG_VOL, String.valueOf(volumeOfBackgSlider.getValue() / 100f));
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.VOICE_VOL, String.valueOf(volumeOfVoiceSlider.getValue() / 100f));
		
		IOM.saveAll();

		dispose();
	}
	
	private void checkConditions() {
		Out.Print(OptMenuFrame.class, LEVEL.ACCENT, "Установка опций...");
		
		isSoundMute = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUND_MUTE);
		isMusicMute = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.MUSIC_MUTE);
		isBackgMute = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.BACKG_MUTE);
		isVoiceMute = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.VOICE_MUTE);
		
		isFullscreen = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.FULLSCREEN);
		isAutoSave = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.AUTO_SAVE_ON);
		isAutoSkipping = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.SKIP_READED);
		isModEnabled = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.USE_MODS);
		
		volumeOfMusicSlider.setValue((int) (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.MUSIC_VOL) * 100f));
		volumeOfSoundSlider.setValue((int) (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUND_VOL) * 100f));
		volumeOfBackgSlider.setValue((int) (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.BACKG_VOL) * 100f));
		volumeOfVoiceSlider.setValue((int) (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.VOICE_VOL) * 100f));
		
		Out.Print(OptMenuFrame.class, LEVEL.INFO, "Проверка опций успешно завершена.");
	}

	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (((JComponent)e.getSource()).getName().equals("volumeOfSound")) {
			Media.setSoundVolume(volumeOfSoundSlider.getValue() / 100f);
		}
		
		if (((JComponent)e.getSource()).getName().equals("volumeOfMusic")) {
			Media.setMusicVolume(volumeOfMusicSlider.getValue() / 100f);
		}
		
		if (((JComponent)e.getSource()).getName().equals("volumeOfBackg")) {
			Media.setBackgVolume(volumeOfBackgSlider.getValue() / 100f);
		}
		
		if (((JComponent)e.getSource()).getName().equals("volumeOfVoice")) {
			Media.setVoiceVolume(volumeOfVoiceSlider.getValue() / 100f);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseNow = e.getPoint();
		
		if (musicMuteRect.contains(mouseNow)) {isMusicMuteOver = true;			
		} else {isMusicMuteOver = false;}
		
		if (soundMuteRect.contains(mouseNow)) {isSoundMuteOver = true;
		} else {isSoundMuteOver = false;}
		
		if (backgMuteRect.contains(mouseNow)) {isBackgMuteOver = true;
		} else {isBackgMuteOver = false;}
		
		if (voiceMuteRect.contains(mouseNow)) {isVoiceMuteOver = true;
		} else {isVoiceMuteOver = false;}
		
		if (downBackFonRect.contains(mouseNow)) {
			if (new Rectangle(downChecker0.x, downChecker0.y, 25, 25).contains(mouseNow)) {isFullscreenOver = true;
			} else {isFullscreenOver = false;}
			
			if (new Rectangle(downChecker1.x, downChecker1.y, 25, 25).contains(mouseNow)) {isModEnabledOver = true;
			} else {isModEnabledOver = false;}
			
			if (new Rectangle(downChecker2.x, downChecker2.y, 25, 25).contains(mouseNow)) {isAutoSaveOver = true;
			} else {isAutoSaveOver = false;}
			
			if (new Rectangle(downChecker3.x, downChecker3.y, 25, 25).contains(mouseNow)) {isAutoSkippingOver = true;
			} else {isAutoSkippingOver = false;}
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		reloadBaseBuffer();
		repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (isSoundMuteOver) {
			isSoundMute = !isSoundMute;
			Media.setSoundEnabled(!isSoundMute);
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUND_MUTE, isSoundMute);
		}
		
		if (isMusicMuteOver) {
			if (isMusicMute) {isMusicMute = false;} else {isMusicMute = true;}
			Media.setMusicEnabled(!isMusicMute);
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.MUSIC_MUTE, isMusicMute);
		}
		
		if (isBackgMuteOver) {
			if (isBackgMute) {isBackgMute = false;} else {isBackgMute = true;}
			Media.setBackgEnabled(!isBackgMute);
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.BACKG_MUTE, isBackgMute);
		}
		
		if (isVoiceMuteOver) {
			if (isVoiceMute) {isVoiceMute = false;} else {isVoiceMute = true;}
			Media.setVoiceEnabled(!isVoiceMute);
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.VOICE_MUTE, isVoiceMute);
		}
		
		if (isFullscreenOver) {
			if (isFullscreen) {isFullscreen = false;} else {isFullscreen = true;}
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.FULLSCREEN, isFullscreen);
		}
			
		if (isModEnabledOver) {
			if (isModEnabled) {isModEnabled = false;} else {isModEnabled = true;}
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USE_MODS, isModEnabled);
		}
			
		if (isAutoSaveOver) {
			if (isAutoSave) {isAutoSave = false;} else {isAutoSave = true;}
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.AUTO_SAVE_ON, isAutoSave);
		}
		
		if (isAutoSkippingOver) {
			if (isAutoSkipping) {isAutoSkipping = false;} else {isAutoSkipping = true;}
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.SKIP_READED, isAutoSkipping);
		}

		Media.playSound("check");
		reloadBaseBuffer();
		repaint();
	}

	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}	
	public void mouseDragged(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
}