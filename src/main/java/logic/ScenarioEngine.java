package logic;

import GUI.GamePlay;
import lombok.Data;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static registry.Registry.*;

@Data
public class ScenarioEngine {
    private final Random rand = new Random();
    private List<String> lines;
    private int currentLineIndex;

    public void load(@NonNull String scenarioFileName) throws IOException {
        Path scenario = Paths.get(blockPath + "/" + scenarioFileName + sBlockExtension);
        lines = Files.readAllLines(scenario, charset);
        currentLineIndex = 0;
    }

    public void choice(int chosenVariantIndex) {
        if (chosenVariantIndex == -1) {
            System.out.println("Default variant was chosen.");
        } else {
            System.out.println("Variant " + chosenVariantIndex + " was chosen.");
        }

        // temporary test:
        do {currentLineIndex++;
        } while (lines.get(currentLineIndex).isBlank());
        lineParser(lines.get(currentLineIndex));
    }

    private void lineParser(@NonNull String line) {
        String meta = null;
        String dialogOwner, dialogText;
        String sceneName = null, npcName = null;
        ArrayList<String> answers = null;

        String[] lineData = line.split(";");

//        if (line.startsWith("H-")) {
//            nextScene(line);
//            return;
//        }

        // name and dialog:
        dialogOwner = lineData[0].split(":")[0].trim();
        if (dialogOwner.equalsIgnoreCase("null")) {
            dialogOwner = "Кто-то:";
        } else {
            dialogOwner = dialogOwner.equals("USERNAME") ? userConf.getUserName() + ":" : dialogOwner + ":";
        }

        dialogText = lineData[0].split(":")[1].replace("\"", "").trim();
        dialogText = dialogText.replaceAll("USERNAME", userConf.getUserName());

        // media:
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

//        if (SCREEN != null) {
//            gameFrame.setCenterImage(SCREEN);
//        }

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

//        if (SOUND != null) {
//            soundPlayer.play(SOUND);
//        }
//        if (VOICE != null) {
//            voicePlayer.play(VOICE);
//        }

//        if (META != null) {
//            String[] metaData = META.split("\",\"");  /* делёж по символам: "," */
//            System.out.println(">>> Meta: " + Arrays.asList(metaData));
//        }

//        text = DIALOG;
//        answers = answers == null || answers.size() == 0 ? null : answers;
//        gameFrame.updateDialogText();


//        backgPlayer.play();
//        musicPlayer.play();
        GamePlay.getGamePlay().setScene(sceneName, npcName);
//        soundPlayer.play();
//        voicePlayer.play();
        GamePlay.getGamePlay().setDialog(dialogOwner, dialogText, answers);
    }
}