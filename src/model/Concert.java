package model;

import java.util.Map;

/**
 * Subclass konkret penampung tipe Acara Konser.
 */
public class Concert extends Event implements Refundable {

    public Concert() {
        super();
        this.setType("concert");
    }

    public Concert(String id, String name, String venueId, String organizerId, 
                   String date, double basePrice, Map<String, Integer> capacities, boolean isPublished) {
        super(id, "concert", name, venueId, organizerId, date, basePrice, capacities, isPublished);
    }

    public Concert(Concert original) {
        super(original);
    }

    @Override
    public double calculateTicketPrice(String category) {
        if (category == null) {
            return this.getBasePrice();
        }

        String targetCategory = category.toLowerCase();
        if (targetCategory.equals("vip")) {
            return this.getBasePrice() * 3.0;
        }
        if (targetCategory.equals("regular")) {
            return this.getBasePrice() * 1.0;
        }
        if (targetCategory.equals("festival")) {
            return this.getBasePrice() * 0.7;
        }
        return this.getBasePrice();
    }

    @Override
    public double calculateRefund(int daysBeforeEvent) {
        if (daysBeforeEvent > 14) {
            return 1.0;
        }
        if (daysBeforeEvent >= 7) {
            return 0.5;
        }
        return 0.0;
    }

    @Override
    public boolean isRefundable() { return true; }

    @Override
    public String toString() {
        return "Concert State [" + super.toString() + ", Refundable: true]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }
}