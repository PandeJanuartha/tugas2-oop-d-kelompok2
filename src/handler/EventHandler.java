package handler;

import model.Event;
import model.Concert;
import model.Seminar;
import model.SportMatch;
import server.Request;
import server.Response;
import service.EventService;
import exception.EventConflictException;

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
    private final repository.VenueRepository venueRepository = new repository.VenueRepository();
    private final repository.UserRepository userRepository = new repository.UserRepository();

    public EventHandler(EventService eventService) {
        this.eventService = eventService;
    }

    public void getAll(Request req, Response res) {
        if (req == null || res == null) { return; }
        try {
            String type = req.getQueryParam("type");
            String dateFrom = req.getQueryParam("dateFrom");

            List<Event> events = this.eventService.getAllEvents(type, dateFrom);
            List<Map<String, Object>> responsePayload = new ArrayList<>();

            for (Event e : events) {
                model.Venue venue = venueRepository.findById(e.getVenueId());
                model.User organizer = userRepository.findById(e.getOrganizerId());

                Map<String, Object> eventMap = new LinkedHashMap<>();
                eventMap.put("id", e.getId());
                eventMap.put("type", e.getType());
                eventMap.put("name", e.getName());

                Map<String, Object> venueMap = new LinkedHashMap<>();
                if (venue != null) {
                    venueMap.put("id", venue.getId());
                    venueMap.put("name", venue.getName());
                } else {
                    venueMap.put("id", e.getVenueId());
                    venueMap.put("name", "Unknown Venue");
                }
                eventMap.put("venue", venueMap);

                Map<String, Object> organizerMap = new LinkedHashMap<>();
                if (organizer != null) {
                    organizerMap.put("id", organizer.getId());
                    organizerMap.put("name", organizer.getName());
                } else {
                    organizerMap.put("id", e.getOrganizerId());
                    organizerMap.put("name", "Unknown Organizer");
                }
                eventMap.put("organizer", organizerMap);

                eventMap.put("date", e.getDate());
                eventMap.put("basePrice", e.getBasePrice());
                eventMap.put("priceList", this.extractPolymorphicPrices(e));

                Map<String, Integer> remaining = this.eventService.getRemainingCapacities(e.getId());
                eventMap.put("remainingCapacity", remaining);

                if (e instanceof model.Refundable) {
                    eventMap.put("refundable", true);
                } else {
                    eventMap.put("refundable", false);
                }

                responsePayload.add(eventMap);
            }

            res.sendSuccess(responsePayload);
        } catch (SQLException ex) {
            res.sendError(500, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            res.sendError(500, "Internal Server Error: " + ex.getMessage());
        }
    }

    public void getById(Request req, Response res) {
        if (req == null || res == null) { return; }
        String id = req.getPathParam("id");
        try {
            Event e = this.eventService.getEventById(id);
            if (e == null) {
                res.sendError(404, "Event dengan id '" + id + "' tidak ditemukan.");
                return;
            }

            model.Venue venue = venueRepository.findById(e.getVenueId());
            model.User organizer = userRepository.findById(e.getOrganizerId());

            Map<String, Object> eventDetail = new LinkedHashMap<>();
            eventDetail.put("id", e.getId());
            eventDetail.put("type", e.getType());
            eventDetail.put("name", e.getName());

            Map<String, Object> venueMap = new LinkedHashMap<>();
            if (venue != null) {
                venueMap.put("id", venue.getId());
                venueMap.put("name", venue.getName());
            } else {
                venueMap.put("id", e.getVenueId());
                venueMap.put("name", "Unknown Venue");
            }
            eventDetail.put("venue", venueMap);

            Map<String, Object> organizerMap = new LinkedHashMap<>();
            if (organizer != null) {
                organizerMap.put("id", organizer.getId());
                organizerMap.put("name", organizer.getName());
            } else {
                organizerMap.put("id", e.getOrganizerId());
                organizerMap.put("name", "Unknown Organizer");
            }
            eventDetail.put("organizer", organizerMap);

            eventDetail.put("date", e.getDate());
            eventDetail.put("basePrice", e.getBasePrice());
            eventDetail.put("priceList", this.extractPolymorphicPrices(e));

            Map<String, Integer> remaining = this.eventService.getRemainingCapacities(id);
            eventDetail.put("remainingCapacity", remaining);

            if (e instanceof model.Refundable) {
                eventDetail.put("refundable", true);
                if (e instanceof model.Concert) {
                    eventDetail.put("refundPolicy", "100% if >14 days, 50% if 7-14 days, 0% if <7 days");
                } else if (e instanceof model.Seminar) {
                    eventDetail.put("refundPolicy", "100% if >1 days, 0% if <=1 days");
                }
            } else {
                eventDetail.put("refundable", false);
            }

            res.sendSuccess(eventDetail);

        } catch (SQLException ex) {
            res.sendError(500, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            res.sendError(500, "Internal Server Error: " + ex.getMessage());
        }
    }

    public void update(Request req, Response res) {
        if (req == null || res == null) { return; }
        String id = req.getPathParam("id");
        try {
            Map<String, Object> requestBody = req.getJSON();
            if (requestBody == null) {
                res.sendError(400, "Payload data tidak boleh kosong.");
                return;
            }

            Event existing = this.eventService.getEventById(id);
            if (existing == null) {
                res.sendError(404, "Event dengan id '" + id + "' tidak ditemukan.");
                return;
            }

            Event tempEvent = resolveEventInstanceFromType(existing.getType());
            tempEvent.setId(id);
            tempEvent.setName((String) requestBody.get("name"));
            tempEvent.setDate((String) requestBody.get("date"));
            if (requestBody.get("basePrice") != null) {
                tempEvent.setBasePrice(((Number) requestBody.get("basePrice")).doubleValue());
            } else {
                tempEvent.setBasePrice(-1.0);
            }

            Event updated = this.eventService.updateEvent(id, tempEvent);
            if (updated == null) {
                res.sendError(404, "Event dengan id '" + id + "' tidak ditemukan.");
                return;
            }

            res.sendSuccess(updated);

        } catch (EventConflictException ex) {
            res.sendError(400, ex.getMessage());
        } catch (SQLException ex) {
            res.sendError(500, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            res.sendError(500, "Internal Server Error: " + ex.getMessage());
        }
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
