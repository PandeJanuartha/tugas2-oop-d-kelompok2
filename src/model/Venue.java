package model;

import java.util.List;
import java.util.Map;

/**
 * model: venue dimana event diselenggarakan.
 *
 * @author Oka
 */
public class Venue {

    private String id;
    private String name;
    private String address;
    private int maxCapacity;
    private String createdAt;

    /** 
     * Ringkasan event yang diselenggarakan. 
     * Dipopulasi saat pemanggilan detail event spesifik (handler.Venue).
     */
    private List<Map<String, Object>> events;

    /** No-Arg Constructor */
    public Venue() {}

    /** Parameterized Constructor */
    public Venue(String id, String name, String address, int maxCapacity, String createdAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.maxCapacity = maxCapacity;
        this.createdAt = createdAt;
    }

    /** Copy Constructor */
    public Venue(Venue other) {
        if (other != null) {
            this.id = other.id;
            this.name = other.name;
            this.address = other.address;
            this.maxCapacity = other.maxCapacity;
            this.createdAt = other.createdAt;
            this.events = other.events;
        }
    }

    // Getters

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getMaxCapacity() { return maxCapacity; }
    public String getCreatedAt() { return createdAt; }
    public List<Map<String, Object>> getEvents() { return events; }

    // Setters

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity > 0) this.maxCapacity = maxCapacity;
    }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setEvents(List<Map<String, Object>> events) { this.events = events; }

    // Standard Method

    @Override
    public String toString() {
        return "Venue [id=" + id + ", nama=" + name + ", Kapasitas=" + maxCapacity + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Venue other = (Venue) obj;
        return this.id != null && this.id.equals(other.id);
    }
}
