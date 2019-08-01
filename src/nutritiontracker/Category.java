
package nutritiontracker;

/**
 * Enum class to model Category of product in the program.
 * @author Michal Zarnowski
 */
public enum Category {
    
    MEAT("Meat"),
    FISH("Fish"),
    ANIMALPRODUCT("Animal Product"),
    VEGETABLE("Vegetable"),
    FRUIT("Fruit"),
    PASTARICE("Pasta/Rice"),
    CARBS("Carbs"),
    OTHER("Other");
    
    private String category;
    
    /**
     * Constructor taking in a String
     * @param category 
     */
    private Category(String category) {
        this.category = category;
    }
    
    /**
     * Accessor method returning the category as a String
     * @return 
     */
    public String getCategory() {
        return category;
    }
}
