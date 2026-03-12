import java.util.*;

/* 1. Custom Exception for Invalid Booking */
class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message);
    }
}


/* 2. Reservation Class */
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


/* 3. Booking Request Queue */
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


/* 4. Inventory Service */
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

    public boolean isValidRoomType(String roomType) {
        return availableCounts.containsKey(roomType);
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
}


/* 5. Invalid Booking Validator */
class InvalidBookingValidator {

    public static void validate(Reservation reservation,
                                InventoryService inventory)
            throws InvalidBookingException {

        if (reservation.getGuestName() == null ||
                reservation.getGuestName().trim().isEmpty()) {

            throw new InvalidBookingException(
                    "Guest name cannot be empty.");
        }

        if (!inventory.isValidRoomType(reservation.getRoomType())) {

            throw new InvalidBookingException(
                    "Invalid room type: " + reservation.getRoomType());
        }

        if (!inventory.isAvailable(reservation.getRoomType())) {

            throw new InvalidBookingException(
                    "No available rooms for type: " +
                            reservation.getRoomType());
        }
    }
}


/* 6. Booking Service */
class BookingService {

    private InventoryService inventory;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void processQueue(BookingRequestQueue queue) {

        while (queue.hasPendingRequests()) {

            Reservation request = queue.getNextRequest();

            try {

                /* Validate before processing */
                InvalidBookingValidator.validate(request, inventory);

                String roomId =
                        inventory.allocateRoom(request.getRoomType());

                request.setAssignedRoomId(roomId);

                System.out.println("✅ Booking Confirmed: "
                        + request.getGuestName()
                        + " assigned to Room "
                        + roomId);

            } catch (InvalidBookingException e) {

                /* Graceful error handling */
                System.out.println("❌ Booking Failed for Guest: "
                        + request.getGuestName()
                        + " | Reason: "
                        + e.getMessage());
            }
        }
    }
}


/* 7. Main Application */
public class UseCase9ValidationAndErrorHandling {

    public static void main(String[] args) {

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        InventoryService inventory = new InventoryService();
        BookingService service = new BookingService(inventory);


        /* Booking Requests (Some Invalid) */

        bookingQueue.addRequest(new Reservation("Abhi", "Suite"));
        bookingQueue.addRequest(new Reservation("Subha", "Suite"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite")); // exceeds inventory
        bookingQueue.addRequest(new Reservation("", "Single")); // invalid guest name
        bookingQueue.addRequest(new Reservation("Rahul", "Luxury")); // invalid room type


        /* Process Queue */

        service.processQueue(bookingQueue);
    }
}