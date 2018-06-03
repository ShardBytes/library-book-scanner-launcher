package io.github.shardbytes.lbslauncher.gui.fx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import io.github.shardbytes.lbslauncher.gui.terminal.TermUtils;
import io.github.shardbytes.lbslauncher.update.AutoUpdate;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
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
	@FXML private JFXTextField db1;
	@FXML private JFXTextField db2;
	@FXML private PieChart pie1;
	@FXML private PieChart pie2;

	@SuppressWarnings("rawtypes")
	@FXML private JFXListView prefList;

	private LauncherGUI app;

	public static boolean isLBSRunning = false;
	public static boolean update = false;
	public static Process p;
	private volatile boolean completed = false;

	@FXML
	private void launchLBS(ActionEvent e){
		int selection = prefList.getSelectionModel().getSelectedIndex();
		String jvm_location;

		if(new File("resources" + File.separator + "splash.png").exists()
				&& new File("resources" + File.separator + "zoznam.xls").exists()
				&& new File("resources" + File.separator + "books.xls").exists()
				&& new File("resources" + File.separator + "persons.xls").exists()){
			if(System.getProperty("os.name").startsWith("Win")){
			    jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
			}else{
			    jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
			}

			ProcessBuilder pb = new ProcessBuilder(jvm_location, "-splash:resources" + File.separator + "splash.png", "-jar", "resources" + File.separator + prefList.getItems().get((selection == -1 ? 0 : selection)) + ".jar", LauncherGUI.LBSDatabaseLocation1, LauncherGUI.LBSDatabaseLocation2);
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
			new Thread(() -> {
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR, "Chyba pri sp\u00FA\u0161\u0165an\u00ED LBS. Skontrolujte pripojenie k internetu a spustite launcher znova.", ButtonType.OK);
					alert.setHeaderText("Chyba");
					alert.setTitle("Library Book Scanner Launcher [" + LauncherGUI.VERSION + "]");
					alert.show();
				});
			}).start();
		}

	}

	@FXML
	private void doUpdate(ActionEvent e) throws IOException{
		if(isLBSRunning){
			new Thread(() -> {
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR, "Ukon\u010Dite LBS a sk\u00FAste znova.", ButtonType.OK);
					alert.setHeaderText("Chyba");
					alert.setTitle("Library Book Scanner Launcher [" + LauncherGUI.VERSION + "]");
					alert.show();
				});
			}).start();
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
					refreshCurrentVersion();
					setCurrentVersionText();
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
		LauncherGUI.LBSDatabaseLocation1 = db1.getText();
		LauncherGUI.LBSDatabaseLocation2 = db2.getText();
		
		JSONObject obj = new JSONObject();
		obj.put("db1", LauncherGUI.LBSDatabaseLocation1);
		obj.put("db2", LauncherGUI.LBSDatabaseLocation2);

		try(FileWriter fw = new FileWriter(new File("launcherConfig.json"))){
			fw.write(obj.toString());
			fw.flush();
		} catch (IOException e1) {
			TermUtils.printerr("Cannot save config file");
		}
		TermUtils.println("Config file saved");

	}

	private void loadSettings(){
		String data = "";
		try(FileInputStream fis = new FileInputStream(new File("launcherConfig.json"))){
			try(BufferedReader buffer = new BufferedReader(new InputStreamReader(fis))){
				data = buffer.lines().collect(Collectors.joining(System.lineSeparator()));
			}
			JSONObject obj = new JSONObject(data);
			LauncherGUI.LBSDatabaseLocation1 = obj.get("db1").toString();
			LauncherGUI.LBSDatabaseLocation2 = obj.get("db2").toString();
			
			db1.setText(LauncherGUI.LBSDatabaseLocation1);
			db2.setText(LauncherGUI.LBSDatabaseLocation2);

		}catch(IOException e1){
			TermUtils.printerr("Cannot read config file");
		}
		TermUtils.println("Config file loaded");

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
		refreshCurrentVersion();
		
		try{
			update = AutoUpdate.updateAvailable();
		}catch(Exception e){
			TermUtils.printerr("Cannot connect to Github.com! Exiting now");
			Platform.runLater(() -> {
				Alert a = new Alert(Alert.AlertType.ERROR, "Ned\u00E1 sa pripoji\u0165 na Github.com!", ButtonType.CLOSE);
				a.setTitle("Library Book Scanner Launcher [" + LauncherGUI.VERSION + "]");
				a.setHeaderText("Chyba");
				a.show();
			});
			
		}

		Runnable runnableUpdate = () -> {
			while(1 < 2){
				if(update){
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.INFORMATION, "Je dostupn\u00E1 nov\u0161ia verzia LBS.", ButtonType.OK);
						alert.setHeaderText("Aktualiz\u00E1cia");
						alert.setTitle("Library Book Scanner Launcher [" + LauncherGUI.VERSION + "]");
						alert.show();
					});
					
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

		setCurrentVersionText();
		loadSettings();

		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(new PieChart.Data("Vypozicane", 35), new PieChart.Data("Dostupne", 10));
		ObservableList<PieChart.Data> pieData2 = FXCollections.observableArrayList(new PieChart.Data("Poezia", 400), new PieChart.Data("Beletria", 1000), new PieChart.Data("Naucna literatura", 750));

		pie1.setStartAngle(90);
		pie1.setTitle("Graf 1");
		pie1.setTitleSide(Side.TOP);
		pie1.setLegendVisible(true);
		pie1.setLabelsVisible(true);
		pie1.setLegendSide(Side.RIGHT);
		pie1.setData(pieData);

		pie2.setStartAngle(90);
		pie2.setTitle("Graf 2");
		pie2.setTitleSide(Side.TOP);
		pie2.setLegendVisible(true);
		pie2.setLabelsVisible(true);
		pie2.setLegendSide(Side.RIGHT);
		pie2.setData(pieData2);

	}

	@SuppressWarnings("unchecked")
	private void addFilesToList(){
		File[] versions = new File("resources" + File.separator).listFiles();
		Arrays.sort(versions);

		int versionsHalfLength = versions.length >> 1;

		for(int i = 0; i < versionsHalfLength; i++){
			File tempFile = versions[i];
			versions[i] = versions[versions.length - i - 1];
			versions[versions.length - i - 1] = tempFile;
		}

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

	private void refreshCurrentVersion(){
		try{
			ArrayList<String> versions = new ArrayList<>();

			for(Object versionObj : prefList.getItems()){
				versions.add(versionObj.toString().substring(22, versionObj.toString().length() - 1));
			}
			Comparator<String> customComparator = AutoUpdate::versionComparator;
			
			AutoUpdate.CURRENT_VERSION = Collections.max(versions, customComparator);				
		}catch(IndexOutOfBoundsException | NoSuchElementException e){
			AutoUpdate.CURRENT_VERSION = "Aktualiz\u00E1cia potrebn\u00E1";
		}

	}

	private void setCurrentVersionText(){
		installedVersionText.setText(AutoUpdate.CURRENT_VERSION);
		lastVersionText.setText(AutoUpdate.LATEST_VERSION);

	}

}
