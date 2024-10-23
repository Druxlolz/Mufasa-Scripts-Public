package Tasks;

import utils.Task;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Inventory;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Player;
import static helpers.Interfaces.Condition;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import main.OakCutter;

public class Woodcutting extends Task {
    private OakCutter oakCutter;  // Reference to OakCutter instance
    private String Location;  // Reference to location from the configuration
    private int LogsToBank;

    // Define the rectangle area to search for the colors (x, y, width, height)
    Rectangle searchRect = new Rectangle(477, 170, 177, 261);
    int tolerance = 6;

    List<Color> targetColors = Arrays.asList(
        Color.decode("#4e5629"),
        Color.decode("#394223"),
        Color.decode("#465613"),
        Color.decode("#2a2f04"),
        Color.decode("#6b7c16"),
        Color.decode("#8c9b48"),
        Color.decode("#545f17"),
        Color.decode("#1a220b"),
        Color.decode("#151b09"),
        Color.decode("#2b311a"),
        Color.decode("#5e6e17")
    );

    public int Rsleep(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;  // Returns a random value between min and max
    }
    

    // Constructor to pass OakCutter instance and Location from the main script
    public Woodcutting(OakCutter oakCutter, String location, int LogsToBank) {
        this.oakCutter = oakCutter;  // Store the OakCutter instance
        this.Location = location;
        this.LogsToBank = LogsToBank;
    }

    @Override
    public boolean activate() {
        // Execute only if "Draynor Village" is selected
        if (Location.equals("Draynor Village")) {
            Logger.log("Checking if woodcutting can start in Draynor Village");

            // Only activate woodcutting if the player has been idle for 3 seconds and inventory is not full
            if (Player.isIdle() && !Inventory.isFull() || OakCutter.isTrulyIdleState && !Inventory.isFull()) {
                Logger.log("Player is not woodcutting, and inventory is not full. starting woodcutting");
                return true;
            }
        }
        Logger.log("We Shouldn't Woodcut");
        return false;  // Don't activate if the conditions aren't met
    }

    @Override
    public boolean execute() {
        
        // Execute only if "Draynor Village" is selected
        if (Location.equals("Draynor Village") && !Inventory.isFull()) {
                Logger.log("Player is idle, starting tree interaction.");
                tapOnTree();
                
            }
        return false; // Indicate the task has finished executing for now
    }


    private void tapOnTree() {
        for (Color targetColor : targetColors) {
            Logger.log("Attempting to tap color: " + targetColor);
            Client.tap(targetColor, searchRect, tolerance); // Simulate a tap on the target color
            Logger.log("Tap successful for color: " + targetColor);
            Condition.sleep(Rsleep(1000, 1600));  // Random sleep after tapping
            break;  // Only perform one tap, then exit the loop
        }
    }
}
