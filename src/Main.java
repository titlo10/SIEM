import java.util.Scanner;

public class Main {
    public static boolean isAutoMode;
    static void main() {
        Simulation simulation = new Simulation();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Input \"a\" for the auto mode, \"Enter\" for Step-by-Step");

        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("a")) {
            isAutoMode = true;
            simulation.runAutoMode();
        } else {
            isAutoMode = false;
            System.out.println("(Step-by-Step Mode):");

            while (simulation.goToNextEvent()) {
                System.out.println("\nPress Enter to proceed to the next event...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
}

enum EventType {
    GENERATE_REQUEST,
    TASK_COMPLETED,
    TASK_UNBUFFER
}