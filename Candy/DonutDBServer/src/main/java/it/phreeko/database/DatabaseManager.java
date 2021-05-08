package it.phreeko.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.commons.dbutils.QueryRunner;

public class DatabaseManager {

    private QueryRunner queryRunner;
    private MysqlDataSource dataSource;

    public DatabaseManager(String host, int port, String user, String passwd, String dbName) {
        String databaseUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, dbName);

        this.dataSource = new MysqlDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(passwd);
        dataSource.setUrl(databaseUrl);

        this.queryRunner = new QueryRunner(dataSource);
    }

    public QueryRunner getQueryRunner() {
        return queryRunner;
    }

}
