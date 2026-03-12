import java.util.*;

/* 1. Reservation Class */
class Reservation {

    private String guestName;
    private String roomType;
    private String assignedRoomId;
    private boolean cancelled = false;

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

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean status) {
        this.cancelled = status;
    }
}


/* 2. Booking Request Queue */
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean hasPendingRequests() {
        return !queue.isEmpty();
    }
}


/* 3. Inventory Service */
class InventoryService {

    private Map<String, Integer> availableCounts = new HashMap<>();
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();

    public InventoryService() {

        availableCounts.put("Single", 10);
        availableCounts.put("Double", 5);
        availableCounts.put("Suite", 2);

        allocatedRooms.put("Single", new HashSet<>());
        allocatedRooms.put("Double", new HashSet<>());
        allocatedRooms.put("Suite", new HashSet<>());
    }

    public boolean isAvailable(String roomType) {
        return availableCounts.getOrDefault(roomType, 0) > 0;
    }

    public String allocateRoom(String roomType) {

        if (!isAvailable(roomType))
            return null;

        String roomId =
                roomType.substring(0,1).toUpperCase()
                        + (100 + allocatedRooms.get(roomType).size() + 1);

        allocatedRooms.get(roomType).add(roomId);

        availableCounts.put(roomType,
                availableCounts.get(roomType) - 1);

        return roomId;
    }

    /* Restore inventory after cancellation */
    public void releaseRoom(String roomType, String roomId) {

        if (allocatedRooms.containsKey(roomType)) {

            allocatedRooms.get(roomType).remove(roomId);

            availableCounts.put(roomType,
                    availableCounts.get(roomType) + 1);
        }
    }
}


/* 4. Booking History */
class BookingHistory {

    private List<Reservation> confirmedBookings = new ArrayList<>();

    public void addBooking(Reservation r) {
        confirmedBookings.add(r);
    }

    public List<Reservation> getAllBookings() {
        return confirmedBookings;
    }

    public Reservation findReservation(String roomId) {

        for (Reservation r : confirmedBookings) {

            if (roomId.equals(r.getAssignedRoomId()))
                return r;
        }

        return null;
    }
}


/* 5. Booking Service */
class BookingService {

    private InventoryService inventory;
    private BookingHistory history;

    public BookingService(InventoryService inventory,
                          BookingHistory history) {

        this.inventory = inventory;
        this.history = history;
    }

    public void processQueue(BookingRequestQueue queue) {

        while (queue.hasPendingRequests()) {

            Reservation request = queue.getNextRequest();

            if (inventory.isAvailable(request.getRoomType())) {

                String roomId =
                        inventory.allocateRoom(request.getRoomType());

                request.setAssignedRoomId(roomId);

                history.addBooking(request);

                System.out.println("✅ Confirmed: "
                        + request.getGuestName()
                        + " assigned to Room "
                        + roomId);

            } else {

                System.out.println("❌ Failed: No availability for "
                        + request.getRoomType()
                        + " (Guest: "
                        + request.getGuestName()
                        + ")");
            }
        }
    }
}


/* 6. Cancellation Service */
class CancellationService {

    private InventoryService inventory;
    private BookingHistory history;

    /* Stack for rollback tracking */
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(InventoryService inventory,
                               BookingHistory history) {

        this.inventory = inventory;
        this.history = history;
    }

    public void cancelBooking(String roomId) {

        Reservation reservation =
                history.findReservation(roomId);

        if (reservation == null) {

            System.out.println("❌ Cancellation Failed: Reservation not found for Room " + roomId);
            return;
        }

        if (reservation.isCancelled()) {

            System.out.println("❌ Cancellation Failed: Booking already cancelled for Room " + roomId);
            return;
        }

        /* Push to rollback stack */
        rollbackStack.push(roomId);

        /* Restore inventory */
        inventory.releaseRoom(
                reservation.getRoomType(),
                roomId
        );

        reservation.setCancelled(true);

        System.out.println("🔁 Booking Cancelled Successfully for Room " + roomId);
    }

    public void showRollbackHistory() {

        System.out.println("\nRollback Stack (Recently Released Rooms):");

        for (String roomId : rollbackStack) {
            System.out.println(roomId);
        }
    }
}


/* 7. Main Application */
public class UseCase10CancellationAndRollback {

    public static void main(String[] args) {

        BookingRequestQueue queue = new BookingRequestQueue();
        InventoryService inventory = new InventoryService();
        BookingHistory history = new BookingHistory();

        BookingService bookingService =
                new BookingService(inventory, history);

        CancellationService cancellationService =
                new CancellationService(inventory, history);


        /* Booking Requests */

        queue.addRequest(new Reservation("Abhi", "Suite"));
        queue.addRequest(new Reservation("Subha", "Suite"));
        queue.addRequest(new Reservation("Rahul", "Single"));


        /* Process Bookings */

        bookingService.processQueue(queue);


        System.out.println("\n---- Cancellation Requests ----");


        /* Cancel bookings */

        cancellationService.cancelBooking("S101");
        cancellationService.cancelBooking("S101"); // duplicate cancel
        cancellationService.cancelBooking("D999"); // non-existent booking


        cancellationService.showRollbackHistory();
    }
}