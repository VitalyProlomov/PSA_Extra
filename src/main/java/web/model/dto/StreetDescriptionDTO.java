package web.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pokerlibrary.models.StreetDescription;
import pokerlibrary.utils.Money;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class StreetDescriptionDTO {

    @JsonProperty("allActions")
    private List<ActionDTO> allActions = new ArrayList<>();

    @JsonProperty("board")
    private BoardDTO board;

    @JsonProperty("playersAfterBetting")
    private List<PlayerInGameDTO> playersAfterBetting = new ArrayList<>();

    @JsonProperty("potAfterBetting")
    private Money potAfterBetting;

    @JsonProperty("isAllIn")
    private boolean isAllIn;

    public StreetDescriptionDTO() {}

    public StreetDescriptionDTO(StreetDescription street) {
        if (street != null) {
            this.potAfterBetting = street.getPotAfterBetting();
            this.board = new BoardDTO(street.getBoard());
            this.isAllIn = street.isAllIn();

            for (pokerlibrary.models.Action action : street.getAllActions()) {
                this.allActions.add(new ActionDTO(action));
            }

            for (pokerlibrary.models.PlayerInGame player : street.getPlayersAfterBetting()) {
                this.playersAfterBetting.add(new PlayerInGameDTO(player));
            }
        }
    }

}