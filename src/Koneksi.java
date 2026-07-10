import java.sql.*;
/**
 *
 * @author Administrator
 */
public class Koneksi {
    private static Connection mysqlconfig;
    public static Connection configDB() throws SQLException{
        try{
            String url = "jdbc:mysql://localhost/servishp";
            String user = "root";
            String pass = "";
            
            mysqlconfig = DriverManager.getConnection(url, user, pass);
        }catch(SQLException e){
            System.out.println("Koneksi Gagal: "+e.getMessage());
        }
        return mysqlconfig;
    }    
}
