package bg.tu_varna.sit.b3.f21621557;

public class IntegerValue implements Value {
    private final int value;

    public IntegerValue(int value) {
        this.value = value;
    }


    @Override
    public DataType getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}