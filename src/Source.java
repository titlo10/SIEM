import java.util.concurrent.atomic.AtomicInteger;

public class Source {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private final Generator timeGen;
    private double timeToGenerate;
    private final PostDispatcher postDisp;

    public Source(PostDispatcher postDisp) {
        this.postDisp = postDisp;
        timeGen = new Generator();
        id = count.incrementAndGet();
        timeToGenerate = timeGen.nextUniform();
    }

    public void generateRequest(double now) {
        if (now >= timeToGenerate) {
            timeToGenerate = now + timeGen.nextUniform();
            postDisp.processRequest(new Request(now, id));
        }
    }
}
