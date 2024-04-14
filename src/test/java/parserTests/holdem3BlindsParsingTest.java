package parserTests;

import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.Game;
import models.PlayerInGame;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.*;

import static models.PositionType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
//
//    @Test
//    public void test() throws FileNotFoundException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
//        GGPokerokHoldem8Max3BlindsParser parser = new GGPokerokHoldem8Max3BlindsParser();
//        String txt = getTextFromFile("/ggPokerokFiles/holdem3BlindsGamesFiles/flopAllInGame");
//        Game topG = parser.parseGame(txt);
//        assertEquals("HD1127456652", topG.getGameId());
//        assertEquals(new Date("2023/02/19 18:02:19"), topG.getDate());
//
//        ArrayList<Pla yerInGame> players = new ArrayList<>(List.of(
//                new PlayerInGame("Hero", SB,25.06),
//                new PlayerInGame("5007fc29", BB,25.06),
//                new PlayerInGame("46396763", TB,25.06),
//                new PlayerInGame("ab86cbdb", UTG,25.06),
//                new PlayerInGame("ea6b9042", LJ,25.06),
//                new PlayerInGame("Hero", HJ,25.06),
//                new PlayerInGame("Hero", CO,25.06),
//                new PlayerInGame("Hero", BTN,25.06)
//                ));
//
//    }

}
