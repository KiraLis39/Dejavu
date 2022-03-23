package door;

import GUI.MainMenu;
import fox.FoxLogo;
import fox.JIOM;
import fox.Out;
import interfaces.Cached;
import secondGUI.NewUserForm;
import tools.Media;
import tools.ModsLoader;
import configurations.Configuration;
import tools.MediaCache;
import registry.Registry;
import configurations.UserConf;
import tools.VolumeConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static registry.Registry.configuration;
import static registry.Registry.userConf;

public class MainClass implements Cached {
    private static boolean isLogEnabled = true;
    private static FoxLogo fl;

    private static void preInit() {
        Out.setEnabled(isLogEnabled);
        Out.setErrorLevel(Out.LEVEL.DEBUG);
        Out.setLogsCountAllow(3);

        try {configuration = JIOM.fileToDto(Registry.globalConfigFile, Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Out.Print(MainClass.class, Out.LEVEL.INFO,
                "\nКодировка системы: " + Charset.defaultCharset() +
                "\nКодировка программы: " + Registry.charset + "\n");
    }

    public static void main(String[] args) {
        float testIn1 = VolumeConverter.volumePercentToGain(0);
        float testIn2 = VolumeConverter.volumePercentToGain(25);
        float testIn3 = VolumeConverter.volumePercentToGain(50);
        float testIn4 = VolumeConverter.volumePercentToGain(75);
        float testIn5 = VolumeConverter.volumePercentToGain(100);
        System.out.println();
        System.out.println("Percent 01: " + VolumeConverter.gainToVolumePercent(testIn1));
        System.out.println("Percent 02: " + VolumeConverter.gainToVolumePercent(testIn2));
        System.out.println("Percent 03: " + VolumeConverter.gainToVolumePercent(testIn3));
        System.out.println("Percent 04: " + VolumeConverter.gainToVolumePercent(testIn4));
        System.out.println("Percent 05: " + VolumeConverter.gainToVolumePercent(testIn5));
        System.out.println();
        System.exit(0);
        preInit();

        if (configuration.isShowLogo()) {
            fl = new FoxLogo();
            fl.setImStyle(FoxLogo.IMAGE_STYLE.WRAP);
            fl.setBStyle(FoxLogo.BACK_STYLE.OPAQUE);
            try {
                fl.start("Версия: " + Registry.version,
                        new BufferedImage[]{ImageIO.read(new File("./resources/logo.png"))});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        existingDirectoriesCheck();

        configurator();

        loadImages();
        loadAudio();

        connectMods();

        if (fl != null) {
            try {
                fl.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Out.Print(MainClass.class, Out.LEVEL.ACCENT, "Запуск MainMenu...");
        new MainMenu();
    }

    private static void existingDirectoriesCheck() {
        if (Files.notExists(Registry.dataDir)) {
            Exit.exit(14, "Error: Data directory is lost! Reinstall the game, please.");
        }

        Path[] scanFiles = new Path[]{
                Registry.usersDir,
//                Registry.usersSaveDir,
                Registry.modsDir,
                Registry.picDir,
                Registry.curDir,
                Registry.dataDir,
                Registry.scenesDir,
                Registry.blockPath,
                Registry.npcAvatarsDir,
                Registry.personasDir
        };
        for (Path p : scanFiles) {
            if (Files.notExists(p)) {
                Out.Print(MainClass.class, Out.LEVEL.ACCENT, "Не найден путь '" + p + "' -> Попытка создания...");
                try {
                    Files.createDirectories(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Out.Print(MainClass.class, Out.LEVEL.INFO, "Проверка наличия необходимых директорий завершена.\n");
    }

    private static void configurator() {
        // настраиваем пользователя:
        try {
            int luHash = configuration.getLastUserHash();
            if (luHash == 0) {
                createNewUser("newEmptyUser", UserConf.USER_SEX.MALE, 14);
            } else {
                Registry.usersSaveDir = Paths.get(Registry.usersDir + "/" + luHash + "/");
                userConf = JIOM.fileToDto(Paths.get(Registry.usersSaveDir + "/config.dto"), UserConf.class);
            }

            JIOM.dtoToFile(configuration);
            JIOM.dtoToFile(userConf);
        } catch (Exception e) {
            Out.Print(MainClass.class, Out.LEVEL.ERROR, "Failed user save: " + e.getMessage());
        }
    }

    public static void createNewUser(String name, UserConf.USER_SEX sex, int age) throws Exception {
        try {
            if (userConf != null) {
            // сохраняем предыдущего пользователя:
                JIOM.dtoToFile(userConf);
            }

            // Настраиваем глобальную конфигурацию:
            configuration.setLastUserName(name);
            configuration.calcUserHash();
            JIOM.dtoToFile(configuration);

            // настраиваем нового пользователя:
            Registry.usersSaveDir = Paths.get(Registry.usersDir + "/" + configuration.getLastUserHash() + "/");
            userConf = JIOM.fileToDto(Paths.get(Registry.usersSaveDir + "/config.dto"), UserConf.class);
            userConf.setUserName(configuration.getLastUserName());
            userConf.setUserSex(sex);
            userConf.setUserAge(age);
            if (userConf.getUserAge() <= 0 || userConf.getUserAge() > 120) {
                userConf.setUserAge(14);
            }
            JIOM.dtoToFile(userConf);

            Out.Print(MainClass.class, Out.LEVEL.INFO, "Приветствуем игрока " + userConf.getUserName() + "!");
        } catch (Exception e) {
            throw e;
        }
    }

    private static void loadImages() {
        // other:
        cache.add("picExitButtonSprite", toBImage(Registry.picDir + "/buttons/exits"));
        cache.add("picPlayButtonSprite", toBImage(Registry.picDir + "/buttons/starts"));
        cache.add("picMenuButtonSprite", toBImage(Registry.picDir + "/buttons/menus"));

        cache.add("picBackButBig", toBImage(Registry.picDir + "/buttons/butListG"));
        cache.add("picMenuButtons", toBImage(Registry.picDir + "/buttons/butListM"));

        cache.add("picGameIcon", toBImage(Registry.picDir + "/32"));

        // cursors & backgrounds:
        cache.add("curSimpleCursor", toBImage(Registry.curDir + "/01"));
        cache.add("curTextCursor", toBImage(Registry.curDir + "/02"));
        cache.add("curGalleryCursor", toBImage(Registry.curDir + "/03"));
        cache.add("curAnyCursor", toBImage(Registry.curDir + "/04"));
        cache.add("curOtherCursor", toBImage(Registry.curDir + "/05"));

        cache.add("picSaveLoad", toBImage(Registry.picDir + "/backgrounds/saveLoad"));
        cache.add("picMenuBase", toBImage(Registry.picDir + "/backgrounds/menuBase"));
        cache.add("picAurora", toBImage(Registry.picDir + "/backgrounds/aurora"));
        cache.add("picGallery", toBImage(Registry.picDir + "/backgrounds/gallery"));
        cache.add("picMenupane", toBImage(Registry.picDir + "/backgrounds/menupane"));
        cache.add("picGender", toBImage(Registry.picDir + "/backgrounds/gender"));
        cache.add("picGamepane", toBImage(Registry.picDir + "/backgrounds/gamepane"));
        cache.add("picAutrs", toBImage(Registry.picDir + "/backgrounds/autrs"));
        cache.add("picGameMenu", toBImage(Registry.picDir + "/backgrounds/gameMenu"));

        // heroes:
        cache.add("0", toBImage(Registry.picDir + "/hero/0"));

        // fema:
        cache.add("1", toBImage(Registry.picDir + "/hero/1"));
        cache.add("2", toBImage(Registry.picDir + "/hero/2"));
        cache.add("3", toBImage(Registry.picDir + "/hero/3"));
        cache.add("4", toBImage(Registry.picDir + "/hero/4"));

        // male:
        cache.add("5", toBImage(Registry.picDir + "/hero/5"));
        cache.add("6", toBImage(Registry.picDir + "/hero/6"));
        cache.add("7", toBImage(Registry.picDir + "/hero/7"));
        cache.add("8", toBImage(Registry.picDir + "/hero/8"));
    }

    private static BufferedImage toBImage(String path) {
        try {
            return ImageIO.read(new File(path + Registry.picExtension));
        } catch (Exception e) {
            Out.Print(MainClass.class, Out.LEVEL.WARN, "Ошибка чтения медиа '" + Paths.get(path) + "': " + e.getMessage());
        }
        return null;
    }

    private static void loadAudio() {
        try {
            Media.loadSounds(new File("./resources/sound/").listFiles());
            Media.loadMusics(new File("./resources/mus/musikThemes/").listFiles());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Media.loadVoices(new File("./resources/sound/voices/").listFiles());
            Media.loadBackgs(new File("./resources/mus/fonMusic/").listFiles());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void connectMods() {
        if (!configuration.isUseMods()) {return;}

        Out.Print(MainClass.class, Out.LEVEL.INFO, "Сканирование папки mods...");
        try {
            new ModsLoader(Registry.modsDir);
            if (ModsLoader.getReadyModsCount() > 0) {
                Out.Print(MainClass.class, Out.LEVEL.ACCENT, "Обнаружены возможные моды в количестве шт: " + ModsLoader.getReadyModsCount());
            } else {
                Out.Print(MainClass.class, Out.LEVEL.INFO, "Моды не обнаружены. Продолжение работы...");
            }
        } catch (Exception e) {
            Out.Print(MainClass.class, Out.LEVEL.WARN, "Загрузка модов провалилась! Ошибка: " + e.getMessage());
        }
    }
}