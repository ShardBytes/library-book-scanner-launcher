package es.esy.playdotv.gui.fx;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import es.esy.playdotv.gui.terminal.TermUtils;
import es.esy.playdotv.update.AutoUpdate;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LauncherGUI extends Application{
	
	public FXMLLoader loader;
	public Parent root;
	public Scene scene;
	public Stage stage;
	public LauncherGUIController controller;

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
	
	public static void main(String[] args){
		TermUtils.init();
		try{
			TermUtils.println("Connecting to github...");
			URL url = new URL("https://raw.githubusercontent.com/ShardBytes/library-book-scanner-launcher/master/splash.png");
			
			TermUtils.println("Downloading resources...");
			InputStream in = url.openStream();
			Files.createDirectories(Paths.get("resources"));
			Files.copy(in, Paths.get("resources/splash.png"));
			
			TermUtils.println("Resources ready, launching");
		}catch(FileAlreadyExistsException e){
			//Do nothing
		}catch(IOException e1){
			TermUtils.printerr("Cannot connect to Github.com! Exiting now");
			System.exit(-1);
		}
		
		Runnable runnableUpdate = () -> {
			AutoUpdate.updateData();
			if(AutoUpdate.updateAvailable()){
				int selection = JOptionPane.showConfirmDialog(null, "Dostupn\u00E1 aktualiz\u00E1cia, stiahnu\u0165 a nain\u0161talova\u0165?", "Aktualiz\u00E1cia", JOptionPane.YES_NO_OPTION);
				if(selection == JOptionPane.YES_OPTION && !LauncherGUIController.isLBSRunning){
					try{
						AutoUpdate.update();
					}catch(IOException e){
						e.printStackTrace();
					}
				}else if(selection == JOptionPane.YES_OPTION && LauncherGUIController.isLBSRunning){
					JOptionPane.showMessageDialog(null, "Ukon\u010Dite LBS a sk\u00FAste znova.", "Chyba", JOptionPane.ERROR_MESSAGE);
				}
				
			}
			try{
				Thread.sleep(1800000);
			}catch(InterruptedException e){
				TermUtils.printerr("InterruptedException");
			}
			
		};
		Thread t = new Thread(runnableUpdate);
		t.setDaemon(true);
		t.setName("GitUpdate-Thread");
		t.start();
		
		LauncherGUI.launch(args);

	}

}
