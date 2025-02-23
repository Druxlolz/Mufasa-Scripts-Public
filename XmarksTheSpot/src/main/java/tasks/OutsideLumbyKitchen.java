package tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;




import static helpers.Interfaces.*;
import static main.XMarksTheSpot.*;

public class OutsideLumbyKitchen extends Task {

    Tile LumbyKitchenSpot = new Tile(123, 123, 123);

    public boolean activate() {
        return !OutsideLumbyKitchen;
    }


    @Override
    public boolean execute() {
        if (WalkToDigSpot()) {
            if (!Inventory.contains(123, 0.8)) {
                Inventory.tapItem(ItemList.SPADE_952, 0.8);

                Condition.wait(() -> Inventory.contains(123, 0.8), 300, 4);
                Condition.sleep(1, 300);
            } else {
                // We dug outside castle of lumby
                OutsideLumbyKitchen = true;
            }
        }

        return false;
    }

    private boolean WalkToDigSpot() {
        if (!Player.atTile(LumbyKitchenSpot)) {

            if (!Walker.isReachable(LumbyKitchenSpot)) {
                Walker.webWalk(LumbyKitchenSpot);
            } else {
                Walker.step(LumbyKitchenSpot);
            }
            return false;
        } else return true;
    }
}
