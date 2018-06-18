package io.github.shardbytes.lbslauncher.gui.fx;

import com.sun.xml.internal.xsom.impl.Ref;
import io.github.shardbytes.lbslauncher.gui.terminal.TermUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LauncherGUI extends Application{
	
	Parent root;
	Stage stage;
	
	static String LBSDatabaseLocation = "data" + File.separator + "lbsdatabase.dat";
	
	public static final String VERSION = "v1.0.3";

	@Override
	public void start(Stage primaryStage) throws Exception{
		initLoading();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
		root = loader.load();
		LauncherGUIController controllerInstance = loader.getController();
		controllerInstance.link(this);
		
		Scene scene = new Scene(root);		
		
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setTitle("Library Book Scanner Launcher [" + VERSION + "]");
		primaryStage.setResizable(false);
		primaryStage.show();
		stage = primaryStage;

	}
	
	@Override
	public void stop() throws Exception{
		super.stop();
		try{
			LauncherGUIController.p.destroy();			
		}catch(Exception e){
			TermUtils.println("Cannot stop process: maybe LBS is not running?");
		}finally{			
			TermUtils.println("Exiting");
		}
		
	}
	
	public static void main(String[] args){
		LauncherGUI.launch();
	}
	
	public void initLoading(){
		/*
		 * Init coloured printer
		 */
		TermUtils.init();
		if(System.console() == null){
			System.setProperty("jansi.passthrough", "true");
		}
		try{
			final Stage dialog = new Stage(StageStyle.TRANSPARENT);
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdatePopup.fxml"));
			final Parent root = loader.load();
			final Scene scene = new Scene(root);
			final UpdatePopupController controller = loader.getController();
			
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.setAlwaysOnTop(true);
			dialog.setResizable(false);
			dialog.setScene(scene);
			
			Task<Void> downloadTask = new Task<Void>(){
				@Override
				protected Void call(){
					try{
						Files.createDirectories(Paths.get("resources"));
						Files.createDirectories(Paths.get("data"));
						File splashFile = new File("resources" + File.separator + "splash.png");
						File zoznamFile = new File("resources" + File.separator + "zoznam.xls");
						File booksFile = new File("resources" + File.separator + "books.xls");
						File personsFile = new File("resources" + File.separator + "persons.xls");
						if(!splashFile.exists() || splashFile.isDirectory()
								|| !zoznamFile.exists() || zoznamFile.isDirectory()
								|| !booksFile.exists() || booksFile.isDirectory()
								|| !personsFile.exists() || personsFile.isDirectory()){
							URL splash = new URL("https://raw.githubusercontent.com/ShardBytes/library-book-scanner-launcher/master/splash.png");
							URL zoznam = new URL("https://github.com/ShardBytes/library-book-scanner/raw/master/zoznam.xls");
							URL books = new URL("https://github.com/ShardBytes/library-book-scanner/raw/master/books.xls");
							URL persons = new URL("https://github.com/ShardBytes/library-book-scanner/raw/master/persons.xls");
							TermUtils.println("Downloading resources...");
							InputStream splash_in = splash.openStream();
							InputStream zoznam_in = zoznam.openStream();
							InputStream books_in = books.openStream();
							InputStream persons_in = persons.openStream();
							Files.copy(splash_in, Paths.get("resources" + File.separator + "splash.png"), StandardCopyOption.REPLACE_EXISTING);
							controller.setProgress(0, 100, 25);
							Files.copy(zoznam_in, Paths.get("resources" + File.separator + "zoznam.xls"), StandardCopyOption.REPLACE_EXISTING);
							controller.setProgress(0, 100, 50);
							Files.copy(books_in, Paths.get("resources" + File.separator + "books.xls"), StandardCopyOption.REPLACE_EXISTING);
							controller.setProgress(0, 100, 75);
							Files.copy(persons_in, Paths.get("resources" + File.separator + "persons.xls"), StandardCopyOption.REPLACE_EXISTING);
							controller.setProgress(0, 100, 100);
							
						}
						
					}catch(FileAlreadyExistsException ignored){
					}catch(Exception e){
						TermUtils.printerr("Something happened");
						TermUtils.printerr(e.getMessage());
					}finally{
						TermUtils.println("Resources ready, launching");
						dialog.close();
					}
					return null;
					
				}
				
			};
			
			downloadTask.setOnRunning((event) -> dialog.show());
			downloadTask.setOnSucceeded((event -> dialog.close()));
			downloadTask.setOnFailed((event -> Platform.exit()));
			
			new Thread(downloadTask).start();
			
		}catch(IOException e){
			TermUtils.printerr(e.getMessage());
		}

	}

}
