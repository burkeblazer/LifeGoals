package bblazer.com.lifegoals;

import bblazer.com.lifegoals.Objects.Food.MacroRatio;

/**
 * Created by bblazer on 4/20/16.
 */
public class WeightLossWeek {
    private String weekStr;
    private MacroRatio currentMacroRatio;

    public String getWeekStr() {
        return weekStr;
    }

    public void setWeekStr(String weekStr) {
        this.weekStr = weekStr;
    }

    public MacroRatio getCurrentMacroRatio() {
        return currentMacroRatio;
    }

    public void setCurrentMacroRatio(MacroRatio currentMacroRatio) {
        this.currentMacroRatio = currentMacroRatio;
    }
}
