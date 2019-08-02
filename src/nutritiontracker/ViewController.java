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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Michal Zarnowski
 */
public class ViewController implements Initializable {

    //FXML variables
    @FXML
    private Button menuBtn, searchBtn;
    @FXML
    private TableView<RecordModel> recordsTbl;
    @FXML
    private TableColumn<RecordModel, String> col1, col5, col6, col7;
    @FXML
    private TableColumn<RecordModel, Double> col2, col3, col4;

    
    //Class variables
    private static File recordsFile = new File("nutritionRecords.dat");
    private static RandomAccessFile recordsAccess; 
    private int recordSize = 129; //Record size in bytes
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Initialize observable list
        ObservableList<RecordModel> observableRecords = FXCollections.observableArrayList();
        
        //Initialize columns in Table View
        col1.setCellValueFactory(new PropertyValueFactory<>("name"));
        col1.setResizable(false);
        col2.setCellValueFactory(new PropertyValueFactory<>("protein"));
        col2.setResizable(false);
        col3.setCellValueFactory(new PropertyValueFactory<>("carb"));
        col3.setResizable(false);
        col4.setCellValueFactory(new PropertyValueFactory<>("fat"));
        col4.setResizable(false);
        col5.setCellValueFactory(new PropertyValueFactory<>("category"));
        col5.setResizable(false);
        col6.setCellValueFactory(new PropertyValueFactory<>("nutrition"));
        col6.setResizable(false);
        col7.setCellValueFactory(new PropertyValueFactory<>("favourite"));
        col7.setResizable(false);

        //Open stream to random access file
        try {
            recordsAccess = new RandomAccessFile(recordsFile, "rw");
        } catch(FileNotFoundException e) {
            //IMPLEMENT WARNING DIALOG
        }
            
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
                observableRecords.add(new RecordModel(name, protein, carb, fat,
                category, nutrition, fave));
            }
        } catch(IOException e) {
            //IMPLEMENT THIS
        }
        
        //Place observable list in the table view
        recordsTbl.setItems(observableRecords);
        
        //BUTTON EVENTS
        
        //Menu btn takes user back to main menu
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
        
        //Search btn opens the search window
        searchBtn.setOnAction((ActionEvent event) -> {
            try {
                //Get current window from the btn element and close it
                Stage stage = (Stage)searchBtn.getScene().getWindow();
                stage.close();
                //Creeate new window and display it
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("SearchFXML.fxml"));
                Scene searchScene = new Scene(fxmlLoader.load(), 700, 550);
                stage = new Stage();
                stage.setResizable(false);
                stage.setTitle("Nutrition Tracker");
                stage.setScene(searchScene);
                stage.show();
            } catch (IOException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create new Window.", e);
            }
        });
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
    
}
