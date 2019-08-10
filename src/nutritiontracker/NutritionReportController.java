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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Michal Zarnowski
 */
public class NutritionReportController implements Initializable {
    
    @FXML
    private Button menuBtn;
    @FXML
    private TextArea categoryTxt, productTxt, proteinTxt, carbTxt, fatTxt;
    
    private File recordsFile = new File("nutritionRecords.dat");
    private static RandomAccessFile recordsAccess; 
    private int recordSize = 129; //Record size in bytes

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Open stream to random access file
        try {
            recordsAccess = new RandomAccessFile(recordsFile, "rw");
        } catch(FileNotFoundException e) {
            //IMPLEMENT WARNING DIALOG
        }
        
        //Get records from the file as an Array List
        ArrayList<RecordModel> records = getRecordsAsArray();
        
        //Sort records into nutrition categories
        ArrayList<ArrayList<RecordModel>> sortedRecords = sortRecordsIntoCategories(records);
        
        //Display the records in text area
        displayRecords(sortedRecords);
        
        //Take user back to main menu on button click
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
    }  
    
    /**
     * Method reads in the data from the file and converts it to an Array List of records
     * @return Array List containing records
     */
    private ArrayList<RecordModel> getRecordsAsArray(){
        ArrayList<RecordModel> records = new ArrayList<>();
        
        //Create RecordModel objects by reading fields from the file
        try {
            //Find number of stored records in file by getting file length divided by size of each record
            long numOfRecords = recordsAccess.length() / recordSize;
            //Loop through all records in file
            for(int i = 0; i < numOfRecords; i++) {
                //Move pointer to beginning of record (i * 129)
                recordsAccess.seek(i * recordSize);
                
                //Read product name, first 30 chars
                String name = readString(recordsAccess, 30);
                
                //Read protein, carb and fat values
                double protein = recordsAccess.readDouble();
                double carb = recordsAccess.readDouble();
                double fat = recordsAccess.readDouble();
                
                //Read category, nutrition category and favourite
                String category = readString(recordsAccess, 15);
                String nutrition = readString(recordsAccess, 7);
                //Convert boolean favourite to String
                String fave = "";
                boolean favourite = false;
                if(recordsAccess.readBoolean()) {
                    favourite = true;
                }
                if(favourite) {
                    fave = "Favourite";
                } 
                
                //Add new RecordModel object to observable list
                records.add(new RecordModel(name, protein, carb, fat,
                category, nutrition, fave));
            }
        } catch(IOException e) {
            //IMPLEMENT THIS
        }
        
        return records;
    }
    
     /**
     * Method returns a string of characters from Random Access file of length based on supplied string size
     * @param file Random Access File to read characters from
     * @param size Amount of characters to extract
     * @return String read from the file
     * @throws IOException 
     */
    private String readString(RandomAccessFile file, int size) throws IOException {
        String string = "";
        for(int i = 0; i < size; i++){
            string += file.readChar();
        }
        
        return string;
    }
    
    /**
     * Method converts an Array List holding records into multiple Array Lists, one for each possible product
     * category.
     * @param records Array List holding all the records
     * @return Array List holding multiple Array Lists of each product category
     */
    private ArrayList<ArrayList<RecordModel>> sortRecordsIntoCategories(ArrayList<RecordModel> records) {
        //Create separate array lists for each available product nutrition category
        ArrayList<RecordModel> proteinCat = new ArrayList<>();
        ArrayList<RecordModel> carbCat = new ArrayList<>();
        ArrayList<RecordModel> fatCat = new ArrayList<>();
        
        //Cycle through all records and sort them into appropriate array lists
        for(int i = 0; i < records.size(); i++){
            RecordModel currentRecord = records.get(i);
            if(currentRecord.getNutrition().trim().equals("Protein"))
                proteinCat.add(currentRecord);
            else if(currentRecord.getNutrition().trim().equals("Carb"))
                carbCat.add(currentRecord);
            else if(currentRecord.getNutrition().trim().equals("Fat"))
                fatCat.add(currentRecord);
        }
        
        //Add each individual category array list to main array list to be returned
        ArrayList<ArrayList<RecordModel>> sortedRecords = new ArrayList<>();
        sortedRecords.add(proteinCat);
        sortedRecords.add(carbCat);
        sortedRecords.add(fatCat);
        
        return sortedRecords;
    }
    
    /**
     * Method displays all records from all Array Lists in appropriate Text Areas
     * @param records Array List holding all records
     */
    private void displayRecords(ArrayList<ArrayList<RecordModel>> records) {
        //Initialize column headers
        String displayCategoryString = "Category\n\n", displayProductString = "Product\n\n",
                displayProteinString = "Protein\n\n", displayCarbString = "Carbs\n\n", displayFatString = "Fat\n\n";
        
        //Cycle through each Array List containing products of specific categories
        for(int i = 0; i < records.size(); i++){
            ArrayList<RecordModel> category = records.get(i);
            //Cycle through all records in the category
            for(int j = 0; j < category.size(); j++) {
                RecordModel record = category.get(j);
                //Add record data to appropriate String
                displayCategoryString += record.getNutrition().trim() + "\n";
                displayProductString += record.getName().trim() + "\n";
                displayProteinString += record.getProtein() + "\n";
                displayCarbString += record.getCarb() + "\n";
                displayFatString += record.getFat() + "\n";
            }
            //White space formatting
            if(category.size() > 0) {
                displayCategoryString += "\n";
                displayProductString += "\n";
                displayProteinString += "\n";
                displayCarbString += "\n";
                displayFatString += "\n";
            }
        }
        //DIsplay columns
        categoryTxt.setText(displayCategoryString);
        productTxt.setText(displayProductString);
        proteinTxt.setText(displayProteinString);
        carbTxt.setText(displayCarbString);
        fatTxt.setText(displayFatString);
    }
    
}
