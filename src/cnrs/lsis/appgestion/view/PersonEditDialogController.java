package cnrs.lsis.appgestion.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import cnrs.lsis.appgestion.MainApp;
import cnrs.lsis.appgestion.model.Person;
import cnrs.lsis.appgestion.model.User;
import cnrs.lsis.appgestion.util.DateUtil;
import eu.hansolo.enzo.notification.Notification;
import eu.hansolo.enzo.notification.Notification.Notifier;

/**
 * Dialog to edit details of a person.
 * 
 * @author Marco Jakob
 */
public class PersonEditDialogController {

	// Reference to the main application
    private MainApp mainApp;
	
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField birthdayField;
    @FXML
    private TextField entranceField;
    @FXML
    private TextField exitField;
    @FXML
    private TextField desktopField;
    @FXML
    private ImageView photo;
    @FXML
    private TextField statusField;
    @FXML
    private TextField teamField;
//    @FXML
//    private TextField emailField;
    private String path ="";

    private Stage dialogStage;
    private Person person;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        
        // Set the dialog icon.
        Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/edit.png"));
        this.dialogStage.getIcons().add(img);
    }

    /**
     * Sets the person to be edited in the dialog.
     * 
     * @param person
     */
    public void setPerson(Person person) {
        this.person = person;
        photo.setImage(new Image(person.getPhotoPath()));
        path = person.getPhotoPath().replaceFirst("file:", "");
        firstNameField.setText(person.getFirstName());
        lastNameField.setText(person.getLastName());
        streetField.setText(person.getStreet());
        postalCodeField.setText(person.getPostalCode());
        cityField.setText(person.getCity());
        birthdayField.setText(DateUtil.format(person.getBirthday()));
        birthdayField.setPromptText("dd.mm.yyyy");
        entranceField.setText(DateUtil.format(person.getEntranceDate()));
        entranceField.setPromptText("dd.mm.yyyy");
        exitField.setText(DateUtil.format(person.getExitDate()));
        exitField.setPromptText("dd.mm.yyyy");
        desktopField.setText(person.getDesktop());
        statusField.setText(person.getStatus());
        teamField.setText(person.getTeam());
        //emailField.setText(person.getEmail());
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    
    /**
     * Opens a FileChooser to let the user select an image to load.
     */
    @FXML
    private void handleLoadImage() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter PNG = new FileChooser.ExtensionFilter(
                "PNG files (*.png)", "*.png");
        FileChooser.ExtensionFilter JPG = new FileChooser.ExtensionFilter(
                "JPG files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter ALL = new FileChooser.ExtensionFilter(
                "All files (*)", "*");
        fileChooser.getExtensionFilters().addAll(JPG, PNG, ALL);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(this.dialogStage);

        if (file != null) {
        	path = file.getAbsolutePath();
        	photo.setImage(new Image("file:"+file.getAbsolutePath()) );
        }
    }
    
    
    /**
     * Remove the image path to a default one.
     */
    @FXML
    private void handleRemoveImage() {
    		Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/person.png"));
        	photo.setImage( img);
        	path = MainApp.class.getResource("/resources/images/person.png").toExternalForm();
    }

    
    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
        	
        	person.setPhotoPath("file:"+path);
            person.setFirstName(firstNameField.getText());
            person.setLastName(lastNameField.getText());
            person.setStreet(streetField.getText());
            person.setPostalCode(postalCodeField.getText());
            person.setCity(cityField.getText());
            person.setBirthday(DateUtil.parse(birthdayField.getText()));
            person.setEntranceDate(DateUtil.parse(entranceField.getText()));
            person.setExitDate(DateUtil.parse(exitField.getText()));
            person.setDesktop(desktopField.getText());
            // Check if the status has changed and add a new history instance if it is the case
            if(!person.getStatus().equals(statusField.getText())){
            	person.setStatus(statusField.getText());
            	try{
	            	String history = person.getHistory();
	            	if(history.isEmpty()){
		                ObjectMapper mapper = new ObjectMapper();
	            		User user = new User();
		    			user.setFirstname(firstNameField.getText());
		    			user.setLastname(lastNameField.getText());
		    			user.setStatus(statusField.getText());
		    			user.setTeam(teamField.getText());
//		    			user.setEmail(emailField.getText());
		    			user.setBirthday(birthdayField.getText());
		    			user.setEntranceDate(entranceField.getText());
		    			user.setExitDate(exitField.getText());
		    			user.setDesktop(desktopField.getText());
		    			user.setCity(cityField.getText());
		    			user.setStreet(streetField.getText());
		    			user.setPostalCode(postalCodeField.getText());
		    			//Update a new listUserHistory to avoid concurrency exception
		    			List<User> listUserHistory2 = new ArrayList<User>();
		    			listUserHistory2.add(user);
		    			//Object to JSON in String
		    			String jsonInString = mapper.writeValueAsString(listUserHistory2);
		    			// Add the new history json string into the person history
		    			person.setHistory(jsonInString);
	            	}else{
		                ObjectMapper mapper = new ObjectMapper();
		    			List<User> listUserHistory = Arrays.asList(mapper.readValue(history, User[].class));
		    			User user = new User();
		    			user.setFirstname(firstNameField.getText());
		    			user.setLastname(lastNameField.getText());
		    			user.setStatus(statusField.getText());
		    			user.setTeam(teamField.getText());
		    			//Update a new listUserHistory to avoid concurrency exception
		    			List<User> listUserHistory2 = new ArrayList<User>();
		    			listUserHistory2.add(user);
		    			listUserHistory2.addAll(listUserHistory);
		    			//Object to JSON in String
		    			String jsonInString = mapper.writeValueAsString(listUserHistory2);
		    			// Add the new history json string into the person history
		    			person.setHistory(jsonInString);
	            	}
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            }else{
            	person.setStatus(statusField.getText());
            }
            person.setTeam(teamField.getText());
            //person.setEmail(emailField.getText());

            okClicked = true;
            Notifier.INSTANCE.notifySuccess("Success !", "Changes updated !");
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (firstNameField.getText() == null || firstNameField.getText().length() == 0) {
            errorMessage += "No valid first name!\n"; 
        }
        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) {
            errorMessage += "No valid last name!\n"; 
        }
        if (streetField.getText() == null || streetField.getText().length() == 0) {
            errorMessage += "No doctoral school!\n"; 
        }

        if (postalCodeField.getText() == null || postalCodeField.getText().length() == 0) {
            errorMessage += "No supervisor!\n"; 
        } 
