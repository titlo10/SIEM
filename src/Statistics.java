import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.Math;

public class Statistics {
    private int totalTransactions = 0;
    private int rejectedTransactions = 0;

    private final Map<Integer, SourceStats> sourceStats = new HashMap<>();
    private final Map<Integer, ProcessorStats> processorStats = new HashMap<>();

    public Statistics(int numSources, int numProcessors) {
        for (int i = 1; i <= numSources; i++) sourceStats.put(i, new SourceStats());
        for (int i = 1; i <= numProcessors; i++) processorStats.put(i, new ProcessorStats());
    }

    public void recordGenerated(int sourceId) {
        totalTransactions++;
        sourceStats.get(sourceId).generated++;
    }

    public void recordRejection(int sourceId) {
        rejectedTransactions++;
        sourceStats.get(sourceId).rejected++;
    }

    public void recordCompleted(Request request, int processorId) {
        double waitTime = request.getWaitTime();
        double serviceTime = request.getServiceTime();
        double systemTime = request.getTotalTime();

        SourceStats sStats = sourceStats.get(request.getSourceId());
        sStats.completed++;
        sStats.totalWaitTime += waitTime;
        sStats.totalServiceTime += serviceTime;
        sStats.totalSystemTime += systemTime;
        sStats.waitTimes.add(waitTime);
        sStats.serviceTimes.add(serviceTime);

        ProcessorStats pStats = processorStats.get(processorId);
        pStats.processed++;
        pStats.busyTime += serviceTime;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public double getRejectionRate() {
        return totalTransactions == 0 ? 0 : (double) rejectedTransactions / totalTransactions;
    }

    private double calculateVariance(List<Double> values, double mean) {
        if (values.size() <= 1) return 0.0;
        double sumOfSquares = 0.0;
        for (double value : values) {
            sumOfSquares += Math.pow(value - mean, 2);
        }
        return sumOfSquares / (values.size() - 1);
    }

    public void printFinalTable(int simulationTime) {
        System.out.printf("%-12s %-8s %-8s %-10s %-10s %-10s %-10s %-10s %-10s\n",
                "Source", "Gen", "Rej", "P_rej(%)", "T_sys", "T_wait", "T_serv", "D_wait", "D_serv");
        System.out.println("-".repeat(100));

        for (Map.Entry<Integer, SourceStats> entry : sourceStats.entrySet()) {
            int id = entry.getKey();
            SourceStats s = entry.getValue();
            if (s.generated == 0) continue;

            double pRej = (double) s.rejected / s.generated * 100.0;
            double avgSys = s.completed == 0 ? 0 : s.totalSystemTime / s.completed;
            double avgWait = s.completed == 0 ? 0 : s.totalWaitTime / s.completed;
            double avgServ = s.completed == 0 ? 0 : s.totalServiceTime / s.completed;
            double varWait = calculateVariance(s.waitTimes, avgWait);
            double varServ = calculateVariance(s.serviceTimes, avgServ);

            System.out.printf("Source-%-3d %-8d %-8d %-10.2f %-10.3f %-10.3f %-10.3f %-10.3f %-10.3f\n",
                    id, s.generated, s.rejected, pRej, avgSys, avgWait, avgServ, varWait, varServ);
        }

        System.out.printf("%-12s %-12s %-15s %-10s\n", "Processor", "Processed", "Busy Time", "Util(%)");
        System.out.println("-".repeat(60));

        for (Map.Entry<Integer, ProcessorStats> entry : processorStats.entrySet()) {
            int id = entry.getKey();
            ProcessorStats p = entry.getValue();
            double util = simulationTime == 0 ? 0 : (p.busyTime / (double)simulationTime) * 100;
            System.out.printf("Proc-%-7d %-12d %-15.2f %-10.2f\n", id, p.processed, p.busyTime, util);
        }
        System.out.println("=".repeat(100));
    }

    private static class SourceStats {
        public int generated = 0;
        public int rejected = 0;
        public int completed = 0;
        public double totalSystemTime = 0.0;
        public double totalServiceTime = 0.0;
        public double totalWaitTime = 0.0;
        public final List<Double> serviceTimes = new ArrayList<>();
        public final List<Double> waitTimes = new ArrayList<>();
    }

    private static class ProcessorStats {
        public double busyTime = 0.0;
        public int processed = 0;
    }
}