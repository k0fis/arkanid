package kfs.arkanoid.comp;

import kfs.arkanoid.ecs.KfsComp;

public class BrickComponent implements KfsComp {
    public int hitPoints;


    public BrickComponent() {
        this(1);
    }

    public BrickComponent(int hitPoints) {
        this.hitPoints = hitPoints;
    }
}
