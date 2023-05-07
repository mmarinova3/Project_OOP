package bg.tu_varna.sit.b3.f21621557;


public class FloatValue implements Value {
    private final Float value;

    public FloatValue(Float value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.FLOAT;
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