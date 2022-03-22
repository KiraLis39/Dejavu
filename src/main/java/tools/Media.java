
package tools;

import javazoom.jl.decoder.Equalizer.EQFunction;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import lombok.NonNull;
import registry.Registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static fox.Out.LEVEL;
import static fox.Out.Print;

public class Media extends EQFunction {
    private static Thread musicThread, backgThread;
    private static Map<String, File> musicMap = new LinkedHashMap<>();
    private static Map<String, File> soundMap = new LinkedHashMap<>();
    private static Map<String, File> voicesMap = new LinkedHashMap<>();
    private static Map<String, File> backgMap = new LinkedHashMap<>();

    private static JavaSoundAudioDevice auDevMusic;
    private static AdvancedPlayer musicPlayer;

    private static JavaSoundAudioDevice auDevBackg;
    private static AdvancedPlayer backgPlayer;

    private static JavaSoundAudioDevice auDevSound;
    private static AdvancedPlayer soundPlayer;

    private static JavaSoundAudioDevice auDevVoice;
    private static AdvancedPlayer voicePlayer;

    private static String lastMusic, lastBackg;


    public static void loadSounds(File[] listFiles) {
        for (File file : listFiles) {
            if (file.isFile()) {
                addSound(file.getName().substring(0, file.getName().length() - 4), file);
            }
        }
    }

    public static void addSound(@NonNull String name, @NonNull File audioFile) {
        soundMap.put(name, audioFile);
    }

    public static void playSound(@NonNull String trackName) {
        if (Registry.configuration.isSoundMuted()) {
            return;
        }

        if (soundMap.containsKey(trackName)) {
            Print(Media.class, LEVEL.DEBUG, "Media: sound '" + trackName + "' was found in the soundMap.");

            new Thread(() -> {
                auDevSound = new JavaSoundAudioDevice();
                try (InputStream potok = new FileInputStream(soundMap.get(trackName))) {
                    auDevSound.setLineGain(Registry.configuration.getSoundVolume());
                    soundPlayer = new AdvancedPlayer(potok, auDevSound);
                    PlaybackListener listener = new PlaybackListener() {
                        @Override
                        public void playbackStarted(PlaybackEvent arg0) {
                        }

                        @Override
                        public void playbackFinished(PlaybackEvent event) {
                        }
                    };
                    soundPlayer.setPlayBackListener(listener);
                    soundPlayer.play();

//						javafx.scene.tools.Media hit = new javafx.scene.tools.Media(musicMap.get(trackName).toURI().toString());
//				        musicPlayer = new MediaPlayer(hit);
//				        musicPlayer.setVolume(gVolume);
//				        musicPlayer.play();
                } catch (Exception err) {
                    err.printStackTrace();
                } finally {
                    soundPlayer.close();
                    auDevSound.close();
                }
            }).start();

            Print(Media.class, LEVEL.INFO, "Media: sound '" + trackName + "' playing now...");
        } else {
            Print(Media.class, LEVEL.INFO, "Media: sound '" + trackName + "' is NOT exist in the soundMap");
            for (String sName : soundMap.keySet()) {
                System.out.println(sName + " (" + soundMap.get(sName) + ")");
            }
        }
    }


    public static void loadMusics(File[] listFiles) {
        for (File file : listFiles) {
            addMusic(file.getName().substring(0, file.getName().length() - 4), file);
        }
    }

    public static void addMusic(@NonNull String name, @NonNull File audioFile) {
        musicMap.put(name, audioFile);
    }

