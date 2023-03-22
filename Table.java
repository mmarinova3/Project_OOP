package database.project.f21621557;

import java.util.List;

public class Table {
    private String name;
    private List<Column> columns;
    private List<Row> rows;

    public Table(String name, List<Column> columns, List<Row> rows) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
    }

    public List<Row> getRows() {
        return rows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }




}