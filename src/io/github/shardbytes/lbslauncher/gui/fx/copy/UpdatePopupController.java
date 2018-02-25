package io.github.shardbytes.lbslauncher.gui.fx.copy;

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
		pgb1.setProgress(0.0d);
		pgi1.setProgress(0.0d);
	}
	
	public void setProgress(double min, double max, double current){
		double progress = calculatePercent(min, max, current) / 100;
		pgb1.setProgress(progress);
		pgi1.setProgress(progress);
	}
	
	public void setIndeterminate(){
		pgb1.setProgress(-1);
		pgi1.setProgress(-1);
	}
	
	private double calculatePercent(double min, double max, double current){
		/*
		 * max     ... 100%
		 * min     ... 0%
		 * current ... x%
		 * ----------------
		 *     (current - min) * 100
		 * x = ---------------------
		 *           max - min
		 */
		return ((current - min) * 100) / (max - min);
	}

}
