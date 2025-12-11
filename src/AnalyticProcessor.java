import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AnalyticProcessor {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private boolean isBusy = false;
    private Request currentRequest = null;
    private final Simulation simulation;
    private final double lambda;
    private final Statistics statistics;
    private final Random random;

    public AnalyticProcessor(Simulation simulation, Statistics statistics, double lambda) {
        this.simulation = simulation;
        this.statistics = statistics;
        this.lambda = lambda;
        this.id = count.incrementAndGet();
        this.random = new Random();
    }

    public boolean isFree() {
        return !isBusy;
    }

    public void startProcessing(Request request, int now) {
        if (!isBusy) {
            currentRequest = request;
            isBusy = true;
            
            request.setStartedProcessingTime(now);
            
            int serviceTime = generateServiceTime();
            int timeToFinish = now + serviceTime;
            
            System.out.printf("[%d] Processor-%d START: %s (Service: %d) -> Finish at %d\n",
                               now, id, currentRequest, serviceTime, timeToFinish);

            simulation.scheduleEvent(new SystemEvent(timeToFinish, EventType.TASK_COMPLETED, id));
        }
    }

    public void completeProcessing(int now) {
        if (currentRequest == null) return;

        currentRequest.setFinishedProcessingTime(now);

        statistics.recordCompleted(currentRequest, id);

        System.out.printf("[%d] Processor-%d END: %s (Total Time: %d)\n",
                now, id, currentRequest, currentRequest.getTotalTime());

        currentRequest = null;
        isBusy = false;

        simulation.scheduleEvent(new SystemEvent(now, EventType.TASK_UNBUFFER, -1));
    }

    int generateServiceTime() {
        double expTime =  Math.log(1 - random.nextDouble()) / (-lambda);
        return Math.max(1, (int) expTime);
    }
    
    @Override
    public String toString() {
        if(isBusy) {
            return String.format("Processor-%d is BUSY (Request %d)", id, currentRequest.getId());
        }
        return String.format("Processor-%d is FREE", id);
    }
}