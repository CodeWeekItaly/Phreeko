package it.phreeko.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.phreeko.database.CompaniesDatabaseManager;
import it.phreeko.objects.Company;
import it.phreeko.objects.LocationRange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import static it.phreeko.server.DBServer.getLogger;

public class CompanyHandler implements HttpHandler {

    private final CompaniesDatabaseManager companiesDatabaseManager;
    private final Gson gson;

    public CompanyHandler(CompaniesDatabaseManager companiesDatabaseManager) {
        this.companiesDatabaseManager = companiesDatabaseManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                if (exchange.getRequestURI().toString().endsWith("from_range")) {
                    getLogger().info("from_range endpoint reached");
                    from_range(exchange);
                } else if (exchange.getRequestURI().toString().endsWith("register")) {
                    getLogger().info("register endpoint reached");
                    register(exchange);
                }
            }
        } catch (SQLException exception) {
            getLogger().severe("DB ERROR");
        }
        exchange.close();
    }

    public void register(HttpExchange exchange) throws SQLException, IOException {
        Company company = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Company.class);
        if(!companiesDatabaseManager.checkCompany(company)) {
            getLogger().info("register request success");
            companiesDatabaseManager.createCompany(company);
            Response.created(exchange);
        } else {
            getLogger().info("register request fail");
            Response.conflict(exchange);
        }
    }

    public void from_range(HttpExchange exchange) throws SQLException, IOException {
        LocationRange locationRange = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), LocationRange.class);
        List<Company> companyList = companiesDatabaseManager.getCompanies(locationRange);
        getLogger().info("from_range request success");
        Response.send(gson.toJson(companyList), exchange);
    }
}
