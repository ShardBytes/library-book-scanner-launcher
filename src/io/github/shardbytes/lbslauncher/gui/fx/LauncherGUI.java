package io.github.shardbytes.lbslauncher.gui.fx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import io.github.shardbytes.lbslauncher.gui.terminal.TermUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LauncherGUI extends Application{
	
	Parent root;
	Stage stage;
	
	static String LBSDatabaseLocation1 = "data" + File.separator + "lbsdatabase.xml";
	static String LBSDatabaseLocation2 = "data" + File.separator + "borrowings.xml";
	
	static final String VERSION = "v1.0.2";

	@Override
	public void start(Stage primaryStage) throws Exception{
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
		TermUtils.init();
		if(System.getProperty("os.name").startsWith("Win")){
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
					Files.copy(zoznam_in, Paths.get("resources" + File.separator + "zoznam.xls"), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(books_in, Paths.get("resources" + File.separator + "books.xls"), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(persons_in, Paths.get("resources" + File.separator + "persons.xls"), StandardCopyOption.REPLACE_EXISTING);
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
						Files.copy(splash_in, Paths.get("resources" + File.separator + "splash.png"));
						Files.copy(zoznam_in, Paths.get("resources" + File.separator + "zoznam.xls"));
						Files.copy(books_in, Paths.get("resources" + File.separator + "books.xls"));
						Files.copy(persons_in, Paths.get("resources" + File.separator + "persons.xls"));
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
