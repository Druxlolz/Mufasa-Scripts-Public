package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.MapChunk;
import helpers.utils.OptionType;
import helpers.utils.Tile;
import tasks.*;
import utils.*;
import java.util.Random;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "Public Miner",
        description = "Mines ores in different places. Feel free to contribute: https://github.com/Moeinich/Public-Scripts",
        version = "1.241",
        guideLink = "https://wiki.mufasaclient.com/docs/publicminer/",
        categories = {ScriptCategory.Mining}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Crab spot",
                        description = "Which crab spot to use",
                        defaultValue = "4 Spot",
                        allowedValues = {
                                @AllowedValue(optionName = "4 Spot"),
                                @AllowedValue(optionName = "3 Spot"),
                                @AllowedValue(optionName = "2 Spot"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Use Food?",
                        description = "Choose which food to use during combat",
                        defaultValue = "Shark",
                        allowedValues = {
                                @AllowedValue(optionIcon = "385", optionName = "Shark"),
                                @AllowedValue(optionIcon = "7946", optionName = "Monkfish"),
                                @AllowedValue(optionIcon = "373", optionName = "Swordfish"),
                                @AllowedValue(optionIcon = "379", optionName = "Lobster"),
                                @AllowedValue(optionIcon = "6705", optionName = "Baked Potato"),
                                @AllowedValue(optionIcon = "3144", optionName = "Karambwan"),
                                @AllowedValue(optionIcon = "391", optionName = "Manta Ray")
                        },
                        optionType = OptionType.STRING
                ),

                @ScriptConfiguration(
                        name = "Use Potions?",
                        description = "Select a potion to use during combat",
                        defaultValue = "Super Combat Potion",
                        allowedValues = {
                                @AllowedValue(optionIcon = "12695", optionName = "Super Combat Potion"),
                                @AllowedValue(optionIcon = "2440", optionName = "Super Str & Atk Potion"),
                                @AllowedValue(optionIcon = "2444", optionName = "Ranging Potion"),
                                @AllowedValue(optionIcon = "2436", optionName = "Super Attack Potion"),
                                @AllowedValue(optionIcon = "2440", optionName = "Super Strength Potion"),
                                @AllowedValue(optionIcon = "2440", optionName = "Super Defence Potion")
                        },
                        optionType = OptionType.STRING
                ),

                @ScriptConfiguration(
                        name = "Eat at",
                        description = "Below selected value to eat food.",
                        defaultValue = "10",
                        optionType = OptionType.INTEGER_SLIDER,
                        minMaxIntValues = {1, 99}
                ),
                @ScriptConfiguration(
                        name = "Boost below",
                        description = "Will boost stats when below selected value",
                        defaultValue = "45",
                        optionType = OptionType.INTEGER_SLIDER,
                        minMaxIntValues = {10, 130}
                ),
                @ScriptConfiguration(
                        name = "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings? The script will only worldhop when other players are nearby",
                        defaultValue = "true",
                        optionType = OptionType.WORLDHOPPER
                ),
        }
)

public class SandcrabExtended extends AbstractScript {
        private static final Random random = new Random();
        private long lastIdleCheckTime = 0;
        public static boolean isTrulyIdleState = false;

        List<Task> SandcrabTask = Arrays.asList(
                new FoodPot(),
                new idle(),
                new Move(),
                new Bank()
        );
    
        public static int boostBelow;
        public static int eatAt;
        public static String crabSpot;
        public static String selectedFood;
        public static String selectedPotion;
        public static int foodID;
        public static int potionID;
        public static String hopProfile;
        public static Boolean hopEnabled;
        public static Boolean useWDH;
        public static List<Integer> potionDoseIDs;
        public static Tile selectedCrabSpot;

            // The Rsleep method to generate a random sleep time between min and max
        public static int Rsleep(int min, int max) {
                return random.nextInt((max - min) + 1) + min; // Returns a random value between min and max
        }
    
        @Override
        public void onStart(){
            Map<String, String> configs = getConfigurations();
            boostBelow = Integer.valueOf(configs.get("Boost below"));
            eatAt = Integer.valueOf(configs.get("Eat at"));
            crabSpot = configs.get("Crab spot");
            selectedFood = configs.get("Use Food?");
            foodID = getFoodID(selectedFood);
            selectedPotion = configs.get("Use Potions?");
            Map<String, List<Integer>> potionDoseIDs = getPotionDoseIDs(selectedPotion);
            hopProfile = configs.get("Use world hopper?");
            hopEnabled = Boolean.valueOf(configs.get("Use world hopper?.enabled"));
            useWDH = Boolean.valueOf(configs.get("Use world hopper?.useWDH"));
    
            // Set up the map chunk if needed
            MapChunk mapChunk = new MapChunk(new String[] { "25-153", "26-153" }, "0");
            Walker.setup(mapChunk);
    
            // Assign selectedCrabSpot based on the selected crabSpot option
            if (crabSpot.equals("4 Spot")) {
                selectedCrabSpot = new Tile(6715, 39037, 0);
            } else if (crabSpot.equals("3 Spot")) {
                selectedCrabSpot = new Tile(6671, 39077, 0);
            } else if (crabSpot.equals("2 Spot")) {
                selectedCrabSpot = new Tile(6679, 39049, 0);
            }
    
            // Log the selectedCrabSpot for verification (optional)
            Logger.log("Selected crab spot tile: " + selectedCrabSpot);
        }

    
        @Override
        public void poll() {
                Condition.sleep(Rsleep(25, 100));
                updateTrulyIdleState();
                for (Task task : SandcrabTask) {
                        if (task.activate()) {
                                task.execute();
                        return;
                        }
                }
        }

