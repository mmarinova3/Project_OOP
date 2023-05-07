package bg.tu_varna.sit.b3.f21621557;

import java.io.*;
import java.util.*;

public class Application {
    public static void main(String[] args) throws IOException {

        String databaseDirectory="D:\\Temp\\input";
        Database myDatabase = new Database("tables",databaseDirectory);

        Table employee = new Table("employee", "employee.csv" );

        TableColumn column1 = new TableColumn("id", DataType.INTEGER);
        TableColumn column2 = new TableColumn("name", DataType.STRING);
        TableColumn column3 = new TableColumn("age", DataType.INTEGER);
        TableColumn column7 = new TableColumn("salary", DataType.FLOAT);
        TableColumn column10 = new TableColumn("hours", DataType.INTEGER);
        TableColumn position_id = new TableColumn("position_id", DataType.INTEGER);
        employee.addColumn(column1);
        employee.addColumn(column2);
        employee.addColumn(column3);
        employee.addColumn(column7);
        employee.addColumn(column10);
        employee.addColumn(position_id);

        TableRow row1 = new TableRow(Arrays.asList(new IntegerValue(1), new StringValue("Alice"), new IntegerValue(25),new FloatValue(1000.0f),new IntegerValue(7),new IntegerValue(3)));
        TableRow row2 = new TableRow(Arrays.asList(new IntegerValue(2), new StringValue("Bob"), new IntegerValue(30),new FloatValue(8000.0f),new IntegerValue(8),new IntegerValue(1)));
        TableRow row7 = new TableRow(Arrays.asList(new IntegerValue(3), new StringValue("Bob"), new IntegerValue(15),new FloatValue(4000.0f),new IntegerValue(8),new IntegerValue(2)));
        TableRow row8 = new TableRow(Arrays.asList(new IntegerValue(4), new StringValue("Chris"), new IntegerValue(35),new FloatValue(6000.0f),new IntegerValue(8),new IntegerValue(1)));
        TableRow row9 = new TableRow(Arrays.asList(new IntegerValue(5), new StringValue("Bob"), new IntegerValue(20),new FloatValue(5000.0f),new IntegerValue(8),new IntegerValue(3)));
        employee.addRow(row1);
        employee.addRow(row2);
        employee.addRow(row7);
        employee.addRow(row8);
        employee.addRow(row9);

        TableFile tableFile = new TableFile(employee, databaseDirectory);
        tableFile.saveToFile();
        myDatabase.addTable(employee);

        Table customers = new Table("customers", "customers.csv" );

        TableColumn column4 = new TableColumn("id", DataType.INTEGER);
        TableColumn column5 = new TableColumn("name", DataType.STRING);
        TableColumn column8 = new TableColumn("address", DataType.STRING);
        customers.addColumn(column4);
        customers.addColumn(column5);
        customers.addColumn(column8);

        TableRow row3 = new TableRow(Arrays.asList(new IntegerValue(1), new StringValue("Katrin"), new StringValue("Varna")));
        TableRow row4 = new TableRow(Arrays.asList(new IntegerValue(2), new StringValue("Ivan"),  new StringValue("Burgas")));
        customers.addRow(row3);
        customers.addRow(row4);

        TableFile tableFile1 = new TableFile(customers, databaseDirectory);
        tableFile1.saveToFile();
        myDatabase.addTable(customers);

        Table position = new Table("position","position.csv");

        TableColumn id_position =new TableColumn("id", DataType.INTEGER);
        TableColumn position_name=new TableColumn("name", DataType.STRING);
        TableColumn min_salary=new TableColumn("min_salary", DataType.FLOAT);
        position.addColumn(id_position);
        position.addColumn(position_name);
        position.addColumn(min_salary);

        TableRow position1 = new TableRow(Arrays.asList(new IntegerValue(1),new StringValue("supervisor"),new FloatValue(2000.0f)));
        TableRow position2 = new TableRow(Arrays.asList(new IntegerValue(2),new StringValue("barman"),new FloatValue(1500.0f)));
        TableRow position3 = new TableRow(Arrays.asList(new IntegerValue(3),new StringValue("waiter"),new NullValue(DataType.FLOAT)));
        position.addRow(position1);
        position.addRow(position2);
        position.addRow(position3);

        myDatabase.addTable(position);
        TableFile tableFile2 = new TableFile(position, databaseDirectory);
        tableFile2.saveToFile();

        CommandLineInterface cli = new CommandLineInterface( myDatabase);
        try {
            cli.start();
        } catch (Exception e) {
            System.out.println("An error occurred while starting the command line interface: " + e.getMessage());
        }
    }
}