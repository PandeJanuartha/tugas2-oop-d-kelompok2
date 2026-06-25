package service;

import exception.EventConflictException;
import model.Event;
import repository.EventRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Penghubung logika bisnis utama penanganan eksekusi data Event.
 */
public class EventService {
    
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void createEvent(Event event) throws EventConflictException, SQLException {
        if (event == null) { return; }

        this.validateVenueAvailability(event.getVenueId(), event.getDate());
        this.eventRepository.save(event);
    }

    public List<Event> getAllEvents() throws SQLException {
        return this.getAllEvents(null, null);
    }

    public List<Event> getAllEvents(String type, String dateFrom) throws SQLException {
        List<Event> all = this.eventRepository.findAll();
        if ((type == null || type.isEmpty()) && (dateFrom == null || dateFrom.isEmpty())) {
            return all;
        }

        List<Event> filtered = new java.util.ArrayList<>();
        for (Event e : all) {
            boolean matchType = true;
            boolean matchDate = true;

            if (type != null && !type.isEmpty()) {
                matchType = e.getType().equalsIgnoreCase(type);
            }
            if (dateFrom != null && !dateFrom.isEmpty()) {
                matchDate = e.getDate().compareTo(dateFrom) >= 0;
            }

            if (matchType && matchDate) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    public Event getEventById(String id) throws SQLException {
        if (id == null) { return null; }
        return this.eventRepository.findById(id);
    }

    public Event updateEvent(String id, Event updatedData) throws SQLException, EventConflictException {
        Event existing = this.eventRepository.findById(id);
        if (existing == null) {
            return null;
        }

        String newDate = updatedData.getDate() != null ? updatedData.getDate() : existing.getDate();

        if (updatedData.getDate() != null && !updatedData.getDate().equals(existing.getDate())) {
            this.validateVenueAvailability(existing.getVenueId(), newDate);
        }

        if (updatedData.getName() != null) {
            existing.setName(updatedData.getName());
        }
        if (updatedData.getDate() != null) {
            existing.setDate(updatedData.getDate());
        }
        if (updatedData.getBasePrice() >= 0) {
            existing.setBasePrice(updatedData.getBasePrice());
        }

        this.eventRepository.update(existing);
        return existing;
    }

    public Map<String, Integer> getRemainingCapacities(String eventId) throws SQLException {
        return this.eventRepository.findRemainingCapacities(eventId);
    }

    /**
     * Mengekstrak aturan validasi bisnis ke metode terisolasi agar kode bersih dan mudah dibaca.
     */
    private void validateVenueAvailability(String venueId, String date) throws EventConflictException, SQLException {
        if (this.eventRepository.hasConflictingEvent(venueId, date)) {
            throw new EventConflictException("Gagal menjadwalkan acara: Lokasi (Venue ID: " 
                + venueId + ") telah dipesan untuk tanggal " + date + ".");
        }
    }
}