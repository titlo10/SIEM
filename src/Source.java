import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Source {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private final Simulation simulation;
    private final int minInterval;
	private final int maxInterval;
    private final PostDispatcher postDisp;
    private final Random random;

    public Source(Simulation simulation, PostDispatcher postDisp, int minInt, int maxInt) {
        this.simulation = simulation;
        this.postDisp = postDisp;
        minInterval = minInt;
        maxInterval = maxInt;
        id = count.incrementAndGet();
        this.random = new Random();

        scheduleNextGeneration(0);
    }

    int getNextGenerationTime() {
        return minInterval + 1 + random.nextInt(maxInterval - minInterval);
    }

    public void scheduleNextGeneration(int now) {
        int nextTime = now + getNextGenerationTime();
        simulation.scheduleEvent(new SystemEvent(nextTime, EventType.GENERATE_REQUEST, id));
    }

    public void generateRequest(int now) {
        Request newRequest = new Request(now, id);
        postDisp.processRequest(newRequest);

        if(!Main.isAutoMode) {
            System.out.printf("[%d] Source-%d GENERATED %s\n", now, id, newRequest);
        }

        scheduleNextGeneration(now);
    }
}
