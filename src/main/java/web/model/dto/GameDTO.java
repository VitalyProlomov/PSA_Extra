package web.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pokerlibrary.models.Game;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class GameDTO {

    @JsonProperty("gameId")
    private String gameId;

    @JsonProperty("date")
    private String date;

    @JsonProperty("players")
    private Map<String, PlayerInGameDTO> players = new HashMap<>();

    @JsonProperty("initialBalances")
    private Map<String, Double> initialBalances = new HashMap<>();

    @JsonProperty("preFlop")
    private StreetDescriptionDTO preFlop;

    @JsonProperty("flop")
    private StreetDescriptionDTO flop;

    @JsonProperty("turn")
    private StreetDescriptionDTO turn;

    @JsonProperty("river")
    private StreetDescriptionDTO river;

    @JsonProperty("heroWinLoss")
    private String heroWinLoss;

    @JsonProperty("finalPot")
    private String finalPot;

    @JsonProperty("playersPostFlop")
    private int playersPostFlop;

    @JsonProperty("bigBlindSize")
    private double bigBlindSize;

    public GameDTO() {}

    public GameDTO(Game game) {
        if (game != null) {
            this.gameId = game.getGameId();
            this.bigBlindSize = game.getBigBlindSize$();

            // Format date
            if (game.getDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
                this.date = sdf.format(game.getDate());
            }

            // Convert players
            for (Map.Entry<String, pokerlibrary.models.PlayerInGame> entry : game.getPlayers().entrySet()) {
                this.players.put(entry.getKey(), new PlayerInGameDTO(entry.getValue()));
            }

            // Convert initial balances
            this.initialBalances.putAll(game.getInitialBalances());

            // Convert streets
            this.preFlop = new StreetDescriptionDTO(game.getPreFlop());
            this.flop = new StreetDescriptionDTO(game.getFlop());
            this.turn = new StreetDescriptionDTO(game.getTurn());
            this.river = new StreetDescriptionDTO(game.getRiver());

            // Calculate formatted values
            DecimalFormat df = new DecimalFormat("#0.00");
            this.heroWinLoss = df.format(game.getHeroWinloss()).replace(',', '.') + "$";
            this.finalPot = df.format(game.getFinalPot()).replace(',', '.') + "$";

            // Players post flop
            if (game.getPreFlop() != null) {
                this.playersPostFlop = game.getPreFlop().getPlayersAfterBetting().size();
            }
        }
    }

}