        private static Map<String, List<Integer>> getPotionDoseIDs(String potionName) {
                switch (potionName) {
                    case "Super Combat Potion":
                        return Map.of("Super Combat", List.of(12695, 12697, 12699, 12701));
                    case "Ranging Potion":
                        return Map.of("Ranging", List.of(2444, 171, 169, 2434));
                    case "Super Attack Potion":
                        return Map.of("Super Attack", List.of(2436, 145, 147, 149));
                    case "Super Strength Potion":
                        return Map.of("Super Strength", List.of(2440, 157, 159, 161));
                    case "Super Str & Atk Potion":
                        return Map.of(
                            "Super Strength", List.of(2440, 157, 159, 161),
                            "Super Attack", List.of(2436, 145, 147, 149)
                        );
                    default:
                        return Map.of(); // Empty map if no match
                }
        }
        
    
        // Retrieve ID for selected food
        private int getFoodID(String foodName) {
                switch (foodName) {
                    case "Shark":
                        return 385;
                    case "Monkfish":
                        return 7946;
                    case "Swordfish":
                        return 373;
                    case "Lobster":
                        return 379;
                    case "Baked Potato":
                        return 6705;
                    case "Karambwan":
                        return 3144;
                    case "Manta Ray":
                        return 391;
                    default:
                        return -1;
                }
            }
    
        // Drink the highest available dose of the selected potion
        public static void drinkAttackPotion() {
                // Early exit if the script is stopping or it's time for a break
                if (Script.isScriptStopping() || Script.isTimeForBreak()) {
                    return;
                }
            
                if (!GameTabs.isInventoryTabOpen()) {
                    GameTabs.openInventoryTab();
                    Condition.wait(() -> GameTabs.isInventoryTabOpen(), 250, 10);
                }
            
                // Get the list of doses for attack potions
                List<Integer> attackPotionDoses = getPotionDoseIDs(selectedPotion).get("Super Attack");
            
                if (attackPotionDoses != null) {
                    for (int doseID : attackPotionDoses) {
                        // Early exit if the script is stopping or it's time for a break
                        if (Script.isScriptStopping() || Script.isTimeForBreak()) {
                            return;
                        }
            
                        if (Inventory.contains(doseID, 0.9)) {
                            Inventory.tapItem(doseID);
                            Logger.log("Drank Attack Potion.");
                            Condition.sleep(Rsleep(140, 240));
                            return;
                        }
                    }
                } else {
                    Logger.log("No Attack Potion found in the selected potion configuration.");
                }
            }
            
            public static void drinkStrengthPotion() {
                // Early exit if the script is stopping or it's time for a break
                if (Script.isScriptStopping() || Script.isTimeForBreak()) {
                    return;
                }
            
                if (!GameTabs.isInventoryTabOpen()) {
                    GameTabs.openInventoryTab();
                    Condition.wait(() -> GameTabs.isInventoryTabOpen(), 250, 10);
                }
            
                // Get the list of doses for strength potions
                List<Integer> strengthPotionDoses = getPotionDoseIDs(selectedPotion).get("Super Strength");
            
                if (strengthPotionDoses != null) {
                    for (int doseID : strengthPotionDoses) {
                        // Early exit if the script is stopping or it's time for a break
                        if (Script.isScriptStopping() || Script.isTimeForBreak()) {
                            return;
                        }
            
                        if (Inventory.contains(doseID, 0.9)) {
                            Inventory.tapItem(doseID);
                            Logger.log("Drank Strength Potion.");
                            Condition.sleep(Rsleep(140, 240));
                            return;
                        }
                    }
                } else {
                    Logger.log("No Strength Potion found in the selected potion configuration.");
                }
            }

