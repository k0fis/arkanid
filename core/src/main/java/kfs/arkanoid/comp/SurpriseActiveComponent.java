package kfs.arkanoid.comp;

import kfs.arkanoid.ecs.KfsComp;

public class SurpriseActiveComponent implements KfsComp {
    public final int inx;
    public final float timeMax;
    public float time;
    public boolean init;

    public SurpriseActiveComponent(int inx) {
        this(inx, 60f);
    }

    public SurpriseActiveComponent(int inx, float timeMax) {
        this.inx = inx;
        this.timeMax = timeMax;
        this.time = 0f;
        this.init = false;
    }
}
