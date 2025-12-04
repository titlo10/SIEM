import java.util.concurrent.atomic.AtomicInteger;

public class Source {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private final Generator timeGen;
    private final Simulation simulation;
    private final PostDispatcher postDisp;

    public Source(Simulation simulation, PostDispatcher postDisp, double lambda) {
        this.simulation = simulation;
        this.postDisp = postDisp;
        timeGen = new Generator(lambda);
        id = count.incrementAndGet();

        scheduleNextGeneration(0.0);
    }

    public int getId() {
        return id;
    }

    public void scheduleNextGeneration(double now) {
        double nextTime = now + timeGen.nextGenerationTime();
        simulation.scheduleEvent(new SystemEvent(nextTime, EventType.GENERATE_REQUEST, id));
    }

    public void generateRequest(double now) {
        Request newRequest = new Request(now, id);
        postDisp.processRequest(newRequest);
        
        System.out.printf("[%.1f] Source-%d GENERATED %s\n", now, id, newRequest);

        scheduleNextGeneration(now);
    }
}
