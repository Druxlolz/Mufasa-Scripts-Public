package Tasks;

import utils.Task;
import helpers.utils.MapChunk;
import helpers.utils.Tile;

import static helpers.Interfaces.Condition;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Player;
import static helpers.Interfaces.Walker;
import static helpers.Interfaces.Inventory;
import java.util.List;
import java.util.Random;

public class MoveTo extends Task {
    private List<Tile> bankTileList;
    private List<Tile> targetTileList;
    private boolean bankingMode;
    MapChunk mapChunk;

    Tile selectedTile = null;
    Tile selectedBankTile = null;

    public int Rsleep(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;  // Returns a random value between min and max
    }

    // Constructor without dynamicBankIdentifier
    public MoveTo(List<Tile> targetTileList, List<Tile> bankTileList, boolean bankingMode, MapChunk mapChunk) {
        this.targetTileList = targetTileList;  // Set target tile list
        this.bankingMode = bankingMode;
        this.mapChunk = mapChunk;
        this.bankTileList = bankTileList;  // Set bank tile list
    }

    @Override
    public boolean activate() {
        Logger.log("MoveTo Activated");
        Tile playerTile = Walker.getPlayerPosition(); // Update player position

        if (bankingMode && Inventory.isFull()) {
            for (Tile banktile : bankTileList) {
                if (Player.tileEquals(playerTile, banktile)) {
                    Logger.log("Already on one of the bank tiles! Skipping MoveTo task.");
                    return false;
                }
            }
            selectedBankTile = bankTileList.get(new Random().nextInt(bankTileList.size()));
            Logger.log("Inventory is full, proceeding to bank.");
            return true;  // Proceed to the bank if banking mode is active and inventory is full
        }

        if (!Inventory.isFull()) {
            // Check if the player is already on one of the target tiles
            for (Tile targetTile : targetTileList) {
                if (Player.tileEquals(playerTile, targetTile)) {
                    Logger.log("Already on one of the target tiles! Skipping MoveTo task.");
                    return false;
                }
            }
            // Pick a random tile from the target tile list
            selectedTile = targetTileList.get(new Random().nextInt(targetTileList.size()));
            Logger.log("Selected random target tile: " + selectedTile);
            return true;  // Proceed to move to the selected tile
        }

        return false;  // No task to perform
    }

    @Override
    public boolean execute() {
        Tile playerTile = Walker.getPlayerPosition(); // Update player position

        if (bankingMode && Inventory.isFull()) {
            Logger.log("Executing banking mode. Moving to bank tiles");
            Walker.step(selectedBankTile);
            Condition.wait(() -> Player.tileEquals(playerTile, selectedBankTile),200, 10);
            return false;
        }

        // Check if selectedTile is null (which can happen if targetTileList was null or empty)
        if (!Inventory.isFull()) {
            if (selectedTile == null) {
                Logger.log("Error: Selected tile is null. Cannot move.");
            } else if (!Player.tileEquals(playerTile, selectedTile)) {
                Logger.log("Executing walk to trees: " + selectedTile);
                Walker.step(selectedTile);
                Condition.wait(() -> Player.tileEquals(playerTile, selectedTile), 200, 10);
            }
        }

        return false;
    }
}