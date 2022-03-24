package tools;

import com.sun.media.controls.GainControlAdapter;
import lombok.NonNull;

import javax.media.GainControl;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static fox.Out.LEVEL;
import static fox.Out.Print;
import static registry.Registry.userConf;

public class Media {
    private static final Map<String, File> musicMap = new LinkedHashMap<>();
    private static final Map<String, File> soundMap = new LinkedHashMap<>();
    private static final Map<String, File> voicesMap = new LinkedHashMap<>();
    private static final Map<String, File> backgMap = new LinkedHashMap<>();
    static long loudCorrectDelay = 15l;
    private static Thread musicThread, backgThread;
//    private static JavaSoundAudioDevice auDevMusic;
//    private static AdvancedPlayer musicPlayer;
//    private static JavaSoundAudioDevice auDevBackg;
//    private static AdvancedPlayer backgPlayer;
//    private static JavaSoundAudioDevice auDevSound;
//    private static AdvancedPlayer soundPlayer;
//    private static JavaSoundAudioDevice auDevVoice;
//    private static AdvancedPlayer voicePlayer;
    private static String lastMusic, lastBackg;


    private static void testPlay() {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("C:/FirstCrusade.ogg"))) {
            try (AudioInputStream in = AudioSystem.getAudioInputStream(bis)) {
                if (in == null) {return;}

                AudioFormat baseFormat = in.getFormat();
                AudioFormat targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false
                );

                try (AudioInputStream dataIn = AudioSystem.getAudioInputStream(targetFormat, in)) {

                    // get a line from a mixer in the system with the wanted format
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat);
                    try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                        if (line != null) {
                            line.open();
                            line.start();

                            GainControl gc = new GainControlAdapter();
                            gc.setLevel(1f);

                            BooleanControl muteContr = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
                            muteContr.setValue(false);

                            FloatControl fc = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                            fc.setValue(0);

                            byte[] buffer = new byte[8192];
                            int nBytesRead = 0;
                            while (nBytesRead != -1) {
                                nBytesRead = dataIn.read(buffer, 0, buffer.length);
                                if (nBytesRead != -1) {
                                    line.write(buffer, 0, nBytesRead);
                                }
                            }

                            line.drain();
                            line.stop();
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }


    public static void loadSounds(File[] listFiles) {
        for (File file : listFiles) {
            if (file.isFile()) {
                addSound(file.getName().substring(0, file.getName().length() - 4), file);
            }
        }
    }

    public static void loadMusics(File[] listFiles) {
        for (File file : listFiles) {
            addMusic(file.getName().substring(0, file.getName().length() - 4), file);
        }
    }

    public static void loadBackgs(File[] listFiles) {
        for (File file : listFiles) {
            addBackg(file.getName().substring(0, file.getName().length() - 4), file);
        }
    }

    public static void loadVoices(File[] listFiles) {
        for (File file : listFiles) {
            addVoice(file.getName().substring(0, file.getName().length() - 4), file);
        }
    }

    public static void addSound(@NonNull String name, @NonNull File audioFile) {
        soundMap.put(name, audioFile);
    }

    public static void addMusic(@NonNull String name, @NonNull File audioFile) {
        musicMap.put(name, audioFile);
    }

    public static void addBackg(@NonNull String name, @NonNull File audioFile) {
        backgMap.put(name, audioFile);
    }

    public static void addVoice(@NonNull String name, @NonNull File audioFile) {
        voicesMap.put(name, audioFile);
    }


    public static void playSound(@NonNull String trackName) {
        if (userConf.isSoundMuted()) {
            return;
        }

//        if (soundMap.containsKey(trackName)) {
//            Print(Media.class, LEVEL.DEBUG, "Media: sound '" + trackName + "' was found in the soundMap.");
//
//            new Thread(() -> {
//                auDevSound = new JavaSoundAudioDevice();
//                try (InputStream potok = new FileInputStream(soundMap.get(trackName))) {
//                    soundPlayer = new AdvancedPlayer(potok, auDevSound);
//                    SwingUtilities.invokeLater(() -> {
//                        try {
//                            TimeUnit.MILLISECONDS.sleep(loudCorrectDelay);
//                        } catch (InterruptedException ignore) {
//                        }
//                        auDevSound.setLineGain(VolumeConverter.volumePercentToGain(userConf.getSoundVolume()));
//                    });
//                    soundPlayer.play();
//                } catch (Exception err) {
//                    err.printStackTrace();
//                } finally {
//                    soundPlayer.close();
//                    auDevSound.close();
//                }
//            }).start();
//
//            Print(Media.class, LEVEL.INFO, "Media: sound '" + trackName + "' playing now...");
//        } else {
//            Print(Media.class, LEVEL.INFO, "Media: sound '" + trackName + "' is NOT exist in the soundMap");
//            for (String sName : soundMap.keySet()) {
//                System.out.println(sName + " (" + soundMap.get(sName) + ")");
//            }
//        }
    }

    public static void playMusic(@NonNull String trackName, boolean rep) {
        testPlay();
        return;

//        if (userConf.isMusicMuted()) {
//            return;
//        }

//        if (musicMap.containsKey(trackName)) {
//            if (musicThread != null && (musicThread.isAlive() || !musicThread.isInterrupted())) {
//                stopMusic();
//            }
//
//            musicThread = new Thread(() -> {
//                boolean replay = rep;
//
//                System.out.println("Media: Played now '" + trackName + "'...");
//                lastMusic = trackName;
//                do {
//                    auDevMusic = new JavaSoundAudioDevice();
//                    auDevMusic.setLineGain(VolumeConverter.volumePercentToGain(userConf.getMusicVolume()));
//                    try (BufferedInputStream potok = new BufferedInputStream(new FileInputStream(musicMap.get(lastMusic)))) {
//                        musicPlayer = new AdvancedPlayer(potok, auDevMusic);
//                        SwingUtilities.invokeLater(() -> {
//                            try {
//                                TimeUnit.MILLISECONDS.sleep(loudCorrectDelay);
//                            } catch (InterruptedException ignore) {
//                            }
//                            auDevMusic.setLineGain(VolumeConverter.volumePercentToGain(userConf.getMusicVolume()));
//                            try {
//                                TimeUnit.MILLISECONDS.sleep(loudCorrectDelay);
//                            } catch (InterruptedException ignore) {
//                            }
//                            auDevMusic.setLineGain(VolumeConverter.volumePercentToGain(userConf.getMusicVolume()));
//                            try {
//                                TimeUnit.MILLISECONDS.sleep(loudCorrectDelay);
//                            } catch (InterruptedException ignore) {
//                            }
//                            auDevMusic.setLineGain(VolumeConverter.volumePercentToGain(userConf.getMusicVolume()));
//                        });
//                        musicPlayer.play();
//                    } catch (Exception err) {
//                        err.printStackTrace();
//                    } finally {
//                        if (!replay) {
//                            stopMusic();
//                        }
//                    }
//                } while (replay && !musicThread.isInterrupted());
//            });
//            musicThread.start();
//
//            Print(Media.class, LEVEL.DEBUG, "Media: music: the '" + trackName + "' was found into musicMap and play now...");
//        } else {
//            Print(Media.class, LEVEL.INFO, "Media: music: music '" + trackName + "' is NOT exist in the musicMap");
//            for (String musName : musicMap.keySet()) {
//                System.out.println(musName + " (" + musicMap.get(musName) + ")");
//            }
//            throw new RuntimeException("Media: music: music '" + trackName + "' is NOT exist in the musicMap");
//        }
    }

    public static void playBackg(@NonNull String trackName) {
        if (userConf.isBackgMuted()) {
            return;
        }

//        if (backgMap.containsKey(trackName)) {
//            if (backgThread != null && (backgThread.isAlive() || !backgThread.isInterrupted())) {
//                stopBackg();
//            }
//
//            backgThread = new Thread(() -> {
//                lastBackg = trackName;
//                do {
//                    auDevBackg = new JavaSoundAudioDevice();
//                    try (BufferedInputStream potok = new BufferedInputStream(new FileInputStream(backgMap.get(lastBackg)))) {
//                        backgPlayer = new AdvancedPlayer(potok, auDevBackg);
//                        SwingUtilities.invokeLater(() -> {
//                            try {
//                                TimeUnit.MILLISECONDS.sleep(loudCorrectDelay);
//                            } catch (InterruptedException ignore) {
//                            }
//                            auDevBackg.setLineGain(VolumeConverter.volumePercentToGain(userConf.getBackgVolume()));
//                            try {
//                                TimeUnit.MILLISECONDS.sleep(loudCorrectDelay);
//                            } catch (InterruptedException ignore) {
//                            }
//                            auDevBackg.setLineGain(VolumeConverter.volumePercentToGain(userConf.getBackgVolume()));
//                            try {
//                                TimeUnit.MILLISECONDS.sleep(loudCorrectDelay);
//                            } catch (InterruptedException ignore) {
//                            }
//                            auDevBackg.setLineGain(VolumeConverter.volumePercentToGain(userConf.getBackgVolume()));
//                        });
//                        backgPlayer.play();
//                    } catch (Exception err) {
//                        err.printStackTrace();
//                    } finally {
//                        if (backgThread.isInterrupted()) {
//                            stopBackg();
//                        }
//                    }
//                    if (auDevBackg != null && auDevBackg.isOpen()) {
//                        auDevBackg.close();
//                    }
//                } while (!backgThread.isInterrupted());
//            });
//            backgThread.start();
//
//            Print(Media.class, LEVEL.DEBUG, "Media: backg: the '" + trackName + "' has found into backgMap and play now...");
//        } else {
//            Print(Media.class, LEVEL.INFO, "Media: backg: '" + trackName + "' is NOT exist in the backgMap");
//        }
    }

    public static void playVoice(@NonNull String trackName) {
        if (userConf.isVoiceMuted()) {
            return;
        }

//        if (voicesMap.containsKey(trackName)) {
//            new Thread(() -> {
//                auDevVoice = new JavaSoundAudioDevice();
//                try (InputStream potok = new FileInputStream(voicesMap.get(trackName))) {
//                    voicePlayer = new AdvancedPlayer(potok, auDevVoice);
//                    SwingUtilities.invokeLater(() -> {
//                        try {
//                            TimeUnit.MILLISECONDS.sleep(loudCorrectDelay);
//                        } catch (InterruptedException ignore) {
//                        }
//                        auDevVoice.setLineGain(VolumeConverter.volumePercentToGain(userConf.getVoiceVolume()));
//                    });
//                    voicePlayer.play();
//                } catch (Exception err) {
//                    err.printStackTrace();
//                } finally {
//                    voicePlayer.close();
//                    auDevVoice.close();
//                }
//            }).start();
//
//            Print(Media.class, LEVEL.DEBUG, "Media: voice: the '" + trackName + "' was found into voiceMap and play now...");
//        } else {
//            Print(Media.class, LEVEL.INFO, "Media: voice '" + trackName + "' is NOT exist in the voiceMap");
//            for (String vName : voicesMap.keySet()) {
//                System.out.println(vName + " (" + voicesMap.get(vName) + ")");
//            }
//        }
    }


    public static void stopMusic() {
//        close(musicPlayer, auDevMusic, musicThread);
    }

    public static void stopBackg() {
//        close(backgPlayer, auDevBackg, backgThread);
    }

//    private static void close(AdvancedPlayer player, JavaSoundAudioDevice dev, Thread thread) {
//        if (player == null) {
//            return;
//        }
//
//        try {
//            player.stop();
//        } catch (Exception a) {/* IGNORE STOPPED ALREADY */}
//        try {
//            player.close();
//        } catch (Exception a) {/* IGNORE CLOSED ALREADY */}
//
//        try {
//            if (dev != null && dev.isOpen()) {
//                dev.close();
//            }
//        } catch (Exception e) {
//            /* IGNORE */
//        }
//
//        thread.interrupt();
//    }


    public static void soundMute(boolean mute) {
        userConf.setSoundMuted(mute);
//        if (mute && soundPlayer != null) {
//            try {
//                soundPlayer.stop();
//            } catch (Exception a) {/* IGNORE STOPPED ALREADY */}
//        }
    }

    public static void musicMute(boolean mute) {
        userConf.setMusicMuted(mute);
//        if (mute && musicPlayer != null) {
//            stopMusic();
//        } else {
//            if (lastMusic != null) {
//                playMusic(lastMusic, true);
//            }
//        }
    }

    public static void backgMute(boolean mute) {
        userConf.setBackgMuted(mute);
//        if (mute && backgPlayer != null) {
//            stopBackg();
//        } else {
//            playBackg(lastBackg);
//        }
    }

    public static void voiceMute(boolean mute) {
        userConf.setVoiceMuted(mute);
//        if (mute && voicePlayer != null) {
//            try {
//                voicePlayer.stop();
//            } catch (Exception a) {/* IGNORE STOPPED ALREADY */}
//        }
    }


    public static void setMusicVolume(float gain) {
//        if (auDevMusic != null) {
//            auDevMusic.setLineGain(gain);
//        }
    }

    public static void setSoundVolume(float gain) {
//        if (auDevSound != null) {
//            auDevSound.setLineGain(gain);
//        }
    }

    public static void setBackgVolume(float gain) {
//        if (auDevBackg != null) {
//            auDevBackg.setLineGain(gain);
//        }
    }

    public static void setVoiceVolume(float gain) {
//        if (auDevVoice != null) {
//            auDevVoice.setLineGain(gain);
//        }
    }
}