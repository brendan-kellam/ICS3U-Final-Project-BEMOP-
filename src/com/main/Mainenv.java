package com.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.main.util.Frame;
import com.main.util.Mail;
import com.main.util.ManipXML;

public class Mainenv extends Frame{
	
	/**
	 * This class is the main program.
	 * It is where all email interfacing occurs
	 */
	
	//Frame variables
	private JFrame window = new JFrame();
	private ManipXML manipXML = new ManipXML();
	private Dimension size = new Dimension(800, 650);
	private Mail mail = new Mail();
	
	//tables
	private JTable emailTable = new JTable();
	private DefaultTableModel tableModel;
	
	//Lists
	@SuppressWarnings("rawtypes")
	private JList accounts = new JList();
	@SuppressWarnings("rawtypes")
	private DefaultListModel accountsModel = new DefaultListModel();
	
	//variables
	private String user;
	private List<Object[]> messages = Mail.getMessages();
	private Timer timer;
	
	//buttons
	private JButton addAccount = new JButton();
	private JButton delAccount = new JButton();
	private JButton refresh = new JButton();
	private JButton open = new JButton();
	private JButton send = new JButton();
	
	//images
	public JLabel refreshing = new JLabel();
	
	//variables
	private List<String> emailAccounts = new ArrayList<String>(); //holds each of the email addresses and passwords
	private List<String> emailPasswords = new ArrayList<String>();
	
	
	public Mainenv(String user){ //Create a new JFrame
		window.setTitle("BeMop");
		window.setSize(size);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(null);
		initWindow(window);
		this.user = user;
		init();
		window.getContentPane().setBackground(Color.LIGHT_GRAY);
		action();
		update();
	}
	
	public void init(){ //Initializes the Frame and adds the necessary components
		
		Object[][] data = new Object[0][0]; //creates a new null data array that will be appended too (needed for creation of a table)
		
		emailTable = table(10, 250, 780, 350, data);
		tableModel = new DefaultTableModel(new Object[]{"From","To", "Date", "Subject"},0){ //Create new tablemodel that will allow editting of the JTable
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column) { //Overrides isCellEditable to return false, so table cannot be eddied
				return false;
			}
		};
		emailTable.setModel(tableModel);
		emailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		label(10, 10, 300, 14, user + "'s accounts:");
		
		accounts = list(accountsModel, 10, 40, 214, 150);
		accounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		addAccount = button(70, 200, 40, 25, "+");
		window.add(addAccount);
		
		delAccount = button(110, 200, 40, 25, "-");
		window.add(delAccount);
		
		refresh = button(160, 200, 40, 25, "↺");
		window.add(refresh);
		
		open = button(235, 45, 140, 25, "open");
		window.add(open);
		
		send = button(235, 95, 140, 25, "new");
		window.add(send);
		
		refreshing = image("/refresh.gif");
		refreshing.setBounds(768, 0, 32, 35);
		refreshing.setForeground(Color.LIGHT_GRAY);
		refreshing.setVisible(false);
		window.add(refreshing);
		
