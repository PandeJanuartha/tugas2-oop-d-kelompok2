package exception;

/**
 * [404] User tidak ditemukan.
 *
 * @author Oka
 */
public class UserNotFoundException extends Exception {
    
    /** No-Arg Constructor */
    public UserNotFoundException() {
        super("User tidak ditemukan.");
    }

    /** Parameterized Constructor */
    public UserNotFoundException(String id) {
        super("User dengan id '" + id + "' tidak ditemukan.");
    }
}
