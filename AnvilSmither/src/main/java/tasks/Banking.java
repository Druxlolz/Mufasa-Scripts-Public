package tasks;


import utils.Task;

import static helpers.Interfaces.*;

import java.awt.Color;
import java.awt.Rectangle;

import main.AnvilSmither;

public class Banking extends Task {
    Color BankBoothColor = Color.decode("#65655b");
    Rectangle BankBooth = new Rectangle(192, 173, 344, 337);
    

    public boolean activate() {
        if (Bank.isOpen()) { 
            return true;
        }
       return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Banking - Triggered");
        if (Inventory.isFull()) {
            Bank.close();
            Condition.wait(()-> !Bank.isOpen(), 350,5);
        } else {
            Bank.withdrawItem(AnvilSmither.barItemId, 0);
            Condition.wait(()-> Inventory.isFull(), 350,5);
            if (Inventory.isFull()) {
                Bank.close();
                Condition.wait(()-> !Bank.isOpen(), 350,5);
            }
        }
        return false;
    }
}
