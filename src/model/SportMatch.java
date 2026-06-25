package model;

import java.util.Map;

/**
 * Subclass konkret penampung tipe Acara Pertandingan Olahraga (Non-Refundable).
 */
public class SportMatch extends Event {

    public SportMatch() {
        super();
        this.setType("sport_match");
    }

    public SportMatch(String id, String name, String venueId, String organizerId, 
                      String date, double basePrice, Map<String, Integer> capacities, boolean isPublished) {
        super(id, "sport_match", name, venueId, organizerId, date, basePrice, capacities, isPublished);
    }

    public SportMatch(SportMatch original) {
        super(original);
    }

    @Override
    public double calculateTicketPrice(String category) {
        if (category == null) {
            return this.getBasePrice();
        }

        String targetCategory = category.toLowerCase();
        if (targetCategory.equals("tribune")) {
            return this.getBasePrice() * 1.0;
        }
        if (targetCategory.equals("vip")) {
            return this.getBasePrice() * 2.5;
        }
        if (targetCategory.equals("vvip")) {
            return this.getBasePrice() * 5.0;
        }
        return this.getBasePrice();
    }

    @Override
    public String toString() {
        return "SportMatch State [" + super.toString() + ", Refundable: false]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }
}