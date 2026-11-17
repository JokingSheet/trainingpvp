package de.jokingsheet.trainingpvp;

/**
 * Lightweight session statistics. Values are deliberately session-only;
 * the mod does not read or upload player data.
 */
public final class TrainingStats {
    private static int reactionAttempts;
    private static int reactionSuccesses;
    private static long bestReactionMs = Long.MAX_VALUE;
    private static int movementKitClaims;

    private TrainingStats() {
    }

    public static void recordReaction(long millis) {
        reactionAttempts++;
        reactionSuccesses++;
        if (millis > 0 && millis < bestReactionMs) {
            bestReactionMs = millis;
        }
    }

    public static void recordEarlyPress() {
        reactionAttempts++;
    }

    public static void recordKitClaim() {
        movementKitClaims++;
    }

    public static int getReactionAttempts() {
        return reactionAttempts;
    }

    public static int getReactionSuccesses() {
        return reactionSuccesses;
    }

    public static long getBestReactionMs() {
        return bestReactionMs;
    }

    public static int getMovementKitClaims() {
        return movementKitClaims;
    }

    public static String getBestReactionText() {
        return bestReactionMs == Long.MAX_VALUE ? "Noch kein Versuch" : bestReactionMs + " ms";
    }
}
