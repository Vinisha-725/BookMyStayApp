import java.util.HashMap;
import java.util.Map;

class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 3);
        roomAvailability.put("Suite Room", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String roomType, int count) {
        if (count >= 0) {
            roomAvailability.put(roomType, count);
        }
    }
}

class RoomTypeDetails {
    private String name;
    private int numberOfbeds;
    private int squareFeet;
    private double pricePerNight;

    public RoomTypeDetails(String name, int numberOfbeds, int squareFeet, double pricePerNight) {
        this.name = name;
        this.numberOfbeds = numberOfbeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public String getName() { return name; }
    public int getBeds() { return numberOfbeds; }
    public int getSize() { return squareFeet; }
    public double getPricePerNight() { return pricePerNight; }
}

public class HotelBookingApp {
    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();

        RoomTypeDetails[] rooms = {
                new RoomTypeDetails("Single Room", 1, 250, 1500.0),
                new RoomTypeDetails("Double Room", 2, 400, 2500.0),
                new RoomTypeDetails("Suite Room", 3, 750, 5000.0)
        };

        System.out.println("Hotel Room Inventory Status\n");

        for (RoomTypeDetails room : rooms) {
            System.out.println(room.getName() + ":");
            System.out.println("Beds: " + room.getBeds());
            System.out.println("Size: " + room.getSize() + " sqft");
            System.out.println("Price: ₹" + room.getPricePerNight());
            System.out.println("Available: " + inventory.getRoomAvailability().get(room.getName()));
            System.out.println();
        }
    }
}