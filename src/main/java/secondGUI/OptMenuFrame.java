package secondGUI;

import fox.FoxCursor;
import fox.FoxFontBuilder;
import fox.InputAction;
import fox.Out;
import fox.Out.LEVEL;
import fox.player.VolumeConverter;
import interfaces.Cached;
import registry.Registry;
import render.FoxRender;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import static registry.Registry.*;

public class OptMenuFrame extends JDialog implements ChangeListener, MouseMotionListener, MouseListener, Cached {
    private final int WIDTH = 400, HEIGHT = 600;
    private final Double widthPercent = WIDTH / 100D, heightPercent = HEIGHT / 100D, horizontalCenter = WIDTH / 2D;
    private final int[] scrollsSize = new int[]{(int) (widthPercent * 90D), (int) (heightPercent * 10D)};
    private final String stringValueSound = "Заглушить звук:";
    private final String stringValueMusic = "Заглушить музыку:";
    private final String stringValueBackg = "Заглушить эффекты:";
    private final String stringValueVoice = "Заглушить голоса:";
    private final String stringFullscreen = "Полный экран:";
    private final String stringAutoSaving = "Автосохранение:";
    private final String stringUseMods = "Искать моды:";
    private final String stringAutoSkipping = "Автопрокрутка:";
    private final Rectangle musicMuteRect;
    private final Rectangle soundMuteRect;
    private final Rectangle backgMuteRect;
    private final Rectangle voiceMuteRect;
    private final Rectangle downBackFonRect;
    private final int[] polygonsDot;
    private VolatileImage baseBuffer;
    private Boolean isSoundMuteOver = false, isMusicMuteOver = false, isBackgMuteOver = false, isVoiceMuteOver = false,
            isFullscreenOver = false, isModEnabledOver = false, isAutoSaveOver = false, isAutoSkippingOver = false;
    private JSlider volumeOfMusicSlider, volumeOfSoundSlider, volumeOfBackgSlider, volumeOfVoiceSlider;
    private Point mouseNow, titlePoint, musTitlePoint, soundTitlePoint, backgTitlePoint, voiceTitlePoint,
            down0Point, down1Point, down2Point, down3Point, downChecker0, downChecker1, downChecker2, downChecker3;
    private int sChCount = 3;

    @Override
    public void paint(Graphics g) {
        if (baseBuffer == null) {
            baseBuffer = getGraphicsConfiguration().createCompatibleVolatileImage(WIDTH, HEIGHT, VolatileImage.TRANSLUCENT);
            reloadBaseBuffer();
        }

        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(baseBuffer, 0, 0, OptMenuFrame.this);

        if (volumeOfSoundSlider != null) {
            volumeOfSoundSlider.repaint();
            volumeOfMusicSlider.repaint();
            volumeOfBackgSlider.repaint();
            volumeOfVoiceSlider.repaint();
        }

        g2D.dispose();
    }

