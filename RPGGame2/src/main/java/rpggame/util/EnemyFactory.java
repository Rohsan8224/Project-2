package rpggame.util;

import rpggame.model.Enemy;
import rpggame.model.Enemy.EnemyType;

import java.util.Random;

/**
 * Factory class for creating Enemy instances.
 * Applies the Factory design pattern — callers don't need to know creation logic.
 */
public class EnemyFactory {
    private static final Random random = new Random();

    /**
     * Creates a random enemy appropriate for the given dungeon floor.
     */
    public static Enemy createForFloor(int floor) {
        if (floor >= 10) {
            return new Enemy(EnemyType.DRAGON_BOSS);
        }
        EnemyType type;
        if (floor <= 2) {
            type = random.nextBoolean() ? EnemyType.GOBLIN : EnemyType.SKELETON;
        } else if (floor <= 5) {
            type = random.nextBoolean() ? EnemyType.ORC : EnemyType.SKELETON;
        } else {
            type = random.nextBoolean() ? EnemyType.TROLL : EnemyType.ORC;
        }
        return new Enemy(type);
    }

    /**
     * Creates a specific enemy by type.
     */
    public static Enemy create(EnemyType type) {
        return new Enemy(type);
    }
}
