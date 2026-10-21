package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class ThongKeDAO {
    public Connection openConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // TODO: Thêm các truy vấn thống kê theo yêu cầu bài tập.
}
