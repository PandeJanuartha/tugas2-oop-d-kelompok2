package exception;

/**
 * [404] Venue tidak ditemukan.
 *
 * @author Oka
 */
public class VenueNotFoundException extends Exception {

    /** No-Arg Constructor */
    public VenueNotFoundException() {
        super("Venue tidak ditemukan.");
    }

    /** Parameterized Constructor */
    public VenueNotFoundException(String id) {
        super("Venue dengan id '" + id + "' tidak ditemukan.");
    }
}
