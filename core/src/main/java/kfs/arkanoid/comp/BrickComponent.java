package kfs.arkanoid.comp;

import kfs.arkanoid.ecs.KfsComp;

public class BrickComponent implements KfsComp {
    public int hitPoints;
    public final int surpriseIndex;

    public BrickComponent(int hitPoints, int surpriseIndex) {
        this.hitPoints = hitPoints;
        this.surpriseIndex = surpriseIndex;
    }
}
