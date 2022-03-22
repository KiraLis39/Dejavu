package configurations;

import fox.interfaces.JConfigurable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.nio.file.Path;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Configuration implements JConfigurable {
    Path source;

    boolean showLogo = true;
    int lastUserHash;
    Float musicVolume;
    boolean musicMuted;
    Float soundVolume;
    boolean soundMuted;
    Float backgVolume;
    boolean backgMuted;
    Float voiceVolume;
    boolean voiceMuted;

    @Override
    public void setSource(Path path) {
        source = path;
    }

    @Override
    public Path getSource() {
        return source;
    }
}
