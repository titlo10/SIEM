import java.util.Random;

public class Generator {
    private final Random random;
    public double lambda; 

    public Generator(double lambda) {
        this.random = new Random();
        this.lambda = lambda;
    }

    public double nextExponential() {
        double expTime = -1.0 / lambda * Math.log(random.nextDouble());

        double roundedTime = Math.round(expTime * 10.0) / 10.0;

        return Math.max(0.1, roundedTime);
    }

    public double nextGenerationTime() {
        return nextExponential();
    }
}