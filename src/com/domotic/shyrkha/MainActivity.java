package com.domotic.shyrkha;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.os.StrictMode;

public class MainActivity extends Activity {
	//---------------------------------------------
	private ListView LV1 = null;
	private List<Command> LstCommand = new ArrayList<Command>();//liste des Command voir classe Command
	CommandAdapter adapter;
	JaModbusTask JMT;
	yamaha_av RXV673;
	private TextToSpeech speech;
	private SpeechRecognizer sr;


	//---------------------------------------------	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		InitCommandList();
        adapter = new CommandAdapter(this, LstCommand);
        LV1 = (ListView)findViewById(R.id.listView1);LV1.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
       
        
        speech =  new TextToSpeech(this, new TextToSpeech.OnInitListener() 
        { @Override  public void onInit(int status)
        	{
        	if (status == TextToSpeech.SUCCESS) 
        	{
        		int result = speech.setLanguage(Locale.FRANCE);
        		if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) 
        		{ Log.e("TTS", "This Language is not supported"); }
        		else 
        		{ 
        			//speak();
        		}// - See more at: http://androidituts.com/android-text-to-speech-example/#sthash.g6EVc9Mr.dpuf
        	}
        	else { Log.e("TTS", "Initilization Failed!"); }
            }
        }
        );
        JMT = new JaModbusTask(this.getApplicationContext(), this , true);
	//=========================================
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        listener sl = new listener();
       // sl.setJamod(JMT);
        sr.setRecognitionListener(sl);      
        
        JMT.execute();//lance tache JaModbus
        
        RXV673 = new yamaha_av("192.168.1.19");
}

class listener implements RecognitionListener          
{
	private JaModbusTask JMT2;
        public void onReadyForSpeech(Bundle params)
        {
                 Log.d("SRE", "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
                 Log.d("SRE", "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
                 Log.d("SRE", "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
                 Log.d("SRE", "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
                 Log.d("SRE", "onEndofSpeech");
        }
        public void onError(int error)
        {
                 Log.d("SRE",  "error " +  error);
                
        }
        public void onResults(Bundle results)                   
        {
                 //String str = new String();
              Log.d("SRE", "onResults " + results.toString());
                 ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
                 {
                           Log.d("SRE", "result " + data.get(i));
                          // str += data.get(i);
                 }/*     */
        //JMT2.Request(data);
            JMT.Request(data);
        }
        public void onPartialResults(Bundle partialResults)
        {
                 Log.d("SRE", "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
                 Log.d("SRE", "onEvent " + eventType);
        }
        public void setJamod(JaModbusTask JMT4SL)
        	{
        	this.JMT2 = JMT4SL;
        	
        	}

}

	
	

	
	
	public void SPEAK( String Msg)
	{
		speech.speak(Msg, TextToSpeech.QUEUE_FLUSH, null); 
		
	}
	
	
    private void speak() 
    {
    	String text = "Bonjour je suis Androïde";
    	speech.speak(text, TextToSpeech.QUEUE_FLUSH, null); 
    } 
	
	
	@Override
	public void onDestroy()
	{
    	
    	if (speech != null) { speech.stop();speech.shutdown();}// shutdown tts 
    	if(sr!=null){ sr.stopListening();sr.cancel();sr.destroy(); }//shutdown SpeechReco
    	super.onDestroy(); 
	}

	
	//---------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//---------------------------------------------	
	public void start(View view)
	{
		
		
		//LstCommand.add( new Command(new Date(), "Command X" , true) ); adapter.notifyDataSetChanged();

		//StartSreFront();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"com.domotic.shyrkha");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5); 
             sr.startListening(intent);

		

	}

	
	
	

	//---------------------------------------------	
	public void stop(View view)
	{
		sr.stopListening(); //ne sert pas la reco s'arrete seule si front
		
	}
	//-----------------------------------------------
	public void initjamodbus(View view)
	{
		//JMT.execute();
		RXV673.NETRADIO_Select();

    }                

                


	
	
	public void reqjamodbus(View view)
	{
		//JMT.Request();
		
		//Log.d("XML", "AV model : " + RXV673.Model);
		RXV673.NETRADIO_ListInfo();
		RXV673.NETRADIO_PlayInfo();
		//RXV673.setMainVolume("-230");
		//RXV673.DebugProtocol();
	}
	
	
	
	// pour essai
	private void InitCommandList()
	{
		LstCommand.clear();
		/*
		LstCommand.add( new Command(new Date(), "Command 1" , true) );
		LstCommand.add( new Command(new Date(), "Command 2" , false) );
		LstCommand.add( new Command(new Date(), "Command 3" , true) );
		 */
	}
	
	private void AddCommandList( String sCMD , boolean bVAL)
	{
		LstCommand.add( new Command(new Date(), sCMD , bVAL) );
		adapter.notifyDataSetChanged();
	}
	//--------------------------------------------------

}


