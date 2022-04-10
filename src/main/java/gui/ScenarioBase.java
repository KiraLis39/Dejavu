package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ScenarioBase {
    protected enum VARIANTS {
        NEXT(-1),
        VAR_ONE(0),
        VAR_TWO(1),
        VAR_THREE(2),
        VAR_FOUR(3),
        VAR_FIVE(4),
        VAR_SIX(5);

        int index;

        VARIANTS(int index) {
           this.index = index;
        }

        int getIndex() {
            return this.index;
        }
    }
    protected final Random rand = new Random();
    protected boolean isChoice;

    protected List<String> lines;
    protected ArrayList<String> varsList;
}
