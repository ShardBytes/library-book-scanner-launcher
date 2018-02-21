package es.esy.playdotv.gui.fx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import es.esy.playdotv.gui.terminal.TermUtils;
import es.esy.playdotv.update.AutoUpdate;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LauncherGUIController implements Initializable{
	
	@FXML private JFXButton runPref;
	@FXML private JFXButton updateButton;
	@FXML private Text installedVersionText;
	@FXML private Text lastVersionText;
	@FXML private JFXButton saveButton;
	@FXML private JFXTextField username;
	@FXML private JFXPasswordField password;
	
	@SuppressWarnings("rawtypes")
	@FXML private JFXListView prefList;
	
	private LauncherGUI app;
	
	public static boolean isLBSRunning = false;
	public static Process p;
	private volatile boolean completed = false;
	
	@FXML
	private void launchLBS(ActionEvent e){
		int selection = prefList.getSelectionModel().getSelectedIndex();
		String jvm_location;
		
		if(new File("resources" + File.separator + "splash.png").exists()){
			if(System.getProperty("os.name").startsWith("Win")){
			    jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
			}else{
			    jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
			}
			
			ProcessBuilder pb = new ProcessBuilder(jvm_location, "-splash:resources" + File.separator + "splash.png", "-jar", "resources" + File.separator + prefList.getItems().get((selection == -1 ? 0 : selection)) + ".jar");
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.PIPE);
			
			try{
				p = pb.start();
				isLBSRunning = true;
				Runnable runnableCheck = () -> {
					try{
						p.waitFor();
					}catch(InterruptedException e1){
						TermUtils.printerr("InterruptedException");
						Platform.exit();
					}
					isLBSRunning = false;
				};
				Thread t = new Thread(runnableCheck);
				t.setName("IsRunning-Thread");
				t.start();
			}catch(IOException e1){
				e1.printStackTrace();
			}
			
		}else{
			new Thread(() -> JOptionPane.showMessageDialog(null, "Chyba pri sp\u00FA\u0161\u0165an\u00ED LBS.", "Chyba", JOptionPane.ERROR_MESSAGE)).start();
		}
		
	}
	
	@FXML
	private void doUpdate(ActionEvent e) throws IOException{
		if(isLBSRunning){
			new Thread(() -> JOptionPane.showMessageDialog(null, "Ukon\u010Dite LBS a sk\u00FAste znova.", "Chyba", JOptionPane.ERROR_MESSAGE)).start();
		}else{
			final Stage dialog = new Stage(StageStyle.TRANSPARENT);
			final Stage mainWindow = app.stage;
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdatePopup.fxml"));
			final Parent root = loader.load();
			final Scene scene = new Scene(root);
			final UpdatePopupController controllerInstance = loader.getController();

			ColorAdjust adj = new ColorAdjust(0.0d, -0.9d, -0.5d, 0.0d);
			GaussianBlur blur = new GaussianBlur(10);
			adj.setInput(blur);
			app.root.setEffect(adj);

			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(app.stage);
			dialog.setAlwaysOnTop(true);
			dialog.setResizable(false);
			dialog.setScene(scene);

			ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
				double stageWidth = newValue.doubleValue();
				dialog.setX(mainWindow.getX() + mainWindow.getWidth() / 2 - stageWidth / 2);
			};
			ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
				double stageHeight = newValue.doubleValue();
				dialog.setY(mainWindow.getY() + mainWindow.getHeight() / 2 - stageHeight / 2);   
			};

			dialog.widthProperty().addListener(widthListener);
			dialog.heightProperty().addListener(heightListener);
			
			dialog.setOnShown((x) -> {
				dialog.widthProperty().removeListener(widthListener);
				dialog.heightProperty().removeListener(heightListener);
			});

			dialog.show();
			
			URL web = new URL(AutoUpdate.GithubData.getAssets_download_url());
			Path out = Paths.get("resources" + File.separator + AutoUpdate.GithubData.getRelease_name() + ".jar");
			
			Runnable downloadRunnable = () -> {
				try(InputStream in = web.openStream()){
					Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
					completed = true;
				}catch(IOException e1){
					completed = false;
					TermUtils.printerr("IOException");
				}
			};
			Runnable checkRunnable = () -> {
				File outFile = out.toFile();
				long toDownload = Long.parseLong(AutoUpdate.GithubData.getAssets_size());
				long downloaded = outFile.length();
				while(!completed){
					controllerInstance.setProgress(0, toDownload, downloaded);
					downloaded = outFile.length();
				}
				completed = false;
				try{
					Thread.sleep(1000);
				}catch(InterruptedException e1){
					TermUtils.printerr("InterruptedException");
				}
				Platform.runLater(() -> {
					dialog.close();
					app.root.setEffect(null);
					prefList.getItems().clear();
					addFilesToList();
				});
			};
			Thread downloadThread = new Thread(downloadRunnable);
			Thread checkThread = new Thread(checkRunnable);
			downloadThread.start();
			checkThread.start();
		}
		
	}
	
	@FXML
	private void saveSettings(ActionEvent e){
		/*
		 * TODO: Také pekné okno čo vyskočí vnútri a pozadie bude rozmazané; pridávanie používateľov maybe?
		 */
	}
	
	public void link(LauncherGUI l){
		app = l;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initialize(URL location, ResourceBundle resources){
		prefList.setEditable(false);
		prefList.setCellFactory(TextFieldListCell.forListView());
		
		addFilesToList();
		try{
			AutoUpdate.CURRENT_VERSION = prefList.getItems().get(0).toString().substring(22, prefList.getItems().get(0).toString().length() - 1);			
		}catch(IndexOutOfBoundsException e){
			AutoUpdate.CURRENT_VERSION = "Aktualiz\u00E1cia potrebn\u00E1";
		}
		Runnable runnableUpdate = () -> {
			while(1 < 2){
				if(AutoUpdate.updateAvailable()){
					JOptionPane.showMessageDialog(null, "Je dostupn\u00E1 nov\u0161ia verzia LBS.", "Aktualiz\u00E1cia", JOptionPane.INFORMATION_MESSAGE);
				}
				try{
					Thread.sleep(1800000);
				}catch(InterruptedException e){
					TermUtils.printerr("InterruptedException");
				}
				
			}
			
		};
		Thread t = new Thread(runnableUpdate);
		t.setDaemon(true);
		t.setName("GitUpdate-Thread");
		t.start();
		
		installedVersionText.setText(AutoUpdate.CURRENT_VERSION);
		lastVersionText.setText(AutoUpdate.LATEST_VERSION);

	}
		/*
		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(new PieChart.Data("Vypozicane", 35), new PieChart.Data("Dostupne", 10));
		
		pie1.setStartAngle(90);
		pie1.setTitle("Graf 1");
		pie1.setTitleSide(Side.TOP);
		pie1.setData(pieData);
		*/
	
	@SuppressWarnings("unchecked")
	private void addFilesToList(){
		File[] versions = new File("resources" + File.separator).listFiles();
		
		for(File f : versions){
			if(f.isDirectory()){
				continue;
			}else{
				if(f.toString().startsWith("resources" + File.separator + "Library Book Scanner [v") && f.toString().endsWith("].jar")){
					prefList.getItems().add(f.toString().substring(10, f.toString().length() - 4));
				}

			}

		}
		
	}
	
}
