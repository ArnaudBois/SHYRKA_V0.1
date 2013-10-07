package com.domotic.shyrkha;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class Command {

	private Date dt;
	private String Command;
	private boolean Valid;
	
	public Command(Date D , String CMD, boolean V) {
		this.dt = D;
		this.Command= CMD;
		this.Valid = V;
	}

	public String  getDate(){
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.FRENCH);
		String sTemp= df.format(dt);
		return sTemp;
		
	}
	
	public String getCommand(){ return this.Command; }
	
	public String getValid(){ 
		String sTemp;
		if (this.Valid == true)
		{
			sTemp="Oui";
		}
		else
		{
			sTemp="Non";
		}
		return sTemp;
		
	}
	
	public String toString()
	{
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.FRENCH);
		String sTemp= df.format(dt) + " : " + Command + " ( "+ Valid +" )";
		return sTemp;
	}
	
	
	
}