    private void reloadBaseBuffer() {
        Graphics2D g2D = baseBuffer.createGraphics();
        FoxRender.setRender(g2D, FoxRender.RENDER.MED);

        g2D.setColor(Color.DARK_GRAY);
        g2D.fillRect(0, 0, getWidth(), getHeight());
        g2D.setColor(Color.BLACK);
        g2D.drawRect(5, 10, getWidth() - 10, getHeight() - 20);
//		g2D.drawImage((BufferedImage) cache.get("picAurora"), 0, 0, getWidth(), getHeight(), OptMenuFrame.this);

        g2D.setFont(Registry.f3);
        if (titlePoint == null) {
            titlePoint = new Point((int) (horizontalCenter - FoxFontBuilder.getStringBounds(g2D, "Настройки игры:").getWidth() / 2),
                    (int) (heightPercent * 6D));
            soundTitlePoint = new Point((int) (widthPercent * 5D), (int) (heightPercent * 14D));
            musTitlePoint = new Point((int) (widthPercent * 5D), (int) (heightPercent * 29D));
            backgTitlePoint = new Point((int) (widthPercent * 5D), (int) (heightPercent * 43D));
            voiceTitlePoint = new Point((int) (widthPercent * 5D), (int) (heightPercent * 57D));
        }
        drawUpTitle(g2D);

        g2D.setFont(Registry.f9);
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

        TextLayout tLayout = new TextLayout("Настройки игры:", Registry.f3, g2D.getFontRenderContext());
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

        if (userConf.isSoundMuted()) {
            if (isSoundMuteOver) {
                g2D.setColor(Color.orange);
            } else {
                g2D.setColor(Color.GREEN);
            }
            g2D.drawRoundRect(soundMuteRect.x, soundMuteRect.y, soundMuteRect.width, soundMuteRect.height, 6, 6);
            g2D.fillPolygon(new Polygon(polygonsDot,
                    new int[]{(int) (heightPercent * 12D), (int) (heightPercent * 14D), (int) (heightPercent * 10D), (int) (heightPercent * 13D)}, 4));
        } else {
            if (isSoundMuteOver) {
                g2D.setColor(Color.orange);
            } else {
                g2D.setColor(Color.WHITE);
            }
            g2D.drawRoundRect(soundMuteRect.x, soundMuteRect.y, soundMuteRect.width, soundMuteRect.height, 6, 6);
        }

        if (userConf.isMusicMuted()) {
            if (isMusicMuteOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.GREEN);
            }
            g2D.drawRoundRect(musicMuteRect.x, musicMuteRect.y, musicMuteRect.width, musicMuteRect.height, 6, 6);
            g2D.fillPolygon(new Polygon(polygonsDot,
                    new int[]{(int) (heightPercent * 27D), (int) (heightPercent * 29D), (int) (heightPercent * 25D), (int) (heightPercent * 28D)}, 4));
        } else {
            if (isMusicMuteOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.WHITE);
            }
            g2D.drawRoundRect(musicMuteRect.x, musicMuteRect.y, musicMuteRect.width, musicMuteRect.height, 6, 6);
        }

        if (userConf.isBackgMuted()) {
            if (isBackgMuteOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.GREEN);
            }
            g2D.drawRoundRect(backgMuteRect.x, backgMuteRect.y, backgMuteRect.width, backgMuteRect.height, 6, 6);
            g2D.fillPolygon(new Polygon(polygonsDot,
                    new int[]{(int) (heightPercent * 41D), (int) (heightPercent * 43D), (int) (heightPercent * 39D), (int) (heightPercent * 42D)}, 4));
        } else {
            if (isBackgMuteOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.WHITE);
            }
            g2D.drawRoundRect(backgMuteRect.x, backgMuteRect.y, backgMuteRect.width, backgMuteRect.height, 6, 6);
        }

        if (userConf.isVoiceMuted()) {
            if (isVoiceMuteOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.GREEN);
            }
            g2D.drawRoundRect(voiceMuteRect.x, voiceMuteRect.y, voiceMuteRect.width, voiceMuteRect.height, 6, 6);
            g2D.fillPolygon(new Polygon(polygonsDot,
                    new int[]{(int) (heightPercent * 55D), (int) (heightPercent * 57D), (int) (heightPercent * 53D), (int) (heightPercent * 56D)}, 4));
        } else {
            if (isVoiceMuteOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.WHITE);
            }
            g2D.drawRoundRect(voiceMuteRect.x, voiceMuteRect.y, voiceMuteRect.width, voiceMuteRect.height, 6, 6);
        }
    }

    private void drawDownMenu(Graphics2D g2D) {
        downSettingsPrepare(g2D);

        if (userConf.isFullScreen()) {
            if (isFullscreenOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.GREEN);
            }
            g2D.fillPolygon(new Polygon(new int[]{downChecker0.x, downChecker0.x + 6, downChecker0.x + 9, downChecker0.x + 5},
                    new int[]{downChecker0.y, downChecker0.y + 9, downChecker0.y - 9, downChecker0.y + 3}, 4));
        }

        if (configuration.isUseMods()) {
            if (isModEnabledOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.GREEN);
            }
            g2D.fillPolygon(new Polygon(new int[]{downChecker1.x, downChecker1.x + 6, downChecker1.x + 9, downChecker1.x + 5},
                    new int[]{downChecker1.y, downChecker1.y + 9, downChecker1.y - 9, downChecker1.y + 3}, 4));
        }

        if (userConf.isAutoSaveOn()) {
            if (isAutoSaveOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.GREEN);
            }
            g2D.fillPolygon(new Polygon(new int[]{downChecker2.x, downChecker2.x + 6, downChecker2.x + 9, downChecker2.x + 5},
                    new int[]{downChecker2.y, downChecker2.y + 9, downChecker2.y - 9, downChecker2.y + 3}, 4));
        }

        if (userConf.isAutoSkipping()) {
            if (isAutoSkippingOver) {
                g2D.setColor(Color.ORANGE);
            } else {
                g2D.setColor(Color.GREEN);
            }
            g2D.fillPolygon(new Polygon(new int[]{downChecker3.x, downChecker3.x + 6, downChecker3.x + 9, downChecker3.x + 5},
                    new int[]{downChecker3.y, downChecker3.y + 9, downChecker3.y - 9, downChecker3.y + 3}, 4));
        }
    }

    private void downSettingsPrepare(Graphics2D g2D) {
        g2D.setFont(Registry.f0);

        if (down0Point == null) {
            down0Point = new Point(
                    (int) (WIDTH / 4 - FoxFontBuilder.getStringBounds(g2D, stringFullscreen).getWidth() / 2), (int) (heightPercent * 77D)
            );

            down2Point = new Point(
                    (int) (WIDTH / 4 * 3 - FoxFontBuilder.getStringBounds(g2D, stringUseMods).getWidth() / 2) - 5, (int) (heightPercent * 77D)
            );

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
        g2D.drawString(stringFullscreen, down0Point.x - 2, down0Point.y + 2);
        g2D.drawString(stringAutoSaving, down1Point.x - 2, down1Point.y + 2);
        g2D.drawString(stringUseMods, down2Point.x - 2, down2Point.y + 2);
        g2D.drawString(stringAutoSkipping, down3Point.x - 2, down3Point.y + 2);

        g2D.drawRoundRect(downChecker0.x - 2, downChecker0.y + 2, soundMuteRect.width, soundMuteRect.height, 6, 6);
        g2D.drawRoundRect(downChecker1.x - 2, downChecker1.y + 2, musicMuteRect.width, musicMuteRect.height, 6, 6);
        g2D.drawRoundRect(downChecker2.x - 2, downChecker2.y + 2, backgMuteRect.width, backgMuteRect.height, 6, 6);
        g2D.drawRoundRect(downChecker3.x - 2, downChecker3.y + 2, voiceMuteRect.width, voiceMuteRect.height, 6, 6);

        g2D.setColor(Color.WHITE);
        g2D.drawString(stringFullscreen, down0Point.x, down0Point.y);
        g2D.drawString(stringAutoSaving, down1Point.x, down1Point.y);
        g2D.drawString(stringUseMods, down2Point.x, down2Point.y);
        g2D.drawString(stringAutoSkipping, down3Point.x, down3Point.y);
    }

    public OptMenuFrame(JFrame parent, GraphicsConfiguration gConfig) {
        super(parent, "OptMenuFrame", true, gConfig);
        Out.Print(OptMenuFrame.class, LEVEL.INFO, "Вход в опции!");

        setLayout(null);
        setUndecorated(true);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curOtherCursor"), "otherCursor"));

        addInAction();

        polygonsDot = new int[]{(int) (widthPercent * 84D), (int) (widthPercent * 87D), (int) (widthPercent * 89D), (int) (widthPercent * 87D)};

        soundMuteRect = new Rectangle((int) (widthPercent * 85D), (int) (heightPercent * 12D), 15, 15);
        musicMuteRect = new Rectangle((int) (widthPercent * 85D), (int) (heightPercent * 27D), 15, 15);
        backgMuteRect = new Rectangle((int) (widthPercent * 85D), (int) (heightPercent * 41D), 15, 15);
        voiceMuteRect = new Rectangle((int) (widthPercent * 85D), (int) (heightPercent * 55D), 15, 15);

        downBackFonRect = new Rectangle((int) (widthPercent * 3D), (int) (heightPercent * 71D), (int) (widthPercent * 94D), (int) (heightPercent * 26D));

        buildVolumeSliders();

        addMouseListener(this);
        addMouseMotionListener(this);

        pack();
        setLocationRelativeTo(null);
        Out.Print(OptMenuFrame.class, LEVEL.DEBUG, "Окно опций OptMenuFrame готово к отображению.");
        setVisible(true);
    }

    private void addInAction() {
        InputAction.add("options", this);
        InputAction.set("options", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAndClose();
            }
        });
    }

    private void buildVolumeSliders() {
        Out.Print(OptMenuFrame.class, LEVEL.INFO, "Построение слайдеров громкости....");

        volumeOfSoundSlider = getSlider("volumeOfSound", userConf.getSoundVolume());
        volumeOfMusicSlider = getSlider("volumeOfMusic", userConf.getMusicVolume());
        volumeOfBackgSlider = getSlider("volumeOfBackg", userConf.getBackgVolume());
        volumeOfVoiceSlider = getSlider("volumeOfVoice", userConf.getVoiceVolume());

        add(volumeOfSoundSlider);
        add(volumeOfMusicSlider);
        add(volumeOfBackgSlider);
        add(volumeOfVoiceSlider);
    }

    private JSlider getSlider(String name, int volume) {
        return new JSlider(0, 100, volume) {
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());

                super.paintComponent(g);
            }

            {
                setName(name);
                setBackground(new Color(0,0,0,0));
                setForeground(Color.ORANGE.brighter());
                setPaintLabels(true);
                setPaintTicks(true);
//                setSnapToTicks(true);
                setMajorTickSpacing(25);
                setMinorTickSpacing(5);
                addChangeListener(OptMenuFrame.this);
            }
        };
    }

    private void saveAndClose() {
        Out.Print(OptMenuFrame.class, LEVEL.ACCENT, "Закрытие окна опций...");

        setModal(false);
        dispose();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (((JComponent) e.getSource()).getName().equals("volumeOfSound")) {
            sChCount--;
            if (sChCount == 0) {
                userConf.setSoundVolume(volumeOfSoundSlider.getValue());
                soundPlayer.setVolume(userConf.getSoundVolume());
            }
        }

        if (((JComponent) e.getSource()).getName().equals("volumeOfMusic")) {
            sChCount--;
            if (sChCount == 0) {
                userConf.setMusicVolume(volumeOfMusicSlider.getValue());
                musicPlayer.setVolume(userConf.getMusicVolume());
            }
        }

        if (((JComponent) e.getSource()).getName().equals("volumeOfBackg")) {
            sChCount--;
            if (sChCount == 0) {
                userConf.setBackgVolume(volumeOfBackgSlider.getValue());
                backgPlayer.setVolume(userConf.getBackgVolume());
            }
        }

        if (((JComponent) e.getSource()).getName().equals("volumeOfVoice")) {
            sChCount--;
            if (sChCount == 0) {
                userConf.setVoiceVolume(volumeOfVoiceSlider.getValue());
//                voicePlayer.setVolume(VolumeConverter.volumePercentToGain(userConf.getVoiceVolume()));
            }
        }

        if (sChCount == 0) {
            sChCount = 3;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseNow = e.getPoint();

        isMusicMuteOver = musicMuteRect.contains(mouseNow);
        isSoundMuteOver = soundMuteRect.contains(mouseNow);
        isBackgMuteOver = backgMuteRect.contains(mouseNow);
        isVoiceMuteOver = voiceMuteRect.contains(mouseNow);

        if (downBackFonRect.contains(mouseNow)) {
            isFullscreenOver = new Rectangle(downChecker0.x, downChecker0.y, 25, 25).contains(mouseNow);
            isModEnabledOver = new Rectangle(downChecker1.x, downChecker1.y, 25, 25).contains(mouseNow);
            isAutoSaveOver = new Rectangle(downChecker2.x, downChecker2.y, 25, 25).contains(mouseNow);
            isAutoSkippingOver = new Rectangle(downChecker3.x, downChecker3.y, 25, 25).contains(mouseNow);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        reloadBaseBuffer();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isSoundMuteOver) {
            userConf.setSoundMuted(!userConf.isSoundMuted());
            soundPlayer.mute(userConf.isSoundMuted());
        }

        if (isMusicMuteOver) {
            userConf.setMusicMuted(!userConf.isMusicMuted());
            musicPlayer.mute(userConf.isMusicMuted());
        }

        if (isBackgMuteOver) {
            userConf.setBackgMuted(!userConf.isBackgMuted());
            backgPlayer.mute(userConf.isBackgMuted());
        }

        if (isVoiceMuteOver) {
            userConf.setVoiceMuted(!userConf.isVoiceMuted());
//            voicePlayer.voiceMute(userConf.isVoiceMuted());
        }

        if (isFullscreenOver) {
            userConf.setFullScreen(!userConf.isFullScreen());
        }

        if (isModEnabledOver) {
            configuration.setUseMods(!configuration.isUseMods());
        }

        if (isAutoSaveOver) {
            userConf.setAutoSaveOn(!userConf.isAutoSaveOn());
        }

        if (isAutoSkippingOver) {
            userConf.setAutoSkipping(!userConf.isAutoSkipping());
        }

        soundPlayer.play("check", false);
        reloadBaseBuffer();
        repaint();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }
}