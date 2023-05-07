package bg.tu_varna.sit.b3.f21621557;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CommandLineInterface {

    private Database database;
    private String currentFilePath;
    private static final int  PAGE_SIZE=1;
    public CommandLineInterface(Database database) {
        this.database = database;
    }

    public void start() throws Exception {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            String[] tokens = input.split(" ");
            String command = tokens[0];
            Table table;
            String tableName;
            String fileName;
            List<TableRow> rows;
            List<TableColumn> columns;
            int columN, targetColumnIndex;

            switch (command) {
                case "open":
                    if (tokens.length != 2) {
                        System.out.println("Invalid arguments. Usage: open <file>");
                        break;
                    }
                    fileName = tokens[1];
                    currentFilePath = database.getDirectoryPath() + File.separator + fileName;
                    File databaseFile = new File(currentFilePath);

                    if (!databaseFile.exists()) {
                        try {
                            boolean fileCreated = databaseFile.createNewFile();
                            if (fileCreated) {
                                System.out.println("New file " + fileName + " created with empty content."+currentFilePath);
                            } else {
                                System.out.println("File " + fileName + " already exists.");
                            }
                        } catch (IOException e) {
                            System.out.println("Error creating new file " + fileName + ": " + e.getMessage());
                            break;
                        }
                    } else {
                        try {
                            TableFile.loadFromFile(currentFilePath);
                            System.out.println("Successfully opened " + fileName);
                        } catch (IOException e) {
                            System.out.println("Error opening " + fileName + ": " + e.getMessage());
                        } catch (ClassNotFoundException | NullPointerException e) {
                            System.out.println("Error loading " + fileName + ": " + e.getMessage());
                        }
                    }
                    break;
                case "import":
                    if (tokens.length != 2) {
                        System.out.println("Invalid arguments. Usage: import <file>");
                        break;
                    }
                    fileName = tokens[1];
                    currentFilePath = database.getDirectoryPath() + File.separator + fileName;
                    databaseFile = new File(currentFilePath);
                    if (!databaseFile.exists()) {
                        System.out.println("File not found ");
                    }
                    try {
                       Table newTable = TableFile.loadFromFile(currentFilePath);

                         tableName = newTable.getName();
                        if (database.tableExists(tableName)) {
                            System.out.println("Table " + tableName + " already exists in the database.");
                            break;
                        }

                        rows = newTable.getRows();
                        if (!rows.isEmpty()) {
                            TableRow typeRow = rows.remove(0);
                            List<String> columnTypes = typeRow.getValues().stream()
                                    .map(Value::toString)
                                    .collect(Collectors.toList());
                            List<String> columnNames = newTable.getColumnNames();
                            if (columnTypes.size() != columnNames.size()) {
                                throw new IllegalArgumentException("Number of column types does not match number of columns.");
                            }
                            columns = newTable.getColumns();
                            for (int i = 0; i < columns.size(); i++) {
                                columns.get(i).setDataType(DataType.fromString(columnTypes.get(i)));
                            }
                            for (int i = 0; i < columnTypes.size(); i++) {
                                DataType dataType;
                                switch (columnTypes.get(i).toUpperCase()) {
                                    case "INTEGER":
                                        dataType = DataType.INTEGER;
                                        break;
                                    case "FLOAT":
                                        dataType = DataType.FLOAT;
                                        break;
                                    case "STRING":
                                        dataType = DataType.STRING;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Invalid column data type: " + columnTypes.get(i));
                                }
                                for (TableRow row : rows) {
                                    Value oldValue = row.getValues().get(i);
                                    Object value = oldValue.getValue();
                                    Value newValue;
                                    switch (dataType) {
                                        case INTEGER:
                                            if (value instanceof String) {
                                                newValue = new IntegerValue(Integer.parseInt((String) value));
                                            } else {
                                                newValue = new IntegerValue((Integer) value);
                                            }
                                            break;
                                        case FLOAT:
                                            if (value instanceof String) {
                                                newValue = new FloatValue(Float.parseFloat((String) value));
                                            } else {
                                                newValue = new FloatValue((Float) value);
                                            }
                                            break;
                                        case STRING:
                                            newValue = new StringValue((String) value);
                                            break;
                                        default:
                                            throw new IllegalArgumentException("Invalid column data type: " + dataType);
                                    }
                                    row.set(i, newValue);
                                }
                            }
                        }
                        database.addTable(newTable);

                        System.out.println("Table " + tableName + " imported from file " + fileName + " successfully.");
                    } catch (IOException e) {
                        System.out.println("Error importing table from file " + fileName + ": " + e.getMessage());
                    } catch (ClassNotFoundException e) {
                        System.out.println("Error loading table from file " + fileName + ": " + e.getMessage());
                    }
                    break;
                case "showtables":
                    database.getTablesNames();
                    break;
                case "describe":
                    if (tokens.length != 2) {
                        System.out.println("Invalid arguments. Usage: describe <table>");
                        break;
                    }
                     tableName = tokens[1];
                    table = database.select(tableName);
                    if (table == null) {
                        break;
                    }
                    columns = table.getColumns();
                    System.out.println("Table " + tableName + " columns:");
                    for (TableColumn column : columns) {
                        System.out.println(column.getName() + " (" + column.getDataType() + ")");
                    }
                    break;
                case "print":
                    if (tokens.length != 2) {
                        System.out.println("Invalid arguments. Usage: print <table>");
                        break;
                    }
                    tableName = tokens[1];
                     table = database.select(tableName);
                    if (table == null) {
                        break;
                    }
                    rows = table.getRows();
                    int pageNumber = 0;
                    while (true) {
                        for (int i = pageNumber * PAGE_SIZE; i < (pageNumber + 1) * PAGE_SIZE && i < rows.size(); i++) {
                            System.out.println(rows.get(i).toString());
                        }
                        System.out.print("Enter command (next, prev, exit): ");
                        String[] commandTokens = scanner.nextLine().trim().split("\\s+");
                        String subcommand = commandTokens[0];
                        if (subcommand.equals("next")) {
                            if ((pageNumber + 1) * PAGE_SIZE < rows.size()) {
                                pageNumber++;
                            } else {
                                System.out.println("Reached the end of the table.");
                            }
                        } else if (subcommand.equals("prev")) {
                            if (pageNumber > 0) {
                                pageNumber--;
                            } else {
                                System.out.println("Already at the beginning of the table.");
                            }
                        } else if (subcommand.equals("exit")) {
                            break;
                        } else {
                            System.out.println("Invalid command.");
                        }
                    }
                    break;
                case "export":
                    if (tokens.length != 3) {
                        System.out.println("Invalid arguments. Usage: export <table> <file>");
                        break;
                    }

                    tableName = tokens[1];
                    fileName = tokens[2];
                    table = database.select(tableName);

                    if (table == null) {
                        break;
                    }

                    if (!database.fileExists(fileName)) {
                        System.out.println("File " + fileName + " does not exist.");
                        break;
                    }

                   currentFilePath= database.getDirectoryPath()+"\\"+fileName;


                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFilePath))) {
                            writer.append(table.getName()).append("\n");
                        StringJoiner columnJoiner = new StringJoiner(",");
                        for (String columnName : table.getColumnNames()) {
                            columnJoiner.add(columnName);
                        }
                        writer.write(columnJoiner.toString());
                        writer.newLine();

                        for (TableRow row : table.getRows()) {
                            List<Value> values = row.getValues();
                            StringJoiner valueJoiner = new StringJoiner(",");
                            for (Value value : values) {
                                valueJoiner.add(value.toString());
                            }
                            writer.write(valueJoiner.toString());
                            writer.newLine();
                        }


                        System.out.println("Table " + tableName + " exported to file " + fileName + " successfully.");
                    } catch (IOException e) {
                        System.out.println("Error exporting table " + tableName + " to file " + fileName + ": " + e.getMessage());
                    }
                    break;
                case "select":
                    if (tokens.length != 4) {
                        System.out.println("Invalid arguments. Usage: select <column-n> <value> <table>");
                        break;
                    }

                    int columnNumber ;
                    try {
                        columnNumber = Integer.parseInt(tokens[1]) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid column number. Please enter an integer.");
                        break;
                    }

                    String value = tokens[2];
                    tableName = tokens[3];
                    table = database.select(tableName);

                    if (table == null) {
                        break;
                    }
                    if (columnNumber < 0 || columnNumber >= table.getNumberOfColumns()) {
                        System.out.println("Column " + (columnNumber + 1) + " does not exist in table " + tableName + ".");
                        break;
                    }
                    rows = table.getRows();
                    List<TableRow> filteredRows = new ArrayList<>();
                    for (TableRow row : rows) {
                        if (row.getValues().size() > columnNumber && row.getValues().get(columnNumber).toString().equals(value)) {
                            filteredRows.add(row);
                        }
                    }

                    pageNumber = 0;
                    while (true) {
                        int start = pageNumber * PAGE_SIZE;
                        int end = Math.min(start + PAGE_SIZE, filteredRows.size());
                        for (int i = start; i < end; i++) {
                            System.out.println(filteredRows.get(i).toString());
                        }

                        if (filteredRows.isEmpty()) {
                            System.out.println("No rows found.");
                        }

                        System.out.print("Enter command (next, prev, exit): ");
                        String[] commandTokens = scanner.nextLine().trim().split("\\s+");
                        String subcommand = commandTokens[0];
                        if (subcommand.equals("next")) {
                            if ((pageNumber + 1) * PAGE_SIZE < filteredRows.size()) {
                                pageNumber++;
                            } else {
                                System.out.println("Reached the end of the table.");
                            }
                        } else if (subcommand.equals("prev")) {
                            if (pageNumber > 0) {
                                pageNumber--;
                            } else {
                                System.out.println("Already at the beginning of the table.");
                            }
                        } else if (subcommand.equals("exit")) {
                            break;
                        } else {
                            System.out.println("Invalid command.");
                        }
                    }
                    break;
                case "addcolumn":
                    if (tokens.length != 4) {
                        System.out.println("Invalid arguments. Usage: addcolumn <table> <name> <type>");
                        break;
                    }
                    tableName = tokens[1];
                    String columnName = tokens[2];
                    String dataType = tokens[3];
                    table = database.select(tableName);

                    if (table == null) {
                        break;
                    }

                    DataType columnDataType;
                    try {
                        columnDataType = DataType.valueOf(dataType.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid data type: " + dataType);
                        break;
                    }

                    TableColumn newColumn = new TableColumn(columnName, columnDataType);

                    try {
                        table.addNewColumn(newColumn);
                        int columnPosition = table.getNumberOfColumns() - 1;
                        for (TableRow row : table.getRows()) {
                            Value defaultValue = new NullValue(columnDataType);
                             row.addValueToSpecificColumn(columnPosition, defaultValue);
                        }
                        System.out.println("Column " + columnName + " of type " + dataType + " added to table " + tableName + ".");
                    } catch (Exception e) {
                        System.out.println("An error occurred while adding a column: " + e.getMessage());
                        break;
                    }
                    break;
                case "update":
                    if (tokens.length < 6) {
                        System.out.println("Invalid arguments. Usage: update <table name> <search column number> <search value> <target column number> <target value>");
                        break;
                    }

                    tableName = tokens[1];

                    try {
                        columN = Integer.parseInt(tokens[2]) - 1;
                        targetColumnIndex = Integer.parseInt(tokens[4]) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid column number. Please enter an integer.");
                        break;
                    }

                    String searchValue = tokens[3];
                    String targetValue = tokens[5];


                    table = database.select(tableName);
                    if(table==null){break;}

                    if (columN < 0 || columN >= table.getNumberOfColumns()) {
                        System.out.println("Column " + (columN + 1) + " does not exist in table " + tableName + ".");
                        break;
                    }

                    if (targetColumnIndex < 0 || targetColumnIndex >= table.getNumberOfColumns()) {
                        System.out.println("Column " + (targetColumnIndex + 1) + " does not exist in table " + tableName + ".");
                        break;
                    }
                    // System.out.println("Before" + "\n" + table.getRows());

                    int rowsUpdated = 0;

                    for (TableRow row : table.getRows()) {
                        Value searchValueInRow = row.getValue(columN);
                        if (searchValueInRow != null && searchValueInRow.toString().trim().equals(searchValue)) {
                            Value targetValueObject = CommandLineInterface.parseValue(targetValue, table.getColumn(targetColumnIndex).getDataType());
                            row.set(columN, targetValueObject);
                            rowsUpdated++;
                        }
                    }

                    // System.out.println("After" + "\n" + table.getRows());
                    System.out.println("Updated " + rowsUpdated + " rows.");
                    break;
                case "delete":
                    if (tokens.length != 4) {
                        System.out.println("Invalid arguments. Usage: delete <table name> <search column n> <search value>");
                        break;
                    }

                    try {
                        columN = Integer.parseInt(tokens[2]) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid column number. Please enter an integer.");
                        break;
                    }

                    value = tokens[3];
                    tableName = tokens[1];
                    table = database.select(tableName);

                    if (table == null) {
                        break;
                    }
                    if (columN < 0 || columN >= table.getNumberOfColumns()) {
                        System.out.println("Column " + (columN + 1) + " does not exist in table " + tableName + ".");
                        break;
                    }

                    rows = table.getRows();
                    int numDeleted = 0;
                    for (int i = 0; i < rows.size(); i++) {
                        TableRow row = rows.get(i);
                        if (row.getValues().size() > columN && row.getValues().get(columN).toString().equals(value)) {
                            table.removeRow(row);
                            numDeleted++;
                            i--;
                        }
                    }
                    System.out.println("Deleted " + numDeleted + " rows containing " + value + " in column " + tokens[2] + " from table " + tableName + ".");
                    break;
                case "insert":

                    if (tokens.length < 3) {
                        System.out.println("Invalid arguments. Usage: insert <table name> <value 1> ... <value n>");
                        break;
                    }
                    tableName = tokens[1];
                    table = database.select(tableName);
                    if (table == null) {
                        break;
                    }

                    List<String> values = Arrays.asList(tokens).subList(2, tokens.length);
                    if (values.size() != table.getColumns().size()) {
                        System.out.println("Invalid number of values provided.");
                        break;
                    }
                    List<Value> rowValues = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                      TableColumn column = table.getColumns().get(i);
                        switch (column.getDataType()) {
                            case INTEGER:
                                try {
                                    rowValues.add(new IntegerValue(Integer.parseInt(values.get(i))));
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid value provided for column " + column.getName() + ". Expected integer value.");
                                    break;
                                }
                                break;
                            case FLOAT:
                                try {
                                    rowValues.add(new FloatValue(Float.parseFloat(values.get(i))));
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid value provided for column " + column.getName() + ". Expected floating-point value.");
                                    break;
                                }
                                break;
                            case STRING:
                                rowValues.add(new StringValue(values.get(i)));
                                break;
                        }
                    }
                    TableRow row = new TableRow(rowValues);
                    table.addRow(row);
                    System.out.println("New row added to table " + tableName + ".");
                    break;
                case "innerjoin":
                    if (tokens.length < 5) {
                        System.out.println("Invalid arguments. Usage: innerjoin <table1 name> <column1 index> <table2 name> <column2 index>");
                        break;
                    }

                    String table1Name = tokens[1];
                    int column1Index = Integer.parseInt(tokens[2]) - 1;
                    String table2Name = tokens[3];
                    int column2Index = Integer.parseInt(tokens[4]) - 1;

                    Table table1 = database.select(table1Name);
                    Table table2 = database.select(table2Name);

                    if (table1==null||table2==null){
                        break;
                    }
                    if (column1Index < 0 || column1Index >= table1.getNumberOfColumns()) {
                        System.out.println("Column " + (column1Index + 1) + " does not exist in table " + table1Name + ".");
                        break;
                    }
                    if (column2Index < 0 || column2Index >= table2.getNumberOfColumns()) {
                        System.out.println("Column " + (column2Index + 1) + " does not exist in table " + table2Name + ".");
                        break;
                    }
                    tableName = table1Name + "-" + table2Name;
                    fileName = tableName+ ".txt";
                    int k = 1;
                    while (database.tableExists(tableName)) {
                        tableName = table1Name + "-" + table2Name + "-" + k;
                        fileName = tableName+".txt";
                        k++;
                    }

                    Table resultTable = new Table(tableName, fileName);

                    for (int i = 0; i < table1.getNumberOfColumns(); i++) {
                        TableColumn columnToAdd = table1.getColumn(i);
                        resultTable.addColumn(columnToAdd);
                    }

                    for (int i = 0; i < table2.getNumberOfColumns(); i++) {
                        TableColumn columnToAdd = table2.getColumn(i);

                        if (!resultTable.hasColumn(columnToAdd)) {
                            resultTable.addColumn(columnToAdd);
                        }
                    }
                    List<TableRow> joinedRows = new ArrayList<>();
                    for (TableRow row1 : table1.getRows()) {
                        for (TableRow row2 : table2.getRows()) {
                            if (row1.getValues().size() > column1Index && row2.getValues().size() > column2Index &&
                                    row1.getValues().get(column1Index).toString().equals(row2.getValues().get(column2Index).toString())) {
                                joinedRows.add(TableRow.join(row1, row2));
                            }
                        }
                    }
                    resultTable.addRows(joinedRows);
                    database.addTable(resultTable);
                    System.out.println("New table created: " + resultTable.getName());
                    break;
                case "rename":
                    if (tokens.length != 3) {
                        System.out.println("Invalid arguments. Usage: rename <old name> <new name>");
                        break;
                    }
                    String oldName = tokens[1];
                    String newName = tokens[2];
                    if (database.tableExists(newName)) {
                        System.out.println("Table " + newName + " already exists.");
                        break;
                    }
                    Table tableToRename = database.select(oldName);
                    if (tableToRename == null) {
                        break;
                    }
                    database.renameTable(oldName, newName);
                    System.out.println("Table " + oldName + " has been renamed to " + newName);
                    break;
                case "count":
                    if (tokens.length != 4) {
                        System.out.println("Invalid arguments. Usage: count <table name> <search column n> <search value>");
                        break;
                    }

                    int columnNum;
                    try {
                        columnNum = Integer.parseInt(tokens[2]) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid column number. Please enter an integer.");
                        break;
                    }
                    value = tokens[3];
                    tableName = tokens[1];
                    table = database.select(tableName);

                    if (table == null) {
                        break;
                    }

                    rows = table.getRows();
                    int count = 0;
                    for (TableRow item : rows) {
                        row = item;
                        if (row.getValues().size() > columnNum && row.getValues().get(columnNum).toString().equals(value)) {
                            count++;
                        }
                    }

                    System.out.println("The number of rows in table "  + tableName  + " where column " + tokens[2] + " contains the value " + value + " is " + count + ".");
                    break;
                case "aggregate":
                    if (tokens.length < 6) {
                        System.out.println("Invalid arguments. Usage: aggregate <table name> <search column n> <search value> <target column n> <operation>");
                        break;
                    }

                    tableName = tokens[1];
                    try {
                        columN = Integer.parseInt(tokens[2]) - 1;
                        targetColumnIndex = Integer.parseInt(tokens[4]) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid column number. Please enter an integer.");
                        break;
                    }
                    searchValue = tokens[3];
                    String operationName = tokens[5];

                    table = database.select(tableName);
                    if (table == null) {
                        break;
                    }

                    if (targetColumnIndex < 0 || targetColumnIndex >= table.getNumberOfColumns()) {
                        System.out.println("Column " + (targetColumnIndex + 1) + " does not exist in table " + tableName + ".");
                        break;
                    }

                    TableColumn targetColumn = table.getColumn(targetColumnIndex);
                    if (targetColumn.getDataType() != DataType.INTEGER && targetColumn.getDataType() != DataType.FLOAT) {
                        System.out.println("Error: column " + targetColumn.getName() + " is not numeric.");
                        break;
                    }

                    List<TableRow> matchingRows = new ArrayList<>();
                    for (TableRow tableRow : table.getRows()) {
                        if (tableRow.getValues().size() > columN && tableRow.getValues().get(columN).toString().equals(searchValue)) {
                            matchingRows.add(tableRow);
                        }
                    }

                    if (matchingRows.isEmpty()) {
                        System.out.println("Error: no rows match the search criteria.");
                        break;
                    }

                    double result = Double.NaN;
                    switch (operationName) {
                        case "sum":
                            result = Operation.sumColumn(matchingRows, targetColumnIndex);
                            break;
                        case "product":
                            result = Operation.productColumn(matchingRows, targetColumnIndex);
                            break;
                        case "maximum":
                            result = Operation.maxColumn(matchingRows, targetColumnIndex);
                            break;
                        case "minimum":
                            result = Operation.minColumn(matchingRows, targetColumnIndex);
                            break;
                        default:
                            System.out.println("Error: unsupported operation " + operationName + ".");
                            break;

                    }

                    if (!Double.isNaN(result)) {
                        System.out.println("Result: " + result);
                    }
                    break;
                case "close":
                    if (currentFilePath != null) {
                        System.out.println("Successfully closed " + currentFilePath);
                        currentFilePath = null;
                    } else {
                        System.out.println("No file is currently open");
                    }

                    break;
                case "save":
                    if (currentFilePath == null) {
                        System.out.println("No file is currently open.");
                        break;
                    }

                    Table tableSave = database.select(TableFile.getSelectedTable(currentFilePath).getName());
                    if (tableSave == null) {
                        System.out.println("No table is currently selected.");
                        break;
                    }

                    try {
                        TableFile tableFile = new TableFile(tableSave, currentFilePath);
                        tableFile.saveToFile(currentFilePath);
                        System.out.println("Changes saved to file " + currentFilePath + " successfully.");
                    } catch (IOException e) {
                        System.out.println("Error saving changes to file " + currentFilePath + ": " + e.getMessage());
                    }
                    break;
                case "saveas":
                    if (tokens.length != 2) {
                        System.out.println("Invalid arguments. Usage: saveas <file>");
                        break;
                    }
                    if (currentFilePath == null) {
                        System.out.println("No file is currently open.");
                        break;
                    }

                    String newFilePath = tokens[1];
                    File file = new File(newFilePath);

                    if (file.isDirectory()) {
                        System.out.println("Error: Invalid file path.");
                        break;
                    }

                    Table tableToSave = database.select(TableFile.getSelectedTable(currentFilePath).getName());
                    if (tableToSave == null) {
                        System.out.println("No table is currently selected.");
                        break;
                    }

                    tableName = tableToSave.getName();
                    fileName = newFilePath;

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                        StringJoiner columnJoiner = new StringJoiner(",");
                        for (String s : tableToSave.getColumnNames()) {
                            columnJoiner.add(s);
                        }
                        writer.write(tableName + "\n");
                        writer.write(columnJoiner.toString());
                        writer.newLine();

                        for (TableRow tableRow : tableToSave.getRows()) {
                            List<Value> v = tableRow.getValues();
                            StringJoiner valueJoiner = new StringJoiner(",");
                            for (Value value1 : v) {
                                valueJoiner.add(value1.toString());
                            }
                            writer.write(valueJoiner.toString());
                            writer.newLine();
                        }
                        System.out.println("Successfully saved another " + fileName + " successfully.");
                    } catch (IOException e) {
                        System.out.println("Error saving changes to file " + fileName + ": " + e.getMessage());
                    }
                    break;
                case "help":
                    System.out.println("The following commands are supported:");
                    System.out.println("open <file>\topens <file>");
                    System.out.println("close\t\tcloses currently opened file");
                    System.out.println("save\t\tsaves the currently open file ");
                    System.out.println("saveas <file>\taves the currently open file in <file>");
                    System.out.println("help\t\tprints this information");
                    System.out.println("exit\t\texists the program");
                    System.out.println("import <file name>\tadd table from file in database");
                    System.out.println("showtables\t\tshow list with all table names");
                    System.out.println("describe <name>\tshow column data types of table");
                    System.out.println("print <name>\tshows table rows");
                    System.out.println("export <name> <file name>\tsaves table in file");
                    System.out.println("select<column-n> <value> <table name>\t");
                    System.out.println("addcolumn <table name> <column name> <column type>");
                    System.out.println("update <table name> <search column n> <search value> <target column n> <target value>\tcloses currently opened file");
                    System.out.println("delete <table name> <search column n> <search value>");
                    System.out.println("insert <table name> <column 1> ... <column n>\tadd new row in table");
                    System.out.println("innerjoin <table 1> <column n1> <table 2> <column n2>");
                    System.out.println("rename <old name> <new name>\tchange table name");
                    System.out.println("count <table name> <search column n> <search value>");
                    System.out.println("aggregate <table name> <search column n> <search value> <target column n> <operation> ");
                    break;
                case "exit":
                    if (database != null) {
                        System.out.println("Closing file " + currentFilePath);
                        database = null;
                        currentFilePath = null;
                    }

                    System.out.println("Exiting the program...");
                    scanner.close();
                    System.exit(0);

                default:
                        System.out.println("No such command");
                    }
            }
        }
    public static Value parseValue(String valueStr, DataType dataType) {
        Value value;
        if (valueStr.equals("NULL")) {
            value = new NullValue(dataType);
        } else {
            try {
                int intValue = Integer.parseInt(valueStr);
                value = new IntegerValue(intValue);
            } catch (NumberFormatException e1) {
                try {
                    float floatValue = Float.parseFloat(valueStr);
                    value = new FloatValue(floatValue);
                } catch (NumberFormatException e2) {
                    value = new StringValue(valueStr);
                }
            }
        }
        return value;
    }

}
