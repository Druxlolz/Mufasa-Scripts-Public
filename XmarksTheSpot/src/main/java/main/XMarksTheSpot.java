package main;

import helpers.*;
// import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.MapChunk;
import helpers.utils.OptionType;

import tasks.*;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;


@ScriptManifest(
        name = "Quest-XMarksTheSpot",
        description = "Completes the quest XMarksTheSpot",
        version = "1.00",
        guideLink = "",
        categories = {ScriptCategory.Misc, ScriptCategory.Ironman}
        //skipClientSetup = true,
        //skipZoomSetup = true
)

@ScriptConfiguration.List(
    value = {
        @ScriptConfiguration(
            name = "Use Glory?",
            description = "Would you like to use glory for faster travel?",
            defaultValue = "false",
            optionType = OptionType.BOOLEAN
        ),

        @ScriptConfiguration(
            name = "Run anti-ban",
            description = "Would you like to run anti-ban features?",
            defaultValue = "true",
            optionType = OptionType.BOOLEAN
        ),

        @ScriptConfiguration(
            name = "Run extended anti-ban",
            description = "Would you like to run additional, extended anti-ban like some additional AFK patterns, on TOP of the regular anti-ban?",
            defaultValue = "false",
            optionType = OptionType.BOOLEAN
        )
    }
)

public class XMarksTheSpot extends AbstractScript {
    public static boolean UseGlory;
    public static boolean HaveRequiredItems = false;
    public static boolean Queststarted = false;
    public static boolean DigOutsideLumbyGate = false;
    public static boolean OutsideLumbyKitchen = false;
    public static boolean DraynorJail = false;
    public static boolean DraynorPig = false;
    public static boolean QuestDone = false;

    private boolean antiBan;
    private boolean extendedAntiBan;

    public static Rectangle clickToContinueRect = new Rectangle(243, 105, 148, 17);
    

    @Override
    public void onStart(){

        Map<String, String> configs = getConfigurations();
        UseGlory = Boolean.valueOf(configs.get("Use Glory?"));


        Logger.log("Preparing..");
        Condition.sleep(200, 500);
        Logger.log("0%");

        
        antiBan = Boolean.valueOf(configs.get("Run anti-ban"));
        extendedAntiBan = Boolean.valueOf(configs.get("Run extended anti-ban"));
        if (antiBan) {
            if (extendedAntiBan) {
                Logger.log("Loading in Extended Anti ban");
            } else {
                Logger.log("Loading in Antiban");
            }

            Condition.sleep(20, 300);
            Logger.log("25%");
        } else {
            Logger.log("25%");
        }

        // disable afk & break handler
        Client.disableAFKHandler();
        Logger.log("Disabled Afk Handler");
        Condition.sleep(200, 500);

        Client.disableBreakHandler();
        Logger.log("Disabled Break Handler");
        Condition.sleep(200, 500);

        Logger.log("50%");
        Condition.sleep(200, 500);


        // Set up walker chunks
        MapChunk chunks = new MapChunk(new String[]{
            "50-50",
            "48-50",
            "48-51",
            "47-51",
            "47-50"
        }, "0");

        Walker.setup(chunks);

        Logger.log("Preparing 100%");

        Condition.sleep(100, 300);
        Logger.log("Questing started");

    }

    List<Task> tutTasks = Arrays.asList(
            new StartQuest(),
            new DigOutsideLumbyGate(),
            new OutsideLumbyKitchen(),
            new DraynorJail(),
            new DraynorPig(),
            new FinishQuest()
    );

    @Override
    public void poll() {

        if (QuestDone) {
            Logger.log("X marks the spot is finished, shutting down");
            Logger.log("Thanks for the ride :)");
            Condition.sleep(300, 1200);

            Logout.logout();
            Script.stop();
        }

        //Run tasks
        for (Task task : tutTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
}
