public record SystemEvent(double eventTime, EventType eventType, int assignedDeviceId) implements Comparable<SystemEvent> {
    @Override
    public int compareTo(SystemEvent other) {
        if (this.eventTime != other.eventTime) {
            return Double.compare(this.eventTime, other.eventTime);
        }

        return this.eventType.compareTo(other.eventType);
    }
}