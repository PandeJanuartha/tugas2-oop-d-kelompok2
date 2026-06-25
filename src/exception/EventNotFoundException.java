package exception;

/**
 * [404] Event tidak ditemukan.
 *
 * @author Oka
 */
public class EventNotFoundException extends Exception {

    /** No-Arg Constructor */
    public EventNotFoundException() {
        super("Event tidak ditemukan.");
    }

    /** Parameterized Constructor */
    public EventNotFoundException(String id) {
        super("Event dengan id '" + id + "' tidak ditemukan.");
    }
}
