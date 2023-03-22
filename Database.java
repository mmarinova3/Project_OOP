package database.project.f21621557;

import java.io.*;
import java.util.*;

public class Database {
    private Map<String, Table> tables;
    private String databaseDirectory;

    public Database(String directory) {
        this.tables = new HashMap<>();
        this.databaseDirectory = directory;
    }

    public void createTable(String tableName, List<Column> columns) throws IOException {
        Table table = new Table(tableName, columns, new ArrayList<>());
        tables.put(tableName, table);

        // write table to file
        writeTableToFile(table);
    }

    public void insertIntoTable(String tableName, List<Value> values) throws IOException {
        Table table = tables.get(tableName);
        Row row = new Row(values);
        table.getRows().add(row);

        // write table to file
        writeTableToFile(table);
    }

    private int getColumnIndex(Table table, String columnName) {
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(columnName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column not found: " + columnName);
    }

    public List<Row> selectFromTable(String tableName, String columnName, Object value) throws IOException {
        Table table = tables.get(tableName);
        List<Row> result = new ArrayList<>();

        int columnIndex = getColumnIndex(table, columnName);

        for (Row row : table.getRows()) {
            if (row.getValues().get(columnIndex).equals(value)) {
                result.add(row);
            }
        }

        return result;

    }

    private void writeTableToFile(Table table) throws IOException {
        String fileName = databaseDirectory + "/" + table.getName() + ".dat";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(table);
        }
    }

    public static Database loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        Database database;

        FileInputStream fileInputStream = new FileInputStream(filePath);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        try {
            database = (Database) objectInputStream.readObject();
        } finally {
            objectInputStream.close();
            fileInputStream.close();
        }

        return database;
    }

    public void saveToFile(String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(this);
        }
    }
}