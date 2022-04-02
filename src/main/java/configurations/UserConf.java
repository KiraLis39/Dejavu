package configurations;

import iom.interfaces.JConfigurable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import render.FoxRender.RENDER;

import java.nio.file.Path;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserConf implements JConfigurable {
    public enum USER_SEX {MALE, FEMALE}
    USER_SEX userSex;

    Path source;

    RENDER quality;
    String userName;
    int userAge;
    int avatarIndex;
    boolean isFullScreen;
    boolean isAutoSkipping;
    boolean isTextAnimated = true;

    int musicVolume;
    boolean musicMuted;
    int soundVolume;
    boolean soundMuted;
    int backgVolume;
    boolean backgMuted;
    int voiceVolume;
    boolean voiceMuted;

    public UserConf(Path source) {
        this.source = source;
    }

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
