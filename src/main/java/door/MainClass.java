package door;

import GUI.MainMenu;
import fox.FoxLogo;
import fox.Out;
import media.Media;
import mods.ModsLoader;
import resourses.Registry;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MainClass {
    private static boolean showLogo = false, isLogEnabled = true, isIOMDebugEnabled = false, isRMDebugEnabled = false;
    private static FoxLogo fl;

    public static void main(String[] args) {
        Out.setEnabled(isLogEnabled);
        Out.setErrorLevel(Out.LEVEL.ACCENT);
        Out.setLogsCountAllow(3);

        IOM.setConsoleOutOn(isIOMDebugEnabled);

        ResManager.setDebugOn(isRMDebugEnabled);

        Out.Print(MainClass.class, Out.LEVEL.INFO, "Подготовка программы...\nКодировка системы: " + Charset.defaultCharset());
        Out.Print(MainClass.class, Out.LEVEL.INFO, "Кодировка программы: " + StandardCharsets.UTF_8);

        if (showLogo) {
            fl = new FoxLogo();
            try {
                fl.start("Версия: " + Registry.version, new BufferedImage[]{ImageIO.read(new File("./resources/logo.png"))}, FoxLogo.IMAGE_STYLE.WRAP, FoxLogo.BACK_STYLE.OPAQUE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        existingFilesCheck();
        buildIOM();

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

//		SystemInfo.printAll();
    }

    private static void existingFilesCheck() {
        if (!Registry.dataDir.exists()) {
            Exit.exit(14, "Error: Data directory is lost! Reinstall the game, please.");
        }

        File[] scanFiles = new File[]{Registry.usersDir, Registry.modsDir, Registry.picDir, Registry.curDir, Registry.dataDir, Registry.scenesDir};
        for (File f : scanFiles) {
            if (!f.exists()) {
                Out.Print(MainClass.class, Out.LEVEL.ACCENT, "Не найден путь " + f + "! Попытка создания...");
                try {
                    f.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Out.Print(MainClass.class, Out.LEVEL.INFO, "Проверка наличия необходимых директорий завершена.");
    }

    private static void buildIOM() {
        // имя последнего игрока:
        IOM.add(IOM.HEADERS.LAST_USER, Registry.lastUserFile);
        if (IOM.getString(IOM.HEADERS.LAST_USER, "LUSER").equals("none")) {
            IOM.set(IOM.HEADERS.LAST_USER, "LUSER", "newUser");
        }

        // файл для хранения конфигурации настроек игры текущего игрока:
        reloadUserData(IOM.getString(IOM.HEADERS.LAST_USER, "LUSER"), null, 0);
    }

    public static void reloadUserData(String userName, String userSex, int userAge) {
        IOM.add(IOM.HEADERS.CONFIG, new File(Registry.usersDir + "/" + userName + ".conf"));
        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_NAME, userName);

        if (IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_SEX).equals("none")) {
            if (userSex == null || userSex.equals("none")) {
                IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_SEX, "male");
            } else {
                IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_SEX, userSex);
            }
        }

        if (IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_AGE).equals("none")) {
            if (userAge <= 0 || userAge > 120) {
                IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_AGE, "14");
            } else {
                IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_AGE, userAge);
            }
        }

        // === Определение конфигурации аудио ===
        Out.Print(MainClass.class, Out.LEVEL.INFO, "Определение конфигурации аудио...");
        if (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.MUSIC_VOL) == -1) {
            IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.MUSIC_VOL, "0.10");
        }
        if (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUND_VOL) == -1) {
            IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUND_VOL, "0.25");
        }
        if (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.BACKG_VOL) == -1) {
            IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.BACKG_VOL, "0.50");
        }
        if (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.VOICE_VOL) == -1) {
            IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.VOICE_VOL, "0.75");
        }

        Out.Print(MainClass.class, Out.LEVEL.INFO, "Приветствуем игрока " + userName + "!");
        IOM.saveAll();
    }


    private static void loadImages() {
        // other:
        try {
            ResManager.add("picExitButtonSprite", new File(Registry.picDir + "/buttons/exits" + Registry.picExtention));
            ResManager.add("picPlayButtonSprite", new File(Registry.picDir + "/buttons/starts" + Registry.picExtention));
            ResManager.add("picMenuButtonSprite", new File(Registry.picDir + "/buttons/menus" + Registry.picExtention));

            ResManager.add("picBackButBig", new File(Registry.picDir + "/buttons/butListG" + Registry.picExtention));
            ResManager.add("picMenuButtons", new File(Registry.picDir + "/buttons/butListM" + Registry.picExtention));

            ResManager.add("picGameIcon", new File(Registry.picDir + "/32" + Registry.picExtention));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // cursors & backgrounds:
        try {
            ResManager.add("curSimpleCursor", new File(Registry.curDir + "/01" + Registry.picExtention));
            ResManager.add("curTextCursor", new File(Registry.curDir + "/02" + Registry.picExtention));
            ResManager.add("curGaleryCursor", new File(Registry.curDir + "/03" + Registry.picExtention));
            ResManager.add("curAnyCursor", new File(Registry.curDir + "/04" + Registry.picExtention));
            ResManager.add("curOtherCursor", new File(Registry.curDir + "/05" + Registry.picExtention));

            ResManager.add("picSaveLoad", new File(Registry.picDir + "/backgrounds/saveLoad" + Registry.picExtention));
            ResManager.add("picMenuBase", new File(Registry.picDir + "/backgrounds/menuBase" + Registry.picExtention));
            ResManager.add("picAurora", new File(Registry.picDir + "/backgrounds/aurora" + Registry.picExtention));
            ResManager.add("picGallery", new File(Registry.picDir + "/backgrounds/gallery" + Registry.picExtention));
            ResManager.add("picMenupane", new File(Registry.picDir + "/backgrounds/menupane" + Registry.picExtention));
            ResManager.add("picGender", new File(Registry.picDir + "/backgrounds/gender" + Registry.picExtention));
            ResManager.add("picGamepane", new File(Registry.picDir + "/backgrounds/gamepane" + Registry.picExtention));
            ResManager.add("picAutrs", new File(Registry.picDir + "/backgrounds/autrs" + Registry.picExtention));
            ResManager.add("picGameMenu", new File(Registry.picDir + "/backgrounds/gameMenu" + Registry.picExtention));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // heroes:
        try {
            ResManager.add("0", new File(Registry.picDir + "/hero/00" + Registry.picExtention));

            // fema:
            ResManager.add("1", new File(Registry.picDir + "/hero/01" + Registry.picExtention));
            ResManager.add("2", new File(Registry.picDir + "/hero/02" + Registry.picExtention));
            ResManager.add("3", new File(Registry.picDir + "/hero/03" + Registry.picExtention));
            ResManager.add("4", new File(Registry.picDir + "/hero/04" + Registry.picExtention));

            // male:
            ResManager.add("5", new File(Registry.picDir + "/hero/05" + Registry.picExtention));
            ResManager.add("6", new File(Registry.picDir + "/hero/06" + Registry.picExtention));
            ResManager.add("7", new File(Registry.picDir + "/hero/07" + Registry.picExtention));
            ResManager.add("8", new File(Registry.picDir + "/hero/08" + Registry.picExtention));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // npc avatars:
        try {
            for (File f : Registry.npcAvatarsDir.listFiles()) {
                ResManager.add(f.getName().replace(Registry.picExtention, ""), f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // scenes load:
        try {
            for (File f : Registry.scenesDir.listFiles()) {
                ResManager.add(f.getName().replace(Registry.picExtention, ""), f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadAudio() {
        try {
            Media.loadSounds(new File("./resources/sound/").listFiles());
            Media.loadVoices(new File("./resources/sound/voices/").listFiles());

            Media.loadMusics(new File("./resources/mus/musikThemes/").listFiles());
            Media.loadBackgs(new File("./resources/mus/fonMusic/").listFiles());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void connectMods() {
        Out.Print(MainClass.class, Out.LEVEL.INFO, "Сканирование папки mods...");

        try {
            new ModsLoader(new File("./mods/"));
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