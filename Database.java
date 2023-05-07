package bg.tu_varna.sit.b3.f21621557;

import java.io.*;
import java.util.*;

public class Database {
    private final String directoryPath;
    private final List<Table> tables;

    public Database(String name, String directoryPath) throws DatabaseException {
        if (name == null || directoryPath == null) {
            throw new DatabaseException("Invalid database name or directory path");
        }
        this.directoryPath = directoryPath;
        this.tables = new ArrayList<>();
    }

    public String getDirectoryPath() {
        return directoryPath;
    }


    public void getTablesNames() {
        for (Table table : tables) {
            System.out.println(table.getName());
        }
    }


    public void addTable(Table table) {
        tables.add(table);
    }


    public Table select(String tableName) {
        try {
            for (Table table : tables) {
                if (table.getName().equals(tableName)) {
                    return table;
                }
            }
            throw new TableNotFoundException("Table not found");
        } catch (TableNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean fileExists(String fileName) {
        File file = new File(directoryPath + "\\" + fileName);
        return file.exists();
    }

    public boolean tableExists(String newName) {
        for (Table table : tables) {
            if (table.getName().equals(newName)) {
                return true;
            }
        }
        return false;
    }

    public void renameTable(String oldName, String newName) {
        for (Table table : tables) {
            if (table.getName().equals(oldName)) {
                table.setName(newName);
            }
        }
    }

}

