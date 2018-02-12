package es.esy.playdotv.gui.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LauncherGUI extends Application{
	
	public FXMLLoader loader;
	public AnchorPane root;
	public Scene scene;
	public Stage stage;

	@Override
	public void start(Stage primaryStage) throws Exception{
		loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
		root = loader.load();
		scene = new Scene(root);
		
		primaryStage.setTitle("Library Book Scanner - Launcher");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.sizeToScene();
		primaryStage.show();

	}
	
	public static void main(String[] args) {
		LauncherGUI.launch(args);

	}

}
