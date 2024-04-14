package exceptions;

/**
 * Thrown to show that the hand is incorrect, invalid (mostly in hand constructor)
 */
public class IncorrectHandException extends Exception {
    public  IncorrectHandException() {
        message = "The hand is invalid - it must consist of 2 different cards";
    }

    public IncorrectHandException(String message) {
        this.message = message;
    }

    private final String message;
    @Override
    public String getMessage() {
        return message;
    }
}
