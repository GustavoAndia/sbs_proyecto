package com.bizlinks.sbs_proyecto.conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author GustavoAndia
 */
public class Conexion {
    
    private static final String DSN_HOSTNAME = "172.19.35.121";
    private static final String DSN_UID = "UWFEAPIREST";
    private static final String DSN_PWD = "658ouTgABMmoLjhSb";
    private static final String DSN_DATABASE = "DBFE";
    private static final String DSN_PORT = "52000";
    private static final String DSN_PROTOCOL = "TCPIP";
    
//    private static final String DSN_HOSTNAME = "172.19.37.224";
//    private static final String DSN_UID = "UWDES";
//    private static final String DSN_PWD = "87654321aA";
//    private static final String DSN_DATABASE = "TESFE2";
//    private static final String DSN_PORT = "52000";
//    private static final String DSN_PROTOCOL = "TCPIP";
    

    public Connection conectar() throws SQLException {
        Connection conn = null;
        try {
            String url = "jdbc:db2://" + DSN_HOSTNAME + ":" + DSN_PORT + "/" + DSN_DATABASE + ":user=" + DSN_UID + ";password=" + DSN_PWD + ";";
            conn = DriverManager.getConnection(url);
            System.out.println("Conexión establecida correctamente a la base de datos DB2");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return conn;
    }
}
