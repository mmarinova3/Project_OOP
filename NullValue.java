package bg.tu_varna.sit.b3.f21621557;

public class NullValue implements Value {
    private final DataType dataType;

    public NullValue(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public Object getValue() {
        return "NULL";
    }

    @Override
    public String toString() {
        return "NULL";
    }
}