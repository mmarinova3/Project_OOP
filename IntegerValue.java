package database.project.f21621557;

public class IntegerValue extends Value {
    private int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public DataType getType() {
        return DataType.INTEGER;
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}