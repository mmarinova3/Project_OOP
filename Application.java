package database.project.f21621557;

import java.io.IOException;

public class Application {

        public static void main(String[] args) {
            String databaseDirectory = "C:\\path\\to\\database\\directory";
            CommandLineInterface cli = new CommandLineInterface(databaseDirectory);

            try {
                cli.start(); // start the command line interface
            } catch (IOException e) {
                System.out.println("An error occurred while starting the command line interface: " + e.getMessage());
            }
        }
}

