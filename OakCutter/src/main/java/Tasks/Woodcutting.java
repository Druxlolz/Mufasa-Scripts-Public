package Tasks;

import utils.Task;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Inventory;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Player;
import static helpers.Interfaces.Condition;
import static helpers.Interfaces.Walker;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import helpers.utils.Tile;
import main.OakCutter;

public class Woodcutting extends Task {
    private OakCutter oakCutter;  // Reference to OakCutter instance
    private String Location;  // Reference to location from the configuration
    private int LogsToBank;

    // Define the rectangle area to search for the colors (x, y, width, height)
    Rectangle searchRect = null;//new Rectangle(477, 170, 177, 261);
    Rectangle searchRect2 = null;
    int tolerance = 6;  // Default tolerance
    List<Color> targetColors;  // Declare targetColors as an instance variable

    public int Rsleep(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;  // Returns a random value between min and max
    }

    // Constructor to pass OakCutter instance and Location from the main script
    public Woodcutting(OakCutter oakCutter, String location, int LogsToBank) {
        this.oakCutter = oakCutter;  // Store the OakCutter instance
        this.Location = location;
        this.LogsToBank = LogsToBank;

        // Set target colors based on the LogsToBank value
        if (LogsToBank == 1521) {  // Oak
            tolerance = 6;  // Adjust the tolerance if necessary 6 normallt
            targetColors = Arrays.asList(
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
        } else if (LogsToBank == 1519) {  // Willow
            Logger.log("Colour for willows are set corectly!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            tolerance = 4;
            targetColors = Arrays.asList(
                Color.decode("#38450e"),
                Color.decode("#313719"),
                Color.decode("#3c441c"),
                Color.decode("#515836")
            );
        }
    }

    @Override
    public boolean activate() {
        // Execute only if "Draynor Village" is selected
        if (Location.equals("Draynor Village")) {
            Logger.log("Checking if woodcutting can start in Draynor Village");

            // Only activate woodcutting if the player is idle and inventory is not full
            if (Player.isIdle() && OakCutter.isTrulyIdleState && !Inventory.contains(LogsToBank, 0.8)) {
                Logger.log("Player is not woodcutting, and inventory has no logs. starting woodcutting");
                return true;
            }
            // Check for inventory change and log the result
            boolean inventoryChanged = oakCutter.updateInventoryChange();
            if (!inventoryChanged && Player.isIdle() && OakCutter.isTrulyIdleState) {
                Logger.log("No inventory changes, starting woodcutting based on idle state");
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
            Tile playerTile = Walker.getPlayerPosition();
            
            if (LogsToBank == 1519) {
                Logger.log("We are setting up regions to click in!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                Tile spot1 = new Tile(12339, 12697, 0);  // Create a new Tile instance
                Tile spot2 = new Tile(12347, 12689, 0);  // Create a new Tile instance
                Tile spot3 = new Tile(12351, 12681, 0);  // Create a new Tile instance
                Tile spot4 = new Tile(12355, 12653, 0);  // Create a new Tile instance
                
                // Use .equals to compare playerTile and targetTile
                if (Player.tileEquals(playerTile, spot1)) {
                    Logger.log("We are at spot 1,We are at spot 1,We are at spot 1,We are at spot 1,We are at spot 1");
                    searchRect = new Rectangle(346, 208, 61, 68);
                    searchRect2 = new Rectangle(428, 284, 69, 85);
                }
                if (Player.tileEquals(playerTile, spot2)) {
                    Logger.log("we are at spot2,we are at spot2,we are at spot2,we are at spot2,we are at spot2,we are at spot2");
                    searchRect = new Rectangle(352, 201, 70, 64);
                    searchRect2 = new Rectangle(496, 246, 58, 65);
                }
                if (Player.tileEquals(playerTile, spot3)) {
                    Logger.log("we are at spot3,we are at spot3,we are at spot3,we are at spot3,we are at spot3");
                    searchRect = new Rectangle(446, 159, 62, 56);
                    searchRect2 = new Rectangle(389, 290, 69, 74);
                }
                if (Player.tileEquals(playerTile, spot4)) {
                    Logger.log("we are at spot4,we are at spot4,we are at spot4,we are at spot4,we are at spot4,");
                    searchRect = new Rectangle(380, 170, 63, 58);
                }
            }
        Condition.sleep(Rsleep(1500, 2500));
        }
        if (Client.isAnyColorInRect(targetColors, searchRect, tolerance)) {
            Logger.log("Tapping tree");
            tapOnTree();
            return false;
        } else if (Client.isAnyColorInRect(targetColors, searchRect2, tolerance)) {
            Logger.log("Tapping tree");
            tapOnTree2();
            return false;
        }
        return false; // Indicate the task has finished executing for now
    }
    

    private void tapOnTree() {
        for (Color targetColor : targetColors) {
            Logger.log("Attempting to tap color: " + targetColor);
            Client.tap(targetColor, searchRect, tolerance); // Simulate a tap on the target color
            Logger.log("Tap successful for color: " + targetColor);
            Condition.wait(() -> Player.isIdle(), 200, 20);
            Condition.sleep(Rsleep(1500, 2500));  // Random sleep after tapping
            break;  // Only perform one tap, then exit the loop
        }
    }

    private void tapOnTree2() {
        for (Color targetColor : targetColors) {
            Logger.log("Attempting to tap color: " + targetColor);
            Client.tap(targetColor, searchRect2, tolerance); // Simulate a tap on the target color
            Logger.log("Tap successful for color: " + targetColor);
            Condition.wait(() -> Player.isIdle(), 200, 20);
            Condition.sleep(Rsleep(1500, 2500));  // Random sleep after tapping
            break;  // Only perform one tap, then exit the loop
        }
    }
}
