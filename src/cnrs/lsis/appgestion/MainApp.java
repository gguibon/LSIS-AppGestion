package cnrs.lsis.appgestion;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import cnrs.lsis.appgestion.model.Person;
import cnrs.lsis.appgestion.model.PersonListWrapper;
import cnrs.lsis.appgestion.model.SendMailTLS;
import cnrs.lsis.appgestion.model.User;
import cnrs.lsis.appgestion.util.OsValidator;
import cnrs.lsis.appgestion.utils.Tools;
import cnrs.lsis.appgestion.view.ContractStatisticsController;
import cnrs.lsis.appgestion.view.EmailSettingsController;
import cnrs.lsis.appgestion.view.MonthStatisticsController;
import cnrs.lsis.appgestion.view.PersonEditDialogController;
import cnrs.lsis.appgestion.view.PersonHistoryController;
import cnrs.lsis.appgestion.view.PersonOverviewController;
import cnrs.lsis.appgestion.view.RootLayoutController;
import cnrs.lsis.appgestion.view.StatusStatisticsController;
import cnrs.lsis.appgestion.view.SupervisorStatisticsController;
import cnrs.lsis.appgestion.view.TeamStatisticsController;
import eu.hansolo.enzo.notification.Notification.Notifier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<Person> personData = FXCollections.observableArrayList();
    
    private String mail = "";

    /**
     * Constructor
     */
    public MainApp() {   
        
    	
        // Add some sample data
        personData.add(new Person("Hans", "Muster"));
        personData.add(new Person("Ruth", "Mueller"));
        personData.add(new Person("Heinz", "Kurz"));
        personData.add(new Person("Cornelia", "Meier"));
//        personData.add(new Person("Werner", "Meyer"));
//        personData.add(new Person("Lydia", "Kunz"));
//        personData.add(new Person("Anna", "Best"));
//        personData.add(new Person("Stefan", "Meier"));
//        personData.add(new Person("Martin", "Mueller"));
    }

    /**
     * Returns the data as an observable list of Persons. 
     * @return
     */
    public ObservableList<Person> getPersonData() {
        return personData;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");
        
        // Set the application icon.
//        this.primaryStage.getIcons().add(new Image("file:/resources/images/address_book_32.png"));
//        this.primaryStage.getIcons().add(new Image(new Tools().accessRessourceFile("/resources/images/adress_book_32.png")));
        Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/address_book_32.png"));
        this.primaryStage.getIcons().add(img);

        this.primaryStage.setMaximized(true);
        
        initRootLayout();

        showPersonOverview();
        
        checkDate(getPersonFilePath());
        
        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }

    /**
     * Initializes the root layout and tries to load the last opened
     * person file.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Try to load last opened person file.
        File file = getPersonFilePath();
        if (file != null) {
            loadPersonDataFromFile(file);
        }
        
//     // Try to load mail pref.
//        this.mail = getMailPref();
//        System.out.println(mail);

    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);

            // Give the controller access to the main app.
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonHistory(List<User> users) {
    	try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonHistory.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Person History");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PersonHistoryController controller = loader.getController();
            controller.setHistories(users);
            controller.setDialogStage(dialogStage);
//            controller.setPerson(person);
            
            // Set the dialog icon.
            Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/edit.png"));
            dialogStage.getIcons().add(img);
//            dialogStage.getIcons().add(new Image("file:///resources/images/edit.png"));

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }
    
    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     * 
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);
            
            // Set the dialog icon.
            Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/edit.png"));
            dialogStage.getIcons().add(img);
//            dialogStage.getIcons().add(new Image("file:///resources/images/edit.png"));

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * Opens a dialog to edit email settings. If the user
     * clicks OK, the changes are saved into the settings object and true
     * is returned.
     * 
     * @param person the person object to be edited
     */
    public void showEmailSettings() {
        try {
        	// Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/EmailSettings.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Email Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Set the dialog icon.
            Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/settings.png"));
            dialogStage.getIcons().add(img);
//            dialogStage.getIcons().add(new Image("file://resources/images/settings.png"));

            // Set the persons into the controller.
            EmailSettingsController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);
            controller.setEmail(this.mail);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
           
        }
    }
    
    
    /**
     * Opens a dialog to show departures per month statistics.
     */
    public void showMonthStatistics() {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/MonthStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("This year's departures");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Set the dialog icon.
            Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/calendar.png"));
            dialogStage.getIcons().add(img);
//            dialogStage.getIcons().add(new Image("file://resources/images/calendar.png"));

            // Set the persons into the controller.
            MonthStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Opens a dialog to show team members statistics.
     */
    public void showTeamStatistics() {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TeamStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Members per team");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Set the dialog icon.
            Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/calendar.png"));
            dialogStage.getIcons().add(img);
//            dialogStage.getIcons().add(new Image("file://resources/images/calendar.png"));

            // Set the persons into the controller.
            TeamStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Opens a dialog to show supervisors statistics.
     */
    public void showSupervisorStatistics() {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SupervisorStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Members per supervisor");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Set the dialog icon.
            Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/calendar.png"));
            dialogStage.getIcons().add(img);
//            dialogStage.getIcons().add(new Image("file://resources/images/calendar.png"));

            // Set the persons into the controller.
            SupervisorStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Opens a dialog to show contracts statistics.
     */
    public void showContractStatistics() {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ContractStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Contracts");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Set the dialog icon.
            Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/calendar.png"));
            dialogStage.getIcons().add(img);
//            dialogStage.getIcons().add(new Image("file://resources/images/calendar.png"));

            // Set the persons into the controller.
            ContractStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Opens a dialog to show status statistics.
     */
    public void showStatusStatistics() {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/StatusStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Statuses");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Set the dialog icon.
            Image img = new Image(MainApp.class.getResourceAsStream("/resources/images/calendar.png"));
            dialogStage.getIcons().add(img);
//            dialogStage.getIcons().add(new Image("file://resources/images/calendar.png"));

            // Set the persons into the controller.
            StatusStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
   
    
    /**
     * Opens a trombinoscope in preferred browser
     * @throws IOException 
     * @throws URISyntaxException 
     */
    public void showTrombi() throws IOException, URISyntaxException {
    	StringBuilder sb = new StringBuilder();
    	sb.append("<!DOCTYPE html><html><head><style>table{ border-collapse: collapse;  width: 100%; } "
    			+ "th, td {  text-align: left;  padding: 8px;}tr:nth-child(even){background-color: #f2f2f2}"
    			+ "th { background-color: #4CAF50; color: white; }"
    			+ "tr:hover {background-color: #e6ffcc}"
    			+ "</style>"
    			+ "</head><body><table border=\"0\" style=\"width:100%\"> "
    			+ "<th>Photo</th><th>First Name</th><th>Last Name</th><th>Status</th><th>Team</th><th>Supervisor</th><th>Desktop</th><th>Exit Date</th>");
        for(Person pers : personData){
        	sb.append( String.format("<tr>%s%s%s%s%s%s%s%s</tr>", "<td><img src="+pers.getPhotoPath().replaceAll(" ", "%20")
        	+" alt=\"Photo\" height=\"42\" width=\"42\"></td>" 
        			,"<td>" + pers.getFirstName() + "</td>"
        			,"<td>" + pers.getLastName() + "</td>"
        			, "<td>" + pers.getStatus() + "</td>"
        			, "<td>" + pers.getTeam() + "</td>"
        			, "<td>" + pers.getPostalCode() + "</td>"
        			, "<td>" + pers.getDesktop() + "</td>"
        			, "<td>" + pers.getExitDate() + "</td>" 
        			) );
        }
        sb.append("</table></body></html>");
        String pathTrombi  = Tools.tempFile("trombiTmp", ".html", sb.toString());        
        
        if (OsValidator.isWindows()) {
			System.out.println("This is Windows");
			
	          try {
	        	  Runtime rt = Runtime.getRuntime();
	        	  String url = "file:///"+pathTrombi.replaceAll("/", "\\");
	        	  rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);
	        	  Alert alert = new Alert(AlertType.INFORMATION);
		        	alert.setTitle("Information");
		        	alert.setHeaderText("Trombinoscope opened in your default browser !");
		        	alert.show();
	          } catch (IOException e) {
	              // TODO Auto-generated catch block
	              e.printStackTrace();
	          }
		} else if (OsValidator.isMac()) {
			System.out.println("This is Mac");
			if(Desktop.isDesktopSupported()){
		          Desktop desktop = Desktop.getDesktop();
		          Thread t = new Thread("New Thread") {
						public void run() {
							try {
				                desktop.browse(new URI("file://"+pathTrombi));
				            }  catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					t.start();
					Alert alert = new Alert(AlertType.INFORMATION);
			        	alert.setTitle("Information");
			        	alert.setHeaderText("Trombinoscope opened in your default browser !");
			        	alert.show();
		      }
		} else if (OsValidator.isUnix()) {
			System.out.println("This is Unix or Linux");
			if(Desktop.isDesktopSupported()){
		          Desktop desktop = Desktop.getDesktop();
		          Thread t = new Thread("New Thread") {
						public void run() {
							try {
				                desktop.browse(new URI("file://"+pathTrombi));
				            }  catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					t.start();
					Alert alert = new Alert(AlertType.INFORMATION);
			        	alert.setTitle("Information");
			        	alert.setHeaderText("Trombinoscope opened in your default browser !");
			        	alert.show();
		      }
		} else {
			System.out.println("Your OS is not support!!");
			Alert alert = new Alert(AlertType.WARNING);
        	alert.setTitle("Warning");
        	alert.setHeaderText("Your OS is not support!! !");
        	alert.show();
		}

        //Desktop.getDesktop().browse(new URI("file://"+new File("trombi.html").getAbsolutePath()));
    }
    
    
    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     * 
     * @return
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }
    
    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     * 
     * @return
     */
    public String getMailPref() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String mailPref = prefs.get("mailPref", null);
        if (mailPref != null) {
            return mailPref;
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     * 
     * @param file the file or null to remove the path
     */
    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("LsisApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("LsisApp");
        }
    }
    
    
    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     * 
     * @param file the file or null to remove the path
     */
    public void setMailPref(String mail) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (mail != null) {
            prefs.put("mailPref", mail);
        } else {
            prefs.remove("mailPref");;
        }
    }
    
    public void setMail(String mail){
    	this.mail = mail;
    }
    
    /**
     * Loads person data from the specified file. The current person data will
     * be replaced.
     * 
     * @param file
     */
    public void checkDate(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(PersonListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

            personData.clear();
            personData.addAll(wrapper.getPersons());
            

            // Save the file path to the registry.
            setPersonFilePath(file);
            
            // Check current time
            Calendar cal = Calendar.getInstance();
            java.util.Date date= new Date();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH)+1;
            int year = cal.get(Calendar.YEAR);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            NumberFormat formatter = new DecimalFormat("00");
            
            String exactDate = year+""+formatter.format(month)+""+formatter.format(day);
            int actualDate = Integer.parseInt(exactDate);
            StringBuilder sb = new StringBuilder();
            
            //iterate persons exitDates
            for(Person pers : personData){
            	String exitDateStr = pers.getExitDate().toString().replaceAll("-", "");
            	int exitDate = Integer.parseInt(exitDateStr);
            	//Notify two months before departure
            	if((actualDate >= exitDate-60)&&(actualDate <= exitDate)){
            		Toolkit.getDefaultToolkit().beep();
            		sb.append(String.format("%s %s (team: %s, supervisor: %s) will leave at %s !\n", pers.getLastName(), pers.getFirstName(), pers.getTeam(), pers.getPostalCode(), pers.getExitDate()));
            		Notifier.INSTANCE.notifyWarning("Attention !", String.format("%s %s (team: %s, supervisor: %s) will leave at %s !\n", pers.getLastName(), pers.getFirstName(), pers.getTeam(), pers.getPostalCode(), pers.getExitDate()));
            	}
            }

            if(!sb.toString().isEmpty()){
	            Alert alert = new Alert(AlertType.WARNING);
	        	alert.setTitle("Warning");
	        	alert.setHeaderText("One person will leave soon !");
	        	alert.setContentText(sb.toString());
	        	alert.show();
	        	
	        	//mails
//	        	JAXBContext contextEmail = JAXBContext
//	                    .newInstance(EmailWrapper.class);
//	            Unmarshaller umEmail = contextEmail.createUnmarshaller();
//	            
//	         // Reading XML from the file and unmarshalling.
//	            EmailWrapper wrapperEmail = (EmailWrapper) umEmail.unmarshal(file);
//	            
//	            emailData.clear();
//	            emailData.addAll(wrapperEmail.getEmails());
	        	SendMailTLS sm = new SendMailTLS();
//            	Email mail = emailData.get(0);
//	        	sm.send(mail.getMailTo(), mail.getMailFrom(), 
//	        			mail.getPassword(), mail.getOutSMTP(), 
//	        			mail.getPortSMTP(), sb.toString());
            	sm.send(mail, "lsis.app.gestion@gmail.com", 
	        			"lsis.app.gestion66", "smtp.gmail.com", 
	        			"587", sb.toString());
            }
            
        } catch (Exception e) { // catches ANY exception
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error");
        	alert.setHeaderText("Could not load data");
        	alert.setContentText("Could not load data from file:\n");
        	
        	alert.showAndWait();
        }
    }
    
    
    /**
     * Loads person data from the specified file. The current person data will
     * be replaced.
     * 
     * @param file
     */
    public void loadPersonDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(PersonListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

            personData.clear();
            personData.addAll(wrapper.getPersons());
            mail = wrapper.getMail();

            // Save the file path to the registry.
            setPersonFilePath(file);

        } catch (Exception e) { // catches ANY exception
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error");
        	alert.setHeaderText("Could not load data");
        	alert.setContentText("Could not load data from file:\n" + file.getPath());
        	
        	alert.showAndWait();
        }
    }

    /**
     * Saves the current person data to the specified file.
     * 
     * @param file
     */
    public void savePersonDataToFile(File file) {
    	setMailPref(this.mail);
        try {
            JAXBContext context = JAXBContext
                    .newInstance(PersonListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our person data.
            PersonListWrapper wrapper = new PersonListWrapper();
            wrapper.setPersons(personData);
            wrapper.setMail(this.mail);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            setPersonFilePath(file);
        } catch (Exception e) { // catches ANY exception
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error");
        	alert.setHeaderText("Could not save data");
        	alert.setContentText("Could not save data to file:\n" + file.getPath());
        	
        	alert.showAndWait();
        }
        
        
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}