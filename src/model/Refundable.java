package model;

/**
 * Kontrak komparatif bagi setiap objek acara (Event) 
 * yang mendukung regulasi pengembalian dana (refund).
 */
public interface Refundable {

    /**
     * Menghitung nilai pengali atau rasio nominal pengembalian dana.
     * @param daysBeforeEvent Sisa hari pemesanan menuju pelaksanaan acara.
     * @return Rasio riil pengembalian (nilai dalam rentang 0.0 hingga 1.0).
     */
    double calculateRefund(int daysBeforeEvent);

    /**
     * Memeriksa kelayakan status acara untuk memproses pengembalian dana.
     * @return true jika kebijakan refund didukung, false jika sebaliknya.
     */
    boolean isRefundable();
}