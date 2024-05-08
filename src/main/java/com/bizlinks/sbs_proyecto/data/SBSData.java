package com.bizlinks.sbs_proyecto.data;

import com.bizlinks.sbs_proyecto.conexion.Conexion;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.bizlinks.sbs_proyecto.conexion.Conexion;
import java.math.BigDecimal;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SBSData extends Weekday {

    private String url = "https://www.sbs.gob.pe/app/stats/seriesH-tipo_cambio_moneda_excel.asp?fecha1=%s&fecha2=%s&moneda=02&cierre=";

    private String url2 = "https://www.sbs.gob.pe/app/stats/seriesH-tipo_cambio_moneda_excel.asp?fecha1=07/05/2024&fecha2=07/05/2024&moneda=02&cierre=";

    public SBSData() {
        super();
    }

    public String getUrlFormatted() {
        String[] fechas = getFechas();
        return String.format(url, fechas[0], fechas[1]);
    }

    public List<List<String>> getSBSData() throws IOException {
        List<List<String>> data = new ArrayList<>();
        HttpsURLConnection connection = null;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            // Desactivar la verificación del certificado SSL
            TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            URL url = new URL(url2);
            connection = (HttpsURLConnection) url.openConnection();
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537");
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            String contentType = connection.getContentType();
            if (!contentType.startsWith("text/html")) {
                throw new IOException("Invalid content type: " + contentType);
            }

            Document doc = Jsoup.parse(url.openStream(), "ISO-8859-1", "");
            Elements rows = doc.select("tr");

            for (Element row : rows) {
                List<String> rowData = new ArrayList<>();
                Elements columns = row.select("td");
                for (Element column : columns) {
                    rowData.add(column.text());
                }
                data.add(rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return data;
    }

    public void insertData(List<List<String>> data) throws SQLException {
        // Obtener la conexión a la base de datos
        Connection conn = new Conexion().conectar();

        // Preparar la consulta SQL
        String sql = "INSERT INTO portalperu.TM_TIPOCAMBIO (FECHA, COMPRA, VENTA) VALUES (?, ?, ?)";
        String sqlLastRecord = "SELECT * FROM portalperu.TM_TIPOCAMBIO WHERE FECHA = ?";

        try {
            // Crear una declaración preparada
            PreparedStatement stmt = conn.prepareStatement(sql);
            PreparedStatement stmtLastRecord = conn.prepareStatement(sqlLastRecord);
            stmtLastRecord.setDate(1, java.sql.Date.valueOf(LocalDate.now().minusDays(1)));  // FECHA del día anterior

            // Obtener los datos del día actual
            List<String> todayData = data.stream()
                    .filter(r -> r.get(0).equals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                    .findFirst()
                    .orElse(null);

            if (todayData == null) {
                // Si no hay datos para el día actual, obtener el último registro de la base de datos
                ResultSet rs = stmtLastRecord.executeQuery();
                if (rs.next()) {
                    todayData = Arrays.asList(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "Dólar de N.A.", rs.getString("COMPRA"), rs.getString("VENTA"));
                } else {
                    // Manejar el caso en que no hay un último registro en la base de datos
                    System.out.println("No se encontró un último registro en la base de datos.");
                    return;  // Salir del método
                }

            }

            // Establecer los valores de los parámetros
            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));  // FECHA
            stmt.setBigDecimal(2, new BigDecimal(todayData.get(2)));  // COMPRA
            stmt.setBigDecimal(3, new BigDecimal(todayData.get(3)));  // VENTA

            // Ejecutar la consulta
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("La inserción para la fecha " + LocalDate.now() + " se realizó correctamente.");
            }

            // Cerrar las declaraciones
            stmt.close();
            stmtLastRecord.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar la conexión
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    public void updateData(List<List<String>> data) throws SQLException {
//        // Obtener la conexión a la base de datos
//        Connection conn = new Conexion().conectar();
//
//        // Preparar la consulta SQL
//        String sql = "UPDATE portalperu.TM_TIPOCAMBIO SET COMPRA = ?, VENTA = ? WHERE FECHA = ?";
//
//        try {
//            // Crear una declaración preparada
//            PreparedStatement stmt = conn.prepareStatement(sql);
//
//            // Recorrer los datos
//            for (List<String> row : data) {
//                // Saltar la fila de encabezados
//                if (row.get(0).equals("FECHA")) {
//                    continue;
//                }
//
//                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                LocalDate date = LocalDate.parse(row.get(0), inputFormatter);
//                // Establecer los valores de los parámetros
//                stmt.setBigDecimal(1, new BigDecimal(row.get(2)));  // COMPRA
//                stmt.setBigDecimal(2, new BigDecimal(row.get(3)));  // VENTA
//                stmt.setDate(3, java.sql.Date.valueOf(date.format(outputFormatter)));  // FECHA
//
//                // Ejecutar la consulta
//                int rowsUpdated = stmt.executeUpdate();
//                if (rowsUpdated > 0) {
//                    System.out.println("La actualización para la fecha " + date.format(outputFormatter) + " se realizó correctamente.");
//                }
//            }
//
//            // Cerrar la declaración
//            stmt.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            // Cerrar la conexión
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
