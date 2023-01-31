package app.cowordle.client;


import java.io.IOException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientApplication extends javafx.application.Application {

    //region Constants

    static final double STAGE_HEIGHT=739.0;
    static final double STAGE_WIDTH=581.0;

    //endregion

    //region Constructors
    public ClientApplication() { }

    //endregion

    //region Public Methods

    public static void main(String[] args) {
        launch(new String[0]);
    }

    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/login-view.fxml"));
            Scene scene = new Scene((Parent)fxmlLoader.load(), STAGE_HEIGHT, STAGE_WIDTH);

            stage.setTitle("Cowordle!");
            stage.setScene(scene);

            stage.setMaxHeight(STAGE_HEIGHT);
            stage.setMaxWidth(STAGE_WIDTH);
            stage.setMinHeight(STAGE_HEIGHT);
            stage.setMinWidth(STAGE_WIDTH);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //endregion
}
