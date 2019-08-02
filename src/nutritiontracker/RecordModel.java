package nutritiontracker;

/**
 * This class represents a record object obtained from readable file for purpouse 
 * of populating TableView on View screen
 * @author Michal Zarnowski
 */
public class RecordModel {

    private String name;
    private double protein;
    private double carb;
    private double fat;
    private String category;
    private String nutrition;
    private String favourite;
    
    public RecordModel(String name, double protein, double carb, double fat, 
            String category, String nutrition, String favourite) {
        this.name = name;
        this.protein = protein;
        this.carb = carb;
        this.fat = fat;
        this.category = category;
        this.nutrition = nutrition;
        this.favourite = favourite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarb() {
        return carb;
    }

    public void setCarb(double carb) {
        this.carb = carb;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNutrition() {
        return nutrition;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }
    
    
}
