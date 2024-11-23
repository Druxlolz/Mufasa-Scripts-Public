package tasks;

import utils.Task;
import java.util.Random;
import static helpers.Interfaces.*;

public class Antiban extends Task {
    private final RandomTimer randomTimer = new RandomTimer();

    @Override
    public boolean activate() {
        // Antiban should not activate if the bank is open
        if (Bank.isOpen()) {
            return false;
        }
        // Only activate when the random timer triggers
        return randomTimer.shouldTrigger();
    }

    @Override
    public boolean execute() {
        // Logic to execute when the timer triggers
        Logger.log("Antiban - Triggered");

        if (new Random().nextBoolean()) {
            // Sleep for a random time
            int randomSleepTime = getRandomSleepTime(2000, 10000); // 2 to 10 seconds
            Condition.sleep(randomSleepTime);
            Logger.log("Antiban: Slept for " + randomSleepTime + " ms.");
            return false;
        } else {
            // Perform tab swapping
            Tabswapping();
        }

        return false; // Task executed successfully
    }

    public static boolean Tabswapping() {
        int randomSleepTime = getRandomSleepTime(1500, 10000); // 1.5 to 10 seconds
        if (new Random().nextBoolean()) {
            // Open inventory tab
            if (!GameTabs.isInventoryTabOpen()) {
                Logger.log("Antiban: Opening Inventory tab");
                GameTabs.openInventoryTab();
                Condition.sleep(randomSleepTime);
            }
        } else {
            // Open stats tab
            if (!GameTabs.isStatsTabOpen()) {
                Logger.log("Antiban: Opening Stats tab");
                GameTabs.openStatsTab();
                Condition.sleep(randomSleepTime);
            }
        }
        return false;
    }

    // Utility method to generate random sleep times
    private static int getRandomSleepTime(int minMillis, int maxMillis) {
        return minMillis + new Random().nextInt(maxMillis - minMillis + 1);
    }

        // RandomTimer inner class
    private static class RandomTimer {
        private long nextTriggerTime;
        private long lastLogTime; // Tracks the last log time
        private static final int MIN_INTERVAL_MINUTES = 3; // Minimum interval in minutes
        private static final int MAX_INTERVAL_MINUTES = 10; // Maximum interval in minutes
        private static final long LOG_INTERVAL_MS = 30000; // Log interval in milliseconds (30 seconds)

        public RandomTimer() {
            setNextTriggerTime();
            lastLogTime = 0; // Initialize lastLogTime
        }

        // Sets the next random trigger time between 5-10 minutes
        private void setNextTriggerTime() {
            long randomInterval = (MIN_INTERVAL_MINUTES + new Random().nextInt(MAX_INTERVAL_MINUTES - MIN_INTERVAL_MINUTES + 1)) * 60 * 1000L;
            nextTriggerTime = System.currentTimeMillis() + randomInterval;
            Logger.log("Antiban: Time remaining until trigger: " + (randomInterval / 1000) + " seconds.");
        }

        // Checks if the random timer should trigger
        public boolean shouldTrigger() {
            long currentTime = System.currentTimeMillis();
            long timeUntilTrigger = nextTriggerTime - currentTime;

            if (timeUntilTrigger <= 0) {
                Logger.log("Antiban: Timer triggered!");
                setNextTriggerTime(); // Reset for next interval
                return true;
            }

            // Log the time remaining only if 5 seconds have passed since the last log
            if (currentTime - lastLogTime >= LOG_INTERVAL_MS) {
                Logger.log("Antiban: Timer not triggered yet. Time until trigger: " + (timeUntilTrigger / 1000) + " seconds.");
                lastLogTime = currentTime; // Update the last log time
            }

            return false;
        }
    }


}
