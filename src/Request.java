import java.util.concurrent.atomic.AtomicInteger;

public class Request {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private final int sourceId;
    private final double generationTime;

    private double startedProcessingTime = 0.0;
    private double finishedProcessingTime = 0.0;

    public Request(double now, int sourceId) {
        id = count.incrementAndGet();
        generationTime = now;
        this.sourceId = sourceId;
    }

    public double getGenerationTime() {
        return generationTime;
    }

    public int getId() {
        return id;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setStartedProcessingTime(double startedProcessingTime) {
        this.startedProcessingTime = startedProcessingTime;
    }

    public void setFinishedProcessingTime(double finishedProcessingTime) {
        this.finishedProcessingTime = finishedProcessingTime;
    }
    
    public double getTotalTime() {
        if (finishedProcessingTime > 0) {
            return finishedProcessingTime - generationTime;
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return String.format("Request-%d from S-%d at %.1f units", id, sourceId, generationTime);
    }
}