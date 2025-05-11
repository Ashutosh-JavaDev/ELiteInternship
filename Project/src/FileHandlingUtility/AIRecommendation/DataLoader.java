package FileHandlingUtility.AIRecommendation;
import FileHandlingUtility.AIRecommendation.Item;
import FileHandlingUtility.AIRecommendation.Preference;
import FileHandlingUtility.AIRecommendation.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for loading user, item, and preference data.
 */
public class DataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    
    private List<User> users;
    private List<Item> items;
    private List<Preference> preferences;
    private Map<Long, User> userMap;
    private Map<Long, Item> itemMap;
    
    public DataLoader() {
        this.users = new ArrayList<>();
        this.items = new ArrayList<>();
        this.preferences = new ArrayList<>();
        this.userMap = new HashMap<>();
        this.itemMap = new HashMap<>();
    }
    
    /**
     * Load users from a CSV file.
     *
     * @param filePath The path to the CSV file
     * @return A list of users
     * @throws IOException If an I/O error occurs
     */
    public List<User> loadUsers(String filePath) throws IOException {
        LOGGER.info("Loading users from: {}", filePath);
        users.clear();
        userMap.clear();
        
        try (Reader reader = new InputStreamReader(getResourceAsStream(filePath), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            for (CSVRecord record : csvParser) {
                long id = Long.parseLong(record.get("id"));
                String username = record.get("username");
                String email = record.get("email");
                
                User user = new User(id, username, email);
                users.add(user);
                userMap.put(id, user);
            }
        }
        
        LOGGER.info("Loaded {} users", users.size());
        return users;
    }
    
    /**
     * Load items from a CSV file.
     *
     * @param filePath The path to the CSV file
     * @return A list of items
     * @throws IOException If an I/O error occurs
     */
    public List<Item> loadItems(String filePath) throws IOException {
        LOGGER.info("Loading items from: {}", filePath);
        items.clear();
        itemMap.clear();
        
        try (Reader reader = new InputStreamReader(getResourceAsStream(filePath), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            for (CSVRecord record : csvParser) {
                long id = Long.parseLong(record.get("id"));
                String name = record.get("name");
                String category = record.get("category");
                String description = record.get("description");
                
                Item item = new Item(id, name, category, description);
                items.add(item);
                itemMap.put(id, item);
            }
        }
        
        LOGGER.info("Loaded {} items", items.size());
        return items;
    }
    
    /**
     * Load preferences from a CSV file.
     *
     * @param filePath The path to the CSV file
     * @return A list of preferences
     * @throws IOException If an I/O error occurs
     */
    public List<Preference> loadPreferences(String filePath) throws IOException {
        LOGGER.info("Loading preferences from: {}", filePath);
        preferences.clear();
        
        try (Reader reader = new InputStreamReader(getResourceAsStream(filePath), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            for (CSVRecord record : csvParser) {
                long userId = Long.parseLong(record.get("user_id"));
                long itemId = Long.parseLong(record.get("item_id"));
                float rating = Float.parseFloat(record.get("rating"));
                
                Preference preference = new Preference(userId, itemId, rating);
                preferences.add(preference);
            }
        }
        
        LOGGER.info("Loaded {} preferences", preferences.size());
        return preferences;
    }
    
    /**
     * Create a Mahout DataModel from preference data.
     *
     * @return A Mahout DataModel
     * @throws IOException If an I/O error occurs
     */
    public DataModel createDataModel() throws IOException {
        LOGGER.info("Creating Mahout DataModel from preferences");
        
        // Create a temporary file with the preference data
        File tempFile = File.createTempFile("mahout-prefs", ".csv");
        tempFile.deleteOnExit();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            for (Preference pref : preferences) {
                writer.println(pref.getUserId() + "," + pref.getItemId() + "," + pref.getValue());
            }
        }
        
        return new FileDataModel(tempFile);
    }
    
    /**
     * Create a Mahout DataModel from a file path.
     *
     * @param filePath The path to the file
     * @return A Mahout DataModel
     * @throws IOException If an I/O error occurs
     */
    public DataModel createDataModelFromFile(String filePath) throws IOException {
        LOGGER.info("Creating Mahout DataModel from file: {}", filePath);
        
        // Extract the preferences file to a temporary location
        File tempFile = File.createTempFile("mahout-prefs", ".csv");
        tempFile.deleteOnExit();
        
        try (InputStream is = getResourceAsStream(filePath);
             OutputStream os = new FileOutputStream(tempFile)) {
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
        
        return new FileDataModel(tempFile);
    }
    
    /**
     * Get a user by their ID.
     *
     * @param userId The user ID
     * @return The user, or null if not found
     */
    public User getUserById(long userId) {
        return userMap.get(userId);
    }
    
    /**
     * Get an item by its ID.
     *
     * @param itemId The item ID
     * @return The item, or null if not found
     */
    public Item getItemById(long itemId) {
        return itemMap.get(itemId);
    }
    
    /**
     * Get all loaded users.
     *
     * @return A list of all users
     */
    public List<User> getUsers() {
        return users;
    }
    
    /**
     * Get all loaded items.
     *
     * @return A list of all items
     */
    public List<Item> getItems() {
        return items;
    }
    
    /**
     * Get all loaded preferences.
     *
     * @return A list of all preferences
     */
    public List<Preference> getPreferences() {
        return preferences;
    }
    
    /**
     * Get an input stream for a resource file.
     *
     * @param resourcePath The path to the resource
     * @return An input stream for the resource
     */
    private InputStream getResourceAsStream(String resourcePath) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        return is;
    }
}
