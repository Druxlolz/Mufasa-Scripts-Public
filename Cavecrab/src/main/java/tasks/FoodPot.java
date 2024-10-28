package tasks;


import main.SandcrabExtended;
import utils.Task;

import static helpers.Interfaces.*;
import static helpers.utils.Skills.*;




public class FoodPot extends Task {
    int MyHP = 0;

    @Override
    public boolean activate() {
        int effectiveAttackLevel = Stats.getEffectiveLevel(ATTACK);
        Logger.log("Effective Attack Level: " + effectiveAttackLevel);
        MyHP = Player.getHP();
        if (MyHP <= SandcrabExtended.eatAt) {
            Logger.log("We should eat now!");
            return true;
        }

        if (!GameTabs.isStatsTabOpen()) {
            GameTabs.openStatsTab();
            Condition.wait(() -> GameTabs.isStatsTabOpen(), 250, 50);
        }

        if (GameTabs.isStatsTabOpen() && Stats.getEffectiveLevel(ATTACK) <= SandcrabExtended.boostBelow) {
            Logger.log("We should be boosting Attack!!!");
            return true;
        }

        if (GameTabs.isStatsTabOpen() && Stats.getEffectiveLevel(STRENGTH) <= SandcrabExtended.boostBelow) {
            Logger.log("We should be boosting Strenght!!!");
            return true;
        }
        Logger.log("No boosting required atm");
        return false;
    }

    @Override
    public boolean execute() {
        if (MyHP <= SandcrabExtended.eatAt) {
            if (!GameTabs.isInventoryTabOpen()) {
                GameTabs.openInventoryTab();
                Condition.wait(() -> GameTabs.isInventoryTabOpen(),200, 15);
            }

            Logger.log("Tapping food.");
            Inventory.tapItem(SandcrabExtended.foodID, 0.7);
            Condition.sleep(SandcrabExtended.Rsleep(80, 180));
        }
        if (Stats.getEffectiveLevel(ATTACK) <= SandcrabExtended.boostBelow) {
            Logger.log("Boosting Attack Level");
            SandcrabExtended.drinkAttackPotion();
            Condition.sleep(SandcrabExtended.Rsleep(80, 180));
        }

        if (Stats.getEffectiveLevel(STRENGTH) <= SandcrabExtended.boostBelow && SandcrabExtended.selectedPotion == "Super Strength Potion") {
            Logger.log("Boosting Strength Level");
            SandcrabExtended.drinkStrengthPotion();
            Condition.sleep(SandcrabExtended.Rsleep(80, 180));
        }
        if (Stats.getEffectiveLevel(ATTACK) <= SandcrabExtended.boostBelow && Stats.getEffectiveLevel(STRENGTH) <= SandcrabExtended.boostBelow && SandcrabExtended.selectedPotion == "Super Str & Atk Potion") {
            Logger.log("Boosting Strenght and Attack");
            SandcrabExtended.drinkSuperCombatPotion();
            Condition.sleep(SandcrabExtended.Rsleep(80, 180));
        }

        return false;
    }

}
