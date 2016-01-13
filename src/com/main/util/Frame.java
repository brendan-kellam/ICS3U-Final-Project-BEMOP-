package com.main.util;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public abstract class Frame {
	
	/**
	 * This Class is for JFrame purposes only. It can be created in any class and call the functions bellow.
	 **/
	
	private JFrame window; //main JFrame, must be initialized by calling initWindow from the host class and passing its own JFrame object.
	
	public JButton button(int x, int y, int w, int h, String text){ //Creates a new JButton and returns it. Accepts: x pos, y pos, width, height, text to display on button
		JButton button = new JButton();
		button.setText(text);
		button.setBounds(x, y, w, h);
		return button;
	}
	
	public JTable table(int x, int y, int w, int h, Object[][] data){ //Creates a new JTable, and returns it. Accepts: x pos, y pos, width, height, the data to be displayed
		JTable table = new JTable();
		JScrollPane scroll = new JScrollPane(table); //scroll pane for the table
		table.setFillsViewportHeight(true);
		scroll.setBounds(x, y, w, h);
		window.add(scroll); //add the scroll pane to the window
		return table;
	}
	
	public JTextArea txtarea(int x, int y, int w, int h){ //Creates a new Text Area and returns it. Accepts: x pos, y pos, width, height
		JTextArea area = new JTextArea();
		JScrollPane scroll = new JScrollPane(); 
		area.setLineWrap(true); //line wrap set to true and wraps on words, not characters
		area.setWrapStyleWord(true);
		
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //never have a horizontal scrollbar
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); //have a vertical scroll bar when needed.
		scroll.setViewportView(area);
		scroll.setBounds(x, y, w, h);
		window.add(scroll); //add the scroll pane to the window
		return area;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" }) //compiler error due to inconsistencies with JList
	public JList list(DefaultListModel model, int x, int y, int w, int h){ //Creates a new JList and returns it. Accepts: list model, x pos, y pos, width, height
		JList list = new JList();
		list.setForeground(new java.awt.Color(0, 0, 255)); //sets the font color to blue
		list.setModel(model);
		
		JScrollPane scroll = new JScrollPane();
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); //never have a horizontal scrollbar
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); //have a vertical scroll bar when needed
		scroll.setViewportView(list);
		scroll.setBounds(x, y, w, h);
		window.add(scroll); //add the scroll pane to the window
		return list;
	}
	
	public boolean yesNoOption(String message, String title){ //Creates a new JPanel that has a yes or no question. Accepts: the message to display, the title of the window 
		int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if(reply == JOptionPane.YES_OPTION){ //Returns a boolean value of the result. (yes: true, no: false)
          return true;
        }else {
           return false;
        }
	}
	
	public void dialogBox(String message){ //Creates a simple JOption message pane. Accepts: the message to be displayed
		JOptionPane.showMessageDialog(null, message);
	}
	
	public String[] dualInput(String labelA, String labelB, String message){ //Create a JPanel that has two text fields, a ok and cancel button. Accepts: Label 1, label 2, message to be displayed
		JTextField fieldA = new JTextField(12); //create two new text fields, with a width of 12 columns.
		JTextField fieldB = new JPasswordField(12);

		JPanel panel = new JPanel();
	    panel.add(new JLabel(labelA));
	    panel.add(fieldA);
	    panel.add(Box.createHorizontalStrut(15)); // add a horizontal spacer of 15
	    panel.add(Box.createVerticalGlue());
	    panel.add(new JLabel(labelB));
	    panel.add(fieldB);
	    
	    int result = JOptionPane.showConfirmDialog(null, panel, message, JOptionPane.OK_CANCEL_OPTION); //get which button is clicked
	    
	    if(result == JOptionPane.OK_OPTION){ //if the button clicked is a OK option
	    	String[] out = {fieldA.getText(), fieldB.getText()};
	    	return out; //Return a array of the results from the two text fields.
	    }
	    return null; //return null if the user clicked cancel
	}
	
	public void label(int x, int y, int w, int h, String message){ //Create a new JLabel that is automatically added to the JFrame. Accepts: x pos, y pos, width, height, message to be displayed
		JLabel label = new JLabel();
		label.setText(message);
		window.getContentPane().add(label);
		label.setBounds(x, y, w, h);
	}
	
	public JTextField txtfield(int x, int y, int w, int h){ //Create a new JTextField and return it. Accepts: x pos, y pos, width, height
		JTextField field = new JTextField();
		field.setForeground(new java.awt.Color(0, 0, 0));
		field.setBounds(x, y, w, h);
		return field;
	}
	
	public JPasswordField pswrdfield(int x, int y, int w, int h){ //Create a new JPasswordField (no characters can be seen) and return it. Accepts: x pos, y pos, width, height
		JPasswordField field = new JPasswordField();
		field.setForeground(new java.awt.Color(0, 0, 0));
		field.setBounds(x, y, w, h);
		return field;
	}
	
	public JLabel image(String image){ //Create a new image to be displayed on a JFrame, and then return the image. Accepts: the image location
		ImageIcon img = new ImageIcon(Frame.class.getResource(image));
		JLabel label = new JLabel(img);
		return label;
	}
	
	public void close(){ //Close the current JFrame
		window.setVisible(false);
		window.dispose();
	}
	
	public void initWindow(JFrame window){ //Initialize the window object. Accepts: a JFrame object
		this.window = window;
	}
	
}
