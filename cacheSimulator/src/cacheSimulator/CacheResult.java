package cacheSimulator;

public class CacheResult {
    public final boolean isHit;
    public final String value;

    public CacheResult(boolean isHit, String value) {
        this.isHit = isHit;
        this.value = value;
    }

    @Override
    public String toString() {
        return (isHit ? "[HIT] " : "[MISS] ") + value;
    }
}
