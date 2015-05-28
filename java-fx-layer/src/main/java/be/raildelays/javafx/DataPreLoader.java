package be.raildelays.javafx;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Almex
 * @since 1.2
 */
public class DataPreLoader extends Preloader {

    private ProgressBar progressBar;
    private StackPane root;
    private Stage preLoaderStage;
    private long startDownload = -1;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataPreLoader.class);

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof PreLoaderHandoverEvent) {
            // handover from pre-loader to application
            final PreLoaderHandoverEvent event = (PreLoaderHandoverEvent) info;

            Platform.runLater(event.getDataLoadingTask());
        } else if (info instanceof StateChangeNotification &&
                ((StateChangeNotification) info).getType() == StateChangeNotification.Type.BEFORE_START) {
            //hide after get any state update from application
            preLoaderStage.hide();

            LOGGER.info("Finished pre-loading!");
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        /*Timeline simulatorTimeline = new Timeline();

        if (info.getType() == StateChangeNotification.Type.BEFORE_INIT) {
            // check for fast download and restart progress
            if ((System.currentTimeMillis() - startDownload) < 500) {
                progressBar.setProgress(0);a
            }*/
        /**
         * We have finished downloading application. Now, we are
         * running application init() method. As we have no way
         * to calculate real progress, we simulate a pretended
         * progress here.
         */
        /*simulatorTimeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(15),
                            new KeyValue(progressBar.progressProperty(), 1)));
            simulatorTimeline.play();
        }*/
    }

    @Override
    public void handleProgressNotification(ProgressNotification info) {
        if (startDownload == -1) {
            startDownload = System.currentTimeMillis();
            progressBar.setProgress(-1);
        } else {
            progressBar.setProgress(-1);
        }
    }

    @Override
    public void init() throws Exception {
        Image image = new Image(getClass().getResource("/splash-screen-350x240.jpg").toString());
        BackgroundImage backgroundImage = new BackgroundImage(image, null, null, null, null);
        StackPane stackPane = new StackPane();

        root = new StackPane();
        progressBar = new ProgressBar(0.0);
        progressBar.setMaxHeight(3);
        progressBar.setMinWidth(350);
        stackPane.setId("Window");
        stackPane.setCache(true);
        stackPane.setBackground(new Background(backgroundImage));
        root.setAlignment(Pos.BOTTOM_CENTER);
        root.getChildren().addAll(stackPane, progressBar);

        LOGGER.info("Pre-loader initialized!");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene preLoaderScene = new Scene(root, 350, 240);

        preLoaderScene.setFill(Color.TRANSPARENT);
        preLoaderStage = primaryStage;
        preLoaderStage.initStyle(StageStyle.TRANSPARENT);
        preLoaderStage.setScene(preLoaderScene);
        preLoaderStage.show();
    }

}
