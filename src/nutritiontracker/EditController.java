/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 * FXML Controller class
 *
 * @author Michal Zarnowski
 */
public class EditController implements Initializable {

    @FXML
    private Button cancelBtn;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //BUTTON EVENTS
        
        //Menu btn takes user back to main menu
        cancelBtn.setOnAction((ActionEvent event) -> {
                //Get current window from the btn element and close it
                Stage stage = (Stage)cancelBtn.getScene().getWindow();
                stage.close();
        });
    }    
    
}
