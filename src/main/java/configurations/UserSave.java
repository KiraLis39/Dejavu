package configurations;

import fox.interfaces.JConfigurable;

import java.nio.file.Path;

public class UserSave implements JConfigurable {
    Path source;

    @Override
    public void setSource(Path path) {
        source = path;
    }

    @Override
    public Path getSource() {
        return source;
    }
}
