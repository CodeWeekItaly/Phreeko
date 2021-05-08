package it.phreeko.server;

import com.sun.net.httpserver.HttpServer;
import it.phreeko.database.CompaniesDatabaseManager;
import it.phreeko.database.DatabaseManager;
import it.phreeko.database.UsersDatabaseManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class DBServer {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$s] %5$s %n");
    }

    public static Logger logger = Logger.getLogger("DB");

    public static void main(String[] args) throws IOException {
        DatabaseManager databaseManager = new DatabaseManager("SNIP");
        UsersDatabaseManager userDBManager = new UsersDatabaseManager(databaseManager);
        CompaniesDatabaseManager companyDBManager = new CompaniesDatabaseManager(databaseManager);
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 1337), 0);
        server.createContext("/db/user", new UserHandler(userDBManager));
        server.createContext("/db/company", new CompanyHandler(companyDBManager));
        server.setExecutor(null);
        server.start();
    }

    public static Logger getLogger() {
        return logger;
    }
}
