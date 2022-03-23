package configurations;

import fox.interfaces.JConfigurable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.nio.file.Path;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserConf implements JConfigurable {
    Path source;

    public enum USER_SEX {MALE, FEMALE}
    USER_SEX userSex;
    String userName;
    int userAge = 14;
    int avatarIndex = 0;
    int cycleCount = 0;
    boolean fullScreen = false;
    boolean autoSaveOn = true;
    boolean autoSkipping = false;

    int musicVolume;
    boolean musicMuted = false;
    int soundVolume;
    boolean soundMuted = false;
    int backgVolume;
    boolean backgMuted = false;
    int voiceVolume;
    boolean voiceMuted = false;

    @Override
    public void setSource(Path path) {
        source = path;
    }

    @Override
    public Path getSource() {
        return source;
    }
}
