package logic;

import GUI.GamePlay;
import components.FOptionPane;
import lombok.Data;
import lombok.NonNull;
import registry.Registry;

import java.io.File;
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
    private List<String> variants;
    private ArrayList<String> allowedVariants;
    private int currentLineIndex;
    private boolean isChoice;

    public void load(@NonNull String scenarioFileName) throws IOException {
        Path scenario = Paths.get(blockPath + "/" + scenarioFileName + sBlockExtension);
        lines = Files.readAllLines(scenario, charset).stream().filter(s -> !s.isBlank()).toList();
        variants = lines.stream().filter(s -> s.startsWith("var ")).toList();

        System.out.println("LOADED: " + lines + "; VARS: " + variants);

        currentLineIndex = -1;
    }

    public void choice(int chosenVariantIndex) {
        if (isChoice) {
            if (chosenVariantIndex == -1) {
                System.out.println("Denied there. Need to choice.");
            } else if (chosenVariantIndex > allowedVariants.size()) {
                System.out.println("Variant is too high!");
            } else {
                System.out.println("Variant chosen: " + chosenVariantIndex);
                isChoice = false;
                try {
                    load(allowedVariants.get(chosenVariantIndex).split("R")[1]);
                    choice(-1);
                } catch (IOException e) {
                    e.printStackTrace();
                    new FOptionPane("Ошибка сценария:", "Не удалось загрузить файл сценария.");
                }
            }
            return;
        }

        if (currentLineIndex < lines.size() - 1) {
            currentLineIndex++;
        } else {
            System.out.println("=== END OF SCENARIO ===");
            return;
        }
        lineParser(lines.get(currentLineIndex));
        if (lines.get(currentLineIndex + 1).startsWith("var ")) {
            // VARIANTS PARSE:
            isChoice = true;
            allowedVariants = new ArrayList<>(
                    variants.stream().filter(s -> Integer.parseInt(s.split(" ")[1]) <= userConf.getCycleCount()).toList()
                            .stream().map(s -> s.split("R")[1].replace("\"", "").trim()
                                    + "R" + s.split("R")[2].replace("\"", "").trim()).toList());
            GamePlay.setAnswers(allowedVariants);
        } else if (lines.get(currentLineIndex + 1).startsWith("nf ")) {
            System.out.println("=== NEXT FILE PLEASE ===");
        }
    }

    private void lineParser(@NonNull String line) {
        if (line.startsWith("H-")) {
            // NPC set:
            switchNpc(line.split("-"));
        } else {
            // SCREEN set:
            switchScreen(line.split(";"));
        }
    }

    private void switchNpc(String[] lineData) {
        // H-Ann-upWork-simple
        if (lineData[1].equals("Clear")) {
            GamePlay.setScene(null, "Clear");
            choice(-1);
            return;
        }

        File[] variants = new File(Registry.personasDir + "/" + lineData[1] + "/" + lineData[2] + "/" + lineData[3]).listFiles();
        GamePlay.setScene(null, variants[rand.nextInt(variants.length)].getName().replace(picExtension, ""));
        choice(-1);
    }

    private void switchScreen(String[] lineData) {
        String dialogOwner, dialogText;
        String sceneName = null;
        ArrayList<String> answers = null;
        String meta = null;

        // dialog owner and dialog text:
        dialogOwner = lineData[0].split(":")[0].trim();
        dialogOwner = dialogOwner.equals("USERNAME") ? userConf.getUserName() : dialogOwner;

        dialogText = lineData[0].split(":")[1].replace("\"", "").trim();
        dialogText = dialogText.replaceAll("USERNAME", userConf.getUserName());

        // other data:
        if (lineData.length > 1) {
            String mediaName;

            for (String lineDatum : lineData) {
                if (lineDatum.trim().startsWith("screen")) {
                    sceneName = lineDatum.split(":")[1].replaceAll("\"", "").trim();
                    continue;
                }
                if (lineDatum.trim().startsWith("music")) {
                    mediaName = lineDatum.split(":")[1].replaceAll("\"", "").trim();
                    if (mediaName.equals("STOP")) {
                        musicPlayer.stop();
                    } else {
                        musicPlayer.play(mediaName);
                    }
                    continue;
                }
                if (lineDatum.trim().startsWith("backg")) {
                    mediaName = lineDatum.split(":")[1].replaceAll("\"", "").trim();
                    if (mediaName.equals("STOP")) {
                        backgPlayer.stop();
                    } else {
                        backgPlayer.play(mediaName);
                    }
                    continue;
                }
                if (lineDatum.trim().startsWith("sound")) {
                    soundPlayer.play(lineDatum.split(":")[1].replaceAll("\"", "").trim());
                    continue;
                }
                if (lineDatum.trim().startsWith("meta")) {
                    meta = lineDatum.split(":")[1].replaceAll("\"", "").trim();
                    continue;
                }
                if (lineDatum.trim().startsWith("voice")) {
//                  voicePlayer.play(lineData[i].split(":")[1].replaceAll("\"", "").trim());
                    continue;
                }
            }
        }

        GamePlay.setScene(sceneName, null);
        GamePlay.setDialog(dialogOwner, dialogText, answers);

        if (meta != null) {
            metaProcessor(meta);
        }
    }

    private void metaProcessor(String meta) {
        System.out.println("Meta: " + meta);
    }

    public void close() {
        backgPlayer.stop();
        musicPlayer.stop();
        soundPlayer.stop();
//        voicePlayer.stop();

        currentLineIndex = -1;
//        lines.clear();
    }
}