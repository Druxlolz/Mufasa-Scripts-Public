package tasks;

import helpers.utils.Tile;
import main.AnvilSmither;

import utils.Task;
import static helpers.Interfaces.*;

import java.awt.Rectangle;






public class Transport extends Task {

    Tile BankTile = new Tile(400,400,0);
    Rectangle BankSpot = new Rectangle(400, 400,400,400);

    Tile AnvilTile = new Tile(400,400,0);
    Rectangle AnvilSpot = new Rectangle(400, 400,400,400);

    
    public boolean activate() {
        if (Smith.SmithingStarted && !Inventory.contains(AnvilSmither.selectedBar, 0.8)) {
            Smith.SmithingStarted = false; 
        }
        
        if (Bank.isOpen()) {return false;}
        if (interfaces.smithingIsOpen()) {return false;}
        if (Smith.SmithingStarted) { 
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean execute() {
        if (Inventory.contains(AnvilSmither.barItemId, 0.8)) {
            if (Player.atTile(AnvilTile)) {
                Client.tap(AnvilSpot);
                Condition.wait(()-> interfaces.smithingIsOpen(), 350,5);
            } else {
                Walker.step(AnvilTile);
                Condition.wait(()-> Player.atTile(AnvilTile), 350,5);
                if (Player.atTile(AnvilTile)) {
                    Client.tap(AnvilSpot);
                    Condition.wait(()-> interfaces.smithingIsOpen(), 350,5);
                }
            }
        } else {
            if (Player.atTile(BankTile)) {
                Client.tap(BankSpot);
                Condition.wait(()-> Bank.isOpen(), 350,5);
            } else {
                Walker.step(BankTile);
                Condition.wait(()-> Player.atTile(BankTile), 350,5);
                if (Player.atTile(BankTile)) {
                    Client.tap(BankSpot);
                    Condition.wait(()-> Bank.isOpen(), 350,5);
                }
            }
        }
        return false;
    }
}
