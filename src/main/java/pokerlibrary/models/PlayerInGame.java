package pokerlibrary.models;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pokerlibrary.utils.Money;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Class that represents the player in a specific game.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
@Table(name="playersInGame")
public class PlayerInGame {
    @Id
    private final String id;
    private PositionType positionType;
    private UserProfile ref;
    private Money balance$;
    private Hand hand;
    private Integer seatNumber;
    /**
     * Constructs new PlayerInGame with given hash
     */
    public PlayerInGame(String id) {
        this.id = id;
    }
    /**
     * Constructs new PlayerInGame with given hash, position in the table
     * and balance in dollars.
     */
    public PlayerInGame(String id, PositionType position, Money balance$) {
        this(id, position, Money.of(String.valueOf(balance$)), null);
    }
    /**
     * Constructs new PlayerInGame with given hash, position in the table,
     * balance in dollars and a Userprofile object (link to the real user).
     */
    @JsonCreator
    public PlayerInGame(@JsonProperty("id") String id, @JsonProperty("position") PositionType position,
                        @JsonProperty("balance") Money balance, @JsonProperty("ref") UserProfile ref) {
        this.id = id;
        this.positionType = position;
        this.balance$ = balance;
        this.ref = ref;
    }

    /**
     * Constructs new PlayerInGame, copying all the fields of the given PlayerInGame
     * @param copyPlayer Player, whose fields are going to be copied.
     */
    public PlayerInGame(PlayerInGame copyPlayer) {
        this.id = copyPlayer.id;
        this.positionType = copyPlayer.positionType;
        this.balance$ = copyPlayer.balance$ != null ? new Money(copyPlayer.balance$.toBigDecimal()) : Money.of("0");
        this.ref = copyPlayer.ref;
        if (copyPlayer.hand != null) {
            this.hand = new Hand(copyPlayer.hand);
        }
        this.seatNumber = copyPlayer.seatNumber;
//        this.vpip = copyPlayer.vpip;
//        this.threeBetPercentage = copyPlayer.threeBetPercentage;
//        this.handsPlayed = copyPlayer.handsPlayed;
    }
//    private int vpip;
//    private int threeBetPercentage;
//    private int handsPlayed;
    /**
     * @return position of the player in table.
     */
    public PositionType getPosition() {
        return positionType;
    }
    /**
     * Sets position of the player in table.
     * @param positionType position to set (no validation, so make sure ypu put correct position)
     */
    public void setPosition(PositionType positionType) {
        this.positionType = positionType;
    }

    public Money getBalance() {
        return balance$;
    }

    /**
     * Sets balance of that player (from double)
     * @param balance balance of that player in dollars
     */
    public void setBalance(Money balance) {
        this.balance$ = balance;
    }

    /**
     * @return hand of this player (copy, not a link)
     */
    public Hand getHand(){
        if (hand == null) {
            return null;
        }
        return new Hand(hand);
    }
    /**
     * Sets a hand (copy, not a link)
     * @param hand hand to set
     */
    public void setHand(Hand hand){
        if (hand == null) {
            this.hand = null;
            return;
        }
        this.hand = new Hand(hand);
    }
    /**
     * PlayerInGame can be equal to another object only if that is another PlayerInGame.
     * IMPORTANT: only use it to compare Players in one exact game, NOT players from different games
     * @param obj compared Object
     * @return true i the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != PlayerInGame.class ) {
            return false;
        }
        if (this.id == null) {
            return ((PlayerInGame) obj).getId() == null;
        }
// Maybe I should change this to not just comparing the hashes
        return this.id.equals(((PlayerInGame) obj).id);
    }
    /**
     * @return hashcode of this object only using the id field
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    /**
     *
     * @return string representation of player in game like following:
     * (PlayerInGame| UserName: x, Id: h, Pos: p, Balance: b)
     * where x is the name of userName attached to PlayerInGame (if userProfile is not defined, x = _UNDEFINED_),
     * h is id, p is position, b is balance.
     */
    @Override
    public String toString() {
        String rep = "(PlayerInGame| UserName: ";
        if (ref != null) {
            rep +=  ref.getUserName();
        } else {
            rep += "_UNDEFINED_";
        }
        rep += ", Id: " + id + ", Pos: " + positionType + ", Balance: " + (balance$ != null ? balance$ : Money.of("0")) + ")";
        return rep;
    }
}