package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class XeKhachDAO {
    public Connection openConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // TODO: Thêm các phương thức CRUD cho XeKhach.
}
