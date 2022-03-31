package configurations;

import iom.interfaces.JConfigurable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import render.FoxRender.RENDER;

import java.nio.file.Path;

@Data
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserConf implements JConfigurable {
    public enum USER_SEX {MALE, FEMALE}
    USER_SEX userSex;

    Path source;

    RENDER quality;
    String userName;
    int userAge;
    int avatarIndex;
    int cycleCount;
    boolean fullScreen;
    boolean autoSaveOn;
    boolean autoSkipping;

    int musicVolume;
    boolean musicMuted;
    int soundVolume;
    boolean soundMuted;
    int backgVolume;
    boolean backgMuted;
    int voiceVolume;
    boolean voiceMuted;

    @Override
    public void setSource(Path source) {
        this.source = source;
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
