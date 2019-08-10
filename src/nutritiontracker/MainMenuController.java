
package nutritiontracker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/** Controller class in charge of the Main Menu window
 *
 * @author Michal Zarnowski
 */
public class MainMenuController implements Initializable {
    
    //FXML variables
    @FXML
    private Button viewBtn, addBtn, searchBtn, catBasedBtn, favBtn, nutBasedBtn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //View btn opens the view records window
        viewBtn.setOnAction((ActionEvent event) -> {
            try {
                //Get current window from the btn element and close it
                Stage stage = (Stage)viewBtn.getScene().getWindow();
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
        });
        
        //Add btn opens the add records window
        addBtn.setOnAction((ActionEvent event) -> {
            try {
                //Get current window from the btn element and close it
                Stage stage = (Stage)addBtn.getScene().getWindow();
                stage.close();
                //Creeate new window and display it
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("AddFXML.fxml"));
                Scene addScene = new Scene(fxmlLoader.load(), 700, 550);
                stage = new Stage();
                stage.setResizable(false);
                stage.setTitle("Nutrition Tracker");
                stage.setScene(addScene);
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
        
        //Open category report window
        catBasedBtn.setOnAction((ActionEvent event) -> {
            try {
                //Get current window from the btn element and close it
                Stage stage = (Stage)catBasedBtn.getScene().getWindow();
                stage.close();
                //Creeate new window and display it
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("CategoryReportFXML.fxml"));
                Scene searchScene = new Scene(fxmlLoader.load(), 500, 550);
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
        
        //Open favourite report window
        favBtn.setOnAction((ActionEvent event) -> {
            try {
                //Get current window from the btn element and close it
                Stage stage = (Stage)favBtn.getScene().getWindow();
                stage.close();
                //Creeate new window and display it
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("FavouriteReportFXML.fxml"));
                Scene searchScene = new Scene(fxmlLoader.load(), 500, 550);
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
        
        //Open nutrition report window
        nutBasedBtn.setOnAction((ActionEvent event) -> {
            try {
                //Get current window from the btn element and close it
                Stage stage = (Stage)nutBasedBtn.getScene().getWindow();
                stage.close();
                //Creeate new window and display it
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("NutritionReportFXML.fxml"));
                Scene searchScene = new Scene(fxmlLoader.load(), 500, 550);
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
    
}
