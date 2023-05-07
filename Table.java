package bg.tu_varna.sit.b3.f21621557;

import java.util.*;

public class Table {
    private String name;
    private final String fileName;
    private final List<TableColumn> columns;
    private final List<TableRow> rows;

    public Table(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public List<TableColumn> getColumns() {
        return columns;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    public void addRow(TableRow row) throws IllegalArgumentException {
        List<Value> values = row.getValues();

        if (values.size() > columns.size()) {
            throw new IllegalArgumentException("Number of values in row is bigger than number of columns in table.");
        }

        for (int i = 0; i < columns.size(); i++) {
            Value value = values.get(i);
            TableColumn column = columns.get(i);

            if (value != null && value.getDataType() != column.getDataType()) {
                throw new IllegalArgumentException("Data type of value in row does not match data type of column in table.");
            }
        }

        rows.add(row);
    }
    public void addRows(List<TableRow> rowsToAdd) {
        for (TableRow row : rowsToAdd) {
            addRow(row);
        }
    }
    public void removeRow(TableRow tableRow) {
        rows.remove(tableRow);
    }


    public void addColumn(TableColumn column) {
        columns.add(column);
        for (TableRow row : rows) {
            Value defaultValue = new NullValue(column.getDataType());
            switch (column.getDataType()) {
                case INTEGER:
                    defaultValue = new IntegerValue(0);
                    break;
                case FLOAT:
                    defaultValue = new FloatValue(0.0f);
                    break;
                case STRING:
                    defaultValue = new StringValue("");
                    break;
            }
            row.addValue(defaultValue);
        }
    }
    public void addNewColumn(TableColumn column) {
        columns.add(column);
    }

    public List<String> getColumnNames() {
        List<String> names = new ArrayList<>();
        for (TableColumn column : columns) {
            names.add(column.getName());
        }
        return names;
    }

    public int getNumberOfColumns() {
        return columns.size();
    }

    public TableColumn getColumn(int index) {
        if (index < 0 || index >= columns.size()) {
            return null;
        }
        return columns.get(index);
    }


    public boolean hasColumn(TableColumn columnToAdd) {
        for (TableColumn column : columns) {
            if (column.equals(columnToAdd)) {
                return true;
            }
        }
        return false;
    }
}
