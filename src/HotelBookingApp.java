import java.util.*;

/* 1. Reservation Class */
class Reservation {

    private String guestName;
    private String roomType;
    private String assignedRoomId;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public void setAssignedRoomId(String id) {
        this.assignedRoomId = id;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getAssignedRoomId() {
        return assignedRoomId;
    }
}


/* 2. Thread-Safe Booking Queue */
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation r) {
        queue.add(r);
    }

    public synchronized Reservation getNextRequest() {

        if(queue.isEmpty())
            return null;

        return queue.poll();
    }
}


/* 3. Inventory Service (Thread-Safe) */
class InventoryService {

    private Map<String, Integer> availableCounts = new HashMap<>();
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();

    public InventoryService() {

        availableCounts.put("Single", 5);
        availableCounts.put("Double", 3);
        availableCounts.put("Suite", 2);

        allocatedRooms.put("Single", new HashSet<>());
        allocatedRooms.put("Double", new HashSet<>());
        allocatedRooms.put("Suite", new HashSet<>());
    }

    /* Critical Section */
    public synchronized String allocateRoom(String roomType) {

        if (!availableCounts.containsKey(roomType)) {
            return null;
        }

        if (availableCounts.get(roomType) <= 0) {
            return null;
        }

        String roomId =
                roomType.substring(0,1).toUpperCase()
                        + (100 + allocatedRooms.get(roomType).size() + 1);

        allocatedRooms.get(roomType).add(roomId);

        availableCounts.put(
                roomType,
                availableCounts.get(roomType) - 1
        );

        return roomId;
    }
}


/* 4. Concurrent Booking Processor (Thread) */
class ConcurrentBookingProcessor extends Thread {

    private BookingRequestQueue queue;
    private InventoryService inventory;

    public ConcurrentBookingProcessor(
            BookingRequestQueue queue,
            InventoryService inventory,
            String threadName
    ) {
        super(threadName);
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            Reservation request = queue.getNextRequest();

            if (request == null)
                break;

            String roomId =
                    inventory.allocateRoom(request.getRoomType());

            if (roomId != null) {

                request.setAssignedRoomId(roomId);

                System.out.println(
                        Thread.currentThread().getName()
                                + " ✅ Confirmed: "
                                + request.getGuestName()
                                + " → Room "
                                + roomId
                );

            } else {

                System.out.println(
                        Thread.currentThread().getName()
                                + " ❌ Failed: No room available for "
                                + request.getGuestName()
                );
            }
        }
    }
}


/* 5. Main Application */
public class UseCase11ConcurrentBookingProcessing {

    public static void main(String[] args) {

        BookingRequestQueue queue = new BookingRequestQueue();
        InventoryService inventory = new InventoryService();


        /* Simulated Guest Requests */

        queue.addRequest(new Reservation("Abhi", "Suite"));
        queue.addRequest(new Reservation("Subha", "Suite"));
        queue.addRequest(new Reservation("Rahul", "Suite"));

        queue.addRequest(new Reservation("Anita", "Double"));
        queue.addRequest(new Reservation("Karthik", "Double"));
        queue.addRequest(new Reservation("Divya", "Double"));
        queue.addRequest(new Reservation("Priya", "Double"));


        /* Multiple Threads Processing Requests */

        ConcurrentBookingProcessor t1 =
                new ConcurrentBookingProcessor(queue, inventory, "Thread-1");

        ConcurrentBookingProcessor t2 =
                new ConcurrentBookingProcessor(queue, inventory, "Thread-2");

        ConcurrentBookingProcessor t3 =
                new ConcurrentBookingProcessor(queue, inventory, "Thread-3");


        t1.start();
        t2.start();
        t3.start();


        try {

            t1.join();
            t2.join();
            t3.join();

        } catch (InterruptedException e) {

            e.printStackTrace();
        }

        System.out.println("\nAll booking requests processed safely.");
    }
}