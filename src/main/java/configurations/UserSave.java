package configurations;

import iom.interfaces.JConfigurable;
import lombok.Data;

import java.nio.file.Path;

@Data
public class UserSave implements JConfigurable {
    Path source;

    int cycleCount;

    int carmaAnn = 0;
    int carmaDmi = 0;
    int carmaKur = 0;
    int carmaMar = 0;
    int carmaMsh = 0;
    int carmaOks = 0;
    int carmaOlg = 0;
    int carmaOle = 0;
    int carmaLis = 0;

    String chapter;
    int today;

    String screen;

    String musicPlayed;
    String backgPlayed;
    String soundPlayed;
    String voicePlayed;

    String script;
    int lineIndex;

    @Override
    public void setSource(Path path) {
        source = path;
    }

    @Override
    public Path getSource() {
        return source;
    }
}
