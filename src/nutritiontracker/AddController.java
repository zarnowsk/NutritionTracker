
package nutritiontracker;

import java.io.File;
import java.io.FileNotFoundException;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class in charge of the Add Window
 * 
 * @author Michal Zarnowski
 */
public class AddController implements Initializable {

    //FXML variables
    @FXML
    private Button menuBtn, addBtn;
    @FXML
    private TextField nameTxt, proteinTxt, carbTxt, fatTxt;
    @FXML
    private ComboBox<String> categoryCmb;
    
    //Class variables
    private static File recordsFile = new File("nutritionRecords.dat");
    private static RandomAccessFile recordsAccess; 
    private int nameStringSize = 30;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        //Initialize observable list and join with combo box
        ObservableList<String> observableCategories = FXCollections.observableArrayList();
        for(Category category : Category.values()) {
            observableCategories.add(category.getCategory());
        }
        categoryCmb.setItems(observableCategories);
        
        //BUTTON EVENTS
        
        //Return user to main menu
        menuBtn.setOnAction((ActionEvent event) -> {
            try {
                //Get current window from the btn element and close it
                Stage stage = (Stage)menuBtn.getScene().getWindow();
                stage.close();
                //Creeate new window and display it
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("MainMenuFXML.fxml"));
                Scene mainMenuScene = new Scene(fxmlLoader.load(), 700, 550);
                stage = new Stage();
                stage.setResizable(false);
                stage.setTitle("Nutrition Tracker");
                stage.setScene(mainMenuScene);
                stage.show();
            } catch (IOException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create new Window.", e);
            }
        });
        
        //Save record into file
        addBtn.setOnAction((ActionEvent event) -> {
            //Vars to be saved
            boolean validNutrition = true; //if all nutrition values are valid
            boolean validRecord = true;
            String name = "", category = "", nutritionCategory = "";
            double protein = 0, carb = 0, fat = 0;
            
            //Open stream to random access file
            try {
                recordsAccess = new RandomAccessFile(recordsFile, "rw");
            } catch(FileNotFoundException e) {
                //IMPLEMENT WARNING DIALOG
            }
            
            //Convert data to their appropriate type, resize Strings
            //PRODUCT NAME
            if(nameTxt.getText().length() > 0) {
                name = resizeString(nameTxt.getText(), nameStringSize);
            } else {
                displayError("Invalid product", "Product name cannot be empty");
                validRecord = false;
            }
            
            //PROTEIN VALUE
            if(proteinTxt.getText().length() > 0) {
                try {
                    protein = Double.parseDouble(proteinTxt.getText());
                    if(protein < 0) {
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
            
            //CARB VALUE
            if(carbTxt.getText().length() > 0) {
                try {
                    carb = Double.parseDouble(carbTxt.getText());
                    if(carb < 0) {
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
            
            //FAT VALUE
            if(fatTxt.getText().length() > 0) {
                try {
                    fat = Double.parseDouble(fatTxt.getText());
                    if(fat < 0) {
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
            
            //CATEGORY
            try {
                if(categoryCmb.getSelectionModel().getSelectedIndex() >= 0) {
                    category = resizeString(categoryCmb.getSelectionModel().getSelectedItem(), 15);
                } else {
                    if(validRecord) {
                        displayError("Invalid product", "You must select a category");
                        validRecord = false;
                    }
                }
            } catch(NumberFormatException e) {
                if(validRecord) {
                        displayError("Invalid product", "You must select a category");
                        validRecord = false;
                }
            }
            
            //NUTRITION CATEGORY
            if(validNutrition) {
                nutritionCategory = resizeString(getNutritionCategory(), 7);
            }
            
            //FAVOURITE
            boolean favourite = false; //always save default value
            
            //Save record to file
            if(validRecord) {
                try {
                    //Move file pointer to end of file
                    recordsAccess.seek(recordsAccess.length());
                    
                    //Add fields
                    recordsAccess.writeChars(name);
                    recordsAccess.writeDouble(protein);
                    recordsAccess.writeDouble(carb);
                    recordsAccess.writeDouble(fat);
                    recordsAccess.writeChars(category);
                    recordsAccess.writeChars(nutritionCategory);
                    recordsAccess.writeBoolean(favourite);
                
                    //Close stream
                    recordsAccess.close();
                
                    //Clear all input fields
                    clearFields();
                
                    //Display confirmation dialog
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Record Saved");
                    alert.setHeaderText(null);
                    alert.setContentText("Record has been saved successfully");
                    alert.showAndWait();
                
                } catch(IOException e) {
                    //IMPLEMENT THIS
                }
            }

        });
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
     * Method clears all data from input fields and resets focus to first field.
     */
    private void clearFields() {
        nameTxt.clear();
        proteinTxt.clear();
        carbTxt.clear();
        fatTxt.clear();
        categoryCmb.getSelectionModel().clearSelection();
        
        nameTxt.requestFocus();
    }
    
    /**
     * Method displays an error message on the screen
     * @param title of the error message
     * @param header of the error message
     */
    public void displayError(String title, String header) {
        Alert dialog = new Alert(AlertType.ERROR);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.show();
    }
}

