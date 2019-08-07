/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nutritiontracker;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 * This class represents the controller of the edit record window
 * @author Michal Zarnowski
 */
public class SearchController implements Initializable {

    @FXML
    private Button menuBtn, searchBtn;
    @FXML
    private TextField searchTxt;
    @FXML
    private Label resultLbl;
    
    //Class variables
    private static File recordsFile = new File("nutritionRecords.dat");
    private static RandomAccessFile recordsAccess; 
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Open stream to file
        try{
            recordsAccess = new RandomAccessFile(recordsFile, "r");
        } catch (IOException e) {
            System.out.println("Couldn't open file");
        }
        
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
                
                recordsAccess.close();
            } catch (IOException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create new Window.", e);
            }
        });
        
        //Search file for record entered by user
        searchBtn.setOnAction((ActionEvent event) -> {
            //Clear result label from previous result
            resultLbl.setText("");
            
            //Retrieve user's input from input field
            boolean found = false;
            if(searchTxt.getText().trim() != null && searchTxt.getText().trim().length() > 0) {
                String userInput = searchTxt.getText().trim().toUpperCase();
                
                try {
                    //Traverse through all product names in the file until a match is found
                    //to name supplied to method
                    String readName = "";
                    for(int i = 0; i < recordsAccess.length(); i+=129) {
                        //Move cursor to begginning of each record
                        recordsAccess.seek(i);
                        //Read product name, first 30 chars
                        for(int j = 0; j < 30; j++) {
                            readName += recordsAccess.readChar();
                        }
                        //If product name in the file matches supplied name, display record
                        if(readName.trim().toUpperCase().equals(userInput)) {
                            displayRecord(i);
                            found = true;
                        }
                        
                        readName = "";
                    }
                    
                    //If no matching record found, display alert box
                    if(!found) {
                        //Display confirmation dialog
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Record Not Found");
                        alert.setHeaderText(null);
                        alert.setContentText("Record you are searching for cannot be located!");
                        alert.showAndWait();
                    }
                } catch(IOException e) {
                    //IMPLEMENT THIS
                }
            }
        });
    }  
    
    /**
     * Method displays matching record based on its position in the file
     * @param recordPosition File byte position of the record
     */
    public void displayRecord(int recordPosition) {
        String recordDisplay = "", name = "", category = "", nutCategory = "";
        double protein = 0, carb = 0, fat = 0;
        
        try {
            //Read the record data
            recordsAccess.seek(recordPosition);
            for(int i = 0; i < 30; i++){
                name += recordsAccess.readChar();
            }
            name = name.trim();
            
            protein = recordsAccess.readDouble();
            carb = recordsAccess.readDouble();
            fat = recordsAccess.readDouble();
            
            for(int i = 0; i < 15; i++){
                category += recordsAccess.readChar();
            }
            category = category.trim();
            
            for(int i = 0; i < 7; i++){
                nutCategory += recordsAccess.readChar();
            }
            nutCategory = nutCategory.trim();
            
            //Combine all data
            recordDisplay = "Product name: " + name + "\n" + "Protein: " + protein + "g/100g\n" + "Carbohydrates: " +
                    carb + "g/100g\n" + "Fat: " +  fat + "g/100g\n" + "Product category: " + category + "\n" + 
                    "Nutrition category: " + nutCategory;
            
            //Display result
            resultLbl.setText(recordDisplay);
            
        } catch(IOException e) {
            System.out.println("Cannot read file");
        }
    }
    
}

