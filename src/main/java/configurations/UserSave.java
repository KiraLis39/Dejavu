package configurations;

import GUI.GamePlay;
import iom.interfaces.JConfigurable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserSave implements JConfigurable {
    String name;
    Path source;

    int cycleCount = 0;

    int carmaAnn = 0;
    int carmaDmi = 0;
    int carmaKur = 0;
    int carmaMar = 0;
    int carmaMsh = 0;
    int carmaOks = 0;
    int carmaOlg = 0;
    int carmaOle = 0;
    int carmaLis = 0;

    String chapter = null;
    GamePlay.MONTH month = GamePlay.MONTH.июнь;
    int today = 3;

    String screen = null;

    String musicPlayed = null;
    String backgPlayed = null;
    String soundPlayed = null;
    String voicePlayed = null;

    String script = "00_INIT_SCENARIO";
    int lineIndex = -1;

    public UserSave(Path source) {
        this.source = source;
    }

    @Override
    public Path getSource() {
        return source;
    }
}
