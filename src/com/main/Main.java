package com.main;

import com.main.util.Frame;

public class Main extends Frame{

	/**
	 * This class the the starting point of the program.
	 */
	
	public Main(){
		init();
	}
	
	private void init(){
		isEstablished(); //check if user has account
	}
	
	private void isEstablished(){ //Login if the user has a account, create a account if not
		boolean reply = yesNoOption("Do you have a account already?", "Account");
		if(reply){
			new Login();
		}else{
			new Creation();
		}
	}
	
	public static void main(String[] args) {
		new Main(); //create a new instance of Main
	}

}
