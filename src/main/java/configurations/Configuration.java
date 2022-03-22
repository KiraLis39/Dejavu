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
    boolean useMods = false;

    @Override
    public void setSource(Path path) {
        source = path;
    }

    @Override
    public Path getSource() {
        return source;
    }
}
