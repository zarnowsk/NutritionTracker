
package nutritiontracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Main class of the application containing main method
 *
 * @author Michal Zarnowski
 */
public class NutritionTracker extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainMenuFXML.fxml"));
        
        Scene mainScene = new Scene(root);
        
        stage.setScene(mainScene);
        stage.setTitle("Nutrition Tracker");
        stage.setResizable(false);
        stage.setHeight(550);
        stage.setWidth(700);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
