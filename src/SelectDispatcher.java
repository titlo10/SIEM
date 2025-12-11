import java.util.ArrayList;

public class SelectDispatcher {
    private final RequestBuffer requestBuffer;
    private final ArrayList<AnalyticProcessor> processors;
    private int pointer = 0;

    public SelectDispatcher(RequestBuffer buffer, ArrayList<AnalyticProcessor> processors) {
        this.requestBuffer = buffer;
        this.processors = processors;
    }

    public void process(int now) {
        AnalyticProcessor processor;

        while (!requestBuffer.isEmpty() && (processor = chooseProcessor()) != null) {

            Request requestToProcess = requestBuffer.getLastRequest(); 
            
            if (requestToProcess == null) break; 
            
            requestBuffer.removeRequest(requestToProcess);

            processor.startProcessing(requestToProcess, now);
        }
    }

    public AnalyticProcessor chooseProcessor() {
        for (int i = 0; i < processors.size(); i++) {
            int index = (pointer + i) % processors.size();
            AnalyticProcessor processor = processors.get(index);
            if (processor.isFree()) {
                pointer = (index + 1) % processors.size();
                return processor;
            }
        }
        return null;
    }

    public void printProcessors() {
        System.out.println("Processors: ");
        for (int i = 0; i < processors.size(); i++) {
            System.out.printf("  %s %s\n", processors.get(i), (pointer == i) ? "<---- Next RR" : "");
        }
    }
}