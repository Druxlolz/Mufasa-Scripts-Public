package tasks;

import helpers.utils.Tile;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.XMarksTheSpot.*;

public class FinishQuest extends Task {
    Tile WizardTalkTile = new Tile(123, 123, 123);
    Rectangle WizardDock = new Rectangle(123,123);

    Color FinishBoxColor = new Color(123,123,123);
    Rectangle FinishBoxRectangle = new Rectangle(123, 123);
    Rectangle FinishBoxCloseRectangle = new Rectangle(123, 123);

    

    public boolean activate() {
        return !QuestDone;
    }

    @Override
    public boolean execute() {
        if (WalkToDigSpot()) {
            Rectangle chatboxRect = Chatbox.findChatboxMenu();
            if (chatboxRect != null) {
                Client.tap(clickToContinueRect);
                // More logic needs to be added for text choices.

            } else {
                Client.tap(WizardDock);
                Condition.sleep(20, 300);
            }
        }

        // Quest box detection & Quest finished flag
        if (Client.isColorInRect(FinishBoxColor, FinishBoxRectangle, 5)) {
            Condition.sleep(250, 1200);
            Client.tap(FinishBoxCloseRectangle);
            QuestDone = true;
        }

        return false;
    }

    private boolean WalkToDigSpot() {
        if (!Player.atTile(WizardTalkTile)) {

            if (!Walker.isReachable(WizardTalkTile)) {
                Walker.webWalk(WizardTalkTile);
            } else {
                Walker.step(WizardTalkTile);
            }

            return false;

        } else 
        
        return true;
    }
}
