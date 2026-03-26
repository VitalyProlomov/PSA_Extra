package pokerlibrary.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.*;

@Getter
public class GamesSet {
    private final HashMap<String, Game> games;

    public GamesSet() {
        this.games = new HashMap<>();
    }

    public GamesSet(@JsonProperty("games") Map<String, Game> games) {
        if (games == null) {
            this.games = new HashMap<>();
            return;
        }
        this.games = new HashMap<>(games);
    }

    public void addGames(Collection<Game> addendumGames) {
        for (Game g : addendumGames) {
            games.put(g.getGameId(), g);
        }
    }
}
