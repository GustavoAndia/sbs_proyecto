package com.bizlinks.sbs_proyecto;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.bizlinks.sbs_proyecto.data.SBSData;
import com.bizlinks.sbs_proyecto.data.VisualizationTabulate;

public class Sbs_proyecto {

    public static void main(String[] args) throws SQLException, IOException {
        SBSData sbsData = new SBSData();
        //VisualizationTabulate tabulate = new VisualizationTabulate();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23); // 24 horas
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND, 0);

        Date time = calendar.getTime();

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {

                    List<List<String>> sbsDataList = sbsData.getSBSData();
//            for (List<String> row : sbsDataList) {
//            System.out.println(row);
//            }

                    // String table = tabulate.getTabulate(sbsDataList);
                    //System.out.println(table);
//             Insertar datos en la base de datos
                    sbsData.insertData(sbsDataList);
                    //sbsData.updateData(sbsDataList);

                    //System.out.println("Proceso completado exitosamente.");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        // Programar la tarea para que se ejecute a las 11:59 PM todos los días
        timer.scheduleAtFixedRate(task, time, 1000 * 60 * 60 * 24);
    }
}
