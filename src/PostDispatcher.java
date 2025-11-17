public class PostDispatcher {
    private final RequestBuffer requestBuffer;
    private int rejectedRequestsCount = 0;
    private int requestsCount = 0;

    public PostDispatcher(RequestBuffer buffer) {
        requestBuffer = buffer;
    }

    public void processRequest(Request request) {
        System.out.println("Received " + request);
        requestsCount++;
        // Request rejection if adding wasn't successful
        if(!requestBuffer.addRequest(request)) {
            System.out.println("Rejected " + request);
            rejectedRequestsCount++;
            Request oldestRequest = requestBuffer.findOldest();
            requestBuffer.removeRequest(oldestRequest);
            requestBuffer.addRequest(request);
        }
    }

    public int getRejectedRequestsCount() {
        return rejectedRequestsCount;
    }

    public int getRequestsCount() {
        return requestsCount;
    }
}
