package handler;

import exception.VenueNotFoundException;
import model.Venue;
import server.Request;
import server.Response;
import service.VenueService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HTTP handler untuk /api/venues endpoints.
 * Semua method adalah entry point statis yang ada di App.java
 *
 * @author Oka
 */
public class VenueHandler {

    private static final VenueService service = new VenueService();

    /** GET /api/venues */
    public static void getAll(Request req, Response res) {
        try {
            List<Venue> venues = service.getAllVenues();
            res.sendSuccess(venues.stream().map(VenueHandler::toMap).collect(Collectors.toList()));
        } catch (SQLException e) {
            res.sendError(500, "database error: " + e.getMessage());
        }
    }

    /** GET /api/venues/{id} */
    public static void getById(Request req, Response res) {
        String id = req.getPathParam("id");
        try {
            Map<String, Object> data = service.getVenueDetail(id);
            res.sendSuccess(data);
        } catch (VenueNotFoundException e) {
            res.sendError(404, e.getMessage());
        } catch (SQLException e) {
            res.sendError(500, "database error: " + e.getMessage());
        }
    }

    /** POST /api/venues */
    public static void create(Request req, Response res) {
        Map<String, Object> body;
        try {
            body = req.getJSON();
        } catch (Exception e) {
            res.sendError(400, "JSON tidak valid: " + e.getMessage());
            return;
        }
        if (body == null) {
            res.sendError(400, "isi permintaan harus berupa JSON yang valid dengan Content-Type: application/json.");
            return;
        }
        try {
            Venue venue = service.createVenue(body);
            res.sendCreated(toMap(venue));
        } catch (IllegalArgumentException e) {
            res.sendError(400, e.getMessage());
        } catch (SQLException e) {
            res.sendError(500, "database error: " + e.getMessage());
        }
    }

    /** PUT /api/venues/{id} */
    public static void update(Request req, Response res) {
        String id = req.getPathParam("id");
        Map<String, Object> body;
        try {
            body = req.getJSON();
        } catch (Exception e) {
            res.sendError(400, "JSON tidak valid: " + e.getMessage());
            return;
        }
        if (body == null) {
            res.sendError(400, "isi permintaan harus berupa JSON yang valid dengan Content-Type: application/json.");
            return;
        }
        try {
            Venue venue = service.updateVenue(id, body);
            res.sendSuccess(toMap(venue));
        } catch (VenueNotFoundException e) {
            res.sendError(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            res.sendError(400, e.getMessage());
        } catch (SQLException e) {
            res.sendError(500, "database error: " + e.getMessage());
        }
    }

    private static Map<String, Object> toMap(Venue v) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", v.getId());
        m.put("name", v.getName());
        m.put("address", v.getAddress());
        m.put("maxCapacity", v.getMaxCapacity());
        return m;
    }
}
