package application;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import dal.DataAccessLayer;
import dal.exceptions.CouldNotConnectException;
import dal.exceptions.EntityNotFoundException;
import dal.exceptions.NotConnectedException;
import dal.exceptions.ValidationException;
import dal.impl.VasutDal;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import model.JaratFejlec;
import model.Jarat;
import model.JaratVonatszam;

/**
 * Class for implementing the logic.
 */
public class Controller implements Initializable {
	private DataAccessLayer<JaratFejlec, Jarat, JaratVonatszam> dal;

	@FXML
	TextField usernameField;
	@FXML
	TextField passwordField;
	@FXML
	Button connectButton;
	@FXML
	Label connectionStateLabel;
	@FXML
	TextField searchTextField;

	@FXML
	ComboBox<ComboBoxItem<String>> tipusCombo;

	@FXML
	TableColumn<JaratVonatszam, Integer> vonatszamStatColumn;
	@FXML
	TableColumn<JaratFejlec, Integer> vonatszamColumn;
	@FXML
	TableColumn<JaratFejlec, String> tipusColumn;
	@FXML
	TableColumn<JaratFejlec, String> megjegyzesColumn;
	@FXML
	TableView<JaratFejlec> searchTable;
	@FXML
	TableView<JaratVonatszam> statisticsTable;

	@FXML
	TextField vonatszamTextField;
	@FXML
	TextField napTextField;
	@FXML
	TextField kezdTextField;
	@FXML
	TextField vegeTextField;
	@FXML
	TextField megjegyzesTextField;

	@FXML
	Label errorLabel;

	public Controller() {
		dal = new VasutDal();
	}

	@FXML
	public void connectEventHandler(ActionEvent event) {
		//Getting the input from the UI.
		String username = usernameField.getText();
		String password = passwordField.getText();

		try {
			//Connect to the database, and update the UI
			dal.connect(username, password);
			connectionStateLabel.setText("Connection created!");
			connectionStateLabel.setTextFill(Paint.valueOf("green"));
		} catch (ClassNotFoundException e) {
			//Driver is not found
			connectionStateLabel.setText("JDBC driver not found!");
			connectionStateLabel.setTextFill(Paint.valueOf("red"));
		} catch (CouldNotConnectException e) {
			//Could not connect, e.g. invalid username or password
			connectionStateLabel.setText("Could not connect to the server!");
			connectionStateLabel.setTextFill(Paint.valueOf("red"));
		}
	}

	@FXML
	public void searchEventHandler() {
		try {
			/* Már nem a példa megoldás */
			List<JaratFejlec> trains = dal.search(searchTextField.getText());
			searchTable.setItems(FXCollections.observableList(trains));
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void commitEventHandler() {
		//TODO: handle the click of the commit button.

	}

	@FXML
	public void editEventHandler() {
		try{
			Jarat uj = new Jarat();						   //Jarat, a tagváltozói később kapnak értéket
			try {
				uj.parseVonatszam(vonatszamTextField.getText());
				uj.parseTipus(tipusCombo.getValue().getValue());
				uj.parseNap(napTextField.getText());
				if (kezdTextField.getText().equals("")) {  //nem kötelező
					uj.setKezd(null);
				} else {
					uj.parseKezd(kezdTextField.getText());
				}
				if (vegeTextField.getText().equals("")) {  //szintén nem az
					uj.setVege(null);
				} else {
					uj.parseVege(vegeTextField.getText());
				}
				uj.parseMegjegyzes(megjegyzesTextField.getText());
				String result = dal.insertOrUpdate(uj, uj.getVonatszam()).toString();
				errorLabel.setText(result);				   //insert vagy update vagy error
			} catch (ValidationException e) {
				errorLabel.setText(e.getMessage() + "\n" + e.getFieldName()); //3. feladat is használja a labelt
			}
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void statisticsEventHandler() {
		try {
			List<JaratVonatszam> trainnumbers = dal.getStatistics();
			statisticsTable.setItems(FXCollections.observableList(trainnumbers));
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		/*Tipus comboBox választható értékei*/
		tipusCombo.getItems().add(new ComboBoxItem<String>("EC", "EC"));
		tipusCombo.getItems().add(new ComboBoxItem<String>("IC", "IC"));
		tipusCombo.getItems().add(new ComboBoxItem<String>("Gyors", "Gyors"));
		tipusCombo.getItems().add(new ComboBoxItem<String>("szemely", "szemely"));

		/*Search  Listázáshoz szükségesek*/
		vonatszamColumn.setCellValueFactory(new PropertyValueFactory<>("Vonatszam"));
		tipusColumn.setCellValueFactory(new PropertyValueFactory<>("Tipus"));
		megjegyzesColumn.setCellValueFactory(new PropertyValueFactory<>("Megjegyzes"));

		/* Statistics egyetlen oszlopa */
		vonatszamStatColumn.setCellValueFactory(new PropertyValueFactory<>("Vonatszam"));
	}

	public void disconnect() {
		dal.disconnect();
	}


}
