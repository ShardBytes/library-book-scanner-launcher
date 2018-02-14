package es.esy.playdotv.gui.fx;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

import com.jfoenix.controls.JFXButton;

import es.esy.playdotv.gui.terminal.TermUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class LauncherGUIController{
	
	@FXML private JFXButton runPref;
	@FXML private JFXButton updateButton;
	@FXML private Text installedVersionText;
	@FXML private Text lastVersionText;
	
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
	
}
