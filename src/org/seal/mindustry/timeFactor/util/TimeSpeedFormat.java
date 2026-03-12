package org.seal.mindustry.timeFactor.util;

/**
 * Utility class for converting speed positions to human-readable strings.
 */
public class TimeSpeedFormat {
    private TimeSpeedFormat() {
        // Приватный конструктор, чтобы нельзя было создать экземпляр
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Converts a speed position to a human-readable string.
     *
     * @param position Speed position (-4 to 4, where 0 = normal speed)
     * @return Formatted speed string (e.g., "x1", "x4", "x1/2")
     */
    public static String pos2str(int position) {
        if (position >= 0) {
            return "x" + (int) Math.pow(2, position);
        } else {
            return "x1/" + (int) Math.pow(2, Math.abs(position));
        }
    }

    /**
     * Gets the speed multiplier for a given position.
     *
     * @param position Speed position
     * @return Speed multiplier (e.g., 1.0, 2.0, 0.5)
     */
    public static float getMultiplier(int position) {
        return (float) Math.pow(2, position);
    }
}
