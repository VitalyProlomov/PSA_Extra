package serializingTests;

import pokerlibrary.analizer.Combination;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pokerlibrary.exceptions.IncorrectBoardException;
import pokerlibrary.exceptions.IncorrectCardException;
import pokerlibrary.exceptions.IncorrectHandException;
import org.junit.jupiter.api.Test;
import parserTests.rushNCashParsingTest;
import pokerlibrary.models.*;
import pokerlibrary.parsers.gg.GGPokerokRushNCashParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SerializerTest {
    private String getFullPath(String path) {
        URL url = rushNCashParsingTest.class.getResource(path);
        assert url != null;

        return url.getFile().replace("%20", " ");
    }

    @Test
    public void serializeGamesTest() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = getFullPath("/ggPokerokFiles/gamesFiles/rushNCash/severalSessions");
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();

        ArrayList<Game> allGames = parser.parseDirectoryFiles(path);

        // Verified Amount on pokerCraft
        assertEquals(3205, allGames.size());

        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/serializedFiles/serializedGames1.txt");

        HashMap<String, Game> gamesMap = new HashMap<String, Game>();
        for (Game g : allGames) {
            gamesMap.put(g.getGameId(), g);
        }
        GamesSet gamesSet = new GamesSet(gamesMap);

        objectMapper.writeValue(file, gamesSet);

//        StringBuilder text = new StringBuilder();
//        FileReader fr = new FileReader(file);
//        BufferedReader bfr = new BufferedReader(fr);
//        String line = bfr.readLine();
//        while (line != null) {
//            text.append(line).append("\n");
//            line = bfr.readLine();
//        }

        GamesSet deserializedGame = objectMapper.readValue(file, GamesSet.class);
        assertEquals(gamesSet.getGames(), deserializedGame.getGames());
        String id;
        for (Game g : gamesSet.getGames().values()) {
            id = g.getGameId();
            assertEquals(g.getPlayers(), deserializedGame.getGames().get(id).getPlayers());
        }
    }

    @Test
    public void serializeActionTest() throws JsonProcessingException {
        Action action = new Action(Action.ActionType.RAISE, "Fish", 10,1.65);
        ObjectMapper objectMapper = new ObjectMapper();

        String JSONtext = "";

        JSONtext = objectMapper.writeValueAsString(action);
        Action deserializedAction = objectMapper.readValue(JSONtext, Action.class);
    }

    @Test
    public void serializeBoardTest() throws JsonProcessingException, IncorrectBoardException, IncorrectCardException {
        Board board = new Board("As", "5d", "5h");
        ObjectMapper objectMapper = new ObjectMapper();

        String JSONtext = "";

        JSONtext = objectMapper.writeValueAsString(board);
        Board deserializedAction = objectMapper.readValue(JSONtext, board.getClass());
    }

    private Object serializeAndDeserializeGivenObject(Object object, Class objClass) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String JSONtext = "";

        JSONtext = objectMapper.writeValueAsString(object);

        return objectMapper.readValue(JSONtext, objClass);
    }

    @Test
    public void serializeAndDeserializeEveryClass() throws IncorrectCardException, IOException, IncorrectBoardException, IncorrectHandException {
        Object obj = new Action(Action.ActionType.CHECK, "", 20, 10);
        Object deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);

        obj = new Board("Ah", "Kd", "Qh");
        deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);

        obj = new Card("2d");
        deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);

        obj = new ComboCardsPair(Combination.PAIR, new ArrayList<Card>(
                List.of(new Card("3h"),
                new Card("Ah"),
                new Card("3d"),
                new Card("4h"),
                new Card("5h"))));
        deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);

        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        Game game = parser.parseFile ("src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/fullGame.txt").get(0);
        Game deserGame = (Game)serializeAndDeserializeGivenObject(game, Game.class);


        assertEquals(game.getTable() , deserGame.getTable());
        assertEquals(game.getGameId() , deserGame.getGameId());
        assertEquals(game.getInitialBalances() , deserGame.getInitialBalances());
        assertEquals(game.getExtraCashAmount() , deserGame.getExtraCashAmount());
        assertEquals(game.getBigBlindSize$() , deserGame.getBigBlindSize$());
        assertEquals(game.getPlayers(), deserGame.getPlayers());
        assertEquals(game.getWinners(), deserGame.getWinners());
        assertEquals(game.getPreFlop(), deserGame.getPreFlop());
        assertEquals(game.getDate(), deserGame.getDate());
        assertEquals(game.getFlop(), deserGame.getFlop());
        assertEquals(game.getSB(), deserGame.getSB());
        assertEquals(game.getTurn() , deserGame.getTurn());
        assertEquals(game.getRiver() , deserGame.getRiver());
        assertEquals(game.getRake() , deserGame.getRake());
        assertEquals(game.getFinalPot() , deserGame.getFinalPot());


        obj = new Hand("7s", "8d");
        deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);

        obj = new PlayerInGame("Fish234", PositionType.CO, 20.00);
        deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);

        obj = PositionType.SB;
        deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);

        obj = new StreetDescription();
        deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);
        obj = new StreetDescription(100,  new Board("Ah", "7h", "Qh", "7s", "7c"),
                new ArrayList<>(List.of(
                        new PlayerInGame("P1", PositionType.BTN, 50.0),
                        new PlayerInGame("P2", PositionType.BB, 50.0))),
                new ArrayList<>(List.of(
                        new Action(Action.ActionType.BET,"P1", 50, 2),
                        new Action(Action.ActionType.CALL,"P2", 50, 2)
                )));
        deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);

        obj = new UserProfile("usersId");
        deserObject = serializeAndDeserializeGivenObject(obj, obj.getClass());
        assertEquals(obj, deserObject);

//        obj = new PlayerInGame()
    }

    @Test
    public void testCorrectBalanceCHanging() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        ArrayList<Game> gameAr = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/returningChipsToBalanceGame.txt");

        GamesSet gameSet = new GamesSet();

        gameSet.addGames(gameAr);
        ObjectMapper objectMapper = new ObjectMapper();
        Object obj = serializeAndDeserializeGivenObject(gameSet, GamesSet.class);
        GamesSet deserializedGameSet = (GamesSet) obj;
        assertTrue(gameSet.getGames().containsKey("RC1328499375"));
    }
}
