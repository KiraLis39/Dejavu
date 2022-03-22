package door;

import GUI.MainMenu;
import fox.FoxLogo;
import fox.JIOM;
import fox.Out;
import tools.Media;
import tools.ModsLoader;
import configurations.Configuration;
import tools.MediaCache;
import registry.Registry;
import configurations.UserConf;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static registry.Registry.configuration;
import static registry.Registry.userConf;

public class MainClass {
    private static boolean isLogEnabled = true;
    private static FoxLogo fl;

    private static void preInit() {
        Out.setEnabled(isLogEnabled);
        Out.setErrorLevel(Out.LEVEL.INFO);
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
        // имя последнего игрока:
        int luHash = configuration.getLastUserHash();
        if (luHash == 0) {
            luHash = "newEmptyUser".hashCode();
            // настраиваем пользователя:
            configuration.setLastUserName("newEmptyUser");
            configuration.calcUserHash();
            Registry.usersSaveDir = Paths.get(Registry.usersDir + "/" + configuration.getLastUserHash() + "/");
        }

        try {
            userConf = JIOM.fileToDto(Paths.get(Registry.usersDir + "/"+ configuration.getLastUserHash() + "/config.dto"), UserConf.class);
            userConf.setUserName(configuration.getLastUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Out.Print(MainClass.class, Out.LEVEL.INFO, "Приветствуем игрока " + userConf.getUserName() + "!");

        if (userConf.getUserSex() == null) {
            userConf.setUserSex(UserConf.USER_SEX.MALE);
        }

        if (userConf.getUserAge() <= 0 || userConf.getUserAge() > 120) {
            userConf.setUserAge(14);
        }

        loadAudioSettings();

        try {
            JIOM.dtoToFile(configuration);
            JIOM.dtoToFile(userConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadAudioSettings() {
        // === Определение конфигурации аудио ===
        Out.Print(MainClass.class, Out.LEVEL.INFO, "Определение конфигурации аудио...");
        if (userConf.getMusicVolume() == null) {
            userConf.setMusicVolume(0.75f);
        }
        if (userConf.getSoundVolume() == null) {
            userConf.setSoundVolume(0.5f);
        }
        if (userConf.getBackgVolume() == null) {
            userConf.setBackgVolume(0.5f);
        }
        if (userConf.getVoiceVolume() == null) {
            userConf.setVoiceVolume(0.75f);
        }
        Out.Print(MainClass.class, Out.LEVEL.INFO, "Аудио сконфигурировано.");
    }


    private static void loadImages() {
        MediaCache cashe = MediaCache.getInstance();
        // other:
        try {
            cashe.add("picExitButtonSprite", toBImage(Registry.picDir + "/buttons/exits"));
            cashe.add("picPlayButtonSprite", toBImage(Registry.picDir + "/buttons/starts"));
            cashe.add("picMenuButtonSprite", toBImage(Registry.picDir + "/buttons/menus"));

            cashe.add("picBackButBig", toBImage(Registry.picDir + "/buttons/butListG"));
            cashe.add("picMenuButtons", toBImage(Registry.picDir + "/buttons/butListM"));

            cashe.add("picGameIcon", toBImage(Registry.picDir + "/32"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // cursors & backgrounds:
        try {
            cashe.add("curSimpleCursor", toBImage(Registry.curDir + "/01"));
            cashe.add("curTextCursor", toBImage(Registry.curDir + "/02"));
            cashe.add("curGaleryCursor", toBImage(Registry.curDir + "/03"));
            cashe.add("curAnyCursor", toBImage(Registry.curDir + "/04"));
            cashe.add("curOtherCursor", toBImage(Registry.curDir + "/05"));

            cashe.add("picSaveLoad", toBImage(Registry.picDir + "/backgrounds/saveLoad"));
            cashe.add("picMenuBase", toBImage(Registry.picDir + "/backgrounds/menuBase"));
            cashe.add("picAurora", toBImage(Registry.picDir + "/backgrounds/aurora"));
            cashe.add("picGallery", toBImage(Registry.picDir + "/backgrounds/gallery"));
            cashe.add("picMenupane", toBImage(Registry.picDir + "/backgrounds/menupane"));
            cashe.add("picGender", toBImage(Registry.picDir + "/backgrounds/gender"));
            cashe.add("picGamepane", toBImage(Registry.picDir + "/backgrounds/gamepane"));
            cashe.add("picAutrs", toBImage(Registry.picDir + "/backgrounds/autrs"));
            cashe.add("picGameMenu", toBImage(Registry.picDir + "/backgrounds/gameMenu"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // heroes:
        try {
            cashe.add("0", new File(Registry.picDir + "/hero/00"));

            // fema:
            cashe.add("1", new File(Registry.picDir + "/hero/01"));
            cashe.add("2", new File(Registry.picDir + "/hero/02"));
            cashe.add("3", new File(Registry.picDir + "/hero/03"));
            cashe.add("4", new File(Registry.picDir + "/hero/04"));

            // male:
            cashe.add("5", new File(Registry.picDir + "/hero/05"));
            cashe.add("6", new File(Registry.picDir + "/hero/06"));
            cashe.add("7", new File(Registry.picDir + "/hero/07"));
            cashe.add("8", new File(Registry.picDir + "/hero/08"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // npc avatars:
        try {
            for (Path path : Registry.npcAvatarsDir) {
                cashe.add(path.toFile().getName().replace(Registry.picExtension, ""), toBImage(path.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // scenes load:
        try {
            for (Path path : Registry.scenesDir) {
                cashe.add(path.toFile().getName().replace(Registry.picExtension, ""), toBImage(path.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage toBImage(String path) {
        try {
            return ImageIO.read(new File(path + Registry.picExtension));
        } catch (IOException e) {
            Out.Print(MainClass.class, Out.LEVEL.WARN, "Ошибка чтения медиа '" + path + "': " + e.getMessage());
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