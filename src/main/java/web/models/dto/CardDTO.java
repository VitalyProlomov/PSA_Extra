package web.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pokerlibrary.models.Card;

@Setter
@Getter
public class CardDTO {
    @JsonProperty("suit")
    private String suit;

    @JsonProperty("rank")
    private String rank;

    // Default constructor for Jackson
    public CardDTO() {}

    public CardDTO(Card card) {
        if (card != null) {
            this.suit = card.getSuit().name();
            this.rank = card.getRank().name();
        }
    }

}