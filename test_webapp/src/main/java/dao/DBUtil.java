package dao;

import javax.sql.DataSource;
import java.util.Arrays;

public class DBUtil {
    private static final String URL = "jdbc:mysql://127.0.01:3306/java_image_server?characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static DataSource dataSource = null;

    public static void main(String[] args) {
        String s = null;
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        String s1 = chars.toString();
    }
}