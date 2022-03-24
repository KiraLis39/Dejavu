package secondGUI;

import fox.FoxCursor;
import fox.InputAction;
import fox.Out.LEVEL;
import interfaces.Cached;
import registry.Registry;
import render.FoxRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static fox.Out.LEVEL.INFO;
import static fox.Out.Print;
import static registry.Registry.*;

public class GalleryFrame extends JDialog implements MouseListener, MouseMotionListener, Cached {
    private final Dimension toolk = Toolkit.getDefaultToolkit().getScreenSize();
    private final int shiftRightLeft = 5;
    private final int shiftDownUp = 5;
    private int linePicturesCount, colPicturesCount, picWidth, picHeight;
    private int shiftHorizontal;
    private int shiftVertical;
    private Point2D mouseWasOnScreen, frameWas;
    private boolean isFullscreen = false;


    public GalleryFrame(JFrame parent) {
        super(parent, true);
        Print(GalleryFrame.class, INFO, "Вход в Галерею.");

        setUndecorated(true);
        setLayout(new BorderLayout());
        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curGalleryCursor"), "galleryCursor"));
        if (userConf.isFullScreen()) {
            setPreferredSize(toolk.getSize());
        } else {
            setPreferredSize(new Dimension(950, 600));
        }
//		Media.stopBackg();
//		Media.playMusic("musGalleryTheme");

        addMouseListener(this);
        addMouseMotionListener(this);

        pack();
        setLocationRelativeTo(null);

        buildMiniatures();

        InputAction.add("gallery", this);
        InputAction.set("gallery", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.play("musMainMenu", true);
                backgPlayer.play("fonKricket", true);
                dispose();
            }
        });
        InputAction.set("gallery", "fullscreen", KeyEvent.VK_F, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (GalleryFrame.this.getSize().getWidth() == toolk.getSize().getWidth()) {
                    setSize(new Dimension(950, 600));
                } else {
                    setSize(toolk.getSize());
                }
                buildMiniatures();
                GalleryFrame.this.setLocationRelativeTo(null);
                isFullscreen = !isFullscreen;
            }
        });

        musicPlayer.play("musGalleryTheme", true);
        backgPlayer.stop();
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        FoxRender.setLowRender(g2D);

        g2D.drawImage(
                (BufferedImage) cache.get("picGallery"),
                0, 0,
                GalleryFrame.this.getWidth(), GalleryFrame.this.getHeight(),
                GalleryFrame.this);

        g2D.setFont(Registry.f0);
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

    private void buildMiniatures() {
        picWidth = (GalleryFrame.this.getWidth() - shiftRightLeft) / 4 - 10;
        picHeight = (GalleryFrame.this.getHeight() - shiftDownUp) / 3 - 25;

        colPicturesCount = (GalleryFrame.this.getWidth() - shiftRightLeft * 4) / picWidth;
        linePicturesCount = (GalleryFrame.this.getHeight() - shiftDownUp * 3) / picHeight;

        shiftHorizontal = (GalleryFrame.this.getWidth() - ((picWidth + shiftRightLeft) * 4)) / 2;
        shiftVertical = (GalleryFrame.this.getHeight() - ((picHeight + shiftDownUp) * 3)) / 2 - 10;

        Print(GalleryFrame.class, LEVEL.INFO, "Gallery may has " + (linePicturesCount * colPicturesCount) + " miniatures.");
    }


    @Override
    public void mousePressed(MouseEvent e) {
        mouseWasOnScreen = new Point(e.getXOnScreen(), e.getYOnScreen());
        frameWas = getLocation();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isFullscreen) {
            return;
        }
        try {
            setLocation(
                    (int) (frameWas.getX() - (mouseWasOnScreen.getX() - e.getXOnScreen())),
                    (int) (frameWas.getY() - (mouseWasOnScreen.getY() - e.getYOnScreen())));
        } catch (Exception e2) {/* IGNORE MOVING */}
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }
}