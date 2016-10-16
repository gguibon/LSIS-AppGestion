package cnrs.lsis.appgestion.model;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import cnrs.lsis.appgestion.util.LocalDateAdapter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a Person.
 *
 * @author Marco Jakob
 */
public class Person {

    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty street;
    private final StringProperty postalCode;
    private final StringProperty city;
    private final ObjectProperty<LocalDate> birthday;
    private final SimpleObjectProperty<LocalDate> entranceDate;
    private final SimpleObjectProperty<LocalDate> exitDate;
    private final StringProperty desktop;
    private final StringProperty photoPath;
    private final StringProperty status;
    private final StringProperty team;
    private final StringProperty email;
    private final StringProperty history;
    
    
    

    /**
     * Default constructor.
     */
    public Person() {
        this(null, null);
    }

    /**
     * Constructor with some initial data.
     * 
     * @param firstName
     * @param lastName
     */
    public Person(String firstName, String lastName){
    	
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        
        // Some initial dummy data, just for convenient testing.
        this.street = new SimpleStringProperty("FR:Ecole doctorale (ex: ED 184)");
        this.postalCode = new SimpleStringProperty("FR:Responsable (ex: M.Bellot)");
        this.city = new SimpleStringProperty("FR:Contract (ex: CIFRE CDD)");
        this.birthday = new SimpleObjectProperty<LocalDate>(LocalDate.of(1999, 2, 21));
        this.entranceDate = new SimpleObjectProperty<LocalDate>(LocalDate.of(2016, 3, 14));
        this.exitDate = new SimpleObjectProperty<LocalDate>(LocalDate.of(2017, 3, 14));
        this.desktop = new SimpleStringProperty("FR:Bureau. some desktop number (23 for instance)");
        this.photoPath = new SimpleStringProperty("file:resources/images/person.png");
        this.status = new SimpleStringProperty("PhD / Engineer / etc");
        this.team = new SimpleStringProperty("Team A");
        this.email = new SimpleStringProperty("an.email@email.com");
        this.history = new SimpleStringProperty("");
        
        
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getStreet() {
        return street.get();
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public StringProperty streetProperty() {
        return street;
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public StringProperty cityProperty() {
        return city;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getBirthday() {
        return birthday.get();
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday.set(birthday);
    }

    public ObjectProperty<LocalDate> birthdayProperty() {
        return birthday;
    }
    
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getEntranceDate() {
        return entranceDate.get();
    }

    public void setEntranceDate(LocalDate entranceDate) {
        this.entranceDate.set(entranceDate);
    }

    public ObjectProperty<LocalDate> entranceDateProperty() {
        return entranceDate;
    }
    
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getExitDate() {
        return exitDate.get();
    }

    public void setExitDate(LocalDate exitDate) {
        this.exitDate.set(exitDate);
    }

    public ObjectProperty<LocalDate> exitDateProperty() {
        return exitDate;
    }
    
    public String getDesktop() {
        return desktop.get();
    }

    public void setDesktop(String desktopName) {
        this.desktop.set(desktopName);
    }

    public StringProperty desktopProperty() {
        return desktop;
    }
    
    public String getPhotoPath() {
        return photoPath.get();
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath.set(photoPath);
    }

    public StringProperty photoPathProperty() {
        return photoPath;
    }
    
    public String getStatus() {
        return status.get();
    }

    public void setStatus(String Status) {
        this.status.set(Status);
    }

    public StringProperty statusProperty() {
        return status;
    }
    
    public String getTeam() {
        return team.get();
    }

    public void setTeam(String team) {
        this.team.set(team);
    }

    public StringProperty teamProperty() {
        return team;
    }

    
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

	public String getHistory() {
		return history.get();
	}
	
	public void setHistory(String history) {
		this.history.set(history);
	}

}