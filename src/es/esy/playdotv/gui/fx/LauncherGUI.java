package es.esy.playdotv.gui.fx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import es.esy.playdotv.gui.terminal.TermUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LauncherGUI extends Application{
	
	public FXMLLoader loader;
	public Parent root;
	public Scene scene;
	public Stage stage;
	public LauncherGUIController controllerInstance;

	@Override
	public void start(Stage primaryStage) throws Exception{
		loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
		root = loader.load();
		controllerInstance = loader.getController();
		controllerInstance.link(this);
		
		scene = new Scene(root);		
		
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setTitle("Library Book Scanner - Launcher");
		primaryStage.setResizable(false);
		primaryStage.show();

	}
	
	@Override
	public void stop() throws Exception{
		super.stop();
		LauncherGUIController.p.destroy();
		
	}
	
	public static void main(String[] args){
		TermUtils.init();
		if(System.getProperty("os.name").startsWith("Win")){
			try{
				Files.createDirectories(Paths.get("resources"));
				File splash = new File("resources" + File.separator + "splash.png");
				if(!splash.exists() || splash.isDirectory()){
					URL url = new URL("https://raw.githubusercontent.com/ShardBytes/library-book-scanner-launcher/master/splash.png");
					TermUtils.println("Downloading resources...");
					InputStream in = url.openStream();
					Files.copy(in, Paths.get("resources" + File.separator + "splash.png"));
				}
				
			}catch(FileAlreadyExistsException e){
				//Do nothing
			}catch(IOException e1){
				TermUtils.printerr("Cannot connect to Github.com! Exiting now");
				Platform.exit();
			}finally{
				TermUtils.println("Resources ready, launching");
			}
		}else{
			new Thread(() -> {
				try{
					Files.createDirectories(Paths.get("resources"));
					File splash = new File("resources" + File.separator + "splash.png");
					if(!splash.exists() || splash.isDirectory()){
						URL url = new URL("https://raw.githubusercontent.com/ShardBytes/library-book-scanner-launcher/master/splash.png");
						TermUtils.println("Downloading resources...");
						InputStream in = url.openStream();
						Files.copy(in, Paths.get("resources" + File.separator + "splash.png"));
					}
					
				}catch(FileAlreadyExistsException e){
					//Do nothing
				}catch(IOException e1){
					TermUtils.printerr("Cannot connect to Github.com! Exiting now");
					Platform.exit();
				}finally{
					TermUtils.println("Resources ready, launching");
				}
			}).start();
		}
		
		LauncherGUI.launch(args);

	}

}
