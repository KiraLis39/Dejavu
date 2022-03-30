package secondGUI;

import images.FoxCursor;
import utils.FoxFontBuilder;
import utils.InputAction;
import interfaces.Cached;
import registry.Registry;
import render.FoxRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class SaveGame extends JDialog implements Cached {
    private final Dimension toolk = Toolkit.getDefaultToolkit().getScreenSize();
    private final int WIDTH = (int) (this.toolk.getWidth() * 0.5D);
    private final Double widthPercent = WIDTH / 100D;
    private final Double buttonsWidth = widthPercent * 20D - 10D;
    private final int HEIGHT = (int) (toolk.getHeight() * 0.75D);
    private final Double heightPercent = HEIGHT / 100D;
    private final Rectangle button0Rect;
    private final Rectangle button1Rect;
    private final Rectangle button2Rect;

    private final Boolean saveChosen = true; // temporary ON

    public SaveGame(JFrame parent, GraphicsConfiguration gConfig) {
        super(parent, "SaveLoadFrame", true, gConfig);
        setSize(WIDTH, HEIGHT);
        setFocusable(true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setCursor(FoxCursor.createCursor((BufferedImage) cache.get("curGalleryCursor"), "galleryCursor"));
        setAutoRequestFocus(true);

        InputAction.add("save", this);
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_IN_FOCUSED_WINDOW, "save", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        button0Rect = new Rectangle((int) (widthPercent * 4D), (int) (heightPercent * 92D), (int) (buttonsWidth * 1), (int) (heightPercent * 4D));
        button1Rect = new Rectangle((int) (widthPercent * 24.5D), (int) (heightPercent * 92D), (int) (buttonsWidth * 1), (int) (heightPercent * 4D));
        button2Rect = new Rectangle((int) (widthPercent * 45D), (int) (heightPercent * 92D), (int) (buttonsWidth * 1), (int) (heightPercent * 4D));

        setLocationRelativeTo(null);
        setModal(true);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        FoxRender.setRender(g2D, FoxRender.RENDER.MED);

        g2D.drawImage((BufferedImage) cache.get("picSaveLoad"), 0, 0, getWidth(), getHeight(), SaveGame.this);

        g2D.setColor(Color.ORANGE);
        g2D.setFont(Registry.f0);
        g2D.drawString("Загрузка и сохранение:", (int) (widthPercent * 4D), (int) (heightPercent * 3.5D));

        g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 6D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));

        if (saveChosen) {
            g2D.setFont(Registry.f1);
            g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 73D));
            g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 77D));
            g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 81D));
            g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 85D));
            g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 89D));

            g2D.setColor(Color.ORANGE);
            g2D.drawRect(button0Rect.x, button0Rect.y, (int) button0Rect.getWidth(), (int) button0Rect.getHeight());
            g2D.drawString(
                    "option 0",
                    (int) (button0Rect.getCenterX() - FoxFontBuilder.getStringBounds(g2D, "option 0").getWidth() / 2D),
                    (int) (button0Rect.getCenterY() + FoxFontBuilder.getStringBounds(g2D, "option 0").getHeight() / 5D));

            g2D.drawRect(button1Rect.x, button1Rect.y, (int) button1Rect.getWidth(), (int) button1Rect.getHeight());
            g2D.drawString(
                    "option 1",
                    (int) (button1Rect.getCenterX() - FoxFontBuilder.getStringBounds(g2D, "option 1").getWidth() / 2D),
                    (int) (button1Rect.getCenterY() + FoxFontBuilder.getStringBounds(g2D, "option 1").getHeight() / 5D));

            g2D.drawRect(button2Rect.x, button2Rect.y, (int) button2Rect.getWidth(), (int) button2Rect.getHeight());
            g2D.drawString(
                    "option 2",
                    (int) (button2Rect.getCenterX() - FoxFontBuilder.getStringBounds(g2D, "option 2").getWidth() / 2D),
                    (int) (button2Rect.getCenterY() + FoxFontBuilder.getStringBounds(g2D, "option 2").getHeight() / 5D));
        }

        g2D.dispose();
    }
}