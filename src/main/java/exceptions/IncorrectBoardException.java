package exceptions;

/**
 * Thrown to indicate that the baord is incorrect - either invalid, or does not have correct size
 */
public class IncorrectBoardException extends Exception {
    public  IncorrectBoardException() {
        message = "The board is incorrect - it must contain from 3 to 5 " +
                "unique cards";
    }

    public IncorrectBoardException(String message) {
        this.message = message;
    }

    private final String message;
    @Override
    public String getMessage() {
        return message;
    }
}
