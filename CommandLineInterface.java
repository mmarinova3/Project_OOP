package database.project.f21621557;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CommandLineInterface {
    private Database database;
    private String currentFilePath;
    private String databaseDirectory;

    public CommandLineInterface(String databaseDirectory) {
        this.databaseDirectory = databaseDirectory;
    }

    public void start() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            String[] tokens = input.split(" ");
            String command = tokens[0];

            switch (command) {
                case "open":
                    if (tokens.length != 2) {
                        System.out.println("Invalid arguments. Usage: open <file>");
                        break;
                    }

                    String filePath = databaseDirectory + File.separator + tokens[1];

                    try {
                        database = Database.loadFromFile(filePath);
                        currentFilePath = filePath;
                        System.out.println("Successfully opened " + tokens[1]);
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Error opening " + tokens[1] + ": " + e.getMessage());
                        // Create new file with empty content if it doesn't exist
                        File file = new File(filePath);
                        if (file.createNewFile()) {
                            System.out.println("File " + tokens[1] + " created with empty content.");
                            database = new Database(databaseDirectory);
                            currentFilePath = filePath;
                        }
                    }

                    break;

                case "close":
                    if (database != null) {
                        System.out.println("Successfully closed " + currentFilePath);
                        database = null;
                        currentFilePath = null;
                    } else {
                        System.out.println("No file is currently open");
                    }

                    break;

                case "save":
                    if (database == null) {
                        System.out.println("No file is currently open");
                        break;
                    }

                    try {
                        database.saveToFile(currentFilePath);
                        System.out.println("Changes saved to " + currentFilePath);
                    } catch (IOException e) {
                        System.out.println("Error saving changes: " + e.getMessage());
                    }

                    break;

                case "saveas":
                    if (database == null) {
                        System.out.println("No file is currently open");
                        break;
                    }

                    if (tokens.length != 2) {
                        System.out.println("Invalid arguments. Usage: saveas <file>");
                        break;
                    }

                    String saveFilePath = tokens[1];

                    try {
                        database.saveToFile(saveFilePath);
                        System.out.println("Successfully saved " + currentFilePath + " as " + saveFilePath);
                        currentFilePath = saveFilePath;
                    } catch (IOException e) {
                        System.out.println("Error saving changes: " + e.getMessage());
                    }

                    break;

                case "help":
                    System.out.println("The following commands are supported:");
                    System.out.println("open <file>\topens <file>");
                    System.out.println("close\t\tcloses currently opened file");
                    System.out.println("save\t\tsaves the currently open file");
                    System.out.println("saveas <file>\tsaves the currently open file in <file>");
                    System.out.println("help\t\tprints this information");
                    System.out.println("exit\t\texists the program");
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
                    if (database == null) {
                        System.out.println("No file is currently open");
                        break;
                    }

                    System.out.println("Executing command: " + input);
                    break;
            }
        }
    }
}