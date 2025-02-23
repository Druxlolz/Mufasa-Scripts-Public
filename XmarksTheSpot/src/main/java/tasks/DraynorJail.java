package tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static helpers.Interfaces.*;
import static main.XMarksTheSpot.*;

public class DraynorJail extends Task {
    Tile DraynorJailSpot = new Tile(123, 123, 123);
    Area DigArea = new Area(
        new Tile(12380, 12826, 0), 
        new Tile(12456, 12730, 0)
    );


    private boolean gloryUsed = false; // Makes sure we only try to equip a glory once. 
    private Rectangle gloryslot = new Rectangle(123, 123);

    List<Color> textColors = Arrays.asList(
        new Color(123, 123, 123),
        new Color(200, 100, 50),
        new Color(255, 255, 0)
    );

    public boolean activate() {
        return !DraynorJail;
    }

    @Override
    public boolean execute() {
        if (WalkToDigSpot()) {
            if (!Inventory.contains(123, 0.8)) {
                Inventory.tapItem(ItemList.SPADE_952, 0.8);
                Condition.wait(() -> Inventory.contains(123, 0.8), 300, 4);
                Condition.sleep(1, 300);
            } else {
                // We dug outside Draynor Jail
                DraynorJail = true;
            }
        }
 
        return false;
    }

    private boolean WalkToDigSpot() {
        if (!Player.atTile(DraynorJailSpot)) {
            if (UseGlory) {
                if (!gloryUsed) {
                    List<Integer> gloryItems = Arrays.asList(
                        ItemList.AMULET_OF_GLORY_1_1706,
                        ItemList.AMULET_OF_GLORY_2_1708,
                        ItemList.AMULET_OF_GLORY_3_1710,
                        ItemList.AMULET_OF_GLORY_4_1712,
                        ItemList.AMULET_OF_GLORY_5_11976,
                        ItemList.AMULET_OF_GLORY_6_11978
                    );

                    // Check for each glory and tap the first one found.
                    for (Integer glory : gloryItems) {
                        if (Inventory.contains(glory, 0.8)) {
                            Inventory.tapItem(glory, 0.8);
                            gloryUsed = true;
                            Condition.sleep(140,350);
                            break;
                        }
                    }
                }

                if (!Player.within(DigArea)) {
                    if (!GameTabs.isTabOpen(UITabs.EQUIP)) {
                        GameTabs.openTab(UITabs.EQUIP);

                        Condition.wait(() -> GameTabs.isTabOpen(UITabs.EQUIP), 100, 25);
                        Condition.sleep(20,300);
                    }

                    Client.longPressWithMenuAction(gloryslot, 123, 123, textColors, "Draynor");
                    
                    Condition.wait(() -> Player.within(DigArea), 100, 75);
                    Condition.sleep(20, 300);
                    return false;
                }
            }

            // Continue walking toward the dig spot.
            if (!Walker.isReachable(DraynorJailSpot)) {
                Walker.webWalk(DraynorJailSpot);
            } else {
                Walker.step(DraynorJailSpot);
            }

            return false;

        } else {
            return true;
        }
    }
}
