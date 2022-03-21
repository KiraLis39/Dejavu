package fomod;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public abstract class ModExample implements Plugin, Runnable {
    public enum MOD_TYPE {OTHER}

    static int modsCounter = 0;
    int id = 0;

    MOD_TYPE type;
    String name;
    String version;
    String author;
    String description;

    Thread modThread;

    @Override
    public void init(MOD_TYPE type, String name, String version, String author, String description) {
        this.type = type;
        this.name = name;
        this.version = version;
        this.author = author;
        this.description = description;

        this.id = modsCounter;
        modsCounter++;
    }

    @Override
    public void start() {
        modThread = new Thread(this);
        modThread.setDaemon(true);
        modThread.start();
    }
}