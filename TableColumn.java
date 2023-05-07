package bg.tu_varna.sit.b3.f21621557;

public class TableColumn {
    private final String name;
    private DataType dataType;

    public TableColumn(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

}
