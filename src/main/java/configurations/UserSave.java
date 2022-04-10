package configurations;

import gui.GamePlay;
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
    int lineIndex = 0;

    public UserSave(Path source) {
        this.source = source;
    }

    @Override
    public Path getSource() {
        return source;
    }

    public void reset() {
        cycleCount = 0;

        carmaAnn = 0;
        carmaDmi = 0;
        carmaKur = 0;
        carmaMar = 0;
        carmaMsh = 0;
        carmaOks = 0;
        carmaOlg = 0;
        carmaOle = 0;
        carmaLis = 0;

        chapter = null;
        month = GamePlay.MONTH.июнь;
        today = 3;

        screen = null;

        musicPlayed = null;
        backgPlayed = null;
        soundPlayed = null;
        voicePlayed = null;

        script = "00_INIT_SCENARIO";
        lineIndex = 0;
    }
}
