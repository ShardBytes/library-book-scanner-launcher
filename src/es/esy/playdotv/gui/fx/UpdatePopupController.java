package es.esy.playdotv.gui.fx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class UpdatePopupController implements Initializable{
	
	@FXML private ProgressBar pgb1;
	@FXML private ProgressIndicator pgi1;
	
	@Override
	public void initialize(URL location, ResourceBundle resources){
		pgb1.setProgress(0.78);
		pgi1.setProgress(0.78);
	}

}
