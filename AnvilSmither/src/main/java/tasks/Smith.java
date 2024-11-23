package tasks;

import utils.Task;
import static helpers.Interfaces.*;
import helpers.utils.*;
import main.AnvilSmither;



public class Smith extends Task {

    private final String barType;
    private final String gearType;

    public static boolean SmithingStarted;

    // Constructor to initialize barType and gearType
    public Smith(String barType, String gearType) {
        this.barType = barType;
        this.gearType = gearType;
    }

    // Activation condition for this task
    @Override
    public boolean activate() {
        // Example activation condition: Check if the player is at the anvil and has bars in inventory
        return Inventory.contains(barType, 0.8) && interfaces.smithingIsOpen();
    }

    @Override
    public boolean execute() {
        Logger.log("Starting to smith " + gearType + " using " + barType);
    
        // Open the smithing interface
        if (interfaces.smithingIsOpen()) {
            Condition.sleep(500);
    
            // Construct the SmithItems enum using the helper method
            SmithItems itemToSmith = AnvilSmither.constructSmithItem(barType, gearType);
    
            if (itemToSmith != null) {
                // Attempt to smith the selected item
                interfaces.smithItem(itemToSmith);
                SmithingStarted = true;
                Logger.log("Smithing " + itemToSmith.name());
                Condition.wait(() -> !Inventory.contains(barType, 0.8), 1000, 20); // Wait until bars are consumed
                return true; // Task executed successfully
            } else {
                Logger.log("Failed to construct a valid smithing item for: " + barType + " + " + gearType);
            }
        } else {
            Logger.log("Failed to open the smithing interface.");
        }
    
        return false; // Task execution failed
    }
    
}