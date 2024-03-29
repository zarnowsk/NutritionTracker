
package nutritiontracker;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class in charge of the Edit Window
 *
 * @author Michal Zarnowski
 */
public class EditController implements Initializable {

    //FXML variables
    @FXML
    private Button cancelBtn, saveBtn;
    @FXML
    private TextField nameTxt, proteinTxt, carbTxt, fatTxt;
    @FXML
    private ComboBox categoryCmb;
    
    //Variables used by this controller
    private static File recordsFile = new File("nutritionRecords.dat");
    private static RandomAccessFile recordsAccess; 
    private static String favourite; //used to save current favourite status
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Initialize observable list and join with combo box
        ObservableList<String> observableCategories = FXCollections.observableArrayList();
        for(Category category : Category.values()) {
            observableCategories.add(category.getCategory());
        }
        categoryCmb.setItems(observableCategories);
        
        //BUTTON EVENTS
        //Cancel btn takes user back to View Records screen
        cancelBtn.setOnAction((ActionEvent event) -> {
                goBack();
        });
        
        //Save record to file
        saveBtn.setOnAction((ActionEvent event) -> {
            boolean validRecord = true, validNutrition = true;
            
            //Get values from entry fields and validate
            String newProduct = nameTxt.getText();
            if(newProduct.length() > 0) {
                newProduct = resizeString(nameTxt.getText(), 30);
            } else {
                displayError("Invalid product", "Product name cannot be empty");
                validRecord = false;
            }
            
            String newProteinTxt = proteinTxt.getText();
            Double newProtein = 0.0;
            if(newProteinTxt.length() > 0) {
                try {
                    newProtein = Double.parseDouble(newProteinTxt);
                    if(newProtein < 0) {
                        if(validRecord) {
                            displayError("Invalid product", "Protein amount must be more than 0.0");
                            validRecord = false;
                        }
                        validNutrition = false;
                    }
                } catch(NumberFormatException e) {
                    if(validRecord) {
                        displayError("Invalid product", "Protein amount must be a valid number");
                        validRecord = false;
                    }
                    validNutrition = false;
                }
            } else {
                if(validRecord) {
                    displayError("Invalid product", "Protein amount must be a valid number");
                    validRecord = false;
                }
                validNutrition = false;
            }
            
            String newCarbTxt = carbTxt.getText();
            Double newCarb = 0.0;
            if(newCarbTxt.length() > 0) {
                try {
                    newCarb = Double.parseDouble(newCarbTxt);
                    if(newCarb < 0) {
                        if(validRecord) {
                            displayError("Invalid product", "Carb amount must be more than 0.0");
                            validRecord = false;
                        }
                        validNutrition = false;
                    }
                } catch(NumberFormatException e) {
                    if(validRecord) {
                        displayError("Invalid product", "Carb amount must be a valid number");
                        validRecord = false;
                    }
                    validNutrition = false;
                }
            } else {
                if(validRecord) {
                    displayError("Invalid product", "Carb amount must be a valid number");
                    validRecord = false;
                }
                validNutrition = false;
            }
            
            String newFatTxt = fatTxt.getText();
            Double newFat = 0.0;
            if(newFatTxt.length() > 0) {
                try {
                    newFat = Double.parseDouble(newFatTxt);
                    if(newFat < 0) {
                        if(validRecord) {
                            displayError("Invalid product", "Fat amount must be more than 0.0");
                            validRecord = false;
                        }
                        validNutrition = false;
                    }
                } catch(NumberFormatException e) {
                    if(validRecord) {
                        displayError("Invalid product", "Fat amount must be a valid number");
                        validRecord = false;
                    }
                    validNutrition = false;
                }
            } else {
                if(validRecord) {
                    displayError("Invalid product", "Fat amount must be a valid number");
                    validRecord = false;
                }
                validNutrition = false;
            }
            
            String newCategory = resizeString(categoryCmb.getSelectionModel().getSelectedItem() + "", 15);
            String newNutritionCategory = "";
            if(validNutrition) {
                newNutritionCategory = resizeString(getNutritionCategory(), 7);
            }
            
            //Save edited record into the file
            if(validRecord) {
                try {
                    recordsAccess.writeChars(newProduct);
                    recordsAccess.writeDouble(newProtein);
                    recordsAccess.writeDouble(newCarb);
                    recordsAccess.writeDouble(newFat);
                    recordsAccess.writeChars(newCategory);
                    recordsAccess.writeChars(newNutritionCategory);
                    recordsAccess.writeBoolean(Boolean.parseBoolean(favourite));
                    
                    //Close the stream
                    recordsAccess.close();
                } catch(IOException e) {
                    
                }
                
                //Return to View Records screen
                goBack();
            }
        });
    }  
    
    /**
     * Method populates all data fields with record passed from view product window.
     * @param record RecordModel of the product selected by user in the View Product window.
     */
    protected void passRecord(RecordModel record, int recordPosition) {
        //Extract raw variables from passed record
        String product = record.getName().trim();
        double protein = record.getProtein();
        double carb = record.getCarb();
        double fat = record.getFat();
        String category = record.getCategory();
        
        //Display passed record in text fields
        nameTxt.setText(product);
        proteinTxt.setText(protein + "");
        carbTxt.setText(carb + "");
        fatTxt.setText(fat + "");
        categoryCmb.getSelectionModel().select(category);
        
        //Save favourite field value
        favourite = record.getFavourite();
        
        try {
            recordsAccess = new RandomAccessFile(recordsFile, "rw");
            recordsAccess.seek(recordPosition);
        } catch(IOException e) {
            
        }
    }
    
    /**
     * Method works out nutrition category of entered product based on its nutritional values
     * @return String Protein/Carb/Fat/N/A(if no high unique value is found)
     */
    private String getNutritionCategory() {
        //Acquire protein, carb and fat data entered by user as doubles
        double protein = Double.parseDouble(proteinTxt.getText());
        double carb = Double.parseDouble(carbTxt.getText());
        double fat = Double.parseDouble(fatTxt.getText());
        
        //Work out the highest value and return corresponding String, otherwise return N/A
        if(protein > carb && protein > fat) return "Protein";
        else if(carb > protein && carb > fat) return "Carb";
        else if(fat > protein && fat > carb) return "Fat";
        else return "N/A";
    }
    
    /**
     * Method resizes an input string to specified size by either padding the string with white spaces or cutting the
     * string down to appropriate length.
     * @param string Input string to be resized
     * @param size Final size of the string
     * @return Resized string
     */
    private String resizeString(String string, int size) {
        int strLength = string.length();
        
        //If string is longer than allowed size, return truncated string
        //If string is shorter than allowed size, return string padded with spaces
        //If string is of correct size, return it
        if(strLength > size) {
            return string.substring(0, size);
        } else if(strLength < size) {
            for(int i = 0; i < (size - strLength); i++) {
                string += " ";
            }
            return string;
        } else {
            return string;
        }
    }
    
    /**
     * Method returns the user to View Product window
     */
    private void goBack(){
        try {
            //Get current window from the btn element and close it
            Stage stage = (Stage)saveBtn.getScene().getWindow();
            stage.close();
            
            //Creeate new window and display it
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("ViewFXML.fxml"));
            Scene viewScene = new Scene(fxmlLoader.load(), 700, 550);
            stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Nutrition Tracker");
            stage.setScene(viewScene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }                               
    }
    
    /**
     * Method displays an error message on the screen
     * @param title of the error message
     * @param header of the error message
     */
     public void displayError(String title, String header) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.show();
    }
    
}
