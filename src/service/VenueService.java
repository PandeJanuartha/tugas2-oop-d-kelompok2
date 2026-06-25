package service;

import exception.VenueNotFoundException;
import model.Venue;
import repository.VenueRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business logic dari venue.
 *
 * @author Oka
 */
public class VenueService {

    private final VenueRepository repo = new VenueRepository();

    public List<Venue> getAllVenues() throws SQLException {
        return repo.findAll();
    }

    /**
     * @throws VenueNotFoundException venue tidak ditemukan
     */
    public Map<String, Object> getVenueDetail(String id) throws SQLException, VenueNotFoundException {
        Venue venue = repo.findById(id);
        if (venue == null) throw new VenueNotFoundException(id);

        List<Map<String, Object>> events = repo.findEventsByVenueId(id);
        return buildDetailMap(venue, events);
    }

    /**
     * @throws IllegalArgumentException field diminta kosong atau invalid
     */
    public Venue createVenue(Map<String, Object> body) throws SQLException {
        String name    = (String) body.get("name");
        String address = (String) body.get("address");
        Object capRaw  = body.get("maxCapacity");

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("nama wajib diisi.");
        if (address == null || address.isBlank())
            throw new IllegalArgumentException("alamat wajib diisi.");
        if (capRaw == null)
            throw new IllegalArgumentException("kapasitas wajib diisi.");

        int maxCapacity;
        try {
            maxCapacity = ((Number) capRaw).intValue();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("kapasitas harus berupa angka.");
        }

        if (maxCapacity <= 0)
            throw new IllegalArgumentException("kapasitas harus lebih besar dari 0.");

        String id = repo.generateNextId();
        Venue venue = new Venue(id, name, address, maxCapacity, null);
        repo.save(venue);
        return repo.findById(id);
    }

    /**
     * @throws VenueNotFoundException  venue id spesifik tidak ada 
     * @throws IllegalArgumentException if no valid update fields are provided
     */
    public Venue updateVenue(String id, Map<String, Object> body) throws SQLException, VenueNotFoundException {
        Venue venue = repo.findById(id);
        if (venue == null) throw new VenueNotFoundException(id);

        if (body == null || body.isEmpty())
            throw new IllegalArgumentException("isi permintaan tidak boleh kosong.");

        if (body.containsKey("name")) {
            String name = (String) body.get("name");
            if (name == null || name.isBlank()) throw new IllegalArgumentException("nama tidak boleh kosong.");
            venue.setName(name);
        }
        if (body.containsKey("address")) {
            String address = (String) body.get("address");
            if (address == null || address.isBlank()) throw new IllegalArgumentException("alamat tidak boleh kosong.");
            venue.setAddress(address);
        }
        if (body.containsKey("maxCapacity")) {
            int cap;
            try {
                cap = ((Number) body.get("maxCapacity")).intValue();
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("kapasitas harus berupa angka.");
            }
            if (cap <= 0) throw new IllegalArgumentException("kapasitas harus lebih besar dari 0.");
            venue.setMaxCapacity(cap);
        }

        repo.update(venue);
        return repo.findById(id);
    }

    private Map<String, Object> buildDetailMap(Venue venue, List<Map<String, Object>> events) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", venue.getId());
        data.put("name", venue.getName());
        data.put("address", venue.getAddress());
        data.put("maxCapacity", venue.getMaxCapacity());
        data.put("events", events);
        return data;
    }
}
