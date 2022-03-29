package GUI;

import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

@NoArgsConstructor
public abstract class iGamePlay extends JFrame {
    public iGamePlay(String name, GraphicsConfiguration gConfig) {
        super(name, gConfig);
    }

    public void setScene(String sceneName, String npcName) {

    }

    public void setDialog(String dialogOwner, String dialogText, ArrayList<String> answers) {

    }
}
