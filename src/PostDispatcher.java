public class PostDispatcher {
    private final RequestBuffer requestBuffer;
    private final Simulation simulation;
    private int rejectedRequestsCount = 0;
    private int requestsCount = 0;

    public PostDispatcher(RequestBuffer buffer, Simulation simulation) {
        this.requestBuffer = buffer;
        this.simulation = simulation;
    }

    public void processRequest(Request request) {
        requestsCount++;
        
        if(requestBuffer.addRequest(request)) {
            simulation.scheduleEvent(new SystemEvent(simulation.getCurrentTime(), EventType.TASK_UNBUFFER, -1));
        } else {
            rejectedRequestsCount++;
            Request oldestRequest = requestBuffer.findOldest();

            requestBuffer.removeRequest(oldestRequest);
            requestBuffer.addRequest(request);
            
            System.out.printf("[%.1f] Dispatcher REJECTED %s (Replaced by %s)\n", 
                               simulation.getCurrentTime(), oldestRequest, request);

            simulation.scheduleEvent(new SystemEvent(simulation.getCurrentTime(), EventType.TASK_UNBUFFER, -1));
        }
    }

    public int getRejectedRequestsCount() {
        return rejectedRequestsCount;
    }

    public int getRequestsCount() {
        return requestsCount;
    }
}