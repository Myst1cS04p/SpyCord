package com.myst1cs04p.spycord.common.stats;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe activity counters for SpyCord.
 *
 * <p>Serves two distinct purposes:
 * <ol>
 *   <li><b>bStats custom charts</b> — lifetime totals (commands logged, webhook
 *       success/failure) that are sampled by the bStats library at its own cadence.</li>
 *   <li><b>Activity digest</b> — per-period counters that accumulate between digest
 *       flushes and are reset to zero when {@link #flushPeriod()} is called.</li>
 * </ol>
 *
 * <p>All fields use {@link AtomicLong} so they are safe to increment from any thread
 * (webhook HTTP calls happen on a CompletableFuture thread pool).
 */
public class ActivityTracker {


    private final AtomicLong totalCommandsLogged  = new AtomicLong();
    private final AtomicLong totalWebhookSuccess  = new AtomicLong();
    private final AtomicLong totalWebhookFailure  = new AtomicLong();


    private final AtomicLong periodCommands       = new AtomicLong();
    private final AtomicLong periodGamemodeChanges = new AtomicLong();
    private final AtomicLong periodJoinQuitEvents  = new AtomicLong();

    // Recording methods

    public void recordCommand() {
        totalCommandsLogged.incrementAndGet();
        periodCommands.incrementAndGet();
    }

    public void recordGamemodeChange() {
        periodGamemodeChanges.incrementAndGet();
    }

    public void recordJoinQuit() {
        periodJoinQuitEvents.incrementAndGet();
    }

    public void recordWebhookSuccess() {
        totalWebhookSuccess.incrementAndGet();
    }

    public void recordWebhookFailure() {
        totalWebhookFailure.incrementAndGet();
    }


    public long getTotalCommandsLogged() { return totalCommandsLogged.get(); }
    public long getTotalWebhookSuccess()  { return totalWebhookSuccess.get(); }
    public long getTotalWebhookFailure()  { return totalWebhookFailure.get(); }

    /**
     * Atomically snapshots and resets the per-period counters.
     *
     * <p>Call this immediately after building the digest message so the window
     * begins fresh. The returned snapshot is immutable.
     *
     * @return A {@link DigestSnapshot} containing the period totals.
     */
    public DigestSnapshot flushPeriod() {
        return new DigestSnapshot(
                periodCommands.getAndSet(0),
                periodGamemodeChanges.getAndSet(0),
                periodJoinQuitEvents.getAndSet(0)
        );
    }

    /**
     * Immutable snapshot of per-period counters captured at digest time.
     *
     * @param commands        Sensitive commands logged this period.
     * @param gamemodeChanges OP gamemode changes detected this period.
     * @param joinQuitEvents  OP join/quit events recorded this period.
     */
    public record DigestSnapshot(long commands, long gamemodeChanges, long joinQuitEvents) {

        /** Returns {@code true} if every counter is zero (nothing happened). */
        public boolean isEmpty() {
            return commands == 0 && gamemodeChanges == 0 && joinQuitEvents == 0;
        }
    }
}
