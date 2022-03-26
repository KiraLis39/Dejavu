package logic;

import GUI.GamePlay;
import lombok.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

@Data
public class ScenarioEngine {
    private final Random rand = new Random();

    private ArrayList<String> answers;

    private int currentHeroAvatar;
    private String currentHeroName;
    private String currentText;
    private GamePlay gameFrame;
    private BufferedImage currentNpcImage;


    public void choice(int chosenVariantIndex) {

    }

    public void load(File scenarioFile) {
        //...
        choice(-1);
    }

//    private void lineParser(String line) {
//        if (line == null) {
//            throw new RuntimeException("reactionManager(): income line = NULL. Line dosnt exist?");
//        }
//
//        if (line.startsWith("H-")) {
//            nextScene(line);
//            return;
//        }
//
//        String HERO, DIALOG, SCREEN = null, MUSIC = null, BACKG = null, SOUND = null, VOICE = null, META = null;
//        String[] lineData = line.split(";");
//
//        // name and dialog:
//        HERO = lineData[0].split(":")[0].trim();
//        if (HERO.equals("null")) {
//            currentHeroName = "Кто-то:";
//            currentHeroAvatar = 0;
//        } else {
//            currentHeroName = HERO.equals("USERNAME") ? userConf.getUserName() + ":" : HERO + ":";
//            currentHeroAvatar = HERO.equals("USERNAME") ? userConf.getAvatarIndex() : npcAvatarIndex.get(HERO);
//        }
//
//        DIALOG = lineData[0].split(":")[1].replaceAll("\"", "").trim();
//        DIALOG = DIALOG.replaceAll("USERNAME", userConf.getUserName());
//
//        // media:
//        if (lineData.length > 1) {
//            for (int i = 1; i < lineData.length - 1; i++) {
//                if (lineData[i].trim().startsWith("music")) {
//                    MUSIC = lineData[i].split(":")[1].replaceAll("\"", "").trim();
//                    continue;
//                }
//                if (lineData[i].trim().startsWith("backg")) {
//                    BACKG = lineData[i].split(":")[1].replaceAll("\"", "").trim();
//                    continue;
//                }
//                if (lineData[i].trim().startsWith("sound")) {
//                    SOUND = lineData[i].split(":")[1].replaceAll("\"", "").trim();
//                    continue;
//                }
//                if (lineData[i].trim().startsWith("voice")) {
//                    VOICE = lineData[i].split(":")[1].replaceAll("\"", "").trim();
//                    continue;
//                }
//                if (lineData[i].trim().startsWith("screen")) {
//                    SCREEN = lineData[i].split(":")[1].replaceAll("\"", "").trim();
//                    continue;
//                }
//
//                if (lineData[i].trim().startsWith("meta")) {
//                    META = lineData[i].split(":")[1].replaceAll("\"", "").trim();
//                    continue;
//                }
//            }
//        }
//
//        if (SCREEN != null) {
//            gameFrame.setCenterImage(SCREEN);
//        }
//
//        if (MUSIC != null) {
//            if (MUSIC.equals("STOP")) {
//                musicPlayer.stop();
//            } else {
//                musicPlayer.play(MUSIC);
//            }
//        }
//        if (BACKG != null) {
//            if (BACKG.equals("STOP")) {
//                backgPlayer.stop();
//            } else {
//                backgPlayer.play(BACKG);
//            }
//        }
//
//        if (SOUND != null) {
//            soundPlayer.play(SOUND);
//        }
//        if (VOICE != null) {
//            voicePlayer.play(VOICE);
//        }
//
//        if (META != null) {
//            String[] metaData = META.split("\",\"");  /* делёж по символам: "," */
//            System.out.println(">>> Meta: " + Arrays.asList(metaData));
//        }
//
//        text = DIALOG;
//        answers = answers == null || answers.size() == 0 ? null : answers;
//        gameFrame.updateDialogText();
//    }
}