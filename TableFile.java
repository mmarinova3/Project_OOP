package bg.tu_varna.sit.b3.f21621557;

import java.io.*;
import java.util.*;

public class TableFile {
    private static Table table;
    private static String directoryPath;

    public TableFile(Table table, String directoryPath) {
        TableFile.table = table;
        TableFile.directoryPath = directoryPath;
    }

    public static void setTable(Table table) {
        TableFile.table = table;
    }

    public void saveToFile() throws IOException {
        String fileName = table.getFileName();
        File file = new File(directoryPath, fileName);
        FileWriter writer = new FileWriter(file);

        writer.append(table.getName()).append("\n");

        List<TableColumn> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            writer.append(columns.get(i).getName());
            if (i != columns.size() - 1) {
                writer.append(",");
            }
        }
        writer.append("\n");

        List<TableRow> rows = table.getRows();
        for (TableRow row : rows) {
            List<Value> values = row.getValues();
            for (int i = 0; i < values.size(); i++) {
                Value value = values.get(i);
                writer.append(value.toString());
                if (i != values.size() - 1) {
                    writer.append(",");
                }
            }
            writer.append("\n");
        }

        writer.flush();
        writer.close();
    }

    public void saveToFile( String filePath) throws IOException {
        File file = new File(filePath);
        FileWriter writer = new FileWriter(file);

        writer.append(table.getName()).append("\n");

        List<TableColumn> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            writer.append(columns.get(i).getName());
            if (i != columns.size() - 1) {
                writer.append(",");
            }
        }
        writer.append("\n");

        List<TableRow> rows = table.getRows();
        for (TableRow row : rows) {
            List<Value> values = row.getValues();
            for (int i = 0; i < values.size(); i++) {
                Value value = values.get(i);
                writer.append(value.toString());
                if (i != values.size() - 1) {
                    writer.append(",");
                }
            }
            writer.append("\n");
        }

        writer.flush();
        writer.close();
    }
    public static Table loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String tableName = reader.readLine();
        if (tableName == null || tableName.isEmpty()) {
            throw new IOException("Invalid file format: table name is missing");
        }
        Table newTable = new Table(tableName, file.getName());

        String columnHeader = reader.readLine();
        String[] columnNames = columnHeader.split(",");
        for (String columnName : columnNames) {
            newTable.addColumn(new TableColumn(columnName, DataType.STRING));
        }

        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            TableRow row = new TableRow();
            for (int i = 0; i < values.length; i++) {
                Value value = null;
                TableColumn column = newTable.getColumns().get(i);
                switch (column.getDataType()) {
                    case INTEGER:
                        value = new IntegerValue(Integer.parseInt(values[i]));
                        break;
                    case FLOAT:
                        value = new FloatValue(Float.parseFloat(values[i]));
                        break;
                    case STRING:
                        value = new StringValue(values[i]);
                        break;
                }
                row.addValue(value);
            }

            newTable.addRow(row);
        }
        reader.close();
        return newTable;
    }

    public static Table getSelectedTable(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist");
        }
        Table selectedTable = loadFromFile(filePath);
        setTable(selectedTable);
        return selectedTable;
    }
}

