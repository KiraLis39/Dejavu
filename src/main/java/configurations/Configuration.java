package configurations;

import iom.interfaces.JConfigurable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.nio.file.Path;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Configuration implements JConfigurable {
    Path source;

    volatile boolean fpsShowed = false;
    boolean showLogo = true;
    boolean isLogEnabled = true;
    int lastUserHash;
    String lastUserName;
    boolean useMods = false;

    public Configuration(Path source) {
        this.source = source;
    }

    @Override
    public Path getSource() {
        return source;
    }

    public void calcUserHash() {
        lastUserHash = lastUserName.hashCode();
    }
}
