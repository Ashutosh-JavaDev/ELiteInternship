
/**
 * Represents an item that can be recommended to users.
 */
public class Item {
    private final long id;
    private final String name;
    private final String category;
    private final String description;
    
    /**
     * Constructor for Item.
     *
     * @param id The unique identifier for the item
     * @param name The name of the item
     * @param category The category the item belongs to
     * @param description A description of the item
     */
    public Item(long id, String name, String category, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
    }
    
    /**
     * Get the ID of the item.
     *
     * @return The item ID
     */
    public long getId() {
        return id;
    }
    
    /**
     * Get the name of the item.
     *
     * @return The item name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the category of the item.
     *
     * @return The item category
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Get the description of the item.
     *
     * @return The item description
     */
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Item item = (Item) o;
        return id == item.id;
    }
    
    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
