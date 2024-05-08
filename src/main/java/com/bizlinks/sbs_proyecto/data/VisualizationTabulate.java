package com.bizlinks.sbs_proyecto.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author GustavoAndia
 */
public class VisualizationTabulate {

    public String getTabulate(List<List<String>> input) {
        String[] headers = input.get(0).toArray(new String[0]);
        List<List<String>> data = input.subList(1, input.size());
        return tabulate(data, headers);
    }

    private String tabulate(List<List<String>> data, String[] headers) {
        StringBuilder sb = new StringBuilder();
        // Añadir encabezados
        sb.append(String.format("%-15s | %-20s | %-10s | %-10s%n", headers));

        // Añadir filas de datos
        for (List<String> row : data) {
            if (!row.isEmpty() && row.size() >= 4) {  // Asegúrate de que la lista no está vacía y tiene suficientes elementos
                // Crear una nueva fila con los datos
                List<String> newRow = new ArrayList<>();
                newRow.add(row.get(0).trim().replace("\n", "").replace("\t", ""));  // FECHA
                newRow.add(row.get(1).trim().replace("\n", "").replace("\t", ""));  // MONEDA
                newRow.add(row.get(2).trim().replace("\n", "").replace("\t", ""));  // COMPRA
                newRow.add(row.get(3).trim().replace("\n", "").replace("\t", ""));  // VENTA
                // Imprimir la fila
                sb.append(String.format("%-15s | %-20s | %-10s | %-10s%n", newRow.toArray()));
            } else {
                // Manejar el caso en que `row` está vacía o no tiene suficientes elementos
                // Puedes decidir qué hacer en este caso, por ejemplo, puedes saltar esta fila o imprimir un mensaje de error.
            }
        }

        return sb.toString();
    }

}