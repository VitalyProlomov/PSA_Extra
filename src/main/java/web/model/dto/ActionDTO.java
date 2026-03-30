package web.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pokerlibrary.models.Action;

@Getter
@Setter
public class ActionDTO {

    @JsonProperty("actionType")
    private String actionType;

    @JsonProperty("playerId")
    private String playerId;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("potBeforeAction")
    private double potBeforeAction;

    public ActionDTO() {}

    public ActionDTO(Action action) {
        if (action != null) {
            this.actionType = action.getActionType().name();
            this.playerId = action.getPlayerId();
            this.amount = action.getAmount();
            this.potBeforeAction = action.getPotBeforeAction();
        }
    }

}