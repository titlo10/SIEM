import java.util.concurrent.atomic.AtomicInteger;

public class AnalyticProcessor {
    private static final AtomicInteger count = new AtomicInteger(0);
    private int id;
    private boolean isBusy = false;
    private Request currentRequest = null;
    private final Generator timeGen;
    private double timeToFinish = 0.0;

    public AnalyticProcessor() {
        id = count.incrementAndGet();
        timeGen = new Generator();
    }

    public boolean isFree() {
        return !isBusy;
    }

    public void analyzeRequest(Request request, double now) {
        currentRequest = request;
        isBusy = true;
        timeToFinish = now + timeGen.nextExponential();
        System.out.printf("AnalyticProcessor-%d started analyzing %s until %.1f\n", id, currentRequest, timeToFinish);
    }

    public void checkAndFinish(double now) {
        if(isBusy && now >= timeToFinish) {
            System.out.printf("AnalyticProcessor-%d ended analyzing %s\n", id, currentRequest);
            isBusy = false;
            timeToFinish = 0.0;
            currentRequest = null;
        }
    }

    @Override
    public String toString() {
        if(isBusy) {
            return String.format("AnalyticProcessor-%d is BUSY until %.1f", id, timeToFinish);
        }
        return String.format("AnalyticProcessor-%d is FREE", id);
    }
}
