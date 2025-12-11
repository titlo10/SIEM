import java.util.ArrayList;
import java.util.PriorityQueue;
import java.lang.Math;

public class Simulation {
    private final PriorityQueue<SystemEvent> eventSet;
    private int currentTime = 0;
    private final Statistics statistics;
    private final RequestBuffer buffer;
    private final SelectDispatcher selectDisp;
    private final ArrayList<AnalyticProcessor> processors;
    private final ArrayList<Source> sources;

    // Constants
    private static final int BUFFER_CAPACITY = 5;
    private static final int NUM_PROCESSORS = 4;
    private static final int MIN_INTERVAL = 0;
    private static final int MAX_INTERVAL = 10;
    private static final int NUM_SOURCES = 20;
    private static final double LAMBDA = 1.0;

    public Simulation() {
        eventSet = new PriorityQueue<>();
        statistics = new Statistics(NUM_SOURCES, NUM_PROCESSORS);
        buffer = new RequestBuffer(BUFFER_CAPACITY);
        processors = new ArrayList<>();

        for(int i = 0; i < NUM_PROCESSORS; i++){
            processors.add(new AnalyticProcessor(this, statistics, LAMBDA));
        }

        PostDispatcher postDisp = new PostDispatcher(buffer, this, statistics);
        sources = new ArrayList<>();
        for(int i = 0; i < NUM_SOURCES; i++){
            sources.add(new Source(this, postDisp, MIN_INTERVAL, MAX_INTERVAL));
        }
        
        selectDisp = new SelectDispatcher(buffer, processors);
    }

    public int getCurrentTime() {
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
        currentTime = currentEvent.eventTime();
        
        System.out.println("\n==================================================");
        System.out.printf(">>> TIME ADVANCED TO T=%d\n", currentTime);
        System.out.printf(">>> PROCESSING EVENT: %s (Device ID: %d)\n", 
                          currentEvent.eventType(), currentEvent.assignedDeviceId());
        System.out.println("==================================================");

        handleEvent(currentEvent);
        printStatus();

        return true; 
    }

    public void runAutoMode() {
        double tAlpha = 1.643;
        double accuracy = 0.1;
        int minTransactions = 200;

        System.out.println("Starting Auto Mode (Target Accuracy: 10%, Confidence: 90%)...");

        while (statistics.getTotalTransactions() < minTransactions) {
            if (!goToNextEvent()) break;
        }

        while (true) {
            double p = statistics.getRejectionRate();
            if (p == 0) p = 0.001;

            double requiredTransactions = (Math.pow(tAlpha, 2) * (1 - p)) / (p * Math.pow(accuracy, 2));

            if (statistics.getTotalTransactions() >= requiredTransactions) {
                System.out.printf("\n*** CRITERIA MET ***\nStopping condition met: N_current=%d >= N_required=%.0f (P_rej=%.4f)\n",
                        statistics.getTotalTransactions(), requiredTransactions, p);
                break;
            }

            if (!goToNextEvent()) break;
        }

        statistics.printFinalTable(currentTime);
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
        System.out.printf("\n--- SYSTEM STATE at T=%d ---\n", currentTime);
        buffer.printBuffer();
        selectDisp.printProcessors();
        System.out.printf("Next Event at T=%d\n", eventSet.isEmpty() ? -1 : eventSet.peek().eventTime());
    }
}