package com.main.util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail extends Frame implements Runnable{
	
	private static Command command;
	public static boolean run;
		
	//attributes
	private String address;
	private String text;
	private String subject;
	private String pass;
	private String from;
	
	//address
	private static final String GMAIL = "@gmail.com";
	private static final String GWOOD = "@greenwoodcollege.com";
	private static final String HOTMAIL = "@hotmail.com";
	
	//mail
	private static List<Object[]> messages = new ArrayList<Object[]>(); //A List of Object arrays that holds the message information, including the address, contents, subject, from and date
	private Thread mail;
	
	/**
	 * This class handles all mail communications
	**/
	
	public enum Command{ //a enumeration for the different commands
	    SEND, RECEIVE
	}

	public Mail(String address, String from, String text, String subject, String pass){ //Constructor called when executing a command
		this.address = address;
		this.from = from;
		this.text = text;
		this.subject = subject;
		this.pass = pass;
	}
	
	public Mail() { //a constructor for initializing the Mail class
		
	}

	public void mail(Command command, String from, String address, String text, String subject, String pass){ //A function called when a command needs to be executed, called from the host class
		Mail.setCommand(command); //set the current command to the inputed command
		
		mail = new Thread(new Mail(address, from, text, subject, pass)); //create a new thread, passing in the information
		
		mail.start(); //start the thread and set the run boolean to true
		run = true;
	}
	
	private void send(){ //if the command is a SEND type
		Session session; //create a new session
		
		if(from.contains(GMAIL) || from.contains(GWOOD)){ //if the email is under the google domain
			session = getGmailS();
		}else if(from.contains(HOTMAIL)){ //if the email is under the Hotmail domain
			session = getHotmailS();
		}else{
			dialogBox(from + " is a invalid email type"); //if the entered email is not of the two types above
			return;
		}
			
		//send a email
		try{
			Message message = new MimeMessage(session); //create a new message
			message.setFrom(new InternetAddress("from@no-spam.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
			message.setSubject(subject);
			message.setText(text);
			
			Transport.send(message); //send the email over a stream
			
		}catch (MessagingException e){
			dialogBox("Email is incorrect or does not exist\nPlease re-validate/resend"); //A error message when a sending error occurs
		}
	}
	
	private Session getGmailS(){ //returns the protocol for sending a gmail email
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		
		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(from, pass);
					}
				});
		return session; //return protocol
	}
	
	private Session getHotmailS(){ //returns the protocol for sending a email from hotmail
		 Properties props = new Properties();   
		 props.setProperty("mail.transport.protocol", "smtp");   
		 props.setProperty("mail.host", "smtp.live.com"); 
		 props.put("mail.smtp.starttls.enable", "true");  
		 props.put("mail.smtp.auth", "true");
		 props.put("mail.smtp.port", "587");      
		 props.put("smtp.starttls.enable", "true");
		 props.put("mail.smtp.socketFactory.fallback", "false");   
		 props.setProperty("mail.smtp.quitwait", "false");   

		 props.put("mail.smtp.starttls.enable", "true");
		 Session session = Session.getDefaultInstance(props,
				 new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(from, pass);
						}
					});
		 return session; //return protocol
	}
	
	private void receive(){ //if the command is RECEIVE
		Store store;
		if(address.contains(GMAIL) || address.contains(GWOOD)){ //if the address is under a gmail domain
			store = getGmailR();
		}else if(address.contains(HOTMAIL)){ //if the address is under a hotmail live domain
			store = getHotmailR();
		}else{
			dialogBox(address + " is a invalid email type"); //if its under a non-supported domain
			return;
		}
		
		try{
			Folder inbox = store.getFolder("INBOX"); //get the inbox
			inbox.open(Folder.READ_ONLY); //set the inbox to read only
							        
	        int size = inbox.getMessageCount();
			for(int i = size; i > size - 25; i--){ //loop through 25 of the latest emails in the inbox
				Message msg = inbox.getMessage(i); //grab the specific mail
				Date date = msg.getSentDate();
				Object content = msg.getContent();
				String subject = msg.getSubject();
				String from = "";
				String to = address;
				
				Address [] in = msg.getFrom();
				for (Address address : in){
					from = address.toString();
				}
				
				Object[] message = new Object[5]; //Create a new array that contains the date, content, subject, from and to.
				message[0] = date;
				message[1] = content;
				message[2] = subject;
				message[3] = manipFrom(from);
				message[4] = to;
				
				getMessages().add(message); //append this array to the messages arrayList (holds arrays of objects)
				
			}

		}catch(Exception e){
			dialogBox("error loading emails from\n" + address + "\n(check back later)"); //error reciving
		}
	}
	
	private String manipFrom(String from){ //Manipulates the from address to remove unneeded prefixes
		String content = "";
		boolean flag = false;
		for(int i = 0; i < from.length(); i++){
			if(from.charAt(i) == '<'){
				flag = true;
				continue;
			}
			if(from.charAt(i) == '>'){
				break;
			}
			if(flag){
				content += from.charAt(i);
			}
		}
		if(content.equals("")){
			return from;
		}
		return content;
	}
	
	private Store getGmailR(){ //get gmail receive credentials
		Properties props = new Properties();
		
		Store store = null;
		try {
			Session session = Session.getInstance(props, null);
			store = session.getStore("imaps");
			store.connect("imap.gmail.com", address, pass);		
		} catch (MessagingException e) {
			//error
		}
		return store;
	}
	
	private Store getHotmailR(){ //get hotmail receive credentials
		Properties props = new Properties();
		
		Store store = null;
		try {
			Session session = Session.getInstance(props, null);
			store = session.getStore("pop3s");
			store.connect("pop3.live.com", 995, address, pass);
		} catch (MessagingException e) {
			//error
		}
		return store;
	}
	
	public void run(){ //After the mail Thread has been initilized, goto run().
		//gets the current command
		Command command = Mail.getCommand();
		switch(command){ //a switch case statement that handles each possibility of a command
			case SEND:
				send();	//call the send function  
				stop();
				break;
			case RECEIVE:
				receive(); //call the receive function  
				stop(); 
				break;
			default:
				break;
		}
	}
	
	public void stop(){ //sets the static running boolean to false
		run = false;		
	}
	
	public static void setCommand(Command command) { //set the current command
		Mail.command = command;
	}
	
	public static Command getCommand(){ //get the command
		return command;
	}
	
	public synchronized boolean isAlive(){ //check if the current Thread is running
		return run;
	}

	public static List<Object[]> getMessages() { //get the inbox
		return messages;
	}

	public static void setMessages(List<Object[]> messages) { //set the inbox
		Mail.messages = messages; 
	}
	
}
