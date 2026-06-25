package handler;

import exception.EventNotFoundException;
import exception.RefundNotAllowedException;
import exception.TicketSoldOutException;
import model.Ticket;
import server.Request;
import server.Response;
import service.TicketService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP Handler untuk memproses rute masuk /api/tickets/* dan /api/reports/sales.
 *
 * Rute yang ditangani:
 *   GET    /api/tickets                    → getAllTickets (filter: eventId, userId, status)
 *   GET    /api/tickets/{id}               → getTicketById
 *   POST   /api/tickets                    → buyTicket
 *   POST   /api/tickets/{id}/refund        → refundTicket
 *   GET    /api/reports/sales?eventId={id} → getSalesReport
 *
 * @author Grevi
 */
public class TicketHandler {

    private final TicketService ticketService;

    public TicketHandler(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // =============================================
    // GET /api/tickets
    // Query params opsional: eventId, userId, status
    // =============================================

    public void getAllTickets(Request req, Response res) {
        if (req == null || res == null) return;

        try {
            String eventId = req.getQueryParam("eventId");
            String userId  = req.getQueryParam("userId");
            String status  = req.getQueryParam("status");

            List<Ticket> tickets = ticketService.getAllTickets(eventId, userId, status);
            res.sendSuccess(tickets);

        } catch (Exception ex) {
            res.sendError(500, "Internal Server Error: " + ex.getMessage());
        }
    }

    // =============================================
    // GET /api/tickets/{id}
    // =============================================

    public void getTicketById(Request req, Response res) {
        if (req == null || res == null) return;

        try {
            String id = req.getPathParam("id");
            Ticket ticket = ticketService.getTicketById(id);
            res.sendSuccess(ticket);

        } catch (EventNotFoundException ex) {
            res.sendError(404, ex.getMessage());
        } catch (Exception ex) {
            res.sendError(500, "Internal Server Error: " + ex.getMessage());
        }
    }

    // =============================================
    // POST /api/tickets
    // Body: { eventId, userId, category, quantity }
    // =============================================

    public void buyTicket(Request req, Response res) {
        if (req == null || res == null) return;

        try {
            Map<String, Object> body = req.getJSON();
            if (body == null) {
                res.sendError(400, "Request body harus berformat JSON.");
                return;
            }

            String eventId  = (String) body.get("eventId");
            String userId   = (String) body.get("userId");
            String category = (String) body.get("category");

            if (eventId == null || eventId.isEmpty()) {
                res.sendError(400, "Field 'eventId' wajib diisi.");
                return;
            }
            if (userId == null || userId.isEmpty()) {
                res.sendError(400, "Field 'userId' wajib diisi.");
                return;
            }
            if (category == null || category.isEmpty()) {
                res.sendError(400, "Field 'category' wajib diisi.");
                return;
            }

            // Parse quantity
            int quantity;
            try {
                quantity = ((Number) body.get("quantity")).intValue();
            } catch (NullPointerException | ClassCastException e) {
                res.sendError(400, "Field 'quantity' wajib diisi dan harus berupa angka.");
                return;
            }

            if (quantity <= 0) {
                res.sendError(400, "Field 'quantity' harus lebih dari 0.");
                return;
            }

            Ticket ticket = ticketService.buyTicket(eventId, userId, category, quantity);
            res.sendCreated(ticket);

        } catch (EventNotFoundException ex) {
            res.sendError(404, ex.getMessage());
        } catch (TicketSoldOutException ex) {
            res.sendError(400, ex.getMessage());
        } catch (SQLException ex) {
            res.sendError(500, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            res.sendError(500, "Internal Server Error: " + ex.getMessage());
        }
    }

    // =============================================
    // POST /api/tickets/{id}/refund
    // =============================================

    public void refundTicket(Request req, Response res) {
        if (req == null || res == null) return;

        try {
            String id = req.getPathParam("id");
            Ticket ticket = ticketService.refundTicket(id);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("ticket", ticket);
            responseData.put("refundAmount", ticket.getRefundAmount());
            responseData.put("message", "Refund berhasil diproses.");

            res.sendSuccess(responseData);

        } catch (EventNotFoundException ex) {
            res.sendError(404, ex.getMessage());
        } catch (RefundNotAllowedException ex) {
            res.sendError(400, ex.getMessage());
        } catch (SQLException ex) {
            res.sendError(500, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            res.sendError(500, "Internal Server Error: " + ex.getMessage());
        }
    }

    // =============================================
    // GET /api/reports/sales?eventId={id}
    // =============================================

    public void getSalesReport(Request req, Response res) {
        if (req == null || res == null) return;

        try {
            String eventId = req.getQueryParam("eventId");
            if (eventId == null || eventId.isEmpty()) {
                res.sendError(400, "Query parameter 'eventId' wajib diisi.");
                return;
            }

            List<Ticket> tickets = ticketService.getSalesReport(eventId);

            // Hitung ringkasan penjualan
            int totalQuantity = 0;
            double totalRevenue = 0.0;
            for (Ticket t : tickets) {
                totalQuantity += t.getQuantity();
                totalRevenue  += t.getTotalPrice();
            }

            Map<String, Object> report = new HashMap<>();
            report.put("eventId", eventId);
            report.put("totalTicketsSold", totalQuantity);
            report.put("totalRevenue", totalRevenue);
            report.put("tickets", tickets);

            res.sendSuccess(report);

        } catch (EventNotFoundException ex) {
            res.sendError(404, ex.getMessage());
        } catch (SQLException ex) {
            res.sendError(500, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            res.sendError(500, "Internal Server Error: " + ex.getMessage());
        }
    }
}
