package mod.fomod;

/**
 * The mods base
 */
public abstract class ModExample implements Plugin, Runnable {
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

    @Override
    public void stop() {
        if (modThread != null && modThread.isAlive()) {
            modThread.interrupt();
        }
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setType(MOD_TYPE modType) {
        this.type = modType;
    }
    @Override
    public MOD_TYPE getType() {
        return type;
    }

    @Override
    public void setName(String modName) {
        this.name = modName;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setVersion(String modVerse) {
        this.version = modVerse;
    }
    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setAuthor(String modAuthor) {
        this.author = modAuthor;
    }
    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setDescription(String modDescription) {
        this.description = modDescription;
    }
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ModExample{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", modThread=" + modThread +
                '}';
    }
}