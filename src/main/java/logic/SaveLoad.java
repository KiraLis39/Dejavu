package logic;

import GUI.GamePlay;
import configurations.UserSave;
import fox.Out;
import fox.Out.LEVEL;
import iom.JIOM;
import registry.Registry;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static registry.Registry.*;
import static registry.Registry.userSave;

public class SaveLoad {
    public static void save() {
        SwingUtilities.invokeLater(() -> {
            try {
                userSave.setBackgPlayed(backgPlayer.getLastTrack());
                userSave.setMusicPlayed(musicPlayer.getLastTrack());
                userSave.setSoundPlayed(soundPlayer.getLastTrack());
//                userSave.setVoicePlayed(voicePlayer.getLastTrack());

                JIOM.dtoToFile(userSave);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void handleSave(Path newSavePath) {
        UserSave newSaveDto = new UserSave(newSavePath);
        try {
            newSaveDto.setCarmaAnn(userSave.getCarmaAnn());
            newSaveDto.setCarmaDmi(userSave.getCarmaDmi());
            newSaveDto.setCarmaKur(userSave.getCarmaKur());
            newSaveDto.setCarmaLis(userSave.getCarmaLis());
            newSaveDto.setCarmaMar(userSave.getCarmaMar());
            newSaveDto.setCarmaMsh(userSave.getCarmaMsh());
            newSaveDto.setCarmaOks(userSave.getCarmaOks());
            newSaveDto.setCarmaOle(userSave.getCarmaOle());
            newSaveDto.setCarmaOlg(userSave.getCarmaOlg());

            newSaveDto.setScreen(userSave.getScreen());

            newSaveDto.setBackgPlayed(backgPlayer.getLastTrack());
            newSaveDto.setMusicPlayed(musicPlayer.getLastTrack());
            newSaveDto.setSoundPlayed(soundPlayer.getLastTrack());
//            newSaveDto.setVoicePlayed(voicePlayer.getLastTrack());

            newSaveDto.setChapter(userSave.getChapter());
            newSaveDto.setToday(userSave.getToday());
            newSaveDto.setMonth(userSave.getMonth());
            newSaveDto.setCycleCount(userSave.getCycleCount());

            newSaveDto.setScript(userSave.getScript());
            newSaveDto.setLineIndex(userSave.getLineIndex());

            JIOM.dtoToFile(newSaveDto);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось записать ручное сохранение: " + e.getMessage());
        }
    }

    public void load(UserSave dto) {
        userSave = dto;
    }
}