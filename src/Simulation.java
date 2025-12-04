import java.util.ArrayList;
import java.util.PriorityQueue;

public class Simulation {
    private final PriorityQueue<SystemEvent> eventSet;
    private double currentTime = 0.0;
    private final double maxTime = 50.0;

    private final RequestBuffer buffer;
    private final PostDispatcher postDisp;
    private final SelectDispatcher selectDisp;
    private final ArrayList<AnalyticProcessor> processors;
    private final ArrayList<Source> sources;

    // Constants
    private static final int BUFFER_CAPACITY = 5;
    private static final int NUM_PROCESSORS = 4;
    private static final int NUM_SOURCES = 3;
    private static final double SOURCE_LAMBDA = 0.5;
    private static final double PROCESSOR_LAMBDA = 0.25;

    public Simulation() {
        eventSet = new PriorityQueue<>();
        buffer = new RequestBuffer(BUFFER_CAPACITY);
        processors = new ArrayList<>();
        
        for(int i = 0; i < NUM_PROCESSORS; i++){
            processors.add(new AnalyticProcessor(this, PROCESSOR_LAMBDA));
        }
        
        postDisp = new PostDispatcher(buffer, this);
        sources = new ArrayList<>();
        for(int i = 0; i < NUM_SOURCES; i++){
            sources.add(new Source(this, postDisp, SOURCE_LAMBDA));
        }
        
        selectDisp = new SelectDispatcher(buffer, processors, this);
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void scheduleEvent(SystemEvent event) {
        eventSet.add(event);
    }

    public boolean goToNextEvent() {
        if (eventSet.isEmpty()) {
            System.out.println("Event calendar is empty. Simulation is completed");
            return false;
        }
        
        SystemEvent currentEvent = eventSet.poll();

        if (currentEvent.eventTime() >= maxTime) {
            currentTime = maxTime; 
            return false;
        }

        currentTime = currentEvent.eventTime();
        
        System.out.println("\n==================================================");
        System.out.printf(">>> TIME ADVANCED TO T=%.1f\n", currentTime);
        System.out.printf(">>> PROCESSING EVENT: %s (Device ID: %d)\n", 
                          currentEvent.eventType(), currentEvent.assignedDeviceId());
        System.out.println("==================================================");

        handleEvent(currentEvent);
        printStatus();

        return true; 
    }

    public void runAutoMode() {
        while (goToNextEvent());
        System.out.println("--- Simulation Finished at Time: " + currentTime + " ---");
        printFinalStatistics();
    }
    
    private void handleEvent(SystemEvent event) {
        int deviceId = event.assignedDeviceId();
        
        switch (event.eventType()) {
            case GENERATE_REQUEST:
                Source source = sources.get(deviceId - 1);
                source.generateRequest(currentTime); 
                break;
                
            case TASK_COMPLETED:
                AnalyticProcessor processor = processors.get(deviceId - 1);
                processor.completeProcessing(currentTime);
                break;
                
            case TASK_UNBUFFER:
                selectDisp.process(currentTime);
                break;
        }
    }

    private void printStatus() {
        System.out.printf("\n--- SYSTEM STATE at T=%.1f ---\n", currentTime);
        buffer.printBuffer();
        selectDisp.printProcessors();
        System.out.printf("Next Event at T=%.1f\n", eventSet.isEmpty() ? -1.0 : eventSet.peek().eventTime());
    }

    public void printFinalStatistics() {
        int totalRequests = postDisp.getRequestsCount();
        int rejectedRequests = postDisp.getRejectedRequestsCount();
        
        System.out.println("\n*** Final Statistics ***");
        System.out.printf("Total time: %.1f\n", currentTime);
        System.out.println("Total requests generated: " + totalRequests);
        System.out.println("Total requests rejected: " + rejectedRequests);
        
        double rejectedPercent = (totalRequests == 0) ? 0.0 : ((double)rejectedRequests / totalRequests * 100.0);
        System.out.printf("Rejected percent: %.2f%%\n", rejectedPercent);
    }
}