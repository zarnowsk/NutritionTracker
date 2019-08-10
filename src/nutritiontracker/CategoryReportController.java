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
public class CategoryReportController implements Initializable {

    @FXML
    private Button menuBtn;
    @FXML
    private TextArea reportTxt;
    
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
        
        //Sort records into categories
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
    
    private ArrayList<ArrayList<RecordModel>> sortRecordsIntoCategories(ArrayList<RecordModel> records) {
        //Create separate array lists for each available product category
        ArrayList<RecordModel> meatCat = new ArrayList<>();
        ArrayList<RecordModel> fishCat = new ArrayList<>();
        ArrayList<RecordModel> animalCat = new ArrayList<>();
        ArrayList<RecordModel> vegCat = new ArrayList<>();
        ArrayList<RecordModel> fruitCat = new ArrayList<>();
        ArrayList<RecordModel> pastaRiceCat = new ArrayList<>();
        ArrayList<RecordModel> carbCat = new ArrayList<>();
        ArrayList<RecordModel> otherCat = new ArrayList<>();
        
        //Cycle through all records and sort them into appropriate array lists
        for(int i = 0; i < records.size(); i++){
            RecordModel currentRecord = records.get(i);
            if(currentRecord.getCategory().trim().equals("Meat"))
                meatCat.add(currentRecord);
            else if(currentRecord.getCategory().trim().equals("Fish"))
                fishCat.add(currentRecord);
            else if(currentRecord.getCategory().trim().equals("Animal Product"))
                animalCat.add(currentRecord);
            else if(currentRecord.getCategory().trim().equals("Vegetable"))
                vegCat.add(currentRecord);
            else if(currentRecord.getCategory().trim().equals("Fruit"))
                fruitCat.add(currentRecord);
            else if(currentRecord.getCategory().trim().equals("Pasta/Rice"))
                pastaRiceCat.add(currentRecord);
            else if(currentRecord.getCategory().trim().equals("Carbs"))
                carbCat.add(currentRecord);
            else if(currentRecord.getCategory().trim().equals("Other"))
                otherCat.add(currentRecord);
        }
        
        //Add each individual category array list to main array list to be returned
        ArrayList<ArrayList<RecordModel>> sortedRecords = new ArrayList<>();
        sortedRecords.add(meatCat);
        sortedRecords.add(fishCat);
        sortedRecords.add(animalCat);
        sortedRecords.add(vegCat);
        sortedRecords.add(fruitCat);
        sortedRecords.add(pastaRiceCat);
        sortedRecords.add(carbCat);
        sortedRecords.add(otherCat);
        
        return sortedRecords;
    }
    
    private void displayRecords(ArrayList<ArrayList<RecordModel>> records) {
        String displayString = "";
        
        for(int i = 0; i < records.size(); i++){
            ArrayList<RecordModel> category = records.get(i);
            for(int j = 0; j < category.size(); j++) {
                RecordModel record = category.get(j);
                displayString += formatRecord(record) + "\n";
            }
            if(category.size() > 0)
                displayString += "\n";
        }
        reportTxt.setText(displayString);
    }
    
    private String formatRecord(RecordModel record) {
        String recordString = "";
        int spaces;
        
        recordString += record.getCategory().trim();
        spaces = 20 - record.getCategory().trim().length();
        for(int i = 0; i < spaces; i++){
            recordString += " ";
        }
        
        recordString += record.getName().trim();
        spaces = 30 - record.getName().trim().length();
        for(int i = 0; i < spaces; i++){
            recordString += " ";
        }
        
        String protein = record.getProtein() + "";
        recordString += protein;
        spaces = 10 - protein.length();
        for(int i = 0; i < spaces; i++){
            recordString += " ";
        }
        
        String carb = record.getCarb() + "";
        recordString += carb;
        spaces = 10 - carb.length();
        for(int i = 0; i < spaces; i++){
            recordString += " ";
        }
        
        String fat = record.getFat() + "";
        recordString += fat;
        spaces = 10 - fat.length();
        for(int i = 0; i < spaces; i++){
            recordString += " ";
        }
        
        return recordString;
    }
    
}