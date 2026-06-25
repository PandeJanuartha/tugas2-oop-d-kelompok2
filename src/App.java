import server.Server;
import database.DatabaseManager;
import handler.VenueHandler;
import handler.UserHandler;
import handler.EventHandler;
import handler.TicketHandler;
import repository.EventRepository;
import repository.TicketRepository;
import service.EventService;
import service.TicketService;

public class App {

    public static void main(String[] args) throws Exception {
        DatabaseManager.initialize();

        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        Server server = new Server(port);

        // --- Event routes (spesifik dulu sebelum {id}) ---
        EventHandler eventHandler = new EventHandler(new EventService(new EventRepository()));
        server.get("/api/events/price-summary", eventHandler::getPriceSummary);
        server.get("/api/events",              eventHandler::getAll);
        server.get("/api/events/{id}",         eventHandler::getById);
        server.post("/api/events",              eventHandler::create);
        server.put("/api/events/{id}",          eventHandler::update);

        // --- Venue routes ---
        server.get("/api/venues",      VenueHandler::getAll);
        server.get("/api/venues/{id}", VenueHandler::getById);
        server.post("/api/venues",     VenueHandler::create);
        server.put("/api/venues/{id}", VenueHandler::update);

        // --- User routes ---
        UserHandler userHandler = new UserHandler();
        server.get("/api/users",      userHandler);
        server.get("/api/users/{id}", userHandler);
        server.post("/api/users",     userHandler);
        server.put("/api/users/{id}", userHandler);

        // --- Ticket routes ---
        TicketHandler ticketHandler = new TicketHandler(
                new TicketService(new TicketRepository(), new EventRepository()));
        server.get("/api/tickets",              ticketHandler::getAllTickets);
        server.get("/api/tickets/{id}",         ticketHandler::getTicketById);
        server.post("/api/tickets",             ticketHandler::buyTicket);
        server.put("/api/tickets/{id}/refund",  ticketHandler::refundTicket);
        server.get("/api/reports/sales",        ticketHandler::getSalesReport);

        System.out.printf("Server running at http://localhost:%d%n", port);
        server.start();
    }
}
