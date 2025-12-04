import java.util.concurrent.atomic.AtomicInteger;

public class AnalyticProcessor {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private boolean isBusy = false;
    private Request currentRequest = null;
    private final Generator timeGen;
    private final Simulation simulation; 

    public AnalyticProcessor(Simulation simulation, double lambda) {
        this.simulation = simulation;
        id = count.incrementAndGet();
        timeGen = new Generator(lambda);
    }

    public int getId() {
        return id;
    }

    public boolean isFree() {
        return !isBusy;
    }

    public void startProcessing(Request request, double now) {
        if (!isBusy) {
            currentRequest = request;
            isBusy = true;
            
            request.setStartedProcessingTime(now);
            
            double serviceTime = timeGen.nextExponential();
            double timeToFinish = now + serviceTime;
            
            System.out.printf("[%.1f] Processor-%d START: %s (Service: %.1f) -> Finish at %.1f\n", 
                               now, id, currentRequest, serviceTime, timeToFinish);

            simulation.scheduleEvent(new SystemEvent(timeToFinish, EventType.TASK_COMPLETED, id));
        }
    }

    public Request completeProcessing(double now) {
        if (currentRequest == null) return null;
        
        currentRequest.setFinishedProcessingTime(now);
        System.out.printf("[%.1f] Processor-%d END: %s (Total Time: %.1f)\n", 
                           now, id, currentRequest, currentRequest.getTotalTime());
        
        Request completedRequest = currentRequest;
        currentRequest = null;
        isBusy = false;

        simulation.scheduleEvent(new SystemEvent(now, EventType.TASK_UNBUFFER, -1));
        
        return completedRequest;
    }
    
    @Override
    public String toString() {
        if(isBusy) {
            return String.format("Processor-%d is BUSY (Request %d)", id, currentRequest.getId());
        }
        return String.format("Processor-%d is FREE", id);
    }
}