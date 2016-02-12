package de.hs.settlers.gui;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import org.spout.api.exception.ConfigurationException;

import de.hs.settlers.Configuration;
import de.hs.settlers.net.message.text.LoginMessage;
import de.hs.settlers.util.HashUtils;

public class LoginViewController extends ViewController implements EventHandler<KeyEvent> {

	boolean usedPseudoPassword = false;

	@FXML
	HBox root;

	@FXML
	TextField username;

	@FXML
	PasswordField password;

	@FXML
	CheckBox keeplogin;

	@FXML
	Button loginbutton;

	@FXML
	Label errorlabel;

	@FXML
	GridPane developerLogos;

	@FXML
	ImageView settlersLogo;

	@FXML
	ImageView logoWASP;

	@FXML
	ImageView logoHS;
	
	// flags for keylistener
	boolean altKeyPressed;
	boolean lKeyPressed;
	
	String usernameInFile;
	String passwordInFile;
	
	/**
	 * @param
	 * @author ffroelich Building layout of Login GUI
	 */
	@Override
	public void init() {
		Image img = new Image("/images/settlersLogo.png");
		settlersLogo.setImage(img);

		Image img2 = new Image("/images/logoWASP_shadow.png");
		logoWASP.setImage(img2);

		Image img3 = new Image("/images/logoHS_shadow.png");
		logoHS.setImage(img3);
		
		altKeyPressed = false;
		lKeyPressed = false;
		
		getRootNode().setOnKeyPressed(this);
	}

	@Override
	public void onShow() {
		// Get Login Info form File

		usernameInFile = new String(Configuration.USERNAME.getString());
		passwordInFile = new String(Configuration.PASSWORD.getString());

		
		if ((passwordInFile.length() == 40 && passwordInFile.matches("[\\dA-Fa-f]+")))
		{
			password.setText(passwordInFile.substring(0, 10));
			usedPseudoPassword = true;
		} else {
			password.setText(passwordInFile);
		}
		
		username.setText(usernameInFile);		
		keeplogin.setSelected(Configuration.SAVE_PASSWORD.getBoolean());

		setButtonState(true);
		
		// set focus to username textfield
		username.requestFocus();
	}

	@Override
	public void onHide() {
		setButtonState(true);
	}
	
	public void onLoginKeyPress() {
		onLoginAction();
	}

	/**
	 * @param action
	 *            at pushing login button
	 */
	public void onLoginAction() {
		
		String username = this.username.getText();
		String password = "";
		
		if (username.equals(usernameInFile)) {
			password = Configuration.PASSWORD.getString();
		} else {
			password = this.password.getText();
		}		
		
		if (username.isEmpty() || password.isEmpty()) {
			displayError("No username or password entered.");
			return;
		}
		else
		{
			clearError();
		}
		
		// Check if password is only string and if, change to hashed string
		if (!(password.length() == 40 && password.matches("[\\dA-Fa-f]+")))
		{
			password = HashUtils.hashFunctionSHA(password);
		}
		
		boolean keeplogin = this.keeplogin.isSelected();
		setButtonState(false);

		if (keeplogin) {
			Configuration.USERNAME.setValue(username);
			Configuration.PASSWORD.setValue(password);
			Configuration.SAVE_PASSWORD.setValue(true);

		} else {
			Configuration.USERNAME.setValue(Configuration.USERNAME.getDefaultValue());
			Configuration.PASSWORD.setValue(Configuration.PASSWORD.getDefaultValue());
			Configuration.SAVE_PASSWORD.setValue(Configuration.SAVE_PASSWORD.getDefaultValue());
			usedPseudoPassword = false;
		}

		try {
			getApplication().getConfiguration().save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		clearError();
		getApplication().getTextCommunicationProtocol().sendMessage(new LoginMessage(username, password));

		// Displayed view will be changed via state change in
		// SettlersApplication, so it is
		// possible to handle errors.
	}

	@Override
	public void displayError(String error) {
			errorlabel.setVisible(true);
			errorlabel.setText("Error: " + error);
			FadeTransition transition = new FadeTransition(
					Duration.millis(500), errorlabel);
			transition.setFromValue(0.0);
			transition.setToValue(1.0);
			transition.play();
			setButtonState(true);
	}

	private void setButtonState(boolean enabled) {
		if (enabled) {
			loginbutton.setContentDisplay(ContentDisplay.TEXT_ONLY);
		} else {
			loginbutton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
		loginbutton.setDisable(!enabled);
	}

	private void clearError()
	{
		errorlabel.setVisible(false);
	}

	@Override
	public Node getRootNode()
	{
		return root;
	}

	/**
	 * checking flags for pressed keys to detect hotkeys
	 * @ author: ckoch
	 */
	@Override
	public void handle(KeyEvent e) {
		if (!loginbutton.isDisabled()) {
			if (e.getEventType().equals(KeyEvent.KEY_PRESSED)) {
				if (e.getCode().equals(KeyCode.ALT)) {
					altKeyPressed = true;
				} else if (e.getCode().equals(KeyCode.L)) {
					lKeyPressed = true;
				}
			} else if (e.getEventType().equals(KeyEvent.KEY_RELEASED)) {
				if (e.getCode().equals(KeyCode.ALT)) {
					altKeyPressed = false;
				}

				lKeyPressed = false;
			}
			if (e.getCode().equals(KeyCode.ENTER)) {
				onLoginAction();
			} else if (altKeyPressed && lKeyPressed) {
				onLoginAction();
			}
		}
	}
}
