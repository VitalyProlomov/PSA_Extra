package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

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

    public HashMap<String, Game> getGames() {
        return games;
    }
//
//    public void setGames(Map<String, Game> games) {
//        this.games = new HashMap<>(games);
//    }
//
//    @JsonIgnore
//    public void setGames(Set<Game> games) {
//        for (Game g : games) {
//            this.games.put(g.getGameId(), g);
//        }
//    }

    public void addGames(Collection<Game> addendumGames) {
        for (Game g : addendumGames) {
            games.put(g.getGameId(), g);
        }
    }
}
