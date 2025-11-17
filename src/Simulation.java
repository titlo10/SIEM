import java.util.ArrayList;

public class Simulation {
    RequestBuffer buffer;
    PostDispatcher postDisp;
    SelectDispatcher selectDisp;
    ArrayList<AnalyticProcessor> processors;
    ArrayList<Source> sources;
    double currentTime = 0.0;
    //TODO: Make maxTime for the automatic mode

    // Constants
    private static final int BUFFER_CAPACITY = 5;
    private static final int NUM_PROCESSORS = 4;
    private static final int NUM_SOURCES = 3;

    public Simulation() {
        buffer = new RequestBuffer(BUFFER_CAPACITY);
        processors = new ArrayList<>();
        for(int i = 0; i < NUM_PROCESSORS; i++){
            processors.add(new AnalyticProcessor());
        }
        postDisp = new PostDispatcher(buffer);
        sources = new ArrayList<>();
        for(int i = 0; i < NUM_SOURCES; i++){
            sources.add(new Source(postDisp));
        }
        selectDisp = new SelectDispatcher(buffer, processors);
    }

    public void nextStep(){
        System.out.printf("Current time: %.1f units\n", currentTime);
        for (Source source : sources){
            source.generateRequest(currentTime);
        }
        for (AnalyticProcessor processor: processors) {
            processor.checkAndFinish(currentTime);
        }
        selectDisp.process(currentTime);
        buffer.printBuffer();
        selectDisp.printProcessors();
        int totalRequests = postDisp.getRequestsCount();
        int rejectedRequests = postDisp.getRejectedRequestsCount();
        System.out.println("Total requests " + totalRequests);
        System.out.println("Rejected requests " + rejectedRequests);
        System.out.printf("Rejected percent %.2f%%\n", (rejectedRequests == 0) ? 0.0 : ((double)rejectedRequests / totalRequests * 100.0));
        currentTime += 0.1;
    }
}
