package com.domotic.shyrkha;

import java.util.ArrayList;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;



public class JaModbusTask extends AsyncTask<Void, Integer, Void>
{
	private Context REFCTXT = null;
	private MainActivity REFACT = null;
	private String sTemp = null;
	private boolean RUNNING = false;		
	private boolean REQCLIENT = false;
	
	private String[] ListEmplacement = new String[] {"garage", "entr�e", "salon", "couloir", "chambre une", "bureau", "cuisine", "cellier", "maison",
			"baie salon","fen�tre salon","cuisine c�t�","cuisine arri�re" };
	private String[] ListElement = new String[] { "lumi�re", "volet", "prise", "porte" };
    private String[] ListAction = new String[] { "allumer", "�teindre", "ouvrir", "fermer", "lire", "consulter","�couter" };
	
	private int EMP,ACT,ELM;
	private int CMD;
	Telerupteur[] LumTlhome;
    VoletRoulant[] VolRlhome;
    PorteAuto[] PortAutohome;
	
    ModbusTCP MODBUSTCP;
	
	
	public JaModbusTask( Context APPCTXT, MainActivity MACT ,boolean RUN )
	{
		this.REFCTXT = APPCTXT;
		this.RUNNING = RUN;
		this.REFACT = MACT;
		SetUpHarwarePoint(); 
		
	}
	//-----------------------------------------------------------------------------------
	@Override
	protected void onPreExecute() {	
			
	
		super.onPreExecute();	
	}
	//-----------------------------------------------------------------------------------
	@Override protected void onProgressUpdate(Integer... values){
		super.onProgressUpdate(values);
		//this.REFPGB.setProgress(values[0]);// Mise � jour de la ProgressBar
	}
	//-----------------------------------------------------------------------------------	
    public void Request()
	{
		//REQCLIENT = true;
		//REFACT.SPEAK("REQUETE CLIENT PRISE EN COMPTE");
		
		ModbusTCP MTCP = new ModbusTCP("192.168.1.100" , 502);
		Log.i("JaModbusTask", "init ModbusTCP r�ussie := "+ MTCP.IsConnected());
		int[] MW100 = new int[1];
		MW100 = MTCP.MDB_ReadWordRegister(100, 1);
		Log.i("JaModbusTask", "lecture MW100 := "+ MW100[0]);
		
		/*
		int[] MW102 = new int[1];MW102[0]=64;
		MTCP.MDB_WriteSingleWordRegister(102,MW102);
		Log.i("JaModbusTask", "ecriture MW102 := 64");
		MW100 = MTCP.MDB_ReadWordRegister(100, 1);
		Log.i("JaModbusTask", "lecture MW100 := "+ MW100[0]);
		*/
		float[] MW0 = new float[1];
		MW0 = MTCP.MDB_ReadRealRegister(0, 1);
		Log.i("JaModbusTask", "lecture MW100 := "+ MW0[0]);
		
		MTCP.destroy();
		MTCP = null;
		
	}
    //-----------------------------------------------------------------------------------
	@Override	protected Void doInBackground(Void... arg0) 
	{
		MODBUSTCP = new ModbusTCP("192.168.1.100" , 502);
		Log.i("JaModbusTask", "init ModbusTCP r�ussie := "+ MODBUSTCP.IsConnected());


			  while(RUNNING)
			  {
				  try {
					
					  if(REQCLIENT)
					  {
						  Log.i("JaModbusTask", "requ�te client prise en compte.");
						  REQCLIENT = false;
					  }
					  else
					  {
						  Log.i("JaModbusTask", "ModbusTCP connect� := "+ MODBUSTCP.IsConnected());
						  if(MODBUSTCP.IsConnected())
						  {
							  LectureLumiere();
						  }
						  Thread.sleep(2000);
						  
					  }
					  

					
					
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			  }

		
		return null;
	}
    //-----------------------------------------------------------------------------------

    //-----------------------------------------------------------------------------------
		
	@Override
	protected void onPostExecute(Void result) {
		Toast.makeText(this.REFCTXT, sTemp, Toast.LENGTH_LONG).show();
	}
	public void Request(ArrayList<String> data) {
		String sTemp;
		EMP = -1;ACT=-1;ELM=-1;
		
		// TODO on doit determiner la commande depuis la liste des phrases reconnues et les list emplacment,element et action
		 for (int i = 0; i < data.size(); i++)
         {
			 sTemp =data.get(i); 
			 
			 for (int j = 0; j < ListEmplacement.length; j++) { if(sTemp.indexOf(ListEmplacement[j]) > -1) { EMP =j;break; } }//recherche emplacement
			 for (int j = 0; j < ListElement.length; j++) { if(sTemp.indexOf(ListElement[j]) > -1) { ELM =j;break; } }//recherche emplacement
			 for (int j = 0; j < ListAction.length; j++) { if(sTemp.indexOf(ListAction[j]) > -1) { ACT =j;break; } }//recherche emplacement
			 
			 if ( (EMP > -1 ) && (ELM > -1 ) && (ACT > -1 ))
			 {
			 Log.d("SRE", "JaModbusTask.Request(ArrayList<String> data) => " + sTemp + "[EMP = "+ EMP + "( "+ ListEmplacement[EMP] +"; ELM ="+ ELM + "( "+ ListElement[ELM] +" ; ACT = "+ ACT + "( "+ ListAction[ACT] +"]");
			 break;
			 } //on a trouve rien ne sert de parcourir la liste plus avant
			 /*
			 Log.d("SRE", "JaModbusTask.Request(ArrayList<String> data) => " + sTemp );
			 Log.d("SRE", "[EMP = "+ EMP + "; ELM ="+ ELM + " ; ACT = "+ ACT + " ]" );
			  */
         }
		
		 CMD = (65536 * ELM) + (256 * EMP) + ACT;
		 if (CMD >= 0) 
		 {
			 String sRes = InterpreterCommande(); 
			 REFACT.SPEAK(sRes);
		 }
	}

	//*****************************************************************

	private String InterpreterCommande() {
		String sTemp="";
		 switch(ELM)
         {
             case 0 :   LectureLumiere();   break; //commande ne concerne que lumiere
             case 1    :   LectureVolet();     break;  //commande volet
             case 3    :   LecturePorte();     break;  //commande porte
             default: break;
         }
		
		 
         switch (CMD) 
         {
		//---------------------------------------------
//       LUMIERE
//---------------------------------------------
//allumer lumi�re garage
             case 0 :if (LumTlhome[0].State) { sTemp = "La lumi�re du garage est d�ja allum�e."; } else { int[] val = {1};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;
//�teindre lumi�re garage
             case 1 :if (!LumTlhome[0].State) { sTemp = "La lumi�re du garage est d�ja �teinte."; } else { int[] val = {1};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;

//allumer lumi�re entr�e
             case 256 :if (LumTlhome[1].State) { sTemp = "La lumi�re de l'entr�e est d�ja allum�e."; } else { int[] val = {2};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;
//�teindre lumi�re entr�e
             case 257 :if (!LumTlhome[1].State) { sTemp = "La lumi�re de l'entr�e est d�ja �teinte."; } else { int[] val = {2};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;

//allumer lumi�re salon
             case 512 :if (LumTlhome[2].State) { sTemp = "La lumi�re du salon est d�ja allum�e."; } else { int[] val = {4};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;
//�teindre lumi�re salon
             case 513 :if (!LumTlhome[2].State) { sTemp = "La lumi�re du salon est d�ja �teinte."; } else { int[] val = {4};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;

//allumer lumi�re couloir
             case 768 :if (LumTlhome[3].State) { sTemp = "La lumi�re du couloir est d�ja allum�e."; } else { int[] val = {8};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;
//�teindre lumi�re couloir
             case 769 :if (!LumTlhome[3].State) { sTemp = "La lumi�re du couloir est d�ja �teinte."; } else { int[] val = {8};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;

//allumer lumi�re chambre une
             case 1024 :if (LumTlhome[4].State) { sTemp = "La lumi�re de la chambre une est d�ja allum�e."; } else { int[] val = {16};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;
//�teindre lumi�re chambre une
             case 1025 :if (!LumTlhome[4].State) { sTemp = "La lumi�re de la chambre une est d�ja �teinte."; } else { int[] val = {16};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;

//allumer lumi�re bureau
             case 1280 :if (LumTlhome[5].State) { sTemp = "La lumi�re du bureau est d�ja allum�e."; } else { int[] val = {32};MODBUSTCP.MDB_WriteSingleWordRegister(102,val);}break;
//�teindre lumi�re bureau
             case 1281 :if (!LumTlhome[5].State) { sTemp = "La lumi�re du bureau est d�ja �teinte."; } else { int[] val = {32};MODBUSTCP.MDB_WriteSingleWordRegister(102,val);}break;

//allumer lumi�re cuisine
             case 1536 :if (LumTlhome[6].State) { sTemp = "La lumi�re de la cuisine est d�ja allum�e."; } else { int[] val = {64};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;
//�teindre lumi�re cuisine
             case 1537 :if (!LumTlhome[6].State) { sTemp = "La lumi�re de la cuisine est d�ja �teinte."; } else { int[] val = {64};MODBUSTCP.MDB_WriteSingleWordRegister(102,val);}break;

//allumer lumi�re cellier
             case 1792 :if (LumTlhome[7].State) { sTemp = "La lumi�re du cellier est d�ja allum�e."; } else { int[] val = {128};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;
//�teindre lumi�re cellier
             case 1793 :if (!LumTlhome[7].State) { sTemp = "La lumi�re du cellier est d�ja �teinte."; } else { int[] val = {128};MODBUSTCP.MDB_WriteSingleWordRegister(102,val); }break;
//allumer lumi�re maison
             case 2048 :
            	 int[] val = {0};
                 if (LumTlhome[0].State) { sTemp = "La lumi�re du garage est d�ja allum�e."; } else { val[0]+=1; }
                 if (LumTlhome[1].State) { sTemp += "La lumi�re de l'entr�e est d�ja allum�e."; } else { val[0]+=2; }
                 if (LumTlhome[2].State) { sTemp += "La lumi�re du salon est d�ja allum�e."; } else { val[0]+=4; }
                 if (LumTlhome[3].State) { sTemp += "La lumi�re du couloir est d�ja allum�e."; } else { val[0]+=8; }
                 if (LumTlhome[4].State) { sTemp += "La lumi�re de la chambre une est d�ja allum�e."; } else { val[0]+=16; }
                 if (LumTlhome[5].State) { sTemp += "La lumi�re du bureau est d�ja allum�e."; } else { val[0]+=32; }
                 if (LumTlhome[6].State) { sTemp += "La lumi�re de la cuisine est d�ja allum�e."; } else { val[0]+=64; }
                 if (LumTlhome[7].State) { sTemp += "La lumi�re du cellier est d�ja allum�e."; } else { val[0]+=128; }
                 MODBUSTCP.MDB_WriteSingleWordRegister(102,val);

                 break;
//�teindre lumi�re maison
             case 2049 :
            	 int[] val2 = {0};
                 if (!LumTlhome[0].State) { sTemp = "La lumi�re du garage est d�ja �teinte."; } else { val2[0]+=1; }
                 if (!LumTlhome[1].State) { sTemp += "La lumi�re de l'entr�e est d�ja �teinte."; } else { val2[0]+=2; }
                 if (!LumTlhome[2].State) { sTemp += "La lumi�re du salon est d�ja �teinte."; } else { val2[0]+=4; }
                 if (!LumTlhome[3].State) { sTemp += "La lumi�re du couloir est d�ja �teinte."; } else { val2[0]+=8; }
                 if (!LumTlhome[4].State) { sTemp += "La lumi�re de la chambre une est d�ja �teinte."; } else { val2[0]+=16; }
                 if (!LumTlhome[5].State) { sTemp += "La lumi�re du bureau est d�ja �teinte."; } else { val2[0]+=32; }
                 if (!LumTlhome[6].State) { sTemp += "La lumi�re de la cuisine est d�ja �teinte."; } else { val2[0]+=64; }
                 if (!LumTlhome[7].State) { sTemp += "La lumi�re du cellier est d�ja �teinte."; } else { val2[0]+=128; }
                 MODBUSTCP.MDB_WriteSingleWordRegister(102,val2);
                 break;
		 
         }
		 return sTemp;
	}
	private void LecturePorte() {
		// TODO Auto-generated method stub
		
	}
	private void LectureVolet() {
		// TODO Auto-generated method stub
		
	}
	//=============================================================================================
	private void LectureLumiere() {
	
        int WordReaded = 0;int WordCompare = 0;int RegisterBitsToRead = 8;int i = 0;
		int[] MW100 = new int[1];
		MW100 = MODBUSTCP.MDB_ReadWordRegister(100, 1);
		//Log.i("JaModbusTask", "LectureLumiere() : lecture MW100 := "+ MW100[0]);
        WordReaded = MW100[0];

        for (i = 0; i < RegisterBitsToRead; i++)
        {
            WordCompare = (int)Math.pow(2, i);
            if ((WordReaded & WordCompare) != 0) { LumTlhome[i].State = true; } else { LumTlhome[i].State = false; }
          //  Log.i("JaModbusTask", " i := "+ i + "LumTlhome[i].State :="+ LumTlhome[i].State);
        }
		
	}
	//=============================================================================================	
	private void SetUpHarwarePoint() 
       {
           Telerupteur tltemp;
           LumTlhome = new Telerupteur[8];//idx dans liste 0-15 correspond a offset dans le mot ici seulement 8 tl
           tltemp = new Telerupteur();     tltemp.TlName = "garage";       LumTlhome[0] = tltemp;
           tltemp = new Telerupteur();     tltemp.TlName = "entr�e";       LumTlhome[1] = tltemp;
           tltemp = new Telerupteur();     tltemp.TlName = "salon";        LumTlhome[2] = tltemp;
           tltemp = new Telerupteur();     tltemp.TlName = "couloir";      LumTlhome[3] = tltemp;
           tltemp = new Telerupteur();     tltemp.TlName = "chambre une";  LumTlhome[4] = tltemp;
           tltemp = new Telerupteur();     tltemp.TlName = "bureau";       LumTlhome[5] = tltemp;
           tltemp = new Telerupteur();     tltemp.TlName = "cuisine";      LumTlhome[6] = tltemp;
           tltemp = new Telerupteur();     tltemp.TlName = "cellier";      LumTlhome[7] = tltemp;

           VoletRoulant vrtemp;
           VolRlhome = new VoletRoulant[7];
           vrtemp = new VoletRoulant();    vrtemp.VrName = "baie salon";   VolRlhome[0] = vrtemp;
           vrtemp = new VoletRoulant();    vrtemp.VrName = "fen�tre salon"; VolRlhome[1] = vrtemp;
           vrtemp = new VoletRoulant();    vrtemp.VrName = "cuisine c�t�"; VolRlhome[2] = vrtemp;
           vrtemp = new VoletRoulant();    vrtemp.VrName = "cusine arri�re"; VolRlhome[3] = vrtemp;
           vrtemp = new VoletRoulant();    vrtemp.VrName = "cellier";      VolRlhome[4] = vrtemp;
           vrtemp = new VoletRoulant();    vrtemp.VrName = "chambre une";  VolRlhome[5] = vrtemp;
           vrtemp = new VoletRoulant();    vrtemp.VrName = "bureau";       VolRlhome[6] = vrtemp;

           PorteAuto patemp;
           PortAutohome = new PorteAuto[1];
           patemp = new PorteAuto();       patemp.PaName = "garage";       PortAutohome[0] = patemp;

       }
}
