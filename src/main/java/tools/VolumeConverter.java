package tools;

public class VolumeConverter {

    private static final float gradientRange = -1f;
    private static final float minimum = -65f;
    /**
     * Метод преобразовывает значения процентов громкости в
     * gain для аудио-устройств.
     * @param percent текущий процент громкости (от 0 до 100)
     * @return gain аудио-устройства (от -104030 до 0).
     */
    public static float volumePercentToGain(float percent) {
//        float gain = -(float) ((min / Math.exp(Math.floor(percent) / 6.0d * Math.log(2d))) * gradientRange);
        float gain = minimum - (minimum * (percent / 100f));
//        System.out.println("Income percent: " + percent + "; Gain: " + gain);
        return gain;
    }

    /**
     * Метод преобразовывает gain аудио-устройства в
     * значение процентов громкости для ползунков.
     * @param gain текущий гейн аудио-устройства (от -104030 до 0)
     * @return значение процентов (от 0 до 100).
     */
    public static int gainToVolumePercent(float gain) {
//        int max = 100;
//        return (int) Math.round(max - Math.log(-gain / gradientRange) / Math.log(2d) * 6.0d);
        return 0;
    }
}
