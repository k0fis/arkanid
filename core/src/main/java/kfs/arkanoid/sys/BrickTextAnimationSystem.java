package kfs.arkanoid.sys;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kfs.arkanoid.World;
import kfs.arkanoid.ecs.KfsSystem;

import java.util.HashMap;
import java.util.Map;

public class BrickTextAnimationSystem implements KfsSystem {
    private final World world;
    private Map<Character, String[]> fontMap;
    private float brickW = 12, brickH = 8;

    private float timer = 0f;
    private float buildSpeed = 0.05f; // delay between bricks
    private String message = "GAME OVER";

    public BrickTextAnimationSystem(World world) {
        this.world = world;
        fontMap = makeBrickFont();
    }

    public void setMessage(String message) {
        this.message = message;
        timer = 0f;
    }

    public boolean isTimeout(float timeout) {
        boolean res = timer > timeout;
        if (res) {
            timer = 0;
        }
        return res;
    }

    @Override
    public void update(float delta) {
        timer += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        drawAnimatedBrickText(batch, message, 40, 400);
    }

    private void drawAnimatedBrickText(SpriteBatch batch, String text, float startX, float startY) {
        float x = startX;
        int brickIndex = 0;
        int visibleCount = (int)(timer / buildSpeed);

        for (char c : text.toUpperCase().toCharArray()) {
            if (c == ' ') {
                x += brickW * 4;
                continue;
            }

            String[] pattern = fontMap.get(c);
            if (pattern != null) {
                for (int row = 0; row < pattern.length; row++) {
                    for (int col = 0; col < pattern[row].length(); col++) {
                        String textureName = "brick_"+ ((brickIndex%2==0)?"1":"2");
                        if (pattern[row].charAt(col)=='1') {
                            if (brickIndex++ < visibleCount) {
                                float drawX = x + col * brickW;
                                float drawY = startY - row * brickH;
                                batch.draw(world.getTexture(textureName), drawX, drawY, brickW, brickH);
                            } else {
                                return; // stop drawing once we hit the current build limit
                            }
                        }
                    }
                }
                x += (pattern[0].length() + 1) * brickW;
            }
        }
    }

    private Map<Character, String[]> makeBrickFont() {
        Map<Character, String[]> f = new HashMap<>();

        f.put('A', new String[]{
            "0110",
            "1001",
            "1111",
            "1001",
            "1001"
        });
        f.put('E', new String[]{
            "1111",
            "1000",
            "1110",
            "1000",
            "1111"
        });
        f.put('G', new String[]{
            "0111",
            "1000",
            "1011",
            "1001",
            "0110"
        });
        f.put('M', new String[]{
            "10001",
            "11011",
            "10101",
            "10001",
            "10001"
        });
        f.put('O', new String[]{
            "0110",
            "1001",
            "1001",
            "1001",
            "0110"
        });
        f.put('V', new String[]{
            "10001",
            "10001",
            "10001",
            "01010",
            "00100"
        });
        f.put('R', new String[]{
            "1110",
            "1001",
            "1110",
            "1010",
            "1001"
        });
        f.put('L', new String[]{
            "1000",
            "1000",
            "1000",
            "1000",
            "1111"
        });
        f.put('N', new String[]{
            "1001",
            "1101",
            "1011",
            "1011",
            "1001"
        });
        f.put('D', new String[]{
            "1110",
            "1001",
            "1001",
            "1001",
            "1110"
        });

        return f;
    }

}