//        else {
//            // try to parse the postal code into an int.
//            try {
//                Integer.parseInt(postalCodeField.getText());
//            } catch (NumberFormatException e) {
//                errorMessage += "No valid postal code (must be an integer)!\n"; 
//            }
//        }

        if (cityField.getText() == null || cityField.getText().length() == 0) {
            errorMessage += "No contract!\n"; 
        }

        if (birthdayField.getText() == null || birthdayField.getText().length() == 0) {
            errorMessage += "No valid birthday!\n";
        } else {
            if (!DateUtil.validDate(birthdayField.getText())) {
                errorMessage += "No valid birthday. Use the format dd.mm.yyyy!\n";
            }
        }

        if (entranceField.getText() == null || entranceField.getText().length() == 0) {
            errorMessage += "No valid entrance date!\n";
        } else {
            if (!DateUtil.validDate(entranceField.getText())) {
                errorMessage += "No valid entrance date. Use the format dd.mm.yyyy!\n";
            }
        }
        
        if (exitField.getText() == null || exitField.getText().length() == 0) {
            errorMessage += "No valid exit date!\n";
        } else {
            if (!DateUtil.validDate(exitField.getText())) {
                errorMessage += "No valid exit date. Use the format dd.mm.yyyy!\n";
            }
        }
        
        if (desktopField.getText() == null || desktopField.getText().length() == 0) {
            errorMessage += "No valid desktop!\n"; 
        }
        
        if (statusField.getText() == null || statusField.getText().length() == 0) {
            errorMessage += "No status!\n"; 
        }
        
        if (teamField.getText() == null || teamField.getText().length() == 0) {
            errorMessage += "No team!\n"; 
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            
            alert.showAndWait();
            
            return false;
        }
    }
    
    
}