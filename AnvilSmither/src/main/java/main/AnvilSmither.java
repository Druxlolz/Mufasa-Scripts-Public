package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.MapChunk;
import helpers.utils.OptionType;
import helpers.utils.Skills;
import helpers.utils.SmithItems;
import tasks.Antiban;
import tasks.Banking;
import tasks.Smith;
import tasks.Transport;
import utils.Task;
import static helpers.Interfaces.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@ScriptManifest(
        name = "Anvil Smither",
        description = "Smiths bars into gear at Varrock West",
        version = "1.0",
        guideLink = "None yet",
        categories = {ScriptCategory.Smithing}
)

@ScriptConfiguration.List(
    value = {
        @ScriptConfiguration(
            name = "Use world hopper?",
            description = "Hops world based on worldhopper settings.",
            defaultValue = "1",
            optionType = OptionType.WORLDHOPPER
        ),

        @ScriptConfiguration(
            name = "Bar Type",
            description = "Which kind of bar to use?",
            defaultValue = "Bronze Bar",
            allowedValues = {
                @AllowedValue(optionIcon = "2349", optionName = "Bronze Bar"),   // Bronze Bar
                @AllowedValue(optionIcon = "2351", optionName = "Iron Bar"),    // Iron Bar
                @AllowedValue(optionIcon = "2353", optionName = "Steel Bar"),   // Steel Bar
                @AllowedValue(optionIcon = "2359", optionName = "Mithril Bar"), // Mithril Bar
                @AllowedValue(optionIcon = "2361", optionName = "Adamant Bar"), // Adamantite Bar
                @AllowedValue(optionIcon = "2363", optionName = "Rune Bar")     // Runite Bar
            },
            optionType = OptionType.STRING
        ),

        @ScriptConfiguration(
            name = "What gear to Smith",
            description = "What geartype to smith",
            defaultValue = "Dagger",
            allowedValues = {
                @AllowedValue(optionIcon = "1205", optionName = "Dagger"),
                @AllowedValue(optionIcon = "1277", optionName = "Scimitar"),
                @AllowedValue(optionIcon = "1307", optionName = "2h Sword"),
                @AllowedValue(optionIcon = "1321", optionName = "Mace"),
                @AllowedValue(optionIcon = "1351", optionName = "Axe"),
                @AllowedValue(optionIcon = "1217", optionName = "Sword"),
                @AllowedValue(optionIcon = "1422", optionName = "Warhammer"),
                @AllowedValue(optionIcon = "1375", optionName = "Battleaxe"),
                @AllowedValue(optionIcon = "1117", optionName = "Platebody"),
                @AllowedValue(optionIcon = "1075", optionName = "Platelegs"),
                @AllowedValue(optionIcon = "1087", optionName = "Plateskirt"),
                @AllowedValue(optionIcon = "1103", optionName = "Chainbody"),
                @AllowedValue(optionIcon = "1139", optionName = "Helmet"),
                @AllowedValue(optionIcon = "1173", optionName = "Square Shield"),
                @AllowedValue(optionIcon = "1189", optionName = "Kite Shield"),
                @AllowedValue(optionIcon = "4820", optionName = "Nails"),
                @AllowedValue(optionIcon = "9140", optionName = "Bolts"),
                @AllowedValue(optionIcon = "819", optionName = "Dart Tips"),
                @AllowedValue(optionIcon = "39", optionName = "Arrowtips"),
                @AllowedValue(optionIcon = "864", optionName = "Throwing Knives")
            },
            optionType = OptionType.STRING
        )        
    }
)



public class AnvilSmither extends AbstractScript {
    public static MapChunk mapChunk;
    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    public static String selectedBar;
    public static String selectedGear;
    public static int barItemId;
    public static int SmithLevel = 0;
    public static List<Task> SmithTasks;
    private static final Random random = new Random();

    @Override
    public void onStart() {
        Logger.log("Starting Anvil Smither");
        Map<String, String> configs = getConfigurations();
        selectedBar = configs.get("Bar Type");
        selectedGear = configs.get("What gear to Smith");
        mapChunk = new MapChunk(new String[] {  "49-53", }, "0");
        hopEnabled = Boolean.parseBoolean(configs.get("Use world hopper?.enabled"));
        useWDH = Boolean.parseBoolean(configs.get("Use world hopper?.useWDH"));

        SmithLevel = Stats.getRealLevel(Skills.SMITHING);
        Logger.log("Smithing level is: " + SmithLevel);
        Logger.log("Selected Bar: " + selectedBar + ", Selected Gear: " + selectedGear);

        // Initialize tasks
        SmithTasks = Arrays.asList(
            new Antiban(),
            new Smith(selectedBar, selectedGear),
            new Banking(),
            new Transport()
        );

        switch (selectedBar) {
            case "Bronze Bar":
                barItemId = 2349; // Bronze Bar ID
                break;
            case "Iron Bar":
                barItemId = 2351; // Iron Bar ID
                break;
            case "Steel Bar":
                barItemId = 2353; // Steel Bar ID
                break;
            case "Mithril Bar":
                barItemId = 2359; // Mithril Bar ID
                break;
            case "Adamant Bar":
                barItemId = 2361; // Adamantite Bar ID
                break;
            case "Rune Bar":
                barItemId = 2363; // Runite Bar ID
                break;
            default:
                barItemId = 2349; // Default to Bronze Bar ID if no valid option is selected
                break;
        }
        Logger.log("Selected Bar ID: " + barItemId);

    }

    @Override
    public void poll() {
        XpBar.getXP();
        if (Smith.SmithingStarted && !Inventory.contains(selectedBar, 0.8)) {
            Smith.SmithingStarted = false; 
        }
        
        if (hopEnabled) {
            Game.hop(hopProfile, useWDH, false); // Check if we should worldhop
        }

        for (Task task : SmithTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }



    public static SmithItems constructSmithItem(String barType, String gearType) {
        try {
            // Convert barType and gearType into the appropriate enum format
            String formattedBar = barType.toUpperCase().replace(" ", "_").replace(" BAR", "");
            String formattedGear = gearType.toUpperCase().replace(" ", "_");

            // Combine formatted strings into the expected enum format
            String combinedItem = formattedBar + "_" + formattedGear;

            // Attempt to retrieve the corresponding SmithItems enum
            return SmithItems.valueOf(combinedItem);
        } catch (IllegalArgumentException e) {
            // Handle case where the combination is invalid
            Logger.log("Invalid smithing item combination: " + barType + " + " + gearType);
            return null;
        }
    }

    public static int Rsleep(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
