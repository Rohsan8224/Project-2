package rpggame.service;

/**
 * Observer contract for game events. View components register themselves with
 * the {@link GameService} (the Subject) and are notified when noteworthy events
 * occur — such as a level-up — without the service holding any direct reference
 * to Swing classes. This keeps the business logic decoupled from the UI
 * (Observer design pattern / Dependency Inversion).
 */
@FunctionalInterface
public interface GameEventListener {
    /**
     * Called by the {@link GameService} when a game event occurs.
     * @param message a human-readable description of the event.
     */
    void onGameEvent(String message);
}
