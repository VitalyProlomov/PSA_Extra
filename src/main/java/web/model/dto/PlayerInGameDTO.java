package web.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pokerlibrary.models.PlayerInGame;
import pokerlibrary.utils.Money;

@Getter
@Setter
public class PlayerInGameDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("position")
    private String position;

    @JsonProperty("seatNumber")
    private Integer seatNumber;

    @JsonProperty("balance")
    private Money balance;

    @JsonProperty("hand")
    private HandDTO hand;

    @JsonProperty("userName")
    private String userName;

    public PlayerInGameDTO() {}

    public PlayerInGameDTO(PlayerInGame player) {
        if (player != null) {
            this.id = player.getId();
            this.position = player.getPosition() != null ? player.getPosition().name() : null;
            this.seatNumber = player.getSeatNumber();
            this.balance = player.getBalance();
            this.hand = new HandDTO(player.getHand());

            if (player.getRef() != null) {
                this.userName = player.getRef().getUserName();
            } else {
                this.userName = player.getId();
            }
        }
    }

}