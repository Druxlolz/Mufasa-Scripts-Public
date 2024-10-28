package tasks;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import utils.Task;

import static helpers.Interfaces.*;

import helpers.utils.Tile;
import main.SandcrabExtended;

public class Move extends Task {


    Tile Spot4 = new Tile(6715, 39037, 0);
    Tile Spot3 = new Tile(6671, 39077, 0);
    Tile Spot2 = new Tile(6679, 39049, 0);

    List<Tile> resetSpots = Arrays.asList(
        new Tile(6755, 39077, 0), // Resetspot1
        new Tile(6583, 39137, 0),
        new Tile(6587, 39137, 0),
        new Tile(6579, 39137, 0),
        new Tile(6583, 39129, 0),
        new Tile(6579, 39141, 0),
        new Tile(6587, 39141, 0),
        new Tile(6583, 39141, 0),
        new Tile(6591, 39141, 0),
        new Tile(6759, 39081, 0),
        new Tile(6759, 39077, 0),
        new Tile(6759, 39073, 0),
        new Tile(6755, 39073, 0),
        new Tile(6751, 39073, 0),
        new Tile(6751, 39075, 0),
        new Tile(6751, 39081, 0),
        new Tile(6755, 39085, 0),
        new Tile(6751, 39085, 0),
        new Tile(6763, 39081, 0),
        new Tile(6763, 39077, 0),
        new Tile(6763, 39073, 0)
    );
    Tile playerTile = null;
    Random random = new Random();
    




    public boolean activate() {
        playerTile = Walker.getPlayerPosition();
    
        if (Player.isIdle() || !Player.tileEquals(playerTile, Spot4) && !Player.tileEquals(playerTile, Spot3) && !Player.tileEquals(playerTile, Spot2)) {
            Logger.log("We are idle 1st check");
            Condition.sleep(SandcrabExtended.Rsleep(200, 1500));
            if (Player.isIdle() && SandcrabExtended.isTrulyIdle()) {
                Logger.log("we are idle 2nd, check going");
                return true;
            }
        }

        return false; // Return false if no action was taken
    }
    

    @Override
    public boolean execute() {
            if (Player.tileEquals(playerTile, Spot4) || Player.tileEquals(playerTile, Spot3) || Player.tileEquals(playerTile, Spot2))  {
                Logger.log("We are at spot 2, 3 or 4!");
                Tile selectedResetSpot = resetSpots.get(random.nextInt(resetSpots.size()));
                Walker.walkTo(selectedResetSpot);
                Logger.log("Walking to Reset spot");
                Condition.wait(() -> Player.tileEquals(playerTile, selectedResetSpot), SandcrabExtended.Rsleep(150, 250), 15);
                return true;
            }
    
            boolean isAtResetSpot = resetSpots.stream().anyMatch(resetSpot -> Player.tileEquals(playerTile, resetSpot));
            if (isAtResetSpot) {
                Logger.log("We are at reset spot!");
                Walker.walkTo(SandcrabExtended.selectedCrabSpot);
                Condition.wait(() -> Player.tileEquals(playerTile, SandcrabExtended.selectedCrabSpot), SandcrabExtended.Rsleep(150, 250), 15);
                Logger.log("Walking back to Sandcrab spot");
                Condition.sleep(SandcrabExtended.Rsleep(1000, 1500));
                return true; 
            }

            if (!isAtResetSpot && !Player.tileEquals(playerTile, Spot4) && !Player.tileEquals(playerTile, Spot3) && !Player.tileEquals(playerTile, Spot2)) {
                Logger.log("We are at some random spot");
                Walker.walkTo(SandcrabExtended.selectedCrabSpot);
                Condition.wait(() -> Player.tileEquals(playerTile, SandcrabExtended.selectedCrabSpot), SandcrabExtended.Rsleep(150, 250), 15);
                Logger.log("Walking to Sandcrab spot");
                Condition.sleep(SandcrabExtended.Rsleep(1000, 1500));
                return true; 
            }

        return false;
    }
}

