package tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;


import static helpers.Interfaces.*;
import static main.XMarksTheSpot.*;


public class DigOutsideLumbyGate extends Task {
    Tile LumbyGateDigSpot = new Tile(123, 123, 123);

    public boolean activate() {
        return !DigOutsideLumbyGate;
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
                DigOutsideLumbyGate = true;
            }
        }

        return false;
    }

    private boolean WalkToDigSpot() {
        if (!Player.atTile(LumbyGateDigSpot)) {

            if (!Walker.isReachable(LumbyGateDigSpot)) {
                Walker.webWalk(LumbyGateDigSpot);
            } else {
                Walker.step(LumbyGateDigSpot);
            }
            return false;
        } else return true;
    }
}
