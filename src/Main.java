import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Simulation simulation = new Simulation();
        while (true) {
            if(scanner.nextLine().isEmpty()){
                simulation.nextStep();
            }
        }
    }
}