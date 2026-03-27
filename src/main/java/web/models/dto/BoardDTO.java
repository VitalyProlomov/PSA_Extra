package web.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pokerlibrary.models.Board;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BoardDTO {

    @JsonProperty("cards")
    private List<CardDTO> cards = new ArrayList<>();

    public BoardDTO() {}

    public BoardDTO(Board board) {
        if (board != null) {
            for (pokerlibrary.models.Card card : board.getCards()) {
                this.cards.add(new CardDTO(card));
            }
        }
    }

}