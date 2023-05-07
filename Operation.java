package bg.tu_varna.sit.b3.f21621557;

import java.util.List;

public class Operation {
    public static Object getValue(int columnIndex, List<Value> values) {
        if (columnIndex >= 0 && columnIndex < values.size()) {
            return values.get(columnIndex).getValue();
        } else {
            throw new IndexOutOfBoundsException("Index " + columnIndex + " is out of bounds for row of size " + values.size());
        }
    }

    public static double sumColumn(List<TableRow> rows, int columnIndex) {
        double result = 0.0;
        for (TableRow row : rows) {
            Object value = getValue(columnIndex, row.getValues());
            if (value instanceof Number) {
                result += ((Number) value).doubleValue();
            }
        }
        return result;
    }

    public static double productColumn(List<TableRow> rows, int columnIndex) {
        double result = 1.0;
        for (TableRow row : rows) {
            Object value = getValue(columnIndex, row.getValues());
            if (value instanceof Number) {
                result *= ((Number) value).doubleValue();
            }
        }
        return result;
    }

    public static double maxColumn(List<TableRow> rows, int columnIndex) {
        double result = Double.NEGATIVE_INFINITY;
        for (TableRow row : rows) {
            Object value = getValue(columnIndex, row.getValues());
            if (value instanceof Number) {
                double numValue = ((Number) value).doubleValue();
                if (numValue > result) {
                    result = numValue;
                }
            }
        }
        return result;
    }

    public static double minColumn(List<TableRow> rows, int columnIndex) {
        double result = Double.POSITIVE_INFINITY;
        for (TableRow row : rows) {
            Object value = getValue(columnIndex, row.getValues());
            if (value instanceof Number) {
                double numValue = ((Number) value).doubleValue();
                if (numValue < result) {
                    result = numValue;
                }
            }
        }
        return result;
    }
}
