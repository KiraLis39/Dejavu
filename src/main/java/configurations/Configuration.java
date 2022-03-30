package configurations;

import utils.JConfigurable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.nio.file.Path;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Configuration implements JConfigurable {
    Path source;

    boolean showLogo = true;
    boolean isLogEnabled = true;
    boolean fpsShowed = false;
    int lastUserHash;
    String lastUserName;
    boolean useMods = false;

    @Override
    public void setSource(Path path) {
        source = path;
    }

    @Override
    public Path getSource() {
        return source;
    }

    public void calcUserHash() {
        lastUserHash = lastUserName.hashCode();
    }
}
