package secondGUI;

import configurations.UserConf;
import render.FoxRender;

import javax.swing.*;
import java.awt.*;

public class UserConfPanel extends JPanel {
    private final UserConf user;

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        FoxRender.setRender(g2D, FoxRender.RENDER.ULTRA);
        super.paintComponent(g2D);
    }

    public UserConfPanel(UserConf user) {
        this.user = user;
    }

    public UserConf getConfig() {
        return user;
    }
}
