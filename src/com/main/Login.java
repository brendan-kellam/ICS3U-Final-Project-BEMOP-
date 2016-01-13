package com.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.main.util.Frame;
import com.main.util.ManipXML;

public class Login extends Frame{
	
	/**
	 * This class allows a user to login to the program.
	 * They must have a account to proceed beyond this point.
	 */
	
	//Frame variables
	private JFrame window = new JFrame();
	private ManipXML manipXML = new ManipXML();
	private Dimension size = new Dimension(405, 145);
	
	//buttons:
	JButton login = new JButton();
	
	//text fields:
	JTextField usernameIn = new JTextField();
	JTextField passwordIn = new JTextField();
	
	public Login(){ //Initializes the JFrame
		window.setTitle("Login");
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
	
	
	private void init(){ //apply the components to the Frame
		login = button(255, 30, 110, 25, "login");
		window.add(login);
		
		label(10, 10, 100, 14, "user name:");
		usernameIn = txtfield(90, 5, 150, 30);
		window.add(usernameIn);
		
		
		label(10, 60, 100, 14, "password:");
		passwordIn = pswrdfield(90, 55, 150, 30);
		window.add(passwordIn);
		
		window.add(image("/logo3.png")).setBounds(260, 72, 108, 33);
	}
	
	private void processCom(String user, String pass){ //process the inputed login info
		
		if(user.equals("") || pass.equals("")){ //checks if the info is empty
			dialogBox("The username or password is empty");
			return;
		}
		manipXML.open(); 
		
		NodeList userData = manipXML.returnChildNodes("users", "user");
		String prevUser;
		String prevPass;
		for(int i = 0; i < userData.getLength(); i++){
			Node node = userData.item(i);
			 
			if("user".equals(node.getNodeName())){ //loops through all users and checks if the inputed info is equal to one of these users
				prevUser = node.getTextContent();
				prevPass = node.getAttributes().getNamedItem("pass").getTextContent();
				if(prevUser.equals(user) && prevPass.equals(pass)){
					login(user); //login if the credentials are correct
					return;
				}
			}
		}
		
		dialogBox("User name or password is incorrect");
	}
	
	private void login(String user){
		close();
		new Mainenv(user); //create a new Main Environment class
	}
	
	private void action(){ 
		login.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						processCom(usernameIn.getText(), passwordIn.getText()); //process the command when the login button is clicked
					}
				}
		);
	}
}
