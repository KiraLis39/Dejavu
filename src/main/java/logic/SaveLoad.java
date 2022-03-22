package logic;

import fox.Out;
import fox.Out.LEVEL;
import registry.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveLoad {
    private static int saveCount = 0;
    private static final int saveTicks = 60;

    public static void tick() {
        saveCount++;
        if (saveCount >= saveTicks) {
            runAutoSaving();
            saveCount = 0;
        }
    }

    private static void runAutoSaving() {
        String autoSavePath = Registry.userSaveDir + "/autosave.tmp";
        Path autoSave = Paths.get(autoSavePath);
        Out.Print(SaveLoad.class, LEVEL.ACCENT, "Работа с автосохранением " + autoSave);

        if (Files.notExists(autoSave)) {
            try {
                Files.createFile(autoSave);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        saveEngine(autoSave);
    }

    public static void saveEngine(Path savePath) {
        Out.Print(SaveLoad.class, LEVEL.ACCENT, "Подготовка к созданию нового сохранения: " + savePath);

//        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(autosave))) {
//            IOM.set(savePropName, IOMs.CONFIG.KARMA_POS, IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_POS));
//            IOM.set(savePropName, IOMs.CONFIG.KARMA_NEG, IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_NEG));

//			IOM.setAnyValue(savePropName, "carmaAnn", IOM.getAnyValue("UCONF", "carmaAnn"));
//			IOM.setAnyValue(savePropName, "carmaOleg", IOM.getAnyValue("UCONF", "carmaOleg"));
//			IOM.setAnyValue(savePropName, "carmaDmitrii", IOM.getAnyValue("UCONF", "carmaDmitrii"));
//			IOM.setAnyValue(savePropName, "carmaOlga", IOM.getAnyValue("UCONF", "carmaOlga"));
//			IOM.setAnyValue(savePropName, "carmaOksana", IOM.getAnyValue("UCONF", "carmaOksana"));
//			IOM.setAnyValue(savePropName, "carmaKuro", IOM.getAnyValue("UCONF", "carmaKuro"));
//			IOM.setAnyValue(savePropName, "carmaMaria", IOM.getAnyValue("UCONF", "carmaMaria"));
//			IOM.setAnyValue(savePropName, "carmaMishka", IOM.getAnyValue("UCONF", "carmaMishka"));
//			IOM.setAnyValue(savePropName, "carmaLissa", IOM.getAnyValue("UCONF", "carmaLissa"));
//			IOM.setAnyValue(savePropName, "rep", IOM.getAnyValue("UCONF", "rep"));

//			IOM.setAnyValue(savePropName, "iconWas", TextEngine.getIcon());
//			IOM.setAnyValue(savePropName, "personWas", TextEngine.getPersona());
//			IOM.setAnyValue(savePropName, "musicWas", TextEngine.getMus());
//			IOM.setAnyValue(savePropName, "fonWas", TextEngine.getFon());
//			IOM.setAnyValue(savePropName, "blockName", TextEngine.isBlockName());
//			IOM.setAnyValue(savePropName, "dialogCount", String.valueOf(TextEngine.getCountDialogs()));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    public static void Loading(String userName) {
//		File save = new File(new File(IOM.get("AUTOSAVE", "propFile")).getParentFile().getPath() + "/" + loadName);

//		Out.Print(className, 1, "Загрузка сохранения: " + loadName + " в папке: " + new File(new File(IOM.getAnyValue("AUTOSAVE", "propFile")).getParent()).getPath());
//		if (save.exists()) {Out.Print(className, 0, "Файл успешно найден: " + save.getName());
//		} else {
//			Out.Print(className, 3, "Файл НЕ найден: " + save.getName());
//			return;
//		}

//		try (InputStreamReader isrLoad = new InputStreamReader(new FileInputStream(save));) {
//			IOM.addNewProperti("SAVE", save);
//			IOM.loadCurrent("SAVE");
//			
//			IOM.setAnyValue("UCONF", "carmaPos", IOM.getAnyValue("SAVE", "carmaPos"));
//			IOM.setAnyValue("UCONF", "carmaNeg", IOM.getAnyValue("SAVE", "carmaNeg"));
//
//			IOM.setAnyValue("UCONF", "carmaAnn", IOM.getAnyValue("SAVE", "carmaAnn"));
//			IOM.setAnyValue("UCONF", "carmaOleg", IOM.getAnyValue("SAVE", "carmaOleg"));
//			IOM.setAnyValue("UCONF", "carmaDmitrii", IOM.getAnyValue("SAVE", "carmaDmitrii"));
//			IOM.setAnyValue("UCONF", "carmaOlga", IOM.getAnyValue("SAVE", "carmaOlga"));
//			IOM.setAnyValue("UCONF", "carmaOksana", IOM.getAnyValue("SAVE", "carmaOksana"));
//			IOM.setAnyValue("UCONF", "carmaKuro", IOM.getAnyValue("SAVE", "carmaKuro"));
//			IOM.setAnyValue("UCONF", "carmaMaria", IOM.getAnyValue("SAVE", "carmaMaria"));
//			IOM.setAnyValue("UCONF", "carmaMishka", IOM.getAnyValue("SAVE", "carmaMishka"));
//			IOM.setAnyValue("UCONF", "carmaLissa", IOM.getAnyValue("SAVE", "carmaLissa"));
//			IOM.setAnyValue("UCONF", "rep", IOM.getAnyValue("SAVE", "rep"));
//			
//			IOM.saveCurrent("UCONF");
//			
//			TextEngine.Init();
//			new NewGameFrame();
//			
//			TextEngine.setIcon(IOM.getAnyValue("SAVE", "iconWas"));
//			TextEngine.setPersona(IOM.getAnyValue("SAVE", "personWas"));
//			TextEngine.setMus(IOM.getAnyValue("SAVE", "musicWas"));
//			TextEngine.setFon(IOM.getAnyValue("SAVE", "fonWas"));
//			
//			if (IOM.getAnyValue("SAVE", "blockName").equals("")) {
//				Out.Print(className, 3, "Имя блока отсутствует в сохранении игры! Сохранение неработоспособно!");
//				JOptionPane.showMessageDialog(null, "Сохранение повреждено. Смотри лог игры.", "Ошибка!", JOptionPane.ERROR_MESSAGE);
//			}
//			
//			TextEngine.blockChanger(IOM.getAnyValue("SAVE", "blockName"));
//			TextEngine.setCountDialogs(Integer.valueOf(IOM.getAnyValue("SAVE", "dialogCount")));
//			
//			NewGameFrame.turnOn();
//		} catch (Exception e) {e.printStackTrace();}
    }
}