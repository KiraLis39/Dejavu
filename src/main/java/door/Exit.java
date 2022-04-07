package door;

import iom.JIOM;
import fox.Out;
import registry.Registry;
import tools.ModsLoaderEngine;

import java.awt.*;

import static fox.Out.LEVEL;
import static fox.Out.Print;
import static registry.Registry.*;

public class Exit {

    public static void exit(int i) {
        exit(i, null);
    }

    public static void exit(int i, String comment) {
        Toolkit.getDefaultToolkit().beep();

        try {
            backgPlayer.stop();
            musicPlayer.stop();
            soundPlayer.stop();
//            voicePlayer.stop();
        } catch (Exception e) {
            i++;
            e.printStackTrace();
        }

        try {
            ModsLoaderEngine.stopMods();
        } catch (Exception e) {
            i++;
            e.printStackTrace();
        }

        Print(Exit.class, LEVEL.ACCENT, "Сохранение...");
        try {
            JIOM.dtoToFile(configuration);
        } catch (Exception e) {
			i++;
            e.printStackTrace();
        }

        try {
            JIOM.dtoToFile(userConf);
        } catch (Exception e) {
			i++;
            e.printStackTrace();
        }

        Print(Exit.class, LEVEL.ERROR, "Код #" + i);
        if (comment != null) {
            Print(Exit.class, LEVEL.ACCENT, "Комментарий завершения: " + comment);
        }

        i += Out.close();
        System.exit(i);
    }
}
