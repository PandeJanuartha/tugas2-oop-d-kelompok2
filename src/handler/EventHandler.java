package handler;

import model.Event;
import model.Concert;
import model.Seminar;
import model.SportMatch;
import server.Request;
import server.Response;
import service.EventService;
import service.EventConflictException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * HTTP Handler untuk memproses rute masuk /api/events/* secara polimorfik.
 */
public class EventHandler {
    
    private final EventService eventService;

    public EventHandler(EventService eventService) {
        this.eventService = eventService;
    }

    public void getPriceSummary(Request req, Response res) {
        if (req == null || res == null) { return; }

        try {
            List<Event> events = this.eventService.getAllEvents();
            List<Map<String, Object>> summaryPayload = new ArrayList<Map<String, Object>>();

            for (Event e : events) {
                Map<String, Object> eventSummary = new LinkedHashMap<String, Object>();
                eventSummary.put("id", e.getId());
                eventSummary.put("name", e.getName());
                eventSummary.put("type", e.getType());
                eventSummary.put("prices", this.extractPolymorphicPrices(e));
                
                summaryPayload.add(eventSummary);
            }
            
            res.sendSuccess(summaryPayload);
        } catch (SQLException ex) {
            res.sendError(500, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            res.sendError(500, "Internal Server Error: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void create(Request req, Response res) {
        if (req == null || res == null) { return; }

        try {
            Map<String, Object> requestBody = req.getJSON();
            if (requestBody == null) {
                res.sendError(400, "Payload data tidak boleh kosong.");
                return;
            }

            String type = (String) requestBody.get("type");
            Event event = this.resolveEventInstanceFromType(type);
            if (event == null) {
                res.sendError(400, "Tipe acara tidak valid.");
                return;
            }

            this.bindJsonPayloadToEvent(event, requestBody);
            this.eventService.createEvent(event);
            res.sendCreated(event);

        } catch (EventConflictException ex) {
            // MULTIPLE CATCH: Urutan Exception Child (Spesifik) ditaruh paling atas
            res.sendError(400, ex.getMessage());
        } catch (SQLException ex) {
            res.sendError(500, "Gagal memproses query database: " + ex.getMessage());
        } catch (Exception ex) {
            // Exception Induk berada di paling bawah
            res.sendError(500, "Terjadi kesalahan internal: " + ex.getMessage());
        }
    }


    /**
     * Menerapkan Aturan Downcasting Aman wajib dicek menggunakan 'instanceof' sebelum diconvert.
     */
    private Map<String, Double> extractPolymorphicPrices(Event e) {
        Map<String, Double> priceMap = new LinkedHashMap<String, Double>();
        
        if (e instanceof Concert) {
            Concert concert = (Concert) e;
            priceMap.put("vip", concert.calculateTicketPrice("vip"));
            priceMap.put("regular", concert.calculateTicketPrice("regular"));
            priceMap.put("festival", concert.calculateTicketPrice("festival"));
        } else if (e instanceof SportMatch) {
            SportMatch sportMatch = (SportMatch) e;
            priceMap.put("tribune", sportMatch.calculateTicketPrice("tribune"));
            priceMap.put("vip", sportMatch.calculateTicketPrice("vip"));
            priceMap.put("vvip", sportMatch.calculateTicketPrice("vvip"));
        } else if (e instanceof Seminar) {
            Seminar seminar = (Seminar) e;
            priceMap.put("general", seminar.calculateTicketPrice("general"));
        }
        
        return priceMap;
    }

    private Event resolveEventInstanceFromType(String type) {
        if (type == null) { return null; }
        String typeKey = type.toLowerCase();
        if (typeKey.equals("concert")) { return new Concert(); }
        if (typeKey.equals("seminar")) { return new Seminar(); }
        if (typeKey.equals("sport_match")) { return new SportMatch(); }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void bindJsonPayloadToEvent(Event event, Map<String, Object> body) {
        String generatedId = "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        event.setId(generatedId);
        event.setName((String) body.get("name"));
        event.setVenueId((String) body.get("venueId"));
        event.setOrganizerId((String) body.get("organizerId"));
        event.setDate((String) body.get("date"));
        
        if (body.get("basePrice") != null) {
            event.setBasePrice(((Number) body.get("basePrice")).doubleValue());
        }
        if (body.get("capacity") != null) {
            event.setCapacities((Map<String, Integer>) body.get("capacity"));
        }
    }
}
