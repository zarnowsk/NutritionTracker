/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nutritiontracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
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
 * FXML Controller class
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
            String name = "", nutritionCategory = "";
            double protein = 0, carb = 0, fat = 0;
            
            //Open stream to random access file
            try {
                recordsAccess = new RandomAccessFile(recordsFile, "rw");
            } catch(FileNotFoundException e) {
                System.out.println("File cannot be located");
            }
            
            //Convert data to their appropriate type, resize Strings
            //PRODUCT NAME
            if(nameTxt.getText().length() > 0) {
                name = resizeString(nameTxt.getText(), nameStringSize);
            } else {
                //IMPLELEMENT VALIDATION FOR EMPTY FIELD
            }
            
            //PROTEIN VALUE
            if(proteinTxt.getText().length() > 0) {
                try {
                    protein = Double.parseDouble(proteinTxt.getText());
                } catch(NumberFormatException e) {
                    //IMPLEMENT VALIDATION FOR NO DOUBLE
                    validNutrition = false;
                }
            } else {
                //IMPLELEMENT VALIDATION FOR EMPTY FIELD
                validNutrition = false;
            }
            
            //CARB VALUE
            if(carbTxt.getText().length() > 0) {
                try {
                    carb = Double.parseDouble(carbTxt.getText());
                } catch(NumberFormatException e) {
                    //IMPLEMENT VALIDATION FOR NO DOUBLE
                    validNutrition = false;
                }
            } else {
                //IMPLELEMENT VALIDATION FOR EMPTY FIELD
                validNutrition = false;
            }
            
            //FAT VALUE
            if(fatTxt.getText().length() > 0) {
                try {
                    fat = Double.parseDouble(fatTxt.getText());
                } catch(NumberFormatException e) {
                    //IMPLEMENT VALIDATION FOR NO DOUBLE
                    validNutrition = false;
                }
            } else {
                //IMPLELEMENT VALIDATION FOR EMPTY FIELD
                validNutrition = false;
            }
            
            //NUTRITION CATEGORY
            if(validNutrition) {
                nutritionCategory = resizeString(getNutritionCategory(), 7);
            }
            
            //FAVOURITE
            boolean favourite = false; //always save default value
            
            //Save record to file
            try {
                //Move file pointer to end of file
                recordsAccess.seek(recordsAccess.length());

                //Add fields
                recordsAccess.writeChars(name);
                recordsAccess.writeDouble(protein);
                recordsAccess.writeDouble(carb);
                recordsAccess.writeDouble(fat);
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
    
    private void clearFields() {
        nameTxt.clear();
        proteinTxt.clear();
        carbTxt.clear();
        fatTxt.clear();
        categoryCmb.getSelectionModel().clearSelection();
    }
}

