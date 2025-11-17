import java.util.concurrent.atomic.AtomicInteger;

public class Request {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private final int sourceId;
    private final double generationTime;

    public Request(double now, int sourceId) {
        id = count.incrementAndGet();
        generationTime = now;
        this.sourceId = sourceId;
    }

    public double getGenerationTime() {
        return generationTime;
    }

    @Override
    public String toString() {
        return String.format("Request-%d from S-%d at %.1f units", id, sourceId, generationTime);
    }
}
