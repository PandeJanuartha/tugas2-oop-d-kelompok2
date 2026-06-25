package exception;

/**
 * Custom Exception pendukung pelaporan kegagalan bentrok lokasi pengerjaan event.
 */
public class EventConflictException extends Exception {

    public EventConflictException() {
        super("Terjadi konflik penjadwalan pada data Event.");
    }

    public EventConflictException(String message) {
        super(message);
    }
}
