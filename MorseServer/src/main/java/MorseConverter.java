import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MorseConverter {
    private static final Map<String, String> morseToEnglishMap = new HashMap<>();
    private static final Map<String, String> englishToMorseMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MorseConverter.class);

    public static void alphabetMapper() {
        String filePath = "alphabet.txt";
        String line;

        try {
            InputStream inputStream = MorseConverter.class.getClassLoader().getResourceAsStream(filePath);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" = ", 2);

                    if (parts.length >= 2) {
                        String key = parts[0];
                        String value = parts[1];

                        englishToMorseMap.put(key, value);
                        morseToEnglishMap.put(value, key);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Alphabet file reading error: " + e.getMessage());
            System.exit(1);
        }
    }


    public static synchronized String englishToMorse(String englishLang) {
        StringBuilder outputMsg = new StringBuilder();

        englishLang = englishLang.toLowerCase(Locale.ROOT);

        for (int i = 0; i < englishLang.length(); i++) {
            String ch = String.valueOf(englishLang.charAt(i));
            outputMsg.append(englishToMorseMap.get(ch));
        }
        return outputMsg.toString();
    }

}