            public static void drinkSuperCombatPotion() {
                // Early exit if the script is stopping or it's time for a break
                if (Script.isScriptStopping() || Script.isTimeForBreak()) {
                    return;
                }
            
                if (!GameTabs.isInventoryTabOpen()) {
                    GameTabs.openInventoryTab();
                    Condition.wait(() -> GameTabs.isInventoryTabOpen(), 250, 10);
                }
            
                // Get the list of doses for strength potions
                List<Integer> strengthPotionDoses = getPotionDoseIDs(selectedPotion).get("Super Combat");
            
                if (strengthPotionDoses != null) {
                    for (int doseID : strengthPotionDoses) {
                        // Early exit if the script is stopping or it's time for a break
                        if (Script.isScriptStopping() || Script.isTimeForBreak()) {
                            return;
                        }
            
                        if (Inventory.contains(doseID, 0.9)) {
                            Inventory.tapItem(doseID);
                            Logger.log("Drank Super Combat Potion.");
                            Condition.sleep(Rsleep(140, 240));
                            return;
                        }
                    }
                } else {
                    Logger.log("No Strength Potion found in the selected potion configuration.");
                }
            }

            public static void drinkRangingPotion() {
                // Early exit if the script is stopping or it's time for a break
                if (Script.isScriptStopping() || Script.isTimeForBreak()) {
                    return;
                }
            
                if (!GameTabs.isInventoryTabOpen()) {
                    GameTabs.openInventoryTab();
                    Condition.wait(() -> GameTabs.isInventoryTabOpen(), 250, 10);
                }
            
                // Get the list of doses for strength potions
                List<Integer> strengthPotionDoses = getPotionDoseIDs(selectedPotion).get("Ranging");
            
                if (strengthPotionDoses != null) {
                    for (int doseID : strengthPotionDoses) {
                        // Early exit if the script is stopping or it's time for a break
                        if (Script.isScriptStopping() || Script.isTimeForBreak()) {
                            return;
                        }
            
                        if (Inventory.contains(doseID, 0.9)) {
                            Inventory.tapItem(doseID);
                            Logger.log("Drank Ranging Potion.");
                            Condition.sleep(Rsleep(140, 240));
                            return;
                        }
                    }
                } else {
                    Logger.log("No Strength Potion found in the selected potion configuration.");
                }
            }
            










        private void updateTrulyIdleState() {
                // Only check every 200ms or longer, to avoid excessive CPU use
                if (System.currentTimeMillis() - lastIdleCheckTime >= 200) {
                    isTrulyIdleState = isTrulyIdle();
                    lastIdleCheckTime = System.currentTimeMillis();
                }
        }







        public static boolean isTrulyIdle() {
                long startTime = System.currentTimeMillis();
                boolean pixelShiftStayedLow = true;  // Assume idle unless proven otherwise
                boolean consistentlyIdle = true;  // Track if Player.isIdle() stays true
            
                int consecutiveIdleChecks = 0;  // Track how many consecutive times the player was idle
                int consecutiveZeroShiftChecks = 0;  // Track consecutive 0.0 pixel shifts
                int minIdleChecks = 4;  // Require at least 5 consecutive checks of idle status
                int maxZeroShiftChecks = 6;  // Require at least 5 consecutive pixel shifts of 0.0
            
                while (System.currentTimeMillis() - startTime < 1000 && !Script.isScriptStopping() && !Script.isTimeForBreak()) {  // Track for 2 seconds
                    double pixelShift = Player.currentPixelShift();
                    boolean isIdle = Player.isIdle();
            
                    // Log the current pixel shift and idle status for debugging purposes
                    // Logger.log("Current Pixel Shift: " + pixelShift + " | IsIdle: " + isIdle);
            
                    // If the pixel shift is higher than 700, reset the pixelShiftStayedLow flag
                    if (pixelShift > 700) {
                        pixelShiftStayedLow = false;  // The player is likely active
                    }
            
                    // If Player.isIdle() is false, reset the consistentlyIdle flag
                    if (!isIdle) {
                        consistentlyIdle = false;  // Player showed activity
                    } else {
                        // Increment the consecutive idle checks
                        consecutiveIdleChecks++;
                    }
            
                    // Track consecutive pixel shifts of 0.0
                    if (pixelShift == 0.0) {
                        consecutiveZeroShiftChecks++;
                    } else {
                        consecutiveZeroShiftChecks = 0;  // Reset if we get a non-zero shift
                    }
            
                    // Sleep for a short interval before checking again
                    Condition.sleep(50);  // Sleep for 100ms between checks
                }
            
                // After 2 seconds, we consider the player truly idle if either:
                // 1. Player.isIdle() was true for at least `minIdleChecks` consecutive times
                // 2. Pixel shift stayed low or at zero for the whole time (pixelShiftStayedLow)
                // 3. Pixel shift stayed at 0.0 for `maxZeroShiftChecks` consecutive checks
                return (consecutiveIdleChecks >= minIdleChecks && pixelShiftStayedLow) || consecutiveZeroShiftChecks >= maxZeroShiftChecks;
            }
    }
