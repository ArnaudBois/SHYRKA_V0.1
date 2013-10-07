package com.domotic.shyrkha;

import java.net.InetAddress;

import android.util.Log;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.ModbusUtil;



public class ModbusTCP {
	private boolean INITOK =false;
	
	private InetAddress addr = null; //the slave's address
	private String astr = "192.168.1.100"; //Modbus Device
	private int port = 502;
	
	private TCPMasterConnection con = null; //the connection
	private ModbusTCPTransaction trans = null; //the transaction
	
	private ReadMultipleRegistersRequest Rreq; private ReadMultipleRegistersResponse Rres;
	private WriteMultipleRegistersRequest Wreq;private WriteMultipleRegistersResponse Wres;
	//================================================================================================
	public ModbusTCP( String sIP , int iPort)
	{
		astr = sIP;port = iPort;
		try {
			addr = InetAddress.getByName(astr);
			con = new TCPMasterConnection(addr);
			con.setPort(port);
			con.connect();
			con.setTimeout(2500);
			INITOK = true;
		}
		catch(Exception e) {
			//sTemp +="Exception config connection IP : " + e.toString();
		}
		
	}
	public boolean IsConnected()
	{
		return INITOK;
	}
	
	
	//================================================================================================
	 public int[] MDB_ReadWordRegister(int AdrWord, int WordToRead)
     {
		Rreq = new ReadMultipleRegistersRequest(12288 + AdrWord , WordToRead);
		Rres = new ReadMultipleRegistersResponse();
		int[] TP = new int[WordToRead];
		
		 try 
		 {
		        trans = new ModbusTCPTransaction(con);
		        trans.setRetries(5);
		        trans.setReconnecting(true);
		        trans.setRequest(Rreq);
		        trans.execute();
		        Rres = (ReadMultipleRegistersResponse) trans.getResponse();
		        
		        for (int k=0;k<WordToRead;k++)
		        {
		        	TP[k] =  Rres.getRegisterValue(k);
		        }       
		   }
		 catch(Exception e) { }				
		
         return TP;
     }
	//================================================================================================
	 public void MDB_WriteSingleWordRegister(int AdrWord, int[] WordToWrite)
     { 
		 Register REG;
		 Register[] registers = new Register[WordToWrite.length];
		 
		 for( int k=0; k < WordToWrite.length; k++) { REG = new SimpleRegister(WordToWrite[k]);registers[k]= REG; }

		 Wreq = new WriteMultipleRegistersRequest(12288 + AdrWord , registers);
		 try 
		 {
		        trans = new ModbusTCPTransaction(con);
		        trans.setRetries(5);
		        trans.setReconnecting(true);
		        trans.setRequest(Wreq);
		        trans.execute();
		        Wres = (WriteMultipleRegistersResponse) trans.getResponse();
		   }
		 catch(Exception e) { 
			 Log.i("JaModbusTask", "exception on sending request " + e.getMessage());
			 
		 }				
		 
     }
	//================================================================================================
     public float[] MDB_ReadRealRegister(int AdrReal, int RealToRead)
     {
    	 int WordToRead = RealToRead * 2 ;
    	 byte[] regBytes;
    	 
         float[] TP = new float[RealToRead];
         //MW0 registre = mot => real sur 4octect donc 2 mot => RealToRead*2 mot a lire
         //
 		Rreq = new ReadMultipleRegistersRequest(12288 + AdrReal , WordToRead);
 		Rres = new ReadMultipleRegistersResponse();
  		 try 
 		 {
 		        trans = new ModbusTCPTransaction(con);
 		        trans.setRetries(5);
 		        trans.setReconnecting(true);
 		        trans.setRequest(Rreq);
 		        trans.execute();
 		        Rres = (ReadMultipleRegistersResponse) trans.getResponse();
 		        
 		        for (int k=0;k<RealToRead;k++)
 		        {
 		        	  regBytes = new byte[4];
		        	  regBytes[0] = Rres.getRegister(2*k+1).toBytes()[0];
	        	      regBytes[1] = Rres.getRegister(2*k+1).toBytes()[1];
	        	      regBytes[2] = Rres.getRegister(2*k).toBytes()[0];
	        	      regBytes[3] = Rres.getRegister(2*k).toBytes()[1];
 		        	TP[k]= ModbusUtil.registersToFloat(regBytes);
 		        }       
 		   }
 		 catch(Exception e) {
 			Log.i("JaModbusTask", "MDB_ReadRealRegister ; exception on sending request " + e.getMessage());
 		 }				
 		
       
         return TP;
     }
	 
	 
	 public void destroy()
	 {
		 con.close();		 
	 }
	 


	 
}
