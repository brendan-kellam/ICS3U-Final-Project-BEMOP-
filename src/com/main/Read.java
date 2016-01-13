package com.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.main.util.Frame;

public class Read extends Frame{
	
	/**
	 * This class handles taking email credentials, processing, and displaying it to the user.
	 */
	
	//Frame variables
	private JFrame window = new JFrame();
	private Dimension size = new Dimension(600, 550);
	private Object[] message;
	private Object[] users;
	private Object[] passes;
	
	//text field
	private JTextField from = new JTextField();
	private JTextField to = new JTextField();
	private JTextField subject = new JTextField();
	private JTextField date = new JTextField();
	
	//buttons
	private JButton reply = new JButton();
	
	//text area
	private JTextArea content = new JTextArea();
	
	public Read(Object[] users, Object[] passes, Object[] message){ //Create a new read JFrame, passes the message and the cached users and passwords (used for replying)
		window.setTitle("Read");
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
		this.message = message;
		this.users = users;
		this.passes = passes;
		
		init();
		action();
	}
	
	private void init(){ //Initializes the frame and adds the components
		label(10, 10, 100, 14, "From:");
		from = txtfield(55, 5, 410, 30);
		from.setEditable(false);
		from.setText(message[3].toString());
		window.add(from);
		
		label(10, 45, 100, 14, "To:");
		to = txtfield(55, 40, 410, 30);
		to.setEditable(false);
		to.setText(message[4].toString());
		window.add(to);
		
		label(5, 80, 100, 14, "Subject:");
		subject = txtfield(55, 75, 410, 30);
		subject.setEditable(false);
		if(message[2] == null) message[2] = "";
		subject.setText(message[2].toString());
		window.add(subject);
		
		label(10, 115, 100, 14, "Date:");
		date = txtfield(55, 110, 215, 30);
		date.setEditable(false);
		date.setText(message[0].toString());
		window.add(date);
		
		window.add(image("/logo3.png")).setBounds(450, 110, 108, 33);
		
		reply = button(280, 113, 130, 25, "reply");
		window.add(reply);
		
		label(10, 150, 100, 14, "Contents:");
		
		content = txtarea(10, 170, 580, 330);
		content.setEditable(false);
		
		String contain = "";
		if(message[1] instanceof String){ //Checks if the content is a simple String/Hyper text format
			contain = (String) message[1];
		}else{
			contain = content((Multipart) message[1]); //if not, process the content
		}
		content.setText(contain); 
	}
	
	private String content(Multipart part){  //gets the body of the content field, and passes it to the getText() function
		String contents = "";
		try {
			BodyPart p = part.getBodyPart(0);
			contents = getText(p);
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return contents;
	}
	
	private String getText(Part part){ //Processes the content, applying the corresponding data structures to the content
		try{
			if (part.isMimeType("text/*")) {
	            String message = (String)part.getContent();
	            return message;
	        }
	
	        if (part.isMimeType("multipart/alternative")) {
	            // prefer html text over plain text
	            Multipart mp = (Multipart)part.getContent();
	            String text = null;
	            for (int i = 0; i < mp.getCount(); i++) {
	                Part bp = mp.getBodyPart(i);
	                if (bp.isMimeType("text/plain")) {
	                    if (text == null)
	                        text = getText(bp);
	                    continue;
	                } else if (bp.isMimeType("text/html")) {
	                    String s = getText(bp);
	                    if (s != null)
	                        return s;
	                } else {
	                    return getText(bp);
	                }
	            }
	            return text;
	        } else if (part.isMimeType("multipart/*")) {
	            Multipart mp = (Multipart)part.getContent();
	            for (int i = 0; i < mp.getCount(); i++) {
	                String message = getText(mp.getBodyPart(i));
	                if (message != null)
	                    return message;
	            }
	        }
		}catch(Exception e){
			dialogBox("error reading message");
		}

        return null; //return null if no message has been formulated
	}

	private void action(){ //actionListener function
		reply.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) { //close the JFrame and create a new Send instance, and fill it with credentials
						close();
						new Send(users, passes, message);
					}
				}
		);
	}
	
}
