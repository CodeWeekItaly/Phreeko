package it.phreeko.database;

import it.phreeko.objects.User;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

public class UsersDatabaseManager {

    private final DatabaseManager databaseManager;

    private final String SELECT_USER = "SELECT id, username, email, phone FROM users WHERE (username = ? OR email = ?) AND password = ? LIMIT 1";
    private final String CHECK_USER = "SELECT id FROM users WHERE username = ? OR email = ? OR phone = ? OR id = ? LIMIT 1";
    private final String INSERT_USER = "INSERT INTO users(id, username, email, phone, password) VALUES(?, ?, ?, ?, ?)";
    private final String UPDATE_USER = "UPDATE users SET email = IFNULL(?, email), phone = IFNULL(?, phone), password = IFNULL(?, password) WHERE id = ? OR username = ?";
    private final String SELECT_USER_ID = "SELECT id, username FROM users WHERE username = ? OR id = ? LIMIT 1";

    public UsersDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public User getUser(User user) throws SQLException {
        ResultSetHandler<User> resultSetHandler = new BeanHandler<>(User.class);
        return databaseManager.getQueryRunner()
                .query(SELECT_USER, resultSetHandler, user.getUsername(), user.getEmail(), user.getPassword());
    }

    public User convertNameId(User user) throws SQLException {
        ResultSetHandler<User> resultSetHandler = new BeanHandler<>(User.class);
        return databaseManager.getQueryRunner()
                .query(SELECT_USER_ID, resultSetHandler, user.getUsername(), user.getId());
    }

    public boolean checkUser(User user) throws SQLException {
        ResultSetHandler<User> resultSetHandler = new BeanHandler<>(User.class);
        return databaseManager.getQueryRunner()
                .query(CHECK_USER, resultSetHandler, user.getUsername(), user.getEmail(), user.getPassword(), user.getId()) != null;
    }

    public void createUser(User user) throws SQLException {
        databaseManager.getQueryRunner()
                .insert(INSERT_USER, new ScalarHandler<>(), user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getPassword());
    }

    public int updateUser(User user) throws SQLException {
        return databaseManager.getQueryRunner().update(UPDATE_USER, user.getEmail(), user.getPhone(), user.getPassword(), user.getId(), user.getUsername());
    }


}
