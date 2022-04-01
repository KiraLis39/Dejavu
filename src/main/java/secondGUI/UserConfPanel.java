package secondGUI;

import configurations.UserConf;
import configurations.UserSave;
import render.FoxRender;

import javax.swing.*;
import java.awt.*;

public class UserConfPanel extends JPanel {
    private final UserConf user;
    private final UserSave save;

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        FoxRender.setRender(g2D, FoxRender.RENDER.ULTRA);
        super.paintComponent(g2D);
    }

    public UserConfPanel(UserConf user, UserSave save) {
        this.user = user;
        this.save = save;
    }

    public UserConf getConfig() {
        return user;
    }

    public UserSave getSave() {
        return save;
    }
}
