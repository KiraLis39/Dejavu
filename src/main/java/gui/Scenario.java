package gui;

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
import java.util.stream.IntStream;

import static registry.Registry.*;

@Data
public class Scenario extends ScenarioBase {
    private GamePlay play;

    public Scenario(GamePlay play) {
        this.play = play;
    }

    public void load(@NonNull String scenarioFileName) throws IOException {
        Path scenario = Paths.get(blockPath + "\\" + scenarioFileName.trim() + sBlockExtension);
        lines = Files.readAllLines(scenario, charset).stream().filter(s -> !s.isBlank() && !s.startsWith("var ") && !s.startsWith("logic ")).toList();
        List<String> allVarsList = Files.readAllLines(scenario, charset).stream().filter(s -> !s.isBlank() && s.startsWith("var ")).toList();
        varsList = new ArrayList<>(
                allVarsList.stream().filter(s -> Integer.parseInt(s.split(" ")[1]) <= userSave.getCycleCount()).toList()
                        .stream().map(s -> s.split("R ")[1].replace("\"", "").trim()
                                + " R " + s.split("R ")[2].replace("\"", "").trim()).toList());

        checkCycleCount(scenarioFileName);

        userSave.setScript(scenarioFileName);
        userSave.setLineIndex(0);
    }

    private void checkCycleCount(String scenarioName) {
        if (scenarioName.trim().contains("00_INIT_SCENARIO")) {
            userSave.setCycleCount(0);
            userSave.setToday(3);
            userSave.setMonth(GamePlay.MONTH.июнь);
        } else if (scenarioName.trim().contains("00SecondAwaked")) {
            userSave.setCycleCount(1);
            userSave.setToday(3);
            userSave.setMonth(GamePlay.MONTH.июнь);
        } else if (scenarioName.trim().contains("00FullAwaked")) {
            userSave.setCycleCount(userSave.getCycleCount() + 1);
            userSave.setToday(3);
            userSave.setMonth(GamePlay.MONTH.июнь);
        }
    }

