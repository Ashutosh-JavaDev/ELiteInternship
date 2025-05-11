
/**
 * Represents a user's preference for an item.
 */
public class Preference {
    private final long userId;
    private final long itemId;
    private final float value;
    
    /**
     * Constructor for Preference.
     *
     * @param userId The ID of the user
     * @param itemId The ID of the item
     * @param value The preference value (rating)
     */
    public Preference(long userId, long itemId, float value) {
        this.userId = userId;
        this.itemId = itemId;
        this.value = value;
    }
    
    /**
     * Get the user ID associated with this preference.
     *
     * @return The user ID
     */
    public long getUserId() {
        return userId;
    }
    
    /**
     * Get the item ID associated with this preference.
     *
     * @return The item ID
     */
    public long getItemId() {
        return itemId;
    }
    
    /**
     * Get the preference value (rating).
     *
     * @return The preference value
     */
    public float getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "Preference{" +
                "userId=" + userId +
                ", itemId=" + itemId +
                ", value=" + value +
                '}';
    }
}
