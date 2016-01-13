package com.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.main.util.Frame;
import com.main.util.Mail;

public class Send extends Frame{

	/**
	 * This class handles all sending functions of the program.
	 * Allows the user to type a fully functional email and send it to any address.
	 * Send has two constructors, one to handle a new, blank email and another to handle if the user is trying to reply and the credentials already exist
	 */
	
	//JFrame variables
	private JFrame window = new JFrame(); 
	private Dimension size = new Dimension(600, 550);
	private Mail mail = new Mail();
	
	//variables
	private Object[] message = null;
	private Object[] users;
	private Object[] passes;
	
	//text fields
	private JTextField to = new JTextField();
	private JTextField subject = new JTextField();

	//Lists
	@SuppressWarnings("rawtypes")
	private JList accounts = new JList();
	@SuppressWarnings("rawtypes")
	private DefaultListModel accountsModel = new DefaultListModel();

	//text area
	private JTextArea content = new JTextArea();
	
	//buttons
	private JButton send = new JButton();
	
	public Send(Object[] users, Object[] passes){ //If a new email is being created
		this.users = users;
		this.passes = passes;
		initWindow();
	}
	
	public Send(Object[] users, Object[] passes, Object[] message){ //If the user is replying to a email
		this.users = users;
		this.passes = passes;
		this.message = message;
		initWindow();
	}
	
	private void initWindow(){ //Initialize frame
		window.setTitle("Send");
		window.setSize(size);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
		window.setLayout(null);
		window.getContentPane().setBackground(Color.LIGHT_GRAY);
		window.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        close();
		    }
		});
		
		initWindow(window);
		init();
		if(message != null) addData(); //if credentials already exist, process them
		action();
	}
	
	@SuppressWarnings("unchecked")
	private void init(){ //Add compents to the JFrame
		label(10, 10, 100, 14, "From:");
		accounts = list(accountsModel, 10, 35, 214, 150);
		accounts.setListData(users);
		accounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		label(240, 15, 100, 14, "To:");
		to = txtfield(280, 10, 290, 30);
		window.add(to);
		
		label(230, 50, 100, 14, "Subject:");
		subject = txtfield(280, 45, 290, 30);
		window.add(subject);
		
		content = txtarea(10, 195, 580, 305);
		
		send = button(280, 113, 130, 25, "send");
		window.add(send);
		
		window.add(image("/logo3.png")).setBounds(450, 110, 108, 33);
	}
	
	private void addData(){ //write credentials to the JFrame
		to.setText((String) message[3]);
		subject.setText((String) message[2]);
		
		for(int i = 0; i < users.length; i++){
			if(users[i].equals(message[4])){
				accounts.setSelectedIndex(i); //set the selector of the JList to the address that received the previous email
			}
		}
	}
	
	private void send(){ //Send email
		int index = accounts.getSelectedIndex(); //get the selected index
		if(index == -1){dialogBox("No address selected"); return;};

		String from = (String) users[index]; //get the user and pass of the selected address to send from
		String pass = (String) passes[index];
		String to = this.to.getText();
		if(to.equals("")){dialogBox("To field cannot be empty"); return;};
		
		String subject = this.subject.getText();
		String content = this.content.getText();
		
		mail.mail(Mail.Command.SEND, from, to, content, subject, pass); //Send the email to the specified email address. Will process correctly if the address is valid.
	}
	
	private void action(){
		send.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) { //attempt to send the email, close the JFrame and notify the user
						send();
						close();
						dialogBox("Email has been sent");
					}
				}
		);
	}
}
