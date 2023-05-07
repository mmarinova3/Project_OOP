package bg.tu_varna.sit.b3.f21621557;

import java.util.*;

public class TableRow {
    private List<Value> values;

    public TableRow(List<Value> values) {
        this.values = new ArrayList<>(values);
    }

    public TableRow() {
        this.values = new ArrayList<>();
    }

    public static TableRow join(TableRow row1, TableRow row2) {
        List<Value> joinedValues = new ArrayList<>(row1.values);
        joinedValues.addAll(row2.values);
        return new TableRow(joinedValues);
    }

    public List<Value> getValues() {
        return values;
    }

    public void addValue(Value value) {
        values.add(value);
    }

    public void addValueToSpecificColumn(int index, Value value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(index, value);
    }

    public void set(int i, Value newValue) {
        if (i < 0 || i >= values.size()) {
            throw new IndexOutOfBoundsException("Index " + i + " out of bounds for row with " + values.size() + " values.");
        }
        values.set(i, newValue);
    }

    public Value getValue(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= values.size()) {
            throw new IndexOutOfBoundsException("Index " + columnIndex + " out of bounds for row with " + values.size() + " values.");
        }
        return values.get(columnIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Value value : values) {
            sb.append(value.toString()).append("\t");
        }
        return sb.toString().trim();
    }

}