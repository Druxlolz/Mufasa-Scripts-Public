package tasks;

import utils.Task;

import java.awt.*;

import helpers.utils.Tile;
import main.XMarksTheSpot;

import static helpers.Interfaces.*;
import static main.XMarksTheSpot.*;


public class StartQuest extends Task {
    Tile WizardTile = new Tile(123, 123, 123);
    Rectangle WizardLumby = new Rectangle(123,123);

    public boolean activate() {
        return !Queststarted;
    }

    @Override
    public boolean execute() {
        if (WalkToWizard()) {
            Rectangle chatboxRect = Chatbox.findChatboxMenu();

            if (chatboxRect != null) {

            }
        }

        
        // Task done
        XMarksTheSpot.Queststarted = true;

        return false;
    }


    private boolean WalkToWizard() {
        if (!Player.atTile(WizardTile)) {

            if (!Walker.isReachable(WizardTile)) {
                Walker.webWalk(WizardTile);
            } else {
                Walker.step(WizardTile);
            }
            return false;
        } else return true;

    }

}
