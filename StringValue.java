package database.project.f21621557;

public class StringValue extends Value {
    private String value;

    public StringValue(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public DataType getType() {
        return DataType.STRING;
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public String toString() {
        return value ;
    }

}


