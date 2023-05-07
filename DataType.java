package bg.tu_varna.sit.b3.f21621557;

public enum DataType {
    INTEGER,
    STRING,
    FLOAT;

    public static DataType fromString(String dataTypeString) {
        switch (dataTypeString.toUpperCase()) {
            case "INTEGER":
                return INTEGER;
            case "FLOAT":
                return FLOAT;
            case "STRING":
                return STRING;
            default:
                throw new IllegalArgumentException("Invalid data type: " + dataTypeString);
        }
    }
}
