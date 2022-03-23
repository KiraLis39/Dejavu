package tools;

public class VolumeConverter {

    private static float gradientRange = -1f;

    /**
     * Метод преобразовывает значения процентов громкости в
     * gain для аудио-устройств.
     * @param volume текущий процент громкости (от 0 до 100)
     * @return gain аудио-устройства (от -104030 до 0).
     */
    public static float volumePercentToGain(float volume) {
        if (volume < 1) {volume = 1;}

        double max = 104030d;
        float gain = -(float) ((max / Math.exp(Math.floor(volume) / 6.0d * Math.log(2d))) * gradientRange);
        System.out.println("Income percent: " + volume + "; Gain: " + gain);
        return gain;
    }

    /**
     * Метод преобразовывает gain аудио-устройства в
     * значение процентов громкости для ползунков.
     * @param gain текущий гейн аудио-устройства (от -104030 до 0)
     * @return значение процентов (от 0 до 100).
     */
    public static int gainToVolumePercent(float gain) {
        int max = 100;
        return (int) Math.round(max - Math.log(-gain / gradientRange) / Math.log(2d) * 6.0d);
    }
}
