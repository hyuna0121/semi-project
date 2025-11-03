package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
	private static final String URL = "jdbc:mysql://192.168.8.141:3306/traveldb?serverTimezone=UTC";
	private static final String USER = "traveldb";
	private static final String PASSWORD = "mysql1234";
	
	// 커넥션 생성
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
            // 🚨 핵심: 격리 레벨을 READ COMMITTED로 강제 설정하여 DB 캐시 문제 방지
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			close(conn); 
			return null;
		}
    }
    
    // 리소스 해제
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) {
                try { 
                		r.close(); 
                } catch (Exception e) { 
                		e.printStackTrace(); 
                }
            }
        }
    }
}