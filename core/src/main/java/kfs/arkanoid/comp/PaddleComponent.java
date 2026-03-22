package kfs.arkanoid.comp;

import kfs.arkanoid.ecs.KfsComp;

public class PaddleComponent implements KfsComp {
    public static final int DEFAULT_LIVES = 3;
    public int lives = DEFAULT_LIVES;
    public int score = 0;
    public float speed = 300f;
}
