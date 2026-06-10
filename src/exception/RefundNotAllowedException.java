package exception;

/**
 * [400] Permintaan refund bersifat non-refundable.
 *
 * @author Oka
 */
public class RefundNotAllowedException extends Exception {

    /** No-Arg Constructor */
    public RefundNotAllowedException() {
        super("Jenis event ini tidak menerima refunds.");
    }

    /** Parameterized Constructor */
    public RefundNotAllowedException(String message) {
        super(message);
    }
}
