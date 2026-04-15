package web.service;

import web.model.dto.ReplayState;
import org.springframework.stereotype.Service;
import pokerlibrary.models.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class PokerReplayService {

    private final Map<String, ReplayState> replayStates = new HashMap<>();

    public ReplayState initializeReplay(String gameId, Game game) {
        ReplayState state = createInitialState(game);
        replayStates.put(gameId, state);
        return state;
    }

    public ReplayState getNextAction(String gameId, Game game) {
        ReplayState state = replayStates.get(gameId);
        if (state == null) {
            return initializeReplay(gameId, game);
        }
        return processNextAction(state, game);
    }

    public ReplayState resetReplay(String gameId, Game game) {
        ReplayState state = createInitialState(game);
        replayStates.put(gameId, state);
        return state;
    }

    public ReplayState getState(String gameId) {
        return replayStates.get(gameId);
    }

    private ReplayState createInitialState(Game game) {
        // Same logic as controller
        return new ReplayState(
                "preflop",
                -1,
                new HashMap<>(game.getInitialBalances()),
                new HashMap<>(game.getInitialBalances()
                ));
    }

    private ReplayState processNextAction(ReplayState state, Game game) {
        // Same logic as controller
        return state;
    }
}