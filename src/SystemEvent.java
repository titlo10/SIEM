public record SystemEvent(int eventTime, EventType eventType, int assignedDeviceId) implements Comparable<SystemEvent> {
    @Override
    public int compareTo(SystemEvent other) {
        if (this.eventTime != other.eventTime) {
            return Integer.compare(this.eventTime, other.eventTime);
        }

        return this.eventType.compareTo(other.eventType);
    }
}