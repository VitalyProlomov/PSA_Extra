package parserTests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.*;


public class holdem3BlindsParsingTest {
    private String getTextFromFile(String path) throws FileNotFoundException {
        URL gameURL = rushNCashParsingTest.class.getResource(path);
        assert gameURL != null;
        FileReader fr = new FileReader(gameURL.getFile().replace("%20", " "));
        Scanner scanner = new Scanner(fr);

        StringBuilder gameText = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            gameText.append(line).append("\n");
        }
        return gameText.toString();
    }


}
