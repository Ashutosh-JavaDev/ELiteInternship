
/**
 * Represents a user in the recommendation system.
 */
public class User {
    private final long id;
    private final String username;
    private final String email;
    
    /**
     * Constructor for User.
     *
     * @param id The unique identifier for the user
     * @param username The username of the user
     * @param email The email of the user
     */
    public User(long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    
    /**
     * Get the ID of the user.
     *
     * @return The user ID
     */
    public long getId() {
        return id;
    }
    
    /**
     * Get the username of the user.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Get the email of the user.
     *
     * @return The email
     */
    public String getEmail() {
        return email;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
