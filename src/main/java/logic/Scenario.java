package logic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

import GUI.GameFrame;
import adds.IOM;
import adds.Out;
import adds.Out.LEVEL;
import tools.Media;
import registry.Registry;


public class Scenario {
	private static Random rand = new Random();
	
	private static ArrayList<String> answers = new ArrayList<String>();
	private static LinkedList<String> dialogBlockArray;
	private static Boolean isChoseMode = false;
	private static int blockLineCount = 0;
	private static ArrayList<String> resultAnswersList;
	
	
	// loading scenario file:
	public static void load(File loadFile) {
//		File blockFile = new File(loadFile);
		
		if (loadFile.exists() && loadFile.canRead()) {
			Out.Print(Scenario.class, LEVEL.INFO, "Читаем loadBlock " + loadFile);
			
			try {
				dialogBlockArray = new LinkedList<String> (Files.readAllLines(Paths.get(loadFile.getPath()), Charset.forName("UTF-8")));
				for (int i = 0; i < dialogBlockArray.size(); i++) {
					if (dialogBlockArray.get(i).length() <= 1) {
						dialogBlockArray.remove(i);
						i--;
					}
				}
				
				setChoise(false);
				blockLineCount = 0;
				if (answers != null) {answers.clear();}
			} catch (IOException e) {e.printStackTrace();}
		} else {Out.Print(Scenario.class, LEVEL.ERROR, "ОШИБКА В loadBlock c файлом " + loadFile);}
	}


	public static void step(int keyChosen) {
		if (keyChosen == -1) {
			System.out.println("Choise: " + isChoise());
			if (isChoise()) {/* if active choise-mode, restrict Space key: */
				answers = variantsAnalyser(nextLine());
				GameFrame.setDialogText(null, answers);
			} else {reactionManager(nextLine());}
		} else {
			System.out.println("\ngetReaction(): keyChosen = " + keyChosen + " (isChoise? " + isChoise() + ").");
			if (!isChoise() && keyChosen == 0) {reactionManager(nextLine());
			} else {
				// обработка выбора одного из вариантов развилки здесь:
				File tmp = GameFrame.getScenario(keyChosen);
				if (tmp == null) {
					System.out.println("Waiting for choise of scensrio index...");
					return;
				}
				load(tmp); // nextScenarioFile.getPath()
				step(-1);
			}
		}
	}
	
	private static String nextLine() {
		blockLineCount++;

		if (!isChoise()) {
			try {
				String nextLine = dialogBlockArray.get(blockLineCount).trim();
				if (nextLine.startsWith("var ")) {setChoise(true);}
				System.out.println("\nNow show: " + dialogBlockArray.get(blockLineCount - 1).trim());
				System.out.println("\nNextLine line: " + nextLine);
			} catch (Exception e) {/* IGNORE OUT VARS */}
		}
		
		try {return dialogBlockArray.get(blockLineCount - 1).trim();			
		} catch (Exception e) {return null;}
	}
	
	private static void reactionManager(String line) {
		if (line == null) {
			throw new RuntimeException("reactionManager(): income line = NULL. Line dosnt exist?");
		}
		
		if (line.startsWith("H-")) {
			nextScene(line);
			return;
		}

		String HERO, DIALOG, SCREEN = null, MUSIC = null, BACKG = null, SOUND = null, VOICE = null, META = null;
		String[] lineData = line.split(";");
		
		// name and dialog:
		HERO = lineData[0].split(":")[0].trim();
		if (HERO.equals("null")) {
			GameFrame.setHeroName("Кто-то:");
			GameFrame.setHeroAvatar(null);
		} else {
			GameFrame.setHeroName(HERO.equals("USERNAME") ? IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_NAME) + ":" : HERO + ":");
			GameFrame.setHeroAvatar(HERO.equals("USERNAME") ? IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.AVATAR_INDEX) : HERO);
		}
		
		DIALOG = lineData[0].split(":")[1].replaceAll("\"", "").trim();
		DIALOG = DIALOG.replaceAll("USERNAME", IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_NAME));
		
		// media:
		if (lineData.length > 1) {
			for (int i = 1; i < lineData.length - 1; i++) {
				if (lineData[i].trim().startsWith("music")) 		{MUSIC = lineData[i].split(":")[1].replaceAll("\"", "").trim(); 		continue;}
				if (lineData[i].trim().startsWith("backg")) 		{BACKG = lineData[i].split(":")[1].replaceAll("\"", "").trim(); 		continue;}
				if (lineData[i].trim().startsWith("sound")) 		{SOUND = lineData[i].split(":")[1].replaceAll("\"", "").trim(); 		continue;}
				if (lineData[i].trim().startsWith("voice")) 		{VOICE = lineData[i].split(":")[1].replaceAll("\"", "").trim(); 			continue;}	
				if (lineData[i].trim().startsWith("screen")) 	{SCREEN = lineData[i].split(":")[1].replaceAll("\"", "").trim(); 		continue;}
				
				if (lineData[i].trim().startsWith("meta")) 		{META = lineData[i].split(":")[1].replaceAll("\"", "").trim(); 			continue;}
			}
		}

		if (SCREEN != null) {GameFrame.setCenterImage(SCREEN);}
		
		if (MUSIC != null) {
			if (MUSIC.equals("STOP")) {Media.stopMusic();
			} else {Media.playMusic(MUSIC, true);}
		}
		if (BACKG != null) {
			if (BACKG.equals("STOP")) {Media.stopBackg();} else {Media.playBackg(BACKG);}
		}
		
		if (SOUND != null) {Media.playSound(SOUND);}		
		if (VOICE != null) {Media.playVoice(VOICE);}
		
		if (META != null) {
			String[] metaData = META.split("\",\"");  /* делёж по символам: "," */
			System.out.println(">>> Meta: " + metaData);
		}
		
		GameFrame.setDialogText(DIALOG, answers == null || answers.size() == 0 ? null : answers);
	}
	
	private static ArrayList<String> variantsAnalyser(String line) {
		if (line == null) {return null;}
		
		resultAnswersList = new ArrayList<String>(5);
		
		do {
			System.out.println("variantsAnalyser has line: " + line);
			String[] varData = line.replace("\"", "").split("R");
			int tmp = Integer.parseInt(varData[0].split(" ")[1]);
			System.out.println("or our data: " + varData[0] + " (" + tmp + ") => (" + IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.CYCLE_COUNTER) + ");");
			if (tmp <= IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.CYCLE_COUNTER)) {
				resultAnswersList.add(varData[1].trim() + "%" + varData[2].trim());
			}
		} while ((line = nextLine()) != null);
		
		System.out.println("variantsAnalyser has vars: " + resultAnswersList.toString());
		return resultAnswersList;
	}

	
	private static void nextScene(String line) {
		String[] lineData = line.split("-");
		
		if (lineData[1].equals("Clear")) {
			GameFrame.setNpcImage(null);			
			step(-1);
			return;
		}
		
		// lineData[1];  npc pictures folder name
		// lineData[2];  npc pictures type
		// lineData[3];  npc pictures mood
		File[] variants = new File(Registry.personasDir + "/" + lineData[1] + "/" + lineData[2] + "/" + lineData[3]).listFiles();
		try {GameFrame.setNpcImage(ImageIO.read(variants[rand.nextInt(variants.length)]));
		} catch (IOException e) {e.printStackTrace();}
		
		step(-1);
	}

	private static Boolean isChoise() {return isChoseMode;}
	private static void setChoise(boolean _isChoseMode) {isChoseMode = _isChoseMode;}
}