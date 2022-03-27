package door;

import GUI.GameMenu;
import configurations.Configuration;
import configurations.UserConf;
import fox.FoxLogo;
import fox.JIOM;
import fox.Out;
import fox.Out.LEVEL;
import interfaces.Cached;
import registry.Registry;
import secondGUI.NewUserForm;
import tools.ModsLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static fox.Out.Print;
import static registry.Registry.*;
import static registry.Registry.userConf;

public class MainClass implements Cached {
    private static boolean isLogEnabled = true;
    private static FoxLogo fl;

    private static void preInit() {
        Out.setEnabled(isLogEnabled);
        Out.setErrorLevel(LEVEL.DEBUG);
        Out.setLogsCountAllow(3);

        try {configuration = JIOM.fileToDto(globalConfigFile, Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Out.Print(MainClass.class, LEVEL.INFO,
                "\nКодировка системы: " + Charset.defaultCharset() +
                        "\nКодировка программы: " + charset + "\n");
    }

    public static void main(String[] args) {
        preInit();

        if (configuration.isShowLogo()) {
            fl = new FoxLogo();
            fl.setImStyle(FoxLogo.IMAGE_STYLE.WRAP);
            fl.setBStyle(FoxLogo.BACK_STYLE.OPAQUE);
            try {
                fl.start("Версия: " + version,
                        new BufferedImage[]{ImageIO.read(new File("./resources/logo.png"))});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        existingDirectoriesCheck();
        loadImages();

        try {fl.join();
        } catch (InterruptedException ignore) {}

        playerCheck();
        postPlayerInit();
    }

    public static void postPlayerInit() {
        loadAudio();
        connectMods();

        Out.Print(MainClass.class, LEVEL.ACCENT, "Запуск MainMenu...");
        new GameMenu();
    }

    private static void existingDirectoriesCheck() {
        if (Files.notExists(dataDir)) {
            Exit.exit(14, "Error: Data directory is lost! Reinstall the game, please.");
        }

        Path[] scanFiles = new Path[]{
                usersDir,
                modsDir,
                picDir,
                curDir,
                dataDir,
                scenesDir,
                blockPath,
                npcAvatarsDir,
                personasDir,
                audioDir,
                audioBackgDir,
                audioSoundDir,
                audioMusicDir,
                audioVoicesDir
        };
        for (Path p : scanFiles) {
            if (Files.notExists(p)) {
                Out.Print(MainClass.class, LEVEL.ACCENT, "Не найден путь '" + p + "' -> Попытка создания...");
                try {
                    Files.createDirectories(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Out.Print(MainClass.class, LEVEL.INFO, "Проверка наличия необходимых директорий завершена.\n");
    }

    private static void playerCheck() {
        try {
            int luHash = configuration.getLastUserHash();
            if (luHash == 0 || Files.notExists(Paths.get(usersDir + "/" + luHash + "/save.dto"))) {
                userConf = regNewbie();
            } else {
                usersSaveDir = Paths.get(usersDir + "/" + luHash + "/");
                userConf = JIOM.fileToDto(Paths.get(usersSaveDir + "/save.dto"), UserConf.class);
            }
        } catch (Exception e) {
            Out.Print(MainClass.class, LEVEL.ERROR, "Failed user load: " + e.getMessage());
            Exit.exit(13, e.getMessage());
        }
    }

    public static UserConf regNewbie() {
        Print(GameMenu.class, LEVEL.INFO, "A newbie creation...");

        try {
            // настраиваем нового пользователя:
            userConf = new NewUserForm().get();
            if (userConf != null) {
                loadUser(userConf);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userConf;
    }

    public static void loadUser(UserConf uConf) throws IOException {
        // Настраиваем глобальную конфигурацию:
        configuration.setLastUserName(uConf.getUserName());
        configuration.calcUserHash();
        JIOM.dtoToFile(configuration);

        usersSaveDir = Paths.get(usersDir + "/" + configuration.getLastUserHash());
        uConf.setSource(Paths.get(usersSaveDir + "/save.dto"));
        JIOM.dtoToFile(uConf);

        userConf = uConf;
        Out.Print(MainClass.class, LEVEL.INFO, "Приветствуем игрока " + userConf.getUserName() + "!");
    }

    private static void loadImages() {
        // other:
        cache.add("picExitButtonSprite", toBImage(picDir + "/buttons/exits"));
        cache.add("picPlayButtonSprite", toBImage(picDir + "/buttons/starts"));
        cache.add("picMenuButtonSprite", toBImage(picDir + "/buttons/menus"));

        cache.add("picBackButBig", toBImage(picDir + "/buttons/butListG"));
        cache.add("picMenuButtons", toBImage(picDir + "/buttons/butListM"));

        cache.add("picGameIcon", toBImage(picDir + "/32"));

        // cursors & backgrounds:
        cache.add("curSimpleCursor", toBImage(curDir + "/SimpleCursor"));
        cache.add("curTextCursor", toBImage(curDir + "/TextCursor"));
        cache.add("curGalleryCursor", toBImage(curDir + "/GalleryCursor"));
        cache.add("curPinkCursor", toBImage(curDir + "/PinkCursor"));
        cache.add("curOtherCursor", toBImage(curDir + "/OtherCursor"));
        cache.add("curCrossCursor", toBImage(curDir + "/CrossCursor"));
        cache.add("curBlueCursor", toBImage(curDir + "/BlueCursor"));
        cache.add("curOrangeCursor", toBImage(curDir + "/OrangeCursor"));

        cache.add("picSaveLoad", toBImage(picDir + "/backgrounds/saveLoad"));
        cache.add("picMenuBase", toBImage(picDir + "/backgrounds/menuBase"));
        cache.add("picAurora", toBImage(picDir + "/backgrounds/aurora"));
        cache.add("picGallery", toBImage(picDir + "/backgrounds/gallery"));
        cache.add("picGamepane", toBImage(picDir + "/backgrounds/gamepane"));
        cache.add("picAutrs", toBImage(picDir + "/backgrounds/autrs"));
        cache.add("picGameMenu", toBImage(picDir + "/backgrounds/gameMenu"));
        cache.add("picMenuBotRight", toBImage(picDir + "/backgrounds/menu_bottomRight"));
        cache.add("picMenuBotLeft", toBImage(picDir + "/backgrounds/menu_bottomLeft"));
        cache.add("picMenuTop", toBImage(picDir + "/backgrounds/menu_top"));

        // heroes:
        cache.add("0", toBImage(picDir + "/hero/0"));

        // fema:
        cache.add("1", toBImage(picDir + "/hero/1"));
        cache.add("2", toBImage(picDir + "/hero/2"));
        cache.add("3", toBImage(picDir + "/hero/3"));
        cache.add("4", toBImage(picDir + "/hero/4"));

        // male:
        cache.add("5", toBImage(picDir + "/hero/5"));
        cache.add("6", toBImage(picDir + "/hero/6"));
        cache.add("7", toBImage(picDir + "/hero/7"));
        cache.add("8", toBImage(picDir + "/hero/8"));
    }

    private static BufferedImage toBImage(String path) {
        try {
            return ImageIO.read(new File(path + picExtension));
        } catch (Exception e) {
            Out.Print(MainClass.class, LEVEL.WARN, "Ошибка чтения медиа '" + Paths.get(path) + "': " + e.getMessage());
        }
        return null;
    }

    private static void loadAudio() {
//        voicePlayer.load(audioVoicesDir);

        musicPlayer.load(audioMusicDir);
        musicPlayer.setLooped(true);
        musicPlayer.mute(userConf.isMusicMuted());
        musicPlayer.setVolume(userConf.getMusicVolume());
        musicPlayer.getVolumeConverter().setMinimum(-50);

        backgPlayer.load(audioBackgDir);
        backgPlayer.setLooped(true);
        backgPlayer.mute(userConf.isBackgMuted());
        backgPlayer.setVolume(userConf.getBackgVolume());
        backgPlayer.getVolumeConverter().setMinimum(-50);
//        backgPlayer.setAudioBufDim(1024); // 4096

        soundPlayer.load(audioSoundDir);
        soundPlayer.setParallelPlayable(true);
        soundPlayer.setLooped(false);
        soundPlayer.mute(userConf.isSoundMuted());
        soundPlayer.setVolume(userConf.getSoundVolume());
        soundPlayer.getVolumeConverter().setMinimum(-50);
//        soundPlayer.setAudioBufDim(8192); // 8192
    }

    static void connectMods() {
        if (!configuration.isUseMods()) {
            return;
        }

        Out.Print(MainClass.class, LEVEL.INFO, "Сканирование папки mods...");
        try {
            new ModsLoader(modsDir);
            if (ModsLoader.getReadyModsCount() > 0) {
                Out.Print(MainClass.class, LEVEL.ACCENT, "Обнаружены возможные моды в количестве шт: " + ModsLoader.getReadyModsCount());
            } else {
                Out.Print(MainClass.class, LEVEL.INFO, "Моды не обнаружены. Продолжение работы...");
            }
        } catch (Exception e) {
            Out.Print(MainClass.class, LEVEL.WARN, "Загрузка модов провалилась! Ошибка: " + e.getMessage());
        }
    }
}