		window.add(image("/logo2.png")).setBounds(390, 10, 353, 188);
	}
	
	private void updateInbox(){ //update the inbox
		if(!Mail.run){
			refreshing.setVisible(true);
			Mail.getMessages().clear();
			
			for(int i = 0; i < emailAccounts.size(); i++){
				mail.mail(Mail.Command.RECEIVE, "", emailAccounts.get(i), "", "", emailPasswords.get(i)); //updates the different inbox's
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void writeAccount(){ //Write to xml file with a new account 
		
		accountsModel.clear(); //clear all cached data from the current inbox
		emailAccounts.clear();
		emailPasswords.clear();
		
		manipXML.open();
		NodeList userData = manipXML.returnChildNodes("users", "user");
		String contents;
		String emails = "";
		String passwords = "";
		
		for(int i = 0; i < userData.getLength(); i++){ 
			Node node = userData.item(i);

			if("user".equals(node.getNodeName())){
				contents = node.getTextContent();
				if(contents.equals(user)){
					if(!manipXML.checkKeyExistance(node, "e_address")){//add a email address (non exist)
						return;
					}
					emails = manipXML.getKey(node, "e_address");
					passwords = manipXML.getKey(node, "e_pass");
				}
			}
		}
		emails += ";"; //add the ';' character the the xml data to allow proper string manip
		passwords += ";";
		
		//This code appends the email accounts to the specified user's node
		String append = ""; 
		for(int i = 0; i < emails.length(); i++){ //same method as below
			char echar = emails.charAt(i);
			if(echar != ';'){
				append += echar;
			}else{
				emailAccounts.add(append);
				append = "";
			}
		}
	
		append = "";
		for(int i = 0; i < passwords.length(); i++){ //manipulate the XML string to produce proper data
			char echar = passwords.charAt(i);
			if(echar != ';'){
				append += echar;
			}else{
				emailPasswords.add(append); //add the resulting cached email passwords to the emailPassowrds arrayList
				append = "";
			}
		}
		
		for(int i = 0; i < emailAccounts.size(); i++){ //add email accounts to the email list
			accountsModel.addElement(emailAccounts.get(i));
		}
		updateInbox(); //update the inbox with the new data
	}
	
	private void addAcc(){ //appends the new account to the users account
		String[] result = dualInput("Email Address", "Email Password", "Please enter your email credentials");
		if(result == null) return;
		String email = result[0];
		String pass = result[1];
		
		for(int i = 0; i < emailAccounts.size(); i++){ //Checks if the entered email is already in use
			if(emailAccounts.get(i).equals(email)){
				dialogBox("This email address is already in use");
				return;
			}
		}
		
		manipXML.open();
		NodeList userData = manipXML.returnChildNodes("users", "user");
		String contents;
		for(int i = 0; i < userData.getLength(); i++){
			Node node = userData.item(i);
			
			if("user".equals(node.getNodeName())){

				contents = node.getTextContent();
				if(contents.equals(user)){
					
					if(!manipXML.checkKeyExistance(node, "e_address")){//add a email address
						manipXML.addKey(node, "e_address", email); //create a new key if the e_address key does not exist
					}else{
						manipXML.appendKey(node, "e_address", ";"+email); //else, append to the e_address key that was already created
					}
					
					if(!manipXML.checkKeyExistance(node, "e_pass")){ //add a password
						manipXML.addKey(node, "e_pass", pass); //create a new key if the e_pass key does not exist
					}else{
						manipXML.appendKey(node, "e_pass", ";"+pass); //else, append to the e_pass key that was already created
					}
					break;
				}
			}
		}
		writeAccount(); //write to the account
	}
	
	private void delAcc(){ //deletes a  already existing account
		int index = accounts.getSelectedIndex(); //get selected item
		if(index == -1){dialogBox("No item selected"); return;};
		
		manipXML.open();
		NodeList userData = manipXML.returnChildNodes("users", "user");
		String contents;
		
		String email;
		String password;
		
		for(int i = 0; i < userData.getLength(); i++){ //loops through all the users in the program
			Node node = userData.item(i);
			
			if("user".equals(node.getNodeName())){

				contents = node.getTextContent();
				
				if(contents.equals(user)){ //if the current user is our user
					
					email = emailAccounts.get(index); 
					password = emailPasswords.get(index);
					
					if(index == 0 && emailAccounts.size()>1){ //manipulation on wether to deleat a specific section of a key or deleat the entire key
						manipXML.delKey(node, "e_address", email+";");
						manipXML.delKey(node, "e_pass", password+";");
					}else if(index == 0){
						manipXML.delKey(node, "e_address", email);
						manipXML.delKey(node, "e_pass", password);
					}else{
						manipXML.delKey(node, "e_address", ";"+email);
						manipXML.delKey(node, "e_pass", ";"+password);
					}
					break;
				}
			}
		}
		
		writeAccount(); //append to the account
	}

	private void writeData(){ //called when the refresh button is clicked
		
		for(Object[] x : Mail.getMessages()){ 
			if(!messages.contains(x)){ //if the email does not exist in the existing inbox
				messages.add(x);
			}
		}
		
		sortEmail(messages); //sorts the email based of the date
	
		tableModel.setNumRows(0); //reset the table
		 
		@SuppressWarnings("unused")
		int count = tableModel.getRowCount() + messages.size(); //resizes the table size (must be applied to a varible)
		int size = messages.size()-1;
		
		Collections.reverse(messages); //reverse the array to sort newest to oldest
		
		for(int i = 0; i < size; i++){ //write to the table with the new data
			tableModel.addRow(new Object[]{messages.get(i)[3], messages.get(i)[4], messages.get(i)[0], messages.get(i)[2]});
			tableModel.isCellEditable(i, 0);
		}
	}
	
	private void sortEmail(List<Object[]> messages){ //Takes a inbox and sorts it by the date sent field
		Collections.sort(messages, new Comparator<Object[]>() {
		    public int compare(Object[] o1, Object[] o2) {
		        return ((Date)o1[0]).compareTo(((Date)o2[0]));
		    }
		});
	}
	
	private void update(){ //a function that updates both a refresh icon and the inbox
		timer = new Timer();
		timer.schedule( new TimerTask() {
		    public void run() {
		    	writeAccount();
		    }
		 }, 0, 120*1000);
		
		timer.schedule( new TimerTask() {
		    public void run() {
		    	if(!Mail.run){
		    		refreshing.setVisible(false);
		    	}
		    }
		 }, 0, 1000);
	}
	
	private void action(){ //the action listener function
		delAccount.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						delAcc(); //delete a account
					}
				}
		);
		
		addAccount.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						addAcc(); //add a account 
					}
				}
		);
		
		refresh.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						writeData(); //refresh inbox
					}
				}
		);
		
		open.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						if(emailTable.getSelectedRow() == -1) return; //if there is no selected row
						Object[] accounts = emailAccounts.toArray();
						Object[] passes = emailPasswords.toArray();
						new Read(accounts, passes, messages.get(emailTable.getSelectedRow())); //Create a new read instance to read the selected email
					}
				}
		);
		
		send.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						Object[] accounts = emailAccounts.toArray(); //Send a new email, credentials will be empty 
						Object[] passes = emailPasswords.toArray();
						new Send(accounts, passes);
					}
				}
		);
	}
}