    public static void playMusic(@NonNull String trackName, boolean rep) {
        if (Registry.configuration.isMusicMuted()) {
            return;
        }

        if (musicMap.containsKey(trackName)) {
            if (musicThread != null) {
                if (!musicThread.isInterrupted()) {
                    stopMusic();
                }

                while (musicThread.isAlive()) {
                    try {
                        musicThread.join(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            musicThread = new Thread(() -> {
                if (auDevMusic != null && auDevMusic.isOpen()) {
                    auDevMusic.close();
                }

                auDevMusic = new JavaSoundAudioDevice();
                try (InputStream potok = new FileInputStream(musicMap.get(trackName))) {
                    lastMusic = trackName;
                    auDevMusic.setLineGain(Registry.configuration.getMusicVolume());
                    musicPlayer = new AdvancedPlayer(potok, auDevMusic);
                    PlaybackListener listener = new PlaybackListener() {
                        @Override
                        public void playbackStarted(PlaybackEvent arg0) {
                        }

                        @Override
                        public void playbackFinished(PlaybackEvent event) {
                        }
                    };
                    musicPlayer.setPlayBackListener(listener);
                    musicPlayer.play();
                } catch (Exception err) {
                    err.printStackTrace();
                } finally {
                    musicPlayer.close();
                    auDevMusic.close();
                }
            });
            musicThread.start();

            Print(Media.class, LEVEL.DEBUG, "Media: music: the '" + trackName + "' was found into musicMap and play now...");
        } else {
            Print(Media.class, LEVEL.INFO, "Media: music: music '" + trackName + "' is NOT exist in the musicMap");
            for (String musName : musicMap.keySet()) {
                System.out.println(musName + " (" + musicMap.get(musName) + ")");
            }
            throw new RuntimeException("Media: music: music '" + trackName + "' is NOT exist in the musicMap");
        }
    }

    public static void stopMusic() {
        if (musicPlayer == null) {
            return;
        }

        try {
            musicPlayer.stop();
        } catch (Exception a) {/* IGNORE STOPPED ALREADY */}
        try {
            musicPlayer.close();
        } catch (Exception a) {/* IGNORE CLOSED ALREADY */}
        musicThread.interrupt();
    }


    public static void loadBackgs(File[] listFiles) {
        for (File file : listFiles) {
            addBackg(file.getName().substring(0, file.getName().length() - 4), file);
        }
    }

    public static void addBackg(@NonNull String name, @NonNull File audioFile) {
        backgMap.put(name, audioFile);
    }

    public static void playBackg(@NonNull String trackName) {
        if (Registry.configuration.isBackgMuted()) {
            return;
        }

        if (backgMap.containsKey(trackName)) {
            if (backgThread != null) {
                if (!backgThread.isInterrupted()) {
                    stopBackg();
                }

                while (backgThread.isAlive()) {
                    try {
                        backgThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            backgThread = new Thread(() -> {
                if (auDevBackg != null && auDevBackg.isOpen()) {
                    auDevBackg.close();
                }

                auDevBackg = new JavaSoundAudioDevice();
                try (InputStream potok = new FileInputStream(backgMap.get(trackName))) {
                    lastBackg = trackName;
                    auDevBackg.setLineGain(Registry.configuration.getBackgVolume());
                    backgPlayer = new AdvancedPlayer(potok, auDevBackg);
                    PlaybackListener listener = new PlaybackListener() {
                        @Override
                        public void playbackStarted(PlaybackEvent arg0) {
                        }

                        @Override
                        public void playbackFinished(PlaybackEvent event) {
                        }
                    };
                    backgPlayer.setPlayBackListener(listener);
                    backgPlayer.play();
                } catch (Exception err) {
                    err.printStackTrace();
                } finally {
                    backgPlayer.close();
                    auDevBackg.close();
                }
            });
            backgThread.start();

            Print(Media.class, LEVEL.DEBUG, "Media: backg: the '" + trackName + "' has found into backgMap and play now...");
        } else {
            Print(Media.class, LEVEL.INFO, "Media: backg: '" + trackName + "' is NOT exist in the backgMap");
        }
    }

    public static void stopBackg() {
        if (backgPlayer == null) {
            return;
        }

        try {
            backgPlayer.stop();
        } catch (Exception a) {/* IGNORE STOPPED ALREADY */}
        try {
            backgPlayer.close();
        } catch (Exception a) {/* IGNORE CLOSED ALREADY */}
        backgThread.interrupt();
    }


    public static void loadVoices(File[] listFiles) {
        for (File file : listFiles) {
            addVoice(file.getName().substring(0, file.getName().length() - 4), file);
        }
    }

    public static void addVoice(@NonNull String name, @NonNull File audioFile) {
        voicesMap.put(name, audioFile);
    }

    public static void playVoice(@NonNull String trackName) {
        if (Registry.configuration.isVoiceMuted()) {
            return;
        }

        if (voicesMap.containsKey(trackName)) {
            new Thread(() -> {
                auDevVoice = new JavaSoundAudioDevice();
                try (InputStream potok = new FileInputStream(voicesMap.get(trackName))) {
                    voicePlayer = new AdvancedPlayer(potok, auDevVoice);
                    auDevVoice.setLineGain(Registry.configuration.getVoiceVolume());
                    voicePlayer.play();
                } catch (Exception err) {
                    err.printStackTrace();
                } finally {
                    voicePlayer.close();
                    auDevVoice.close();
                }
            }).start();

            Print(Media.class, LEVEL.DEBUG, "Media: voice: the '" + trackName + "' was found into voiceMap and play now...");
        } else {
            Print(Media.class, LEVEL.INFO, "Media: voice '" + trackName + "' is NOT exist in the voiceMap");
            for (String vName : voicesMap.keySet()) {
                System.out.println(vName + " (" + voicesMap.get(vName) + ")");
            }
        }
    }


    public static void soundMute(boolean mute) {
        Registry.configuration.setSoundMuted(mute);
        if (mute && soundPlayer != null) {
            try {
                soundPlayer.stop();
            } catch (Exception a) {/* IGNORE STOPPED ALREADY */}
        }
    }

    public static void musicMute(boolean mute) {
        Registry.configuration.setMusicMuted(mute);
        if (mute && musicPlayer != null) {
            stopMusic();
        } else {
            playMusic(lastMusic, true);
        }
    }

    public static void backgMute(boolean mute) {
        Registry.configuration.setBackgMuted(mute);
        if (mute && backgPlayer != null) {
            stopBackg();
        } else {
            playBackg(lastBackg);
        }
    }

    public static void voiceMute(boolean mute) {
        Registry.configuration.setVoiceMuted(mute);
        if (mute && voicePlayer != null) {
            try {
                voicePlayer.stop();
            } catch (Exception a) {/* IGNORE STOPPED ALREADY */}
        }
    }


    public static void setMusicVolume(float volume) {
        Registry.configuration.setMusicVolume((float) (Math.log(volume) / Math.log(2) * 6.0f));
        if (auDevMusic != null) {
            auDevMusic.setLineGain(Registry.configuration.getMusicVolume());
        }
    }

    public static void setSoundVolume(float volume) {
        Registry.configuration.setSoundVolume((float) (Math.log(volume) / Math.log(2) * 6.0f));
        if (auDevSound != null) {
            auDevSound.setLineGain(Registry.configuration.getSoundVolume());
        }
    }

    public static void setBackgVolume(float volume) {
        Registry.configuration.setBackgVolume((float) (Math.log(volume) / Math.log(2) * 6.0f));
        if (auDevBackg != null) {
            auDevBackg.setLineGain(Registry.configuration.getBackgVolume());
        }
    }

    public static void setVoiceVolume(float volume) {
        Registry.configuration.setVoiceVolume((float) (Math.log(volume) / Math.log(2) * 6.0f));
        if (auDevVoice != null) {
            auDevVoice.setLineGain(Registry.configuration.getVoiceVolume());
        }
    }
}