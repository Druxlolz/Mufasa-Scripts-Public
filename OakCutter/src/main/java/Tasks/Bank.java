package Tasks;
import utils.Task;
import static helpers.Interfaces.*;

import java.util.List;
import java.util.Random;

import helpers.utils.MapChunk;
import helpers.utils.Tile;
import java.awt.Rectangle;

public class Bank extends Task {
    private List<Tile> bankTileList;
    String Location;
    MapChunk mapChunk;
    Rectangle bankPosition = new Rectangle(379, 253, 31, 34);
    public int LogsToBank; // Add a variable to store the logs to bank
    

    public int Rsleep(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;  // Returns a random value between min and max
    }

    // Constructor to pass the LogsToBank value from the main class (OakCutter)
    public Bank(int LogsToBank, MapChunk mapChunk, String Location, List<Tile> bankTileList) {
        this.LogsToBank = LogsToBank;
        this.mapChunk = mapChunk;
        this.Location = Location;
        this.bankTileList = bankTileList;

    }

    @Override
    public boolean activate() {
        // Get the player's current position (inside the method for an updated value)
        Tile playerTile = Walker.getPlayerPosition();
        Logger.log("Checking if we are at bank tiles so we can bank!");
        // Loop through bankTileList1 to see if the player is on one of the bank tiles
        for (Tile bankTile : bankTileList) {
            if (Player.tileEquals(playerTile, bankTile)) {  // Assuming tileEquals compares two tiles
                Logger.log("Player is on bank tile: " + bankTile);
                return true;  // Activate the task if the player is on a bank tile
            }
        }
        
        Logger.log("Player is not on any bank tiles.");
        return false;  // Don't activate if the player is not on a bank tile
    }

    @Override
    public boolean execute() {
        Logger.log("Bank operations are going now!!!");
        if (!Bank.isOpen()) {
            // Tap the center of the bankPosition rectangle
            Client.tap(bankPosition);
            Condition.wait(() -> Bank.isOpen(), Rsleep(120, 180), 10);

            if (!Bank.isOpen()) { 
                Client.tap(bankPosition);
                Condition.wait(() -> Bank.isOpen(), Rsleep(120, 180), 10);
                return false;
            }
        }

        // Make sure the bank interface is set to withdraw/deposit all
        if (!Bank.isSelectedQuantityAllButton()) {
            Client.tap(Bank.findQuantityAllButton());
            Condition.wait(Bank::isSelectedQuantityAllButton, 150, 20);
        }

        // Tap on the logs to bank
        Inventory.tapItem(LogsToBank, 0.80);
        Logger.log("Depositing Logs!");
        Condition.wait(() -> !Inventory.contains(LogsToBank, 0.80), 150, 20);

        // Check if the logs have been successfully deposited
        if (!Inventory.contains(LogsToBank, 0.80)) {
            Logger.log("No logs in inventory, closing Bank");
            Bank.close();
            Condition.wait(() -> !Bank.isOpen(), 200, 20);
            return false;  // Task is complete, no more logs
        } else if (Inventory.contains(LogsToBank, 0.80)) {
            Logger.log("Depositing Logs! 2 attempt");
            Inventory.tapItem(LogsToBank, 0.80);
            Condition.wait(() -> !Inventory.contains(LogsToBank, 0.80), 150, 20);
            Bank.close();
            Condition.wait(() -> !Bank.isOpen(), 200, 20);
            return false;  // Task is complete, no more logs
        }

        return true;  // Continue the task if there are still logs in the inventory
    }
}
