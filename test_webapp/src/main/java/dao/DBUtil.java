package dao;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class DBUtil {
    private static final String URL = "jdbc:mysql://127.0.01:3306/java_image_server?characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static DataSource dataSource = null;

    public static DataSource getDataSource(){
        if (dataSource == null){
            synchronized (DBUtil.class) {
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();
                    MysqlDataSource tmpDataSource = (MysqlDataSource)dataSource;
                    tmpDataSource.setURL(URL);
                    tmpDataSource.setUser(USERNAME);
                    tmpDataSource.setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }
    public static java.sql.Connection getConnection()  {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null){
                resultSet.close();
            }
            if (statement != null){
                statement.close();
            }
            if (connection !=null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}