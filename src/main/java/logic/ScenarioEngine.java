package logic;

import gui.GamePlay;
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
import java.util.List;
import java.util.Random;

import static registry.Registry.*;

@Data
public class ScenarioEngine {
    private final Random rand = new Random();
    private List<String> lines;
    private List<String> variants;
    private ArrayList<String> allowedVariants;
    private boolean isChoice;

    public void load(@NonNull String scenarioFileName) throws IOException {
        Path scenario = Paths.get(blockPath + "\\" + scenarioFileName.trim() + sBlockExtension);
        lines = Files.readAllLines(scenario, charset).stream().filter(s -> !s.isBlank() && !s.startsWith("var ")).toList();
        variants = Files.readAllLines(scenario, charset).stream().filter(s -> !s.isBlank() && s.startsWith("var ")).toList();
//        choice(-1);
    }

    public void choice(int chosenVariantIndex) {
        String loadedScript = null;

        if (chosenVariantIndex != -1 && allowedVariants == null && userSave.getLineIndex() > -1 && !lines.get(userSave.getLineIndex()).startsWith("nf ")) {
            System.err.println("Быд выбран вариант, но лист вариантов пуст!");
            return;
        }

        try {
            if (!isChoice && userSave.getLineIndex() > -1 && lines.get(userSave.getLineIndex() + 1).startsWith("nf ")) {
                loadedScript = lines.get(userSave.getLineIndex() + 1).replace("nf ", "");
                load(loadedScript);
                userSave.setScript(loadedScript);
                userSave.setLineIndex(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new FOptionPane("Ошибка сценария:", "Не удалось загрузить файл сценария: " + loadedScript);
        }

        if (isChoice) {
            if (chosenVariantIndex == -1) {
                System.out.println("Denied there. Need to choice.");
            } else if (chosenVariantIndex <= allowedVariants.size()) {
                isChoice = false;
                try {
                    loadedScript = allowedVariants.get(chosenVariantIndex).split("R ")[1];
                    load(loadedScript);
                    userSave.setScript(loadedScript);
                    userSave.setLineIndex(-1);
                    choice(userSave.getLineIndex());
                } catch (Exception e) {
                    e.printStackTrace();
                    new FOptionPane("Ошибка сценария:", "Не удалось загрузить файл сценария: " + loadedScript);
                }
            }
        } else {
            if (userSave.getLineIndex() < lines.size() - 2) {
                userSave.setLineIndex(userSave.getLineIndex() + 1);
                lineParser(lines.get(userSave.getLineIndex()));
            } else {
                userSave.setLineIndex(userSave.getLineIndex() + 1);
                if (userSave.getLineIndex() == lines.size() - 1 && (variants != null && variants.size() > 0)) {
                    lineParser(lines.get(userSave.getLineIndex()));
                    takeAnswers();
                }
                isChoice = true;
            }
        }
    }

    private void takeAnswers() {
        allowedVariants = new ArrayList<>(
                variants.stream().filter(s -> Integer.parseInt(s.split(" ")[1]) <= userSave.getCycleCount()).toList()
                        .stream().map(s -> s.split("R ")[1].replace("\"", "").trim()
                                + " R " + s.split("R ")[2].replace("\"", "").trim()).toList());
        GamePlay.setAnswers(allowedVariants);
    }

    private void lineParser(@NonNull String line) {
        String[] lineData = line.split(";");
        String dialogOwner, dialogText;
        String sceneName = null, npcImage = null;
        ArrayList<String> answers = null;
        int carma = 0;

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
                    mediaName = lineDatum.split(":")[1].replaceAll("\"", "").trim();
                    if (mediaName.equals("-")) {continue;}
                    sceneName = mediaName;
                    continue;
                }
                if (lineDatum.trim().startsWith("music")) {
                    mediaName = lineDatum.split(":")[1].replaceAll("\"", "").trim();
                    if (mediaName.equals("-")) {continue;}
                    if (mediaName.equalsIgnoreCase("STOP")) {
                        musicPlayer.stop();
                    } else {
                        musicPlayer.play(mediaName);
                        userSave.setMusicPlayed(mediaName);
                    }
                    continue;
                }
                if (lineDatum.trim().startsWith("backg")) {
                    mediaName = lineDatum.split(":")[1].replaceAll("\"", "").trim();
                    if (mediaName.equals("-")) {continue;}
                    if (mediaName.equalsIgnoreCase("STOP")) {
                        backgPlayer.stop();
                    } else {
                        backgPlayer.play(mediaName);
                        userSave.setBackgPlayed(mediaName);
                    }
                    continue;
                }
                if (lineDatum.trim().startsWith("sound")) {
                    mediaName = lineDatum.split(":")[1].replaceAll("\"", "").trim();
                    if (mediaName.equals("-")) {continue;}
                    if (mediaName.equalsIgnoreCase("STOP")) {
                        soundPlayer.stop();
                    } else {
                        soundPlayer.play(mediaName);
                        userSave.setSoundPlayed(mediaName);
                    }
                    continue;
                }
//                if (lineDatum.trim().startsWith("voice")) {
//                    mediaName = lineDatum.split(":")[1].replaceAll("\"", "").trim();
//                    if (mediaName.equals("-")) {continue;}
//                    voicePlayer.play(mediaName);
//                    continue;
//                }
                if (lineDatum.trim().startsWith("npc")) {
                    npcImage = switchNpc(lineDatum.trim().split(":")[1].replaceAll("\"", "").split(","));
                    continue;
                }
                if (lineDatum.trim().startsWith("meta")) {
                    metaProcessor(lineDatum.trim().split(":")[1].replaceAll("\"", "").split(","));
                    continue;
                }
                if (lineDatum.trim().startsWith("carma")) {
                    carma = Integer.parseInt(lineDatum.split(":")[1].replaceAll("\"", "").trim());
                }
            }
        }

        GamePlay.setScene(sceneName, npcImage);
        GamePlay.setDialog(dialogOwner, dialogText, carma);
    }

    private String switchNpc(String[] lineData) {
        // npc:Ann,upWork,simple
        if (lineData[0].equals("-")) {
            return "Clear";
        }

        File[] variants = new File(Registry.personasDir + "/" + lineData[0] + "/" + lineData[1] + "/" + lineData[2]).listFiles();
        return variants[rand.nextInt(variants.length)].getName().replace(picExtension, "");
    }

    private void metaProcessor(String[] meta) {
        String chapter = meta[0].trim();
        boolean isNextDay = Boolean.valueOf(meta[1].trim());
        if (!chapter.equals("-")) {
            GamePlay.setChapter(chapter);
        }
        if (isNextDay) {
            GamePlay.dayAdd();
        }
    }

    public void close() {
        backgPlayer.stop();
        musicPlayer.stop();
        soundPlayer.stop();
//        voicePlayer.stop();

//        currentLineIndex = -1;
//        lines.clear();
    }
}