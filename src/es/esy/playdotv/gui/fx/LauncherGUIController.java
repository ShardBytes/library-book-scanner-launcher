package es.esy.playdotv.gui.fx;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import es.esy.playdotv.gui.terminal.TermUtils;
import es.esy.playdotv.update.AutoUpdate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.text.Text;

public class LauncherGUIController implements Initializable{
	
	@FXML private JFXButton runPref;
	@FXML private JFXButton updateButton;
	@FXML private Text installedVersionText;
	@FXML private Text lastVersionText;
	@FXML private JFXButton saveButton;
	@FXML private JFXTextField username;
	@FXML private JFXPasswordField password;
	
	@SuppressWarnings("unused")
	private LauncherGUI app;
	
	public static boolean isLBSRunning = false;
	
	@FXML
	private void launchLBS(ActionEvent e){
		String jvm_location;
		if (System.getProperty("os.name").startsWith("Win")) {
		    jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
		} else {
		    jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		}
		
		ProcessBuilder pb = new ProcessBuilder(jvm_location, "-splash:resources/splash.png", "-jar", "resources/lbs.jar");
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectError(Redirect.PIPE);
		
		try{
			Process p = pb.start();
			isLBSRunning = true;
			Runnable runnableCheck = () -> {
				try{
					p.waitFor();
				}catch(InterruptedException e1){
					TermUtils.printerr("InterruptedException");
					System.exit(-1);
				}
				isLBSRunning = false;
			};
			Thread t = new Thread(runnableCheck);
			t.setName("IsRunning-Thread");
			t.start();
		}catch(IOException e1){
			e1.printStackTrace();
		}
		
	}
	
	@FXML
	private void doUpdate(ActionEvent e){
		
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
	public void initialize(URL location, ResourceBundle resources){
		installedVersionText.setText(AutoUpdate.CURRENT_VERSION);
		lastVersionText.setText(AutoUpdate.LATEST_VERSION);
		/*
		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(new PieChart.Data("Vypozicane", 35), new PieChart.Data("Dostupne", 10));
		
		pie1.setStartAngle(90);
		pie1.setTitle("Graf 1");
		pie1.setTitleSide(Side.TOP);
		pie1.setData(pieData);
		*/
		
	}
	
}
