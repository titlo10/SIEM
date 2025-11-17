import java.util.ArrayList;

public class SelectDispatcher {
    private final RequestBuffer requestBuffer;
    private final ArrayList<AnalyticProcessor> processors;
    private int pointer = 0;

    public SelectDispatcher(RequestBuffer buffer, ArrayList<AnalyticProcessor> processors) {
        requestBuffer = buffer;
        this.processors = processors;
    }

    public void process(double now) {
        if (!requestBuffer.isEmpty() && isAnyProcessorFree()) {
            Request requestToProcess = requestBuffer.getLastRequest();
            requestBuffer.removeRequest(requestToProcess);
            AnalyticProcessor processor = chooseProcessor();
            processor.analyzeRequest(requestToProcess, now);
        }
    }

    public AnalyticProcessor chooseProcessor() {
        // The same logic as for buffer
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
            System.out.printf("%s. %s %s\n", i, processors.get(i), (pointer == i) ? "<----" : "");
        }
    }

    public boolean isAnyProcessorFree() {
        for (AnalyticProcessor processor : processors) {
            if (processor.isFree()) {
                return true;
            }
        }
        return false;
    }
}
