package secondGUI;

import images.FoxCursor;
import utils.InputAction;
import interfaces.Cached;
import render.FoxRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class SaveGame extends JDialog implements Cached {
    private final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private final int WIDTH = (int) (screen.getWidth() * 0.5d);
    private final int HEIGHT = (int) (screen.getHeight() * 0.75d);
    private final BufferedImage backImage;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        FoxRender.setRender(g2D, FoxRender.RENDER.MED);

        g2D.drawImage(backImage,
                0, 0,
                getWidth(), getHeight(),
                this);

        g2D.dispose();
    }

    public SaveGame(JFrame parent, GraphicsConfiguration gConfig) {
        super(parent, "SaveLoadFrame", true, gConfig);

        backImage = (BufferedImage) cache.get("picSaveLoad");

        setSize(WIDTH, HEIGHT);
        setFocusable(true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curGalleryCursor"), "galleryCursor"));
        setAutoRequestFocus(true);

        inAc();

        setLocationRelativeTo(null);
        setModal(true);
        setVisible(true);
    }

    private void inAc() {
        InputAction.add("save", this);
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_IN_FOCUSED_WINDOW, "save", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    public static void save() {

    }
}