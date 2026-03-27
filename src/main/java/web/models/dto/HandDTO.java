package web.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pokerlibrary.models.Hand;

@Getter
@Setter
public class HandDTO {
    @JsonProperty("card1")
    private CardDTO card1;

    @JsonProperty("card2")
    private CardDTO card2;

    public HandDTO() {}

    public HandDTO(Hand hand) {
        if (hand != null) {
            this.card1 = new CardDTO(hand.getCard1());
            this.card2 = new CardDTO(hand.getCard2());
        }
    }
}