import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class RequestBuffer {
    private final int capacity;
    private final ArrayList<Request> requests;
    private int pointer;

    public RequestBuffer(int size) {
        capacity = size;
        pointer = 0;
        requests = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            requests.add(null);
        }
    }

    public Request findOldest() {
        return requests.stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparingDouble(Request::getGenerationTime))
                .orElse(null);
    }

    public Request getLastRequest() {
        return requests.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(Request::getGenerationTime)) 
                .orElse(null);
    }

    public boolean addRequest(Request request) {
        int startIndex = pointer;
        int freeSlotIndex = -1;

        // Search for free slot in the buffer
        for (int i = 0; i < capacity; i++) {
            int index = (startIndex + i) % capacity;
            if (requests.get(index) == null) {
                freeSlotIndex = index;
                break;
            }
        }

        if (freeSlotIndex != -1) {
            requests.set(freeSlotIndex, request);
            pointer = (freeSlotIndex + 1) % capacity;
            return true;
        }
        return false;
    }

    public void removeRequest(Request request) {
        if (request != null && requests.contains(request)) {
            requests.set(requests.indexOf(request), null);
        }
    }

    public boolean isEmpty() {
        return requests.stream().allMatch(Objects::isNull);
    }

    public void printBuffer() {
        System.out.println("Buffer: ");
        for (int i = 0; i < capacity; i++) {
            String content = (requests.get(i) != null) ? requests.get(i).toString() : "NULL";
            System.out.printf("  %s. %s %s\n", i, content, (pointer == i) ? "<---- Next Free" : "");
        }
    }
}