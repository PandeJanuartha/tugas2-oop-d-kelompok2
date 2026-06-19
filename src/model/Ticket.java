package model;

public class Ticket {

    private String id;
    private String eventId;
    private String userId;
    private String category;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String purchaseDate;
    private String status;
    private double refundAmount;

    // Constructor kosong
    public Ticket() {
    }

    // Constructor lengkap
    public Ticket(String id, String eventId, String userId, String category,
                  int quantity, double unitPrice, double totalPrice,
                  String purchaseDate, String status, double refundAmount) {

        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
        this.status = status;
        this.refundAmount = refundAmount;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getStatus() {
        return status;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", status='" + status + '\'' +
                ", refundAmount=" + refundAmount +
                '}';
    }
}