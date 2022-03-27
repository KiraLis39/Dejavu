package secondGUI;

import configurations.UserConf;
import javax.swing.*;

public class UserConfPanel extends JPanel {
    private final UserConf user;

    public UserConfPanel(UserConf user) {
        this.user = user;
    }

    public UserConf getConfig() {
        return user;
    }
}
