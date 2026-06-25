package handler;

import exception.UserNotFoundException;
import model.User;
import server.Request;
import server.Response;
import server.RouteHandler;
import service.UserService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserHandler implements RouteHandler {

    private final UserService userService;

    public UserHandler() {
        this.userService = new UserService();
    }

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Request req, Response res) throws Exception {
        String method = req.getMethod();
        String path = req.getPath();

        boolean hasId = req.getPathParam("id") != null && !req.getPathParam("id").isEmpty();

        if ("GET".equalsIgnoreCase(method) && !hasId) {
            handleGetAll(req, res);
        } else if ("POST".equalsIgnoreCase(method) && !hasId) {
            handleCreate(req, res);
        } else if ("GET".equalsIgnoreCase(method) && hasId) {
            handleGetById(req, res);
        } else if ("PUT".equalsIgnoreCase(method) && hasId) {
            handleUpdate(req, res);
        } else {
            res.sendNotFound("Endpoint not found.");
        }
    }

    private void handleGetAll(Request req, Response res) throws Exception {
        String roleFilter = req.getQueryParam("role");
        List<User> users = userService.findAll(roleFilter);
        res.sendOk(users);
    }

    private void handleCreate(Request req, Response res) throws Exception {
        User user = req.getBody(User.class);
        if (user == null) {
            res.sendBadRequest("Request body is required.");
            return;
        }

        try {
            User created = userService.register(user);
            res.sendCreated(created);
        } catch (IllegalArgumentException e) {
            res.sendBadRequest(e.getMessage());
        }
    }

    private void handleGetById(Request req, Response res) throws Exception {
        String id = req.getPathParam("id");
        try {
            User user = userService.findById(id);
            Map<String, Object> activitySummary = userService.getActivitySummary(user);

            Map<String, Object> responseBody = new LinkedHashMap<>();
            responseBody.put("id", user.getId());
            responseBody.put("name", user.getName());
            responseBody.put("email", user.getEmail());
            responseBody.put("phone", user.getPhone());
            responseBody.put("role", user.getRole());
            responseBody.put("created_at", user.getCreatedAt());
            responseBody.put("activity_summary", activitySummary);

            res.sendOk(responseBody);
        } catch (UserNotFoundException e) {
            res.sendNotFound(e.getMessage());
        }
    }

    private void handleUpdate(Request req, Response res) throws Exception {
        String id = req.getPathParam("id");
        User updatedData = req.getBody(User.class);
        if (updatedData == null) {
            res.sendBadRequest("Request body is required.");
            return;
        }

        try {
            User updated = userService.update(id, updatedData);
            res.sendOk(updated);
        } catch (UserNotFoundException e) {
            res.sendNotFound(e.getMessage());
        } catch (IllegalArgumentException e) {
            res.sendBadRequest(e.getMessage());
        }
    }
}
