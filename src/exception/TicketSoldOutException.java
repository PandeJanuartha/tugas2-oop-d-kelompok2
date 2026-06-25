package exception;

/**
 * [400] Tiket terjual habis.
 *
 * @author Oka
 */
public class TicketSoldOutException extends Exception {
  
    /** No-Arg Constructor */
    public TicketSoldOutException() {
        super("Tiket untuk kategori diminta telah habis.");
    }

    /** Parameterized Constructor */
    public TicketSoldOutException(String category) {
        super("Tiket untuk kategori '" + category + "' telah habis.");
    }
}
