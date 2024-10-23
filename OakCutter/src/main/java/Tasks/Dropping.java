package Tasks;

import utils.Task;
import static helpers.Interfaces.Logger;

import java.util.Random;

import static helpers.Interfaces.Inventory;



public class Dropping extends Task {
    private int LogsToBank;


    public int Rsleep(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;  // Returns a random value between min and max
    }
    
    public Dropping(int LogsToBank) {
        this.LogsToBank = LogsToBank;
    }

    
    @Override
    public boolean activate() {
        // Have your conditions to activate this task here

        Logger.log("Checking if we should drop logs");
        if (Inventory.isFull()) {

            return true;
        }
        return false;
    }

    @Override
    public boolean execute() {
        Inventory.tapAllItems(LogsToBank, 0.9);
        Logger.log("Dropping Logs!");
        return false;
    }
}