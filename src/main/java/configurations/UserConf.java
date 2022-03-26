package configurations;

import fox.interfaces.JConfigurable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import render.FoxRender;
import render.FoxRender.RENDER;

import java.nio.file.Path;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserConf implements JConfigurable {
    public enum USER_SEX {MALE, FEMALE}

    Path source;
    RENDER quality = RENDER.LOW;
    USER_SEX userSex;
    String userName = "newEmptyUser";
    int userAge = 14;
    int avatarIndex = 0;
    int cycleCount = 0;
    boolean fullScreen = false;
    boolean autoSaveOn = true;
    boolean autoSkipping = false;

    int musicVolume = 75;
    boolean musicMuted = false;
    int soundVolume = 50;
    boolean soundMuted = false;
    int backgVolume = 50;
    boolean backgMuted = false;
    int voiceVolume = 75;
    boolean voiceMuted = false;

    @Override
    public void setSource(Path path) {
        source = path;
    }

    @Override
    public Path getSource() {
        return source;
    }

    public void nextQuality() {
        int curQ = quality.ordinal();
        quality = RENDER.values().length > curQ + 1 ? RENDER.values()[curQ + 1] : RENDER.values()[0];
    }
}
