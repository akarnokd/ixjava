package ix;

/**
 * An Iterable plus a key representing a group for the
 * operator {@code groupBy()}.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public abstract class GroupedIx<K, V> extends Ix<V> {

    protected final K key;
    
    public GroupedIx(K key) {
        this.key = key;
    }
    
    public final K key() {
        return key;
    }

}
