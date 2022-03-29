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
}
