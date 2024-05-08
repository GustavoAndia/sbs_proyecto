package com.bizlinks.sbs_proyecto.conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;
/**
 *
 * @author GustavoAndia
 */
public class Conexion {
    
    private String DSN_HOSTNAME;
    private String DSN_UID;
    private String DSN_PWD;
    private String DSN_DATABASE;
    private String DSN_PORT;
    private String DSN_PROTOCOL;

    public Conexion() {
        try {
            Properties props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("database.properties");
            props.load(is);

            DSN_HOSTNAME = props.getProperty("DSN_HOSTNAME");
            DSN_UID = props.getProperty("DSN_UID");
            DSN_PWD = props.getProperty("DSN_PWD");
            DSN_DATABASE = props.getProperty("DSN_DATABASE");
            DSN_PORT = props.getProperty("DSN_PORT");
            DSN_PROTOCOL = props.getProperty("DSN_PROTOCOL");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
