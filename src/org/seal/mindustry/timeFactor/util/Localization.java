package org.seal.mindustry.timeFactor.util;

import arc.Core;
import arc.util.Log;

/**
 * Utility class for localization.
 */
public class Localization {

    private Localization() {}

    /**
     * Get localized string
     */
    public static String get(String key) {
        try {
            // Проверяем, что Core.bundle вообще существует
            if (Core.bundle == null) {
                Log.err("Core.bundle is NULL! Localization not available yet.");
                return "!" + key + "!";
            }

            String value = Core.bundle.get(key);

            // Дополнительная проверка: если вернулся тот же ключ
            if (value.equals(key)) {
                Log.warn("Localization key '" + key + "' not found - check your bundle files");
                return "[" + key + "]"; // Возвращаем что-то видимое, но не ???
            }

            return value;

        } catch (Exception e) {
            Log.err("Exception getting localization key: " + key);
            Log.err(e);
            return "{" + key + "}";
        }
    }

    /**
     * Get formatted localized string
     */
    public static String format(String key, Object... args) {
        try {
            if (Core.bundle == null) {
                Log.err("Core.bundle is NULL!");
                return "!" + key + "!";
            }
            return Core.bundle.format(key, args);
        } catch (Exception e) {
            Log.err("Exception formatting key: " + key);
            Log.err(e);
            return "{" + key + "}";
        }
    }

    /**
     * Get current language
     */
    public static String getCurrentLanguage() {
        if (Core.bundle == null || Core.bundle.getLocale() == null) {
            return "unknown";
        }
        return Core.bundle.getLocale().getLanguage();
    }

    /**
     * Diagnostic method to check all bundle files
     */
    public static void diagnose() {
        Log.info("=== LOCALIZATION DIAGNOSTICS ===");

        // 1. Check Core.bundle
        if (Core.bundle == null) {
            Log.err("Core.bundle is NULL!");
            return;
        }

        // 2. Check locale
        Log.info("Current locale: " + Core.bundle.getLocale());
        Log.info("Current language: " + getCurrentLanguage());

        // 3. Try to get some keys
        String[] testKeys = {
                "tf.settings.tooltip",
                "tf.reset.tooltip",
                "tf.speed.current",
                "non.existent.key"  // заведомо несуществующий
        };

        for (String key : testKeys) {
            try {
                String value = Core.bundle.get(key);
                Log.info("  " + key + " = '" + value + "'");
                Log.info("    equals key? " + value.equals(key));
            } catch (Exception e) {
                Log.err("  " + key + " - EXCEPTION: " + e.getMessage());
            }
        }

        // 4. Try to get all keys (not directly possible, but we can check bundle source)
        Log.info("Bundle class: " + Core.bundle.getClass().getName());
    }
}