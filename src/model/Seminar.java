package model;

import java.util.Map;

/**
 * Subclass konkret penampung tipe Acara Seminar.
 */
public class Seminar extends Event implements Refundable {

    public Seminar() {
        super();
        this.setType("seminar");
    }

    public Seminar(String id, String name, String venueId, String organizerId, 
                   String date, double basePrice, Map<String, Integer> capacities, boolean isPublished) {
        super(id, "seminar", name, venueId, organizerId, date, basePrice, capacities, isPublished);
    }

    public Seminar(Seminar original) {
        super(original);
    }

    @Override
    public double calculateTicketPrice(String category) {
        return this.getBasePrice();
    }

    @Override
    public double calculateRefund(int daysBeforeEvent) {
        if (daysBeforeEvent > 1) {
            return 1.0;
        }
        return 0.0;
    }

    @Override
    public boolean isRefundable() { return true; }

    @Override
    public String toString() {
        return "Seminar State [" + super.toString() + ", Refundable: true]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }
}