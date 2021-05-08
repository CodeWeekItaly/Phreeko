package it.phreeko.database;

import it.phreeko.objects.Company;
import it.phreeko.objects.LocationRange;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class CompaniesDatabaseManager {

    private final DatabaseManager databaseManager;

    private final String SELECT_COMPANY = "SELECT * FROM companies WHERE owner = ? OR iva = ? OR name = ? OR category = ?";
    private final String SELECT_COMPANY_BYLOC = "SELECT * FROM companies WHERE lat BETWEEN ? AND ? AND lng BETWEEN ? AND ?";
    private final String CHECK_COMPANY = "SELECT iva FROM companies WHERE name = ? OR owner = ? OR email = ? OR phone = ? OR iva = ? LIMIT 1";
    private final String INSERT_COMPANY = "INSERT INTO companies(owner, iva, name, category, phone, email, lat, lng, delivery, rating_tmp) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public CompaniesDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Company> getCompanies(Company company) throws SQLException {
        ResultSetHandler<List<Company>> resultSetHandler = new BeanListHandler<>(Company.class);
        return databaseManager.getQueryRunner()
                .query(SELECT_COMPANY, resultSetHandler, company.getOwner(), company.getIva(), company.getName(), company.getCategory());
    }

    public List<Company> getCompanies(LocationRange locationRange) throws SQLException {
        ResultSetHandler<List<Company>> resultSetHandler = new BeanListHandler<>(Company.class);
        return databaseManager.getQueryRunner()
                .query(SELECT_COMPANY_BYLOC, resultSetHandler, locationRange.getMinLat(), locationRange.getMaxLat(), locationRange.getMinLng(), locationRange.getMaxLng());
    }

    public boolean checkCompany(Company company) throws SQLException {
        ResultSetHandler<Company> resultSetHandler = new BeanHandler<>(Company.class);
        return databaseManager.getQueryRunner()
                .query(CHECK_COMPANY, resultSetHandler, company.getName(), company.getOwner(), company.getEmail(), company.getPhone(), company.getIva()) != null;
    }

    public int createCompany(Company company) throws SQLException {
        return databaseManager.getQueryRunner()
                .insert(INSERT_COMPANY, new ScalarHandler<>(), company.getOwner(), company.getIva(), company.getName(),
                        company.getCategory(), company.getPhone(), company.getEmail(), company.getLat(), company.getLng(), company.getDelivery());
    }



}
