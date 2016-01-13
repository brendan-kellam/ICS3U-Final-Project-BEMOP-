package com.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.main.util.Frame;
import com.main.util.Mail;
import com.main.util.ManipString;
import com.main.util.ManipXML;

public class Creation extends Frame{
	
	/**
	 * This class provides the user the ability to create a new account.
	 * Handles all logic and frame.
	 */

	//frame variables
	private JFrame window = new JFrame();
	private Dimension size = new Dimension(405, 600);
	private ManipXML manipXML = new ManipXML();
	private ManipString manipS = new ManipString();
	private Mail mail = new Mail();

	//buttons
	JButton validate = new JButton();
	JButton confirm = new JButton();
	
	//text fields
	JTextField fname = new JTextField();
	JTextField lname = new JTextField();
	JTextField country = new JTextField();
	JTextField username = new JTextField();
	JTextField password = new JTextField();
	JTextField comfpassword = new JTextField();
	JTextField email = new JTextField();
	JTextField validcode = new JTextField();
	
	//images
	JLabel logo = new JLabel();
	
	//varibles
	private String token;
	private String first;
	private String last;
	private String residence;
	private String user;
	private String pass;
	private String comfpass;
	private String address;
	
	private final String EMAIL = "bemopnoreply@gmail.com"; //the program email that sends the verification code
	private final String PASS = "computer28"; //password to that email, do not copy pls :) 
	
	public Creation(){ //Create the JFrame
		window.setTitle("Account Creation");
		window.setSize(size);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(null);
		initWindow(window);
		init();
		window.getContentPane().setBackground(Color.LIGHT_GRAY);
		action();
	}
	
	private void init(){ //Initialize the JFrame with the components
		validate = button(40, 395, 140, 25, "validate");
		window.add(validate);
		
		confirm = button(225, 395, 140, 25, "confirm");
		confirm.setEnabled(false);
		window.add(confirm);
		
		label(80, 10, 100, 14, "first name:");
		fname = txtfield(210, 5, 150, 30);
		window.add(fname);
		
		label(80, 60, 100, 14, "last name:");
		lname = txtfield(210, 55, 150, 30);
		window.add(lname);
		
		label(55, 110, 160, 14, "country of residence:");
		country = txtfield(210, 105, 150, 30);
		window.add(country);
		
		label(80, 160, 160, 14, "username:");
		username = txtfield(210, 155, 150, 30);
		window.add(username);
		
		label(80, 210, 160, 14, "password:");
		password = pswrdfield(210, 205, 150, 30);
		window.add(password);
		
		label(55, 260, 160, 14, "confirm password:");
		comfpassword = pswrdfield(210, 255, 150, 30);
		window.add(comfpassword);
		
		label(67, 310, 160, 14, "email address:");
		email = txtfield(210, 305, 150, 30);
		window.add(email);
		
		label(30, 360, 160, 14, "validation code:");
		validcode = txtfield(140, 355, 75, 30);
		validcode.setEditable(false);
		window.add(validcode);
				
		window.add(image("/logo.png")).setBounds(35, 445, 326, 98);
	}
	
	private void write(){ //write to the xml the information gathered from the user
		String[] keys = {"first", "last", "country", "pass"};
		String[] values = {first, last, residence, pass};
		manipXML.createChildNode("users", "user", user, keys, values);
	}
	
	private void authentication(){ //checks if the code the user entered matches the validation code sent to the user's email
		String entercode = validcode.getText();
		if(token.equals(entercode)){
			dialogBox("Your account has been successfully created");
			write();
			close();
			new Login(); //create a new login window
		}else{
			dialogBox("The code you entered is invalid");
		}
	}
	
	private void validate(){ //Check all of the users information entered and check if it matches the credentials
		validcode.setText(""); //reset the validation field
		validcode.setEditable(false); //set the validation and confirm buttons to false
		confirm.setEnabled(false);
		
		//get all of the user entered data
		first = fname.getText();
		last = lname.getText();
		residence = country.getText();
		user = username.getText();
		pass = password.getText();
		comfpass = comfpassword.getText();
		address = email.getText();
		
		if(manipS.null1(first)){dialogBox("first name is empty"); return;};
		if(manipS.null1(last)){dialogBox("last name is empty"); return;};

		
		if(!residenceCheck(residence)){ //check residence
			dialogBox("Invalid country");
			return;
		}
		
		if(manipS.null1(user)){dialogBox("username is empty"); return;} 
		if(!userCheck(user)){ //checks if the username is already taken
			dialogBox("Username already taken");
			return;
		}
		
		if(pass.equals(comfpass)){ //checks if the password is valid
			if(!passCheck(pass, first, last)){
				return;
			}
		}else{
			dialogBox("Passwords do not match");
			return;
		}
		
		if(manipS.null1(address)){dialogBox("Email address is empty"); return;};
		
		//all credentials have been verified to be true
		String subject = "Validation";
		token = generateToken(); //generate a new validation token
		String message = "Hello " + first + " " + last + ",\nYour validation code is " + token + ". Please enter this value into the validation code box.\nThanks,\nMail Team";
		mail.mail(Mail.Command.SEND, EMAIL, address, message, subject, PASS);
		
		dialogBox("A validation code will be sent to \n" + address);
		validcode.setEditable(true);
		confirm.setEnabled(true);
	}
	
	private String generateToken(){ //generates a 5 character, alpha-numerical token
		String alpha = "abcdefghijklmnopqrstuvwxyz";
		String num = "0123456789";
		String[] data = {alpha, num};
		String token = "";
		Random rn = new Random();
		for(int i = 0; i < 5; i++){
			String charData = data[rn.nextInt(2)];
			token += charData.charAt(rn.nextInt(charData.length()));
		}
		return token;
	}
	
	private boolean passCheck(String pass, String first, String last){ //checks if the password follows the required credentials (between 6 and 20 character and contains atleast one number)
		if(pass.length() <= 6 || pass.length() >= 20){
			dialogBox("Password must between 6 and 20 characters");
			return false;
		}
		
		for(int i = 0; i <= 9; i++){
			if(pass.contains(Integer.toString(i))) break;
			if(i == 9){
				dialogBox("Password must contain at least one number");
				return false;
			}
		}
		
		if(pass.contains(first) || pass.contains(last)){dialogBox("Password cannot contain first or last name"); return false;};
		
		return true;
	}
	
	private boolean userCheck(String user){ //Checks if a user already exists
		manipXML.open();
		NodeList userData = manipXML.returnChildNodes("users", "user");
		String prevUser;
	
		for(int i = 0; i < userData.getLength(); i++){ //loops through all users that are cached 
			Node node = userData.item(i);
			 
			if("user".equals(node.getNodeName())){ //case sensitive
				prevUser = node.getTextContent();
				if(prevUser.equals(user)){ 
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean residenceCheck(String residence){ //check if the inputed country of residence is a valid country
		String[] locales = Locale.getISOCountries();
		 
		for (String countryCode : locales) { //loops through each country under the ISO index of Countries and checks if the inputed country is valid
			Locale obj = new Locale("", countryCode);
			String country = obj.getDisplayCountry();
			if(country.equalsIgnoreCase(residence)) return true; //case insensitive
		}
		return false;
	}
	
	private void action(){ //the Actionlistener function
		validate.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						validate(); //call the validate function if the validate button is clicked
					}
				}
		);
		
		confirm.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						authentication(); //call the authentication function if the confirm button is clicked
					}
				}
		);
	}
}
