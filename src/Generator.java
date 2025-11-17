import java.text.DecimalFormat;
import java.util.Random;

public class Generator {
    private final Random random;
    private double lambda = 0.1;

    public Generator() {
        this.random = new Random();
    }

    public Generator(double lambda) {
        this.random = new Random();
        this.lambda = lambda;
    }

    public double nextUniform() {
        double randTime = 0.1 + random.nextDouble(1);

        // Truncate number to one decimal
        DecimalFormat df = new DecimalFormat("#.#");
        return Double.parseDouble(df.format(randTime));
    }

    // Generate time with exponential distribution
    public double nextExponential() {
        double expTime = -1.0 / lambda * Math.log(1.0 - random.nextDouble());

        // Truncate number to one decimal
        DecimalFormat df = new DecimalFormat("#.#");
        double truncatedNumber = Double.parseDouble(df.format(expTime));

        // Can't be less than our time step
        return Math.max(0.1, truncatedNumber);
    }
}