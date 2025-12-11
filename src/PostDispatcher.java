public class PostDispatcher {
    private final RequestBuffer requestBuffer;
    private final Simulation simulation;
    private final Statistics statistics;

    public PostDispatcher(RequestBuffer buffer, Simulation simulation, Statistics statistics) {
        this.requestBuffer = buffer;
        this.simulation = simulation;
        this.statistics = statistics;
    }

    public void processRequest(Request request) {
        statistics.recordGenerated(request.getSourceId());

        if(requestBuffer.addRequest(request)) {
            simulation.scheduleEvent(new SystemEvent(simulation.getCurrentTime(), EventType.TASK_UNBUFFER, -1));
        } else {
            Request oldestRequest = requestBuffer.findOldest();

            if (oldestRequest != null) {
                requestBuffer.removeRequest(oldestRequest);
                statistics.recordRejection(oldestRequest.getSourceId());

                System.out.printf("[%d] Dispatcher REJECTED %s (Replaced by %s)\n",
                        simulation.getCurrentTime(), oldestRequest, request);
            }
            requestBuffer.addRequest(request);

            simulation.scheduleEvent(new SystemEvent(simulation.getCurrentTime(), EventType.TASK_UNBUFFER, -1));
        }
    }
}