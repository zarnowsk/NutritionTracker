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
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Michal Zarnowski
 */
public class ViewController implements Initializable {

    //FXML variables
    @FXML
    private Button menuBtn, searchBtn, nextBtn, prevBtn, favouriteBtn, deleteBtn, editBtn;
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
    private static ObservableList<RecordModel> observableRecords;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Initialize observable list
        observableRecords = FXCollections.observableArrayList();
        
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
        
        //Display records in the table view
        populateTable();
        recordsTbl.getSelectionModel().select(0);
            
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
                
                recordsAccess.close();
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
                
                recordsAccess.close();
            } catch (IOException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create new Window.", e);
            }
        });
        
        //Select record before currently selected record
        prevBtn.setOnAction((ActionEvent event) -> {
            int currentSelection = recordsTbl.getSelectionModel().getSelectedIndex();
            if(currentSelection != 0) {
                recordsTbl.getSelectionModel().select(currentSelection - 1);
            }
        });
        
        //Select record after currently selected record
        nextBtn.setOnAction((ActionEvent event) -> {
            int currentSelection = recordsTbl.getSelectionModel().getSelectedIndex();
            if(currentSelection < (observableRecords.size() - 1)) {
                recordsTbl.getSelectionModel().select(currentSelection + 1);
            }
        });
        
        //Toggle favourite on/off of currently selected record
        favouriteBtn.setOnAction((ActionEvent event) -> {
            //Get product name of currently selected record
            int recordIndex = recordsTbl.getSelectionModel().getSelectedIndex();
            String product = recordsTbl.getSelectionModel().getSelectedItem().getName();
            
            //Get byte position of selected record in the file
            int productPosition = findProductPosition(product);
            
            //Get current value of favourite field
            boolean favouriteValue = getFavouriteValue(productPosition);
            
            //Edit favourite field value to opposit of current status
            toggleFavourite(productPosition, favouriteValue);
            
            //Re-display updated records in the table with same record selected
            populateTable();
            recordsTbl.getSelectionModel().select(recordIndex);
            
        });
        
        //Delete celected record
        deleteBtn.setOnAction((ActionEvent event) -> {
            //Confirm action with the user
            String title = "Delete record";
            String header = "You're about to delete a record";
            String content = "Are you sure you want to proceed?";
            Optional<ButtonType> result = displayConfirmation(title, header, content);
            
            //If user confirms, proceed to delete the record
            if(result.get() == ButtonType.OK) {
                //Delete record and re-populate updated table
                deleteRecord();
                populateTable();
            }
        });
        
        //Edit record
        editBtn.setOnAction((ActionEvent event) -> {
            //Open a new window for editing records
            try {
                //Get current window from the btn element and close it
                Stage stage = (Stage)editBtn.getScene().getWindow();
                stage.close();
                //Creeate new window and display it
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("EditFXML.fxml"));
                Scene editScene = new Scene(fxmlLoader.load(), 500, 350);
                Stage editStage = new Stage();
                editStage.initStyle(StageStyle.UNDECORATED);
                editStage.setResizable(false);
                editStage.setTitle("Edit Product");
                editStage.setScene(editScene);
                editStage.show();
                
                //Get controller of edit window
                EditController editController = fxmlLoader.getController();
                //Pass selected record to edit controller
                int recordToPassIndex = recordsTbl.getSelectionModel().getFocusedIndex();
                RecordModel recordToPass = getRecordsAsList().get(recordToPassIndex);
                int editedRecordPosition = findProductPosition(recordsTbl.getSelectionModel().getSelectedItem().getName());
                editController.passRecord(recordToPass, editedRecordPosition);
                                
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
    
    /**
     * Method populates the table in view screen with records from the file
     * @param observableRecords Observable list object to hold records
     */
    protected void populateTable() {
        //Clear list of records displayed previously
        observableRecords.clear();
        
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

    }
    
    /**
     * Method returns the byte position of the currently selected record
     * @param name Product name field of the record we're searching for
     * @return Byte position of the record we're searching for
     */
    private int findProductPosition(String name) {
        String readName = "";
        try {
            //Traverse through all product names in the file until a match is found
            //to name supplied to method
            for(int i = 0; i < recordsAccess.length(); i+=129) {
                //Move cursor to begginning of each record
                recordsAccess.seek(i);
                //Read product name, first 30 chars
                for(int j = 0; j < 30; j++) {
                    readName += recordsAccess.readChar();
                }
                //If product name in the file matches supplied name, return cursor position
                if(readName.equals(name)) {
                    return i;                    
                }

                readName = "";
            }
        } catch(IOException e) {
            //IMPLEMENT THIS
        }

        return 0;
    }
    
    /**
     * Method returns current value of the favourite field of the selected record
     * @param position Byte position of currently selected record
     * @return Value of the favourite field of the selected record
     */
    private boolean getFavouriteValue(int position) {
        try {
            //Move pointer to favourite field position in the record
            recordsAccess.seek(position + 128);
            //Return the value of favourite field
            return recordsAccess.readBoolean();
        } catch(IOException e) {
            //IMPLEMENT THIS
        }
        return false;
    }
    
    /**
     * Method changes the favourite field value to opposite of current value of the 
     * currently selected record
     * @param position Byte position of currently selected record
     * @param currentValue Current favourite field value
     */
    private void toggleFavourite(int position, boolean currentValue) {
        //Set new value to opposit of current value
        boolean newValue;
        if(currentValue == false) {
            newValue = true;
        } else {
            newValue = false;
        }

        try {
            //Move file pointer to favourite field of currently selected record
            recordsAccess.seek(position + 128);
            //Overwrite favourite field value to new value
            recordsAccess.writeBoolean(newValue);
        } catch(IOException e) {
            //IMPLEMENT THIS
        }
    }
    
    /**
     * Method displays a confirmation pop-ip window and returns user's selection
     * @param title String to display as title of pop-up window
     * @param header String to display as header of pop-up window
     * @param content String to display as content of pop-up window
     * @return Button value of user's selection
     */
    private Optional<ButtonType> displayConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        return alert.showAndWait();
    }
    
    /**
     * Method returns all records as an array list of RecordModel type.
     * @return ArrayList containing RecordModel variables
     */
    private ArrayList<RecordModel> getRecordsAsList() {
        //Variables for the method
        String name, category, nutCategory;
        double protein, carb, fat;
        boolean favourite;
        ArrayList<RecordModel> tempRecords = new ArrayList<>();
        
        try {
            long numOfRecords = recordsAccess.length() / 129;
            recordsAccess.seek(0);
            //Add all records from the file into a temporary array list
            for(int i = 0; i < numOfRecords; i++) {
                name = readString(recordsAccess, 30);
                protein = recordsAccess.readDouble();
                carb = recordsAccess.readDouble();
                fat = recordsAccess.readDouble();
                category = readString(recordsAccess, 15);
                nutCategory = readString(recordsAccess, 7);
                favourite = recordsAccess.readBoolean();
                
                tempRecords.add(new RecordModel(name, protein, carb, fat,
                category, nutCategory, favourite+""));
            }
        } catch(IOException e) {
            //IMPLEMENT THIS
        }
        
        return tempRecords;
    }
    
    /**
     * Method deletes currently selected record from the file
     */
    private void deleteRecord() {
        //Use method to obtain all records as array list
        ArrayList<RecordModel> tempRecords = getRecordsAsList();
        int numOfRecords = tempRecords.size();
      
        try {
            //Remove currently selected record from the array list
            int recordToRemove = recordsTbl.getSelectionModel().getSelectedIndex();
            tempRecords.remove(recordToRemove);
            
            //Remove all records from the file
            recordsAccess.setLength(0);
            recordsAccess.seek(0);
            
            //Repopulate the file with all remaining records
            for(int i = 0; i < (numOfRecords - 1); i++) {
                recordsAccess.writeChars(tempRecords.get(i).getName());
                recordsAccess.writeDouble(tempRecords.get(i).getProtein());
                recordsAccess.writeDouble(tempRecords.get(i).getCarb());
                recordsAccess.writeDouble(tempRecords.get(i).getFat());
                recordsAccess.writeChars(tempRecords.get(i).getCategory());
                recordsAccess.writeChars(tempRecords.get(i).getNutrition());
                recordsAccess.writeBoolean(Boolean.parseBoolean(tempRecords.get(i).getFavourite()));
            }
                        
        } catch(IOException e) {
            //IMPLEMENT THIS
        }
    }
}
