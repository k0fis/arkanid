# Arkanoid - Analýza projektu

Stav k březnu 2026.

## Tech stack

| Vrstva | Technologie |
|--------|-------------|
| Jazyk | Java 17 |
| Engine | LibGDX 1.14.0 |
| Build | Gradle (multi-module) |
| Web | gdx-teavm 1.4.0 |
| Desktop | LWJGL3 + ANGLE |
| Deploy | kofis.eu/kuba/gm005/ |

## Architektura

Custom ECS (Entity-Component-System):

**10 komponent:**
- PositionComponent, VelocityComponent, SizeComponent, RenderComponent
- PaddleComponent (lives, speed), BallComponent (marker)
- BrickComponent (hitPoints, surpriseIndex)
- ParticleComponent (lifetime, age)
- SurpriseComponent (inx: 1-3), SurpriseActiveComponent (timer)

**9 systémů:**
- RenderSystem, MovementSystem, CollisionSystem
- PaddleInputSystem (klávesnice + touch/myš)
- WorldBoundsSystem (odrazy, ztráta života)
- LevelCheckerSystem (všechny cihly zničeny → nový level)
- ParticleSystem, SurpriseActiveSystem, BrickTextAnimationSystem

## Herní mechaniky

- **Pálka:** 80×16, speed 300, lives 3, input klávesnice + touch
- **Míč:** 12×12, velocity 150/150, odrazy od zdí/pálky/cihel
- **Cihly:** 48×20, HP 1-4 (random), layout random 3-7 řad × ~12 sloupců
- **Powerupy (3 typy):** 20% šance z cihly, trvání 60s
  1. Větší pálka (+20px)
  2. Rychlejší pálka (+50 speed)
  3. Extra míč (spawn 2. míč)
- **Levely:** zničit všechny cihly → animace → nový level (bez progrese obtížnosti)
- **Částice:** 12 particles na zásah cihly

## Současný stav

### Co funguje
- Základní gameplay (pálka, míč, cihly, kolize)
- Powerupy (3 typy)
- Levely (random zdi, auto-reset)
- Lives systém (3 životy)
- Zvuky + hudba
- Desktop + web build
- Touch/myš input

### Co chybí
- **Score systém** - žádné bodování, jen životy
- **Obrazovky** - existuje POUZE GameScreen (žádný MainScreen, GameOverScreen, LeaderboardScreen)
- **BaseScreen** - žádná base třída pro screen management
- **Leaderboard integrace** - žádný ScoreClient, game.properties, API
- **CI/CD** - žádný GitHub Actions workflow
- **Deploy script** - jen Gradle task `remoteInstall` (SSH)
- **Navigace** - nelze se vrátit do menu, hra jen resetuje

## Score systém - TODO

Momentálně: `PaddleComponent.lives` (3), zobrazeno jako "Lives: N".
Žádné body za cihly, žádné score.

Návrh bodování:
- Cihla HP 1: 100 bodů
- Cihla HP 2: 200 bodů
- Cihla HP 3: 300 bodů
- Cihla HP 4: 500 bodů
- Powerup sebrání: 50 bodů
- Level bonus: zbývající životy × 500

## Klíčové soubory

```
core/src/main/java/kfs/arkanoid/
├── KfsMain.java              # extends Game, spouští GameScreen
├── GameScreen.java           # JEDINÁ obrazovka, HUD + world
├── World.java                # Entity factory, setup, level management
├── ecs/
│   ├── KfsWorld.java         # ECS registr (entity, components, systems)
│   ├── Entity.java           # ID holder
│   ├── KfsComp.java          # Component interface
│   └── KfsSystem.java        # System interface
├── comp/                     # 10 komponent
├── sys/                      # 9 systémů
└── outp/
    └── MusicManager.java     # Hudba + SFX
```

## Plán integrace

1. [ ] Přidat score do PaddleComponent (nebo nový ScoreComponent)
2. [ ] Bodování v CollisionSystem při zásahu/zničení cihly
3. [ ] Zobrazení score v GameScreen HUD
4. [ ] Vytvořit BaseScreen (vzor z Space Invaders/River Raid)
5. [ ] Vytvořit MainScreen s PLAY + LEADERBOARD + HI-SCORE
6. [ ] Vytvořit GameOverScreen se state machine + letter picker
7. [ ] Vytvořit LeaderboardScreen
8. [ ] Přidat ScoreClient.java + game.properties
9. [ ] GameScreen.show() → `Gdx.input.setInputProcessor(null)` (ghost button fix)
10. [ ] Přidat .github/workflows/build.yml (CI/CD)
11. [ ] Přidat deploy-arkanoid.sh
12. [ ] Vytvořit GitHub repo, push, ověřit CI
