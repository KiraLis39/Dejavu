package door;

import configurations.Configuration;
import configurations.UserConf;
import configurations.UserSave;
import fox.FoxLogo;
import fox.Out;
import fox.Out.LEVEL;
import fox.player.FoxPlayer;
import gui.GameMenu;
import interfaces.Cached;
import iom.JIOM;
import lombok.NonNull;
import secondGUI.NewUserForm;
import tools.ModsLoaderEngine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static fox.Out.Print;
import static registry.Registry.*;

public class MainClass implements Cached {
    private static boolean isLogEnabled;
    private static FoxLogo fl;

    private static void preInit() {
        Out.setEnabled(true);
        Out.setErrorLevel(LEVEL.DEBUG);
        Out.setLogsCountAllow(3);

        try {
            configuration = JIOM.fileToDto(globalConfigFile, Configuration.class);
            isLogEnabled = configuration.isLogEnabled();
            Out.setEnabled(isLogEnabled);
        } catch (Exception e) {
            Out.Print(MainClass.class, LEVEL.WARN,"Что-то не так с загрузкой конфигурации игры: " + e.getMessage());
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
            fl.setColor(Color.BLACK);
            try {
                fl.start("Версия: " + version,
                        new BufferedImage[]{ImageIO.read(new File("./resources/logo.png"))},
                        FoxLogo.IMAGE_STYLE.DEFAULT,
                        FoxLogo.BACK_STYLE.ASIS
                );
            } catch (Exception e) {
                Out.Print(MainClass.class, LEVEL.WARN,"Что-то не так с заставкой игры: " + e.getMessage());
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

        Out.Print(MainClass.class, LEVEL.INFO, "Запуск MainMenu...");
        new GameMenu();
    }

    private static void existingDirectoriesCheck() {
        Out.Print(MainClass.class, LEVEL.DEBUG,"Проверка директорий...");
        if (Files.notExists(dataDir)) {
            Exit.exit(14, "Error: Data directory is lost! Reinstall the game, please.");
        }

        Path[] scanFiles = new Path[] {
                usersDir,
                modsDir,
                picDir,
                curDir,
                dataDir,
                scenesDir,
                blockPath,
                personasDir,
                npcAvatarsDir,
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
        Out.Print(MainClass.class, LEVEL.DEBUG,"Проверка профиля игрока...");

        try {
            int luHash = configuration.getLastUserHash();
            if (luHash == 0 || Files.notExists(Paths.get(usersDir + "/" + luHash + "/save.dto"))) {
                userConf = regNewbie();
            } else {
                usersSaveDir = Paths.get(usersDir + "/" + luHash + "/");
                userConf = JIOM.fileToDto(Paths.get(usersSaveDir + "/uconf.dto"), UserConf.class);
            }
            userSave = JIOM.fileToDto(Paths.get(usersSaveDir + "\\save.dto"), UserSave.class);
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
        uConf.setSource(Paths.get(usersSaveDir + "/uconf.dto"));
        JIOM.dtoToFile(uConf);

        userConf = uConf;
        Out.Print(MainClass.class, LEVEL.INFO, "Приветствуем игрока " + userConf.getUserName() + "!");
    }

    private static void loadImages() {
        Out.Print(MainClass.class, LEVEL.DEBUG,"Загрузка изображений...");

        // other:
        cache.addIfAbsent("picExitButtonSprite", toBImage(picDir + "/buttons/exits"));
        cache.addIfAbsent("picPlayButtonSprite", toBImage(picDir + "/buttons/starts"));

        cache.addIfAbsent("picBackButBig", toBImage(picDir + "/buttons/butListG"));
        cache.addIfAbsent("picMenuButtons", toBImage(picDir + "/buttons/butListM"));

        cache.addIfAbsent("picGameIcon", toBImage(picDir + "/32"));

        // cursors & backgrounds:
        cache.addIfAbsent("curSimpleCursor", toBImage(curDir + "/SimpleCursor"));
        cache.addIfAbsent("curTextCursor", toBImage(curDir + "/TextCursor"));
        cache.addIfAbsent("curGalleryCursor", toBImage(curDir + "/GalleryCursor"));
        cache.addIfAbsent("curPinkCursor", toBImage(curDir + "/PinkCursor"));
        cache.addIfAbsent("curOtherCursor", toBImage(curDir + "/OtherCursor"));
        cache.addIfAbsent("curCrossCursor", toBImage(curDir + "/CrossCursor"));
        cache.addIfAbsent("curBlueCursor", toBImage(curDir + "/BlueCursor"));
        cache.addIfAbsent("curOrangeCursor", toBImage(curDir + "/OrangeCursor"));

        cache.addIfAbsent("picMenuBase", toBImage(picDir + "/backgrounds/menuBase"));
        cache.addIfAbsent("picGallery", toBImage(picDir + "/backgrounds/gallery"));
        cache.addIfAbsent("picGameMenu", toBImage(picDir + "/backgrounds/gameMenu"));
        cache.addIfAbsent("picMenuBotRight", toBImage(picDir + "/backgrounds/menu_bottomRight"));
        cache.addIfAbsent("picMenuBotLeft", toBImage(picDir + "/backgrounds/menu_bottomLeft"));
        cache.addIfAbsent("picGamepane", toBImage(picDir + "/backgrounds/gamepaneUp"));

        // heroes:
        cache.addIfAbsent("0", toBImage(picDir + "/hero/0"));

        // fema:
        cache.addIfAbsent("1", toBImage(picDir + "/hero/1"));
        cache.addIfAbsent("2", toBImage(picDir + "/hero/2"));
        cache.addIfAbsent("3", toBImage(picDir + "/hero/3"));
        cache.addIfAbsent("4", toBImage(picDir + "/hero/4"));

        // male:
        cache.addIfAbsent("5", toBImage(picDir + "/hero/5"));
        cache.addIfAbsent("6", toBImage(picDir + "/hero/6"));
        cache.addIfAbsent("7", toBImage(picDir + "/hero/7"));
        cache.addIfAbsent("8", toBImage(picDir + "/hero/8"));

        Out.Print(MainClass.class, LEVEL.DEBUG,"Загрузка изображений завершена.");
    }

    private static BufferedImage toBImage(@NonNull String path) {
        try {
            return ImageIO.read(new File(path + picExtension));
        } catch (Exception e) {
            Out.Print(MainClass.class, LEVEL.WARN, "Ошибка чтения медиа '" + Paths.get(path) + "': " + e.getMessage());
        }
        return null;
    }

    private static void loadAudio() {
        FoxPlayer.getVolumeConverter().setMinimum(-50);

//        voicePlayer.load(audioVoicesDir);

        musicPlayer.load(audioMusicDir);
        musicPlayer.mute(userConf.isMusicMuted());
        musicPlayer.setVolume(userConf.getMusicVolume());

        backgPlayer.load(audioBackgDir);
        backgPlayer.mute(userConf.isBackgMuted());
        backgPlayer.setVolume(userConf.getBackgVolume());
//        backgPlayer.setAudioBufDim(1024); // 4096

        soundPlayer.load(audioSoundDir);
        soundPlayer.setParallelPlayable(true);
        soundPlayer.setLooped(false);
        soundPlayer.mute(userConf.isSoundMuted());
        soundPlayer.setVolume(userConf.getSoundVolume());
//        soundPlayer.setAudioBufDim(8192); // 8192
    }

    static void connectMods() {
        if (!configuration.isUseMods()) {
            return;
        }

        Out.Print(MainClass.class, LEVEL.DEBUG, "Сканирование папки с модами " + modsDir);
        try {
            new ModsLoaderEngine();
            Out.Print(MainClass.class, LEVEL.ACCENT,
                    ModsLoaderEngine.getReadyModsCount() > 0 ?
                            "Обнаружены возможные моды в количестве шт: " + ModsLoaderEngine.getReadyModsCount() :
                            "Моды не обнаружены. Продолжение работы...");
        } catch (Exception e) {
            Out.Print(MainClass.class, LEVEL.WARN, "Загрузка модов провалилась! Ошибка: " + e.getMessage());
        }
    }
}