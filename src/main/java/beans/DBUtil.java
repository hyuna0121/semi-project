package beans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    // DB 연결 메소드
    public static Connection getConnection() throws SQLException {
        try {
            // MySQL JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // DB URL, 사용자, 비밀번호
            String url = "jdbc:mysql://192.168.8.141:3306/traveldb?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
            String user = "traveldb";
            String password = "mysql1234";

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("DB 연결 성공!");
            return conn;

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 실패:");
            e.printStackTrace();
            throw new SQLException("JDBC 드라이버 로드 실패", e);

        } catch (SQLException e) {
            System.out.println("DB 연결 실패:");
            e.printStackTrace();
            throw e;
        }
    }
}

