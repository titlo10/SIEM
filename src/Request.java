import java.util.concurrent.atomic.AtomicInteger;

public class Request {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private final int sourceId;
    private final int generationTime;

    private int startedProcessingTime = 0;
    private int finishedProcessingTime = 0;

    public Request(int now, int sourceId) {
        id = count.incrementAndGet();
        generationTime = now;
        this.sourceId = sourceId;
    }

    public int getGenerationTime() { return generationTime; }
    public int getId() { return id; }
    public int getSourceId() { return sourceId; }

    public void setStartedProcessingTime(int t) { this.startedProcessingTime = t; }
    public void setFinishedProcessingTime(int t) { this.finishedProcessingTime = t; }

    public int getWaitTime() {
        if (startedProcessingTime > 0) return startedProcessingTime - generationTime;
        return 0;
    }

    public int getServiceTime() {
        if (finishedProcessingTime > 0) return finishedProcessingTime - startedProcessingTime;
        return 0;
    }

    public int getTotalTime() {
        if (finishedProcessingTime > 0) return finishedProcessingTime - generationTime;
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Request-%d from S-%d at %d units", id, sourceId, generationTime);
    }
}