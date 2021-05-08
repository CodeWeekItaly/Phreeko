package it.phreeko.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.phreeko.database.UsersDatabaseManager;
import it.phreeko.objects.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import static it.phreeko.server.DBServer.getLogger;

public class UserHandler implements HttpHandler {

    private final UsersDatabaseManager usersDatabaseManager;
    private final Gson gson;

    public UserHandler(UsersDatabaseManager usersDatabaseManager) {
        this.usersDatabaseManager = usersDatabaseManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                if (exchange.getRequestURI().toString().endsWith("login")) {
                    getLogger().info("login endpoint reached");
                    login(exchange);
                } else if (exchange.getRequestURI().toString().endsWith("register")) {
                    getLogger().info("register endpoint reached");
                    register(exchange);
                } else if (exchange.getRequestURI().toString().endsWith("change_user")) {
                    getLogger().info("change_user endpoint reached");
                    change_user(exchange);
                } else if (exchange.getRequestURI().toString().endsWith("convert_id_name")) {
                    getLogger().info("convert_id_name endpoint reached");
                    convert_id_name(exchange);
                }
            }
        } catch (SQLException exception) {
            getLogger().severe("DB ERROR");
        }
        exchange.close();
    }

    public void login(HttpExchange exchange) throws SQLException, IOException {
        User user = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), User.class);
        user = usersDatabaseManager.getUser(user);
        if(user != null) {
            getLogger().info( "login request success");
            Response.send(gson.toJson(user), exchange);
        } else {
            getLogger().info("login request fail");
            Response.notFound(exchange);
        }
    }

    public void register(HttpExchange exchange) throws SQLException, IOException {
        User user = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), User.class);
        if(!usersDatabaseManager.checkUser(user)) {
            getLogger().info("register request success");
            usersDatabaseManager.createUser(user);
            Response.created(exchange);
        } else {
            getLogger().info("register request fail");
            Response.conflict(exchange);
        }
    }

    public void change_user(HttpExchange exchange) throws SQLException, IOException {
        User user = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), User.class);
        if(usersDatabaseManager.checkUser(user)) {
            getLogger().info("change_user request success");
            usersDatabaseManager.updateUser(user);
            Response.ok(exchange);
        } else {
            getLogger().info("change_user request fail");
            Response.notFound(exchange);
        }
    }

    public void convert_id_name(HttpExchange exchange) throws SQLException, IOException {
        User user = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), User.class);
        user = usersDatabaseManager.convertNameId(user);
        if(user != null) {
            getLogger().info( "convert_id_name request success");
            Response.send(gson.toJson(user), exchange);
        } else {
            getLogger().info("convert_id_name request fail");
            Response.notFound(exchange);
        }
    }

}
