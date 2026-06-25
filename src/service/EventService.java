package service;

import model.Event;
import repository.EventRepository;

import java.sql.SQLException;
import java.util.List;

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
        return this.eventRepository.findAll();
    }

    public Event getEventById(String id) throws SQLException {
        if (id == null) { return null; }
        return this.eventRepository.findById(id);
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