    /**
     * Сюда может придти либо -1 как "Далее" по умолчанию,
     * либо вариант ответа от 0 до 5.
     *
     * В зависимости от типа скрипта (окончания) выполняется следующая обработка:
     *  "Следующая линия", если скрипт еще не завершен;
     *  "Следующий скрипт", если скрипт кончается на nf <путь_к_скрипту>;
     *  "Логический + Следующий скрипт", если требуется автоматический расчет следующего скрипта.
     *  всё это обрабатывает метод {@see this.defaultNext()}
     *
     *  "Режим ответа", если требуется ввод одного из вариантов ответов;
     *  это уже обрабатывает метод {@link this.choseControl(ScenarioBase.VARIANTS)}
     *
     * @param answer - индекс ответа пользователя от -1 до 5
     */
    public void choice(VARIANTS answer) {
        try {
            if (answer.index <= VARIANTS.VAR_ONE.index && !isChoice) {
                // default nest line:
                defaultNext();
            } else if (answer.index >= 0 && isChoice) {
                // choose a variant:
                choseControl(answer.index);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new FOptionPane("Ошибка сценария:", "Не удалось загрузить файл сценария: " + e.getMessage());
            return;
        }
    }

    /**
     * Сюда может придти либо -1 как "Далее" по умолчанию,
     * либо вариант ответа от 0 с аналогичным воздействием (если пользователь
     * нажал цифру "1" вместо пробела, мало ли ему так проще.
     *
     * В зависимости от типа скрипта (окончания) выполняется следующая обработка:
     *  "Следующая линия", если скрипт еще не завершен;
     *  "Следующий скрипт", если скрипт кончается на nf <путь_к_скрипту>;
     *  "Логический + Следующий скрипт", если требуется автоматический расчет следующего скрипта.
     */
    private void defaultNext() throws IOException, ArrayIndexOutOfBoundsException {
        if (userSave.getLineIndex() >= lines.size()) {
            return;
        }

        if (isNextFileLine()) {
            nextFile();
        } else if (isLogicLine()) {
            logicChoice();
        } else {
            lineParser(lines.get(userSave.getLineIndex()));
            userSave.setLineIndex(userSave.getLineIndex() + 1);

            if (isLastLine() && varsList.size() > 0) {
                play.setAnswers(varsList);
                isChoice = true;
            }
        }
    }

    private void nextFile() throws IOException {
        isChoice = false;
        String loadedScript = lines.get(userSave.getLineIndex()).replace("nf ", "");
        load(loadedScript);
        choice(VARIANTS.NEXT);
    }

    /**
     * Сюда может придти либо вариант ответа от 0 до 5.
     *
     * @param answerIndex - индекс ответа пользователя от 0 до 5
     */
    private void choseControl(int answerIndex) {
        if (answerIndex >= varsList.size()) {
            return;
        }

        try {
            String loadedScript = varsList.get(answerIndex).split("R ")[1];
            load(loadedScript);
            isChoice = false;
            choice(VARIANTS.NEXT);
        } catch (Exception e) {
            e.printStackTrace();
            new FOptionPane("Ошибка сценария:", "Не удалось загрузить файл сценария: " + e.getMessage());
        }
    }

    private void logicChoice() throws IOException {
        String loadedScript = null;
        String[] logicData = lines.get(userSave.getLineIndex()).split(" ");
        int carmNeed = Integer.parseInt(logicData[0].split("\\(")[1].replace(")", ""));
        int[] arr = new int[] {
                userSave.getCarmaAnn(),
                userSave.getCarmaKur(),
                userSave.getCarmaMar(),
                userSave.getCarmaOlg()
        };
        int max = IntStream.of(arr).distinct().max().getAsInt();

        for (String logicDatum : logicData) {
            if (logicDatum.startsWith("Ann") && userSave.getCarmaAnn() >= carmNeed && userSave.getCarmaAnn() == max) {
                loadedScript = logicDatum.split("=")[1];
            }
            if (logicDatum.startsWith("Kuro") && userSave.getCarmaKur() >= carmNeed && userSave.getCarmaKur() == max) {
                loadedScript = logicDatum.split("=")[1];
            }
            if (logicDatum.startsWith("Mary") && userSave.getCarmaMar() >= carmNeed && userSave.getCarmaMar() == max) {
                loadedScript = logicDatum.split("=")[1];
            }
            if (logicDatum.startsWith("Olga") && userSave.getCarmaOlg() >= carmNeed && userSave.getCarmaOlg() == max) {
                loadedScript = logicDatum.split("=")[1];
            }
            if (logicDatum.startsWith("Oksana") && userSave.getCarmaOks() >= carmNeed && userSave.getCarmaOks() == max) {
                loadedScript = logicDatum.split("=")[1];
            }
            if (logicDatum.startsWith("Lissa") && userSave.getCarmaLis() >= carmNeed && userSave.getCarmaLis() == max) {
                loadedScript = logicDatum.split("=")[1];
            }
        }

        if (loadedScript == null) {
            loadedScript = lines.get(userSave.getLineIndex()).split(" Else=")[1];
        }

        load(loadedScript);
        choice(VARIANTS.NEXT);
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

        play.setScene(sceneName, npcImage);
        play.setDialog(dialogOwner, dialogText, carma);
    }

    private String switchNpc(String[] lineData) {
        // npc:Ann,upWork,simple
        if (lineData[0].equals("-")) {
            return "Clear";
        }

        File[] variants = new File(Registry.personasDir + "/" + lineData[0] + "/" + lineData[1] + "/" + lineData[2]).listFiles();
        return variants == null ? "Scenario.switchNpc: null variants array!" : variants[rand.nextInt(variants.length)].getName().replace(picExtension, "");
    }

    private void metaProcessor(String[] meta) {
        String chapter = meta[0].trim();
        boolean isNextDay = Boolean.parseBoolean(meta[1].trim());
        if (!chapter.equals("-")) {
            play.setChapter(chapter);
        }
        if (isNextDay) {
            play.dayAdd();
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


    private boolean isNextFileLine() {
        return lines.get(userSave.getLineIndex()).startsWith("nf ");
    }

    private boolean isLogicLine() {
        return lines.get(userSave.getLineIndex()).startsWith("logic");
    }

    private boolean isLastLine() {
        return userSave.getLineIndex() == lines.size();
    }
}