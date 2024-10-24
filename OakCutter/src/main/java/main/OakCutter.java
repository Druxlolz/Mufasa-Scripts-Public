package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.MapChunk;
import helpers.utils.OptionType;
import helpers.utils.Tile;
import Tasks.*;
import utils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;



@ScriptManifest(
        name = "NewScript",
        description = "Automates a new activity based on the structure of Public Miner",
        version = "1.0",
        guideLink = "",
        categories = {ScriptCategory.Woodcutting}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Location",
                        description = "Which location would you like to use?",
                        defaultValue = "Draynor Village",
                        allowedValues = {
                                @AllowedValue(optionName = "Lumbridge"),
                                @AllowedValue(optionName = "Draynor Village"),
                               // @AllowedValue(optionName = "Woodcutting Guild"),
                               // @AllowedValue(optionName = "Seers"),
                               // @AllowedValue(optionName = "Isle of Souls")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Tree type",
                        description = "Which tree would you like to cut?",
                        defaultValue = "Oak",
                        allowedValues = {
                                @AllowedValue(optionIcon = "1511", optionName = "Tree"),
                                @AllowedValue(optionIcon = "1521", optionName = "Oak"),
                                @AllowedValue(optionIcon = "1519", optionName = "Willow"),
                               // @AllowedValue(optionIcon = "1517", optionName = "Maple"),
                                //@AllowedValue(optionIcon = "1515", optionName = "Yew")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Bank logs",
                        description = "Would you like to bank the logs?",
                        defaultValue = "true",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                    name = "Safespot Willow",
                    description = "Safespot at willows, intented for powercutting for lowl lvl",
                    defaultValue = "false",
                    optionType = OptionType.BOOLEAN
            ),
                @ScriptConfiguration(
                        name = "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "true",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class OakCutter extends AbstractScript {
    private boolean initialLogAcquired = false;  // Track if the first log has been gained
    private int lastItemCount = -1;
    private long lastInventoryChangeTime = 0;
    private boolean inventoryChangedRecently = false;
    public static boolean isTrulyIdleState = false;  // Field to store idle state
    private long lastIdleCheckTime = 0;  // Track last check time for `isTrulyIdle()`
    public static boolean bankingMode;
    public static String Location;
    public static String treeType;
    public static Boolean bankLogs;
    public static Boolean useSafeSpot;
    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    public static int LogsToBank;
    public String dynamicBankIdentifier;
    List<Task> woodcuttingTasks;
    public static List<Tile> targetTileList = Collections.emptyList();
    public static List<Tile> bankTileList = Collections.emptyList();  // Use this list in Bank class
    MapChunk mapChunk;



    

    @Override
    public void onStart() {
        // Setup configurations
        Map<String, String> configs = getConfigurations();
        Location = configs.get("Location");
        treeType = configs.get("Tree type");
        bankLogs = Boolean.valueOf(configs.get("Bank logs"));
        useSafeSpot = Boolean.valueOf(configs.get("Safespot Willow"));
        hopProfile = configs.get("Use world hopper?");
        hopEnabled = Boolean.valueOf(configs.get("Use world hopper?.enabled"));
        useWDH = Boolean.valueOf(configs.get("Use world hopper?.useWDH"));
        
        // Set banking mode based on bankLogs configuration
        bankingMode = bankLogs;

        switch (Location) {
            case "Lumbridge":
                mapChunk = new MapChunk(new String[] {"50-50"}, "0");
                break;
            case "Draynor Village":
                Logger.log("MoveTo Draynor Selected Mapchunk");
                if (treeType.equals("Oak")) {
                    mapChunk = new MapChunk(new String[] {"48-50"}, "0");
                    // Initialize target tiles for cutting trees
                    targetTileList = Arrays.asList(
                        new Tile(12403, 12725, 0),
                        new Tile(12403, 12721, 0),
                        new Tile(12403, 12717, 0)
                    );
                }
                if (treeType.equals("Willow")) {
                    mapChunk = new MapChunk(new String[] {"48-50", "48-51", "49-50", "47-50"}, "0");
                    // Initialize target tiles for cutting trees
                    if (!useSafeSpot) {
                        Logger.log("Tiles for NO safespot selected");
                        targetTileList = Arrays.asList(
                            new Tile(12339, 12697, 0),
                            new Tile(12347, 12689, 0),
                            new Tile(12351, 12681, 0)

                        );
                    } else {
                        Logger.log("Tile for safespot selected");
                        mapChunk = new MapChunk(new String[] {"48-50", "48-51", "49-50", "47-50"}, "0");
                        targetTileList = Arrays.asList(
                            new Tile(12355, 12653, 0)
                        );
                    }
                }

                // Initialize bank tiles
                bankTileList = Arrays.asList(
                    new Tile(12367, 12721, 0),
                    new Tile(12367, 12717, 0),
                    new Tile(12367, 12729, 0)
                );
                break;

            default:
                Logger.log("Unknown location: " + Location);
                break;
        }
        Walker.setup(mapChunk);
        setupRegionInfo();
        setupTreeType();


        // Initialize task list after LogsToBank and Location are set
        if (bankingMode) {
    
            MoveTo moveToTask = new MoveTo(targetTileList, bankTileList, bankingMode, mapChunk);
            Bank bankTask = new Bank(LogsToBank, mapChunk, Location, bankTileList);
            Woodcutting woodcuttingTask = new Woodcutting(this, Location, LogsToBank);
                woodcuttingTasks = Arrays.asList(
                    moveToTask, 
                    bankTask, 
                    woodcuttingTask
                );
            } 
        else {
            MoveTo moveToTask = new MoveTo(targetTileList, bankTileList, bankingMode, mapChunk);
            Dropping droppingTask = new Dropping(LogsToBank);
            Woodcutting woodcuttingTask = new Woodcutting(this, Location, LogsToBank);
                woodcuttingTasks = Arrays.asList(
                    moveToTask, 
                    droppingTask, 
                    woodcuttingTask
               );
        }
    }



    public int Rsleep(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;  // Returns a random value between min and max
    }
    
    
    
    @Override
    public void poll() {
        updateTrulyIdleState();
        updateInventoryChange();
        Condition.sleep(Rsleep(5, 25));

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        // Run tasks
        for (Task task : woodcuttingTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
    
    private void setupRegionInfo() {
        Logger.debugLog("Setting up region info");
        switch (Location) {
            case "Lumbridge":
                // Setup Lumbridge region-specific info
                break;
            case "Draynor Village":
                // Setup Draynor Village region-specific info
                break;
            case "Woodcutting Guild":
                // Setup Woodcutting Guild region-specific info
                break;
            case "Seers":
                // Setup Seers region-specific info
                break;
            case "Isle of Souls":
                // Setup Isle of Souls region-specific info
                break;
            default:
                Logger.log("Incorrect script setup for location.");
                break;
        }
    }
    
    private void setupTreeType() {
        Logger.debugLog("Setting up tree type info");
        switch (treeType) {
            case "Tree":
                LogsToBank = 1511; // Normal tree logs (OSRS ID)
                break;
            case "Oak":
                LogsToBank = 1521; // Oak tree logs (OSRS ID)
                break;
            case "Willow":
                LogsToBank = 1519; // Willow tree logs (OSRS ID)
                break;
            case "Maple":
                LogsToBank = 1517; // Maple tree logs (OSRS ID)
                break;
            case "Yew":
                LogsToBank = 1515; // Yew tree logs (OSRS ID)
                break;
            default:
                Logger.log("Incorrect setup for tree type.");
                Script.stop();
        }
    }

    private void updateTrulyIdleState() {
        // Only check every 200ms or longer, to avoid excessive CPU use
        if (System.currentTimeMillis() - lastIdleCheckTime >= 200) {
            isTrulyIdleState = isTrulyIdle();
            lastIdleCheckTime = System.currentTimeMillis();
        }
    }


    
    // Field to track the time of the last inventory change


    public boolean updateInventoryChange() {
        int currentItemCount = Inventory.count(LogsToBank, 0.8);  // Get the current count
        int gracePeriod = 6500;  // 3 seconds grace period
    
        // If it's time for a break or the script is stopping, return false
        if (Script.isScriptStopping() || Script.isTimeForBreak()) {
            return false;
        }
    
        // First-time initialization of item count
        if (lastItemCount == -1) {
            lastItemCount = currentItemCount;
        }
    
        // If inventory count changes, reset the grace period timer
        if (currentItemCount != lastItemCount) {
            Logger.log("Inventory change detected! Resetting grace period.");
    
            // Mark the first log as acquired once inventory changes for the first time
            initialLogAcquired = true;
    
            lastItemCount = currentItemCount;  // Update the item count
            lastInventoryChangeTime = System.currentTimeMillis();  // Reset the grace period timer
            inventoryChangedRecently = true;
            return true;
        }
    
        // If within grace period, we consider inventory changed recently
        if (inventoryChangedRecently && (System.currentTimeMillis() - lastInventoryChangeTime < gracePeriod)) {
            Logger.log("Within grace period. Considering inventory recently changed.");
            return true;
        }
    
        // If no changes and the grace period has expired
        if (System.currentTimeMillis() - lastInventoryChangeTime >= gracePeriod) {
            Logger.log("Grace period expired. No recent inventory changes detected.");
            inventoryChangedRecently = false;  // Reset flag
            return false;
        }
    
        return false;
    }


    
    

    public boolean isTrulyIdle() {
        long startTime = System.currentTimeMillis();
        boolean pixelShiftStayedZero = true;  // Assume idle until proven otherwise
        boolean isIdleFlag = false;
        
        while (System.currentTimeMillis() - startTime < 400 && !Script.isScriptStopping() && !Script.isTimeForBreak()) {  // Track over 2 seconds
            double pixelShift = Player.currentPixelShift();
            boolean isIdle = Player.isIdle();

            // Log the current pixel shift for debugging purposes
            Logger.log("Current Pixel Shift: " + pixelShift + " | IsIdle: " + isIdle);

            // If pixel shift is non-zero at any point, reset the flag for pixel shift tracking
            if (pixelShift > 0) {
                pixelShiftStayedZero = false;  // Not truly idle based on pixel shift
            }

            // If player is idle at any point, set the idle flag
            if (isIdle) {
                isIdleFlag = true;
            }

            // Sleep for a short interval before checking again
            Condition.sleep(80);  // Sleep for 200ms between checks
        }

        // After 2 seconds, we consider the player idle if either:
        // 1. Pixel shift stayed at 0 for the whole time, or
        // 2. Player.isIdle() returned true at least once
        return pixelShiftStayedZero || isIdleFlag;
    }


    
}
    
