package tasks;


import helpers.utils.ItemList;
import helpers.utils.Tile;

import utils.Task;



import static helpers.Interfaces.*;
import static main.XMarksTheSpot.*;

public class DraynorPig extends Task {
    Tile DigSpot = new Tile(123, 123, 123);

    
    public boolean activate() {
        return !DraynorPig;
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
                DraynorPig = true;
            }
        }

        return false;
    }

    private boolean WalkToDigSpot() {
        if (!Player.atTile(DigSpot)) {

            if (!Walker.isReachable(DigSpot)) {
                Walker.webWalk(DigSpot);
            } else {
                Walker.step(DigSpot);
            }

            return false;

        } else 
        
        return true;
    }
}
