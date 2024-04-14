package exceptions;

/**
 * Thrown to show that the card is incorrect (Mostly in Card constructor)
 */
public class IncorrectCardException extends Exception{
    public  IncorrectCardException() {
        message = "The hand is invalid - it must consist of 2 different cards";
    }

    public IncorrectCardException(String message) {
        this.message = message;
    }

    private final String message;
    @Override
    public String getMessage() {
        return message;
    }
}
