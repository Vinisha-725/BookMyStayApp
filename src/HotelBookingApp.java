import java.util.HashMap;
import java.util.Map;

class Room {
    private String name;
    private int numberOfBeds;
    private int squareFeet;
    private double pricePerNight;

    Room(String name, int numberOfBeds, int squareFeet, double pricePerNight) {
        this.name = name;
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public String getName() {
        return name;
    }
    public int getBeds() {
        return numberOfBeds;
    }
    public int getSize() {
        return squareFeet;
    }
    public double getPricePerNight() {
        return pricePerNight;
    }

    public void displayRoomInfo(int available) {
        System.out.println(name + " Room:");
        System.out.println("  Beds: " + numberOfBeds);
        System.out.println("  Size: " + squareFeet + " sqft");
        System.out.println("  Price per night: " + pricePerNight);
        System.out.println("  Available: " + available + "\n");
    }
}

class RoomInventory {
    private Map<String, Integer> availability;

    public RoomInventory() {
        availability = new HashMap<>();
    }

    public void setRoomAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    public Map<String, Integer> getRoomAvailability() {
        return availability;
    }
}

class RoomSearchService {
    public void searchAvailableRooms(
            RoomInventory inventory,
            Room singleRoom,
            Room doubleRoom,
            Room suiteRoom) {

        Map<String, Integer> availability = inventory.getRoomAvailability();

        System.out.println("Room Search\n");

        if (availability.get("Single") > 0) {
            singleRoom.displayRoomInfo(availability.get("Single"));
        }
        if (availability.get("Double") > 0) {
            doubleRoom.displayRoomInfo(availability.get("Double"));
        }
        if (availability.get("Suite") > 0) {
            suiteRoom.displayRoomInfo(availability.get("Suite"));
        }
    }
}

// Application entry point class
public class HotelBookingApp {
    public static void main(String[] args) {
        Room singleRoom = new Room("Single", 1, 250, 1500.0);
        Room doubleRoom = new Room("Double", 2, 400, 2500.0);
        Room suiteRoom = new Room("Suite", 3, 750, 5000.0);

        RoomInventory inventory = new RoomInventory();
        inventory.setRoomAvailability("Single", 5);
        inventory.setRoomAvailability("Double", 3);
        inventory.setRoomAvailability("Suite", 2);

        RoomSearchService searchService = new RoomSearchService();
        searchService.searchAvailableRooms(inventory, singleRoom, doubleRoom, suiteRoom);
    }
}