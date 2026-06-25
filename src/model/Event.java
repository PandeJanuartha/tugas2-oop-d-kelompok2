package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Kelas induk abstrak yang membungkus blueprint utama entitas Acara.
 * Menerapkan enkapsulasi variabel instans secara ketat dan anti-privacy leak.
 */
public abstract class Event {
    
    private String id;
    private String type;
    private String name;
    private String venueId;
    private String organizerId;
    private String date; 
    private double basePrice;
    private Map<String, Integer> capacities; 
    private boolean isPublished;

    /**
     * a. NO-ARG CONSTRUCTOR (Inisialisasi Deterministik)
     */
    public Event() {
        this.id = "";
        this.type = "";
        this.name = "";
        this.venueId = "";
        this.organizerId = "";
        this.date = "2026-01-01";
        this.basePrice = 0.0;
        this.capacities = new HashMap<String, Integer>();
        this.isPublished = false;
    }

    /**
     * b. PARAMETERIZED CONSTRUCTOR
     */
    public Event(String id, String type, String name, String venueId, String organizerId, 
                 String date, double basePrice, Map<String, Integer> capacities, boolean isPublished) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.venueId = venueId;
        this.organizerId = organizerId;
        this.date = date;
        this.setBasePrice(basePrice);
        this.setCapacities(capacities);
        this.isPublished = isPublished;
    }

    /**
     * c. COPY CONSTRUCTOR (Deep Copy)
     */
    public Event(Event original) {
        if (original != null) {
            this.id = original.id;
            this.type = original.type;
            this.name = original.name;
            this.venueId = original.venueId;
            this.organizerId = original.organizerId;
            this.date = original.date;
            this.basePrice = original.basePrice;
            this.isPublished = original.isPublished;
            this.setCapacities(original.capacities);
        }
    }

    /**
     * Menghitung nominal harga tiket berdasarkan kategori tertentu secara polimorfik.
     */
    public abstract double calculateTicketPrice(String category);

    // ==========================================
    // ACCESSORS & MUTATORS (Anti-Privacy Leak)
    // ==========================================
    
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return this.type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getVenueId() { return this.venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }

    public String getOrganizerId() { return this.organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getDate() { return this.date; }
    public void setDate(String date) { this.date = date; }

    public double getBasePrice() { return this.basePrice; }
    
    public void setBasePrice(double basePrice) {
        if (basePrice >= 0.0) {
            this.basePrice = basePrice;
        }
    }

    public Map<String, Integer> getCapacities() {
        return this.deepCopyMap(this.capacities);
    }

    public void setCapacities(Map<String, Integer> capacities) {
        this.capacities = this.deepCopyMap(capacities);
    }

    public boolean isPublished() { return this.isPublished; }
    public void setPublished(boolean isPublished) { this.isPublished = isPublished; }

    /**
     * Helper internal untuk melakukan klon atau deep copy pemetaan Map mutable.
     */
    private Map<String, Integer> deepCopyMap(Map<String, Integer> sourceMap) {
        Map<String, Integer> destinationMap = new HashMap<String, Integer>();
        if (sourceMap != null) {
            for (Map.Entry<String, Integer> entry : sourceMap.entrySet()) {
                destinationMap.put(entry.getKey(), entry.getValue());
            }
        }
        return destinationMap;
    }

    // ==========================================
    // OBJECT MANDATORY OVERRIDES
    // ==========================================
    
    @Override
    public String toString() {
        return "Event State [ID: " + this.id + ", Tipe: " + this.type + ", Nama: " + this.name + ", Harga Dasar: " + this.basePrice + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Event other = (Event) obj;
        return this.isFieldContentEqual(other);
    }

    private boolean isFieldContentEqual(Event other) {
        boolean isIdMatch = (this.id == null) ? (other.id == null) : this.id.equals(other.id);
        boolean isTypeMatch = (this.type == null) ? (other.type == null) : this.type.equals(other.type);
        boolean isNameMatch = (this.name == null) ? (other.name == null) : this.name.equals(other.name);
        boolean isVenueMatch = (this.venueId == null) ? (other.venueId == null) : this.venueId.equals(other.venueId);
        boolean isOrganizerMatch = (this.organizerId == null) ? (other.organizerId == null) : this.organizerId.equals(other.organizerId);
        boolean isDateMatch = (this.date == null) ? (other.date == null) : this.date.equals(other.date);
        
        return isIdMatch && isTypeMatch && isNameMatch && isVenueMatch && 
               isOrganizerMatch && isDateMatch && (this.basePrice == other.basePrice) && 
               (this.isPublished == other.isPublished);
    }
}