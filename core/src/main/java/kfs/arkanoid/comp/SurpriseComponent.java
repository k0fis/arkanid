package kfs.arkanoid.comp;

import kfs.arkanoid.ecs.KfsComp;

public class SurpriseComponent implements KfsComp {
    public final int inx;

    public SurpriseComponent(int inx) {
        this.inx = inx;
    }
}
