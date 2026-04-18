package com.myst1cs04p.spycord.common.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Decorator that adds an in-memory read cache with dirty-flag invalidation
 * to any {@link IPluginConfig} implementation.
 *
 * <p>Values are loaded from the delegate on first access and cached until:
 * <ul>
 *   <li>A specific key is written via {@link #setBoolean} — that key's entry is
 *       updated in-place so subsequent reads are consistent without a round-trip.</li>
 *   <li>{@link #reload()} is called — the entire cache is cleared and values are
 *       re-populated lazily on the next access.</li>
 * </ul>
 *
 * <p>Thread-safe: the backing map is a {@link ConcurrentHashMap}, and
 * {@code computeIfAbsent} is used for atomic check-then-insert semantics.
 */
public class CachedPluginConfig implements IPluginConfig {

    private final IPluginConfig delegate;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public CachedPluginConfig(IPluginConfig delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getString(String path, String fallback) {
        // computeIfAbsent guarantees the delegate is called at most once per key.
        return (String) cache.computeIfAbsent(path, k -> {
            String value = delegate.getString(k, fallback);
            // If delegate returns null (shouldn't happen, but defensively) fall back.
            return value != null ? value : fallback;
        });
    }

    @Override
    public boolean getBoolean(String path, boolean fallback) {
        return (boolean) cache.computeIfAbsent(path, k -> delegate.getBoolean(k, fallback));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getStringList(String path) {
        return (List<String>) cache.computeIfAbsent(path, k -> delegate.getStringList(k));
    }

    /**
     * Writes the value through to the delegate and immediately updates the cache
     * so subsequent reads in the same tick see the new value without a cache miss.
     */
    @Override
    public void setBoolean(String path, boolean value) {
        delegate.setBoolean(path, value);
        cache.put(path, value); // dirty-flag: update in place, no stale read
    }

    /**
     * Delegates the reload and then invalidates the entire cache.
     * All values are re-fetched lazily on the next access.
     */
    @Override
    public void reload() {
        delegate.reload();
        cache.clear();
    }
}
