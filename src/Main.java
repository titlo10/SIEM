import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Input \"a\" for the auto mode, \"Enter\" for Step-by-Step");

        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("a")) {
            simulation.runAutoMode();
        } else {
            System.out.println("(Step-by-Step Mode):");

            while (simulation.goToNextEvent()) {
                System.out.println("\nPress Enter to proceed to the next event...");
                scanner.nextLine();
            }
            simulation.printFinalStatistics(); 
        }
    }
}