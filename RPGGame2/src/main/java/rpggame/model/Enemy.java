package rpggame.model;

/**
 * A non-player enemy. Each {@link EnemyType} maps to a distinct stat block
 * and a unique special move (FR-07). Defeating an enemy yields XP and gold
 * rewards (FR-08).
 */
public class Enemy extends AbstractEntity {

    public enum EnemyType { GOBLIN, ORC, SKELETON, TROLL, DRAGON_BOSS }

    private final EnemyType type;
    private final int xpReward;
    private final int goldReward;

    public Enemy(EnemyType type) {
        super(prettyName(type), baseHealth(type), baseAttack(type), baseDefense(type));
        this.type       = type;
        this.xpReward   = baseXP(type);
        this.goldReward = baseGold(type);
    }

    public Enemy(String customName, EnemyType type) {
        this(type);
        setName(customName);          // validated mutator instead of direct field write
    }

    private static String prettyName(EnemyType t) {
        String n = t.name().replace('_', ' ').toLowerCase();
        return java.lang.Character.toUpperCase(n.charAt(0)) + n.substring(1);
    }

    private static int baseHealth(EnemyType t) {
        return switch (t) {
            case GOBLIN -> 40; case ORC -> 70; case SKELETON -> 55;
            case TROLL -> 110; case DRAGON_BOSS -> 250;
        };
    }
    private static int baseAttack(EnemyType t) {
        return switch (t) {
            case GOBLIN -> 8; case ORC -> 14; case SKELETON -> 11;
            case TROLL -> 18; case DRAGON_BOSS -> 35;
        };
    }
    private static int baseDefense(EnemyType t) {
        return switch (t) {
            case GOBLIN -> 2; case ORC -> 5; case SKELETON -> 4;
            case TROLL -> 8; case DRAGON_BOSS -> 15;
        };
    }
    private static int baseXP(EnemyType t) {
        return switch (t) {
            case GOBLIN -> 30; case ORC -> 55; case SKELETON -> 45;
            case TROLL -> 80; case DRAGON_BOSS -> 300;
        };
    }
    private static int baseGold(EnemyType t) {
        return switch (t) {
            case GOBLIN -> 10; case ORC -> 20; case SKELETON -> 15;
            case TROLL -> 35; case DRAGON_BOSS -> 150;
        };
    }

    @Override
    public String useSpecialAbility(AbstractEntity target) {
        return switch (type) {
            case GOBLIN -> {
                target.takeDamage(getAttack() + 5);
                yield getName() + " throws a POISON DART! +5 bonus damage!";
            }
            case ORC -> {
                target.takeDamage(getAttack() * 2);
                yield getName() + " performs a SAVAGE SWING! Double damage!";
            }
            case SKELETON -> {
                heal(20);
                yield getName() + " uses BONE MEND! Recovered 20 HP!";
            }
            case TROLL -> {
                target.takeDamage(getAttack() + 10);
                yield getName() + " SLAMS the ground! Shockwave deals bonus damage!";
            }
            case DRAGON_BOSS -> {
                target.takeDamage(getAttack() * 3);
                yield getName() + " breathes DRAGONFIRE! Catastrophic damage!";
            }
        };
    }

    public int getXpReward()   { return xpReward; }
    public int getGoldReward() { return goldReward; }
    public EnemyType getType() { return type; }
    public boolean isBoss()    { return type == EnemyType.DRAGON_BOSS; }
}
