package vn.ptit.library.dao;

import vn.ptit.library.config.Database;

import java.sql.Connection;
import java.sql.SQLException;

/** Common connection access for all DAOs. Each query closes its own connection. */
public abstract class DAO {
    protected DAO() {}

    protected final Connection openConnection() throws SQLException {
        return Database.open();
    }
}
