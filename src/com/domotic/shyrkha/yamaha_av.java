package com.domotic.shyrkha;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

public class yamaha_av {
	private String IPAV = null;
	private DefaultHttpClient httpclient = null;
	private HttpPost httppost = null;  
	private InputStream is=null;
	//--------------------------------------
	private String MainVolume = null;
	
	public String Model;public String ID;public String Version;
	public boolean capMain_Zone;public boolean capZone_2;public boolean capZone_3;public boolean capZone_4;
	public boolean capTuner;public boolean capHD_Radio;public boolean capRhapsody;public boolean capNapster;public boolean capSiriusXM;public boolean capPandora;
	public boolean capSERVER;public boolean capNET_RADIO;public boolean capUSB;public boolean capiPod_USB;public boolean capAirPlay;
	
	public String nradMenuStatus;
	
	private float fMainVolume;
	
	
	
	
	//===============================================
    //192.168.1.21
	public yamaha_av(String sIP)
	{
		httpclient = new DefaultHttpClient();
		httppost = new HttpPost("http://"+ sIP +"/YamahaRemoteControl/ctrl");  
		getSysConfig();
		getMainVolume();
		setMainVolume((float)-22.5);
		
	}
	//===============================================
	public void setMainVolume(String sMAINVOL)
	{
		String sXML = "<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Volume><Lvl><Val>"+sMAINVOL+"</Val><Exp>1</Exp><Unit>dB</Unit></Lvl></Volume></Main_Zone></YAMAHA_AV>";
		SendPUTRequest( sXML);
	}
	//===============================================	
	public void setMainVolume(float fMAINVOL)
	{
		String sXML = "<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Volume><Lvl><Val>"+FloatVolToString(fMAINVOL)+"</Val><Exp>1</Exp><Unit>dB</Unit></Lvl></Volume></Main_Zone></YAMAHA_AV>";
		SendPUTRequest( sXML);
	}
	//===============================================
	//on peut tirer plus d'information
	/*
	 <YAMAHA_AV rsp="GET" RC="0">
	 	<Main_Zone>
	 		<Basic_Status>
	 			<Power_Control>
	 				<Power>On</Power>
	 				<Sleep>Off</Sleep>
	 			</Power_Control>
	 			<Volume>
	 				<Lvl>
	 					<Val>-230</Val>
	 					<Exp>1</Exp>
	 					<Unit>dB</Unit>
	 				</Lvl>
	 				<Mute>Off</Mute>
	 				<Subwoofer_Trim>
	 					<Val>0</Val>
	 					<Exp>1</Exp>
	 					<Unit>dB</Unit>
	 				</Subwoofer_Trim>
	 			</Volume>
	 				<Input>
	 					<Input_Sel>HDMI1</Input_Sel>
	 					<Input_Sel_Item_Info>
	 						<Param>HDMI1</Param>
	 						<RW>RW</RW>
	 						<Title>HDMI1</Title>
	 						<Icon>
	 							<On>/YamahaRemoteControl/Icons/icon004.png</On>
	 							<Off></Off>
	 						</Icon>
	 						<Src_Name>
	 						</Src_Name>
	 						<Src_Number>1</Src_Number>
	 					</Input_Sel_Item_Info>
	 				</Input>
	 				<Surround>
	 					<Program_Sel>
	 						<Current>
	 							<Straight>Off</Straight>
	 							<Enhancer>On</Enhancer>
	 							<Sound_Program>Surround Decoder</Sound_Program>
	 						</Current>
	 					</Program_Sel>
	 					<_3D_Cinema_DSP>Auto</_3D_Cinema_DSP>
	 				</Surround>
	 				<Sound_Video>
	 					<Tone>
	 						<Bass>
	 							<Val>0</Val>
	 							<Exp>1</Exp>
	 							<Unit>dB</Unit>
	 						</Bass>
	 						<Treble>
	 							<Val>0</Val>
	 							<Exp>1</Exp>
	 							<Unit>dB</Unit>
	 						</Treble>
	 					</Tone>
	 					<Pure_Direct>
	 						<Mode>Off</Mode>
	 					</Pure_Direct>
	 					<HDMI>
	 						<Standby_Through_Info>Off</Standby_Through_Info>
	 						<Output>
	 							<OUT_1>On</OUT_1>
	 						</Output>
	 					</HDMI>
	 					<Adaptive_DRC>Off</Adaptive_DRC>
	 					<Dialogue_Adjust>
	 						<Dialogue_Lift>0</Dialogue_Lift>
	 							<Dialogue_Lvl>0</Dialogue_Lvl>
	 					</Dialogue_Adjust>
	 				</Sound_Video>
	 			</Basic_Status>
	 		</Main_Zone>
	 	</YAMAHA_AV>
 
	 */
	public void getMainVolume()
		{
		Element XmlDoc = SendGETRequest("<YAMAHA_AV cmd=\"GET\"><Main_Zone><Basic_Status>GetParam</Basic_Status></Main_Zone></YAMAHA_AV>");
	     try {
	             NodeList properties = XmlDoc.getElementsByTagName("Main_Zone");
	             XPath xpath = XPathFactory.newInstance().newXPath();
	             String expression = "//Volume/Lvl/Val";
	             NodeList nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
	             this.fMainVolume = StringVolToFloat(nodes.item(0).getTextContent());
		     	}
	         
	         catch (Exception e) { 
	         	Log.d("XML", "erro : " + e.getMessage());
	         	//throw new RuntimeException(e); 
	         	} 

			//---------------------------------------
	}
	//===============================================
	
	public void setMainPowerOn()
	{
		String sXML = "<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Power_Control><Power>On</Power></Power_Control></Main_Zone></YAMAHA_AV>";
		SendPUTRequest( sXML);
	}

	public void setMainPowerOff()
	{
		String sXML = "<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Power_Control><Power>Standby</Power></Power_Control></Main_Zone></YAMAHA_AV>";
		SendPUTRequest( sXML);
	}
	
	public void setMute()
	{
		// mute
		//<YAMAHA_AV cmd="PUT"><Main_Zone><Volume><Mute>On</Mute></Volume></Main_Zone></YAMAHA_AV>')		
	}
	
	public void getSysConfig()
	{
		String sTemp;
		Element XmlDoc = SendGETRequest("<YAMAHA_AV cmd=\"GET\"><System><Config>GetParam</Config></System></YAMAHA_AV>");
		//--------------------------------------
		
		
	     try {
             NodeList properties = XmlDoc.getElementsByTagName("System");
             XPath xpath = XPathFactory.newInstance().newXPath();
             
             String expression = "./Config/Model_Name";
             NodeList nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             this.Model = nodes.item(0).getTextContent();
             
             expression = "./Config/System_ID";
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             this.ID = nodes.item(0).getTextContent();
             
             expression = "./Config/Version";
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             this.Version = nodes.item(0).getTextContent();
             
             expression = "./Config/Feature_Existence/Main_Zone";
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             sTemp = nodes.item(0).getTextContent(); if(sTemp.compareTo("1") == 0){ this.capMain_Zone =true; } 
             Log.d("XML", "Main_Zone: " + this.capMain_Zone);
             
             expression = "./Config/Feature_Existence/Zone_2";
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capZone_2 =true; } Log.d("XML", "Zone_2: " + this.capZone_2);
             
             expression = "./Config/Feature_Existence/Zone_3";
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capZone_3 =true; } Log.d("XML", "Zone_3: " + this.capZone_3);
             
             expression = "./Config/Feature_Existence/Zone_4";
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capZone_4 =true; } Log.d("XML", "Zone_4: " + this.capZone_4);         	

             expression = "./Config/Feature_Existence/Tuner";
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capTuner =true; } Log.d("XML", "Tuner: " + this.capTuner);
             
             expression = "./Config/Feature_Existence/HD_Radio";
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capHD_Radio =true; } Log.d("XML", "HD_Radio: " + this.capHD_Radio);
             
             expression = "./Config/Feature_Existence/Rhapsody";
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capRhapsody =true; } Log.d("XML", "Rhapsody: " + this.capRhapsody);

            expression = "./Config/Feature_Existence/Napster";
            nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
            sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capNapster =true; } Log.d("XML", "Napster: " + this.capNapster);

            expression = "./Config/Feature_Existence/SiriusXM";
            nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
            sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capSiriusXM =true; } Log.d("XML", "SiriusXM: " + this.capSiriusXM);
            
            expression = "./Config/Feature_Existence/Pandora";
            nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
            sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capPandora =true; } Log.d("XML", "Pandora: " + this.capPandora);
         	
            expression = "./Config/Feature_Existence/SERVER";
            nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
            sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capSERVER =true; } Log.d("XML", "SERVER: " + this.capSERVER);
      
            expression = "./Config/Feature_Existence/NET_RADIO";
            nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
            sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capNET_RADIO =true; } Log.d("XML", "NET_RADIO: " + this.capNET_RADIO);

            expression = "./Config/Feature_Existence/USB";
            nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
            sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capUSB =true; } Log.d("XML", "USB: " + this.capUSB);

            expression = "./Config/Feature_Existence/iPod_USB";
            nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
            sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capiPod_USB =true; } Log.d("XML", "iPod_USB: " + this.capiPod_USB);

            expression = "./Config/Feature_Existence/AirPlay";
            nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
            sTemp = nodes.item(0).getTextContent();if(sTemp.compareTo("1") == 0){ this.capAirPlay =true; } Log.d("XML", "AirPlay: " + this.capAirPlay);
            
             
	     	}
         
         catch (Exception e) { 
         	Log.d("XML", "erro : " + e.getMessage());
         	//throw new RuntimeException(e); 
         	} 
		/*
		 
		  "<YAMAHA_AV rsp=""GET"" RC=""0"">
			<System><Config><Model_Name>RX-V673</Model_Name><System_ID>0E516513</System_ID><Version>1.64/2.06</Version>
    						<Feature_Existence>
								<Main_Zone>1</Main_Zone><Zone_2>1</Zone_2><Zone_3>0</Zone_3><Zone_4>0</Zone_4>
								<Tuner>1</Tuner><HD_Radio>0</HD_Radio><Rhapsody>0</Rhapsody><Napster>1</Napster><SiriusXM>0</SiriusXM>
								<Pandora>0</Pandora><SERVER>1</SERVER><NET_RADIO>1</NET_RADIO><USB>1</USB><iPod_USB>1</iPod_USB><AirPlay>1</AirPlay>
							</Feature_Existence>
    						<Name>
								<Input>
									<HDMI_1>HDMI1</HDMI_1><HDMI_2>HDMI2</HDMI_2><HDMI_3>HDMI3</HDMI_3><HDMI_4>HDMI4</HDMI_4><HDMI_5>HDMI5</HDMI_5>
      								<AV_1>AV1</AV_1><AV_2>AV2</AV_2><AV_3>AV3</AV_3><AV_4>AV4</AV_4><AV_5>AV5</AV_5><AV_6>AV6</AV_6>
									<V_AUX>V-AUX</V_AUX>
      								<AUDIO_1>AUDIO1</AUDIO_1><AUDIO_2>AUDIO2</AUDIO_2>
      								<USB>USB</USB>
								</Input>
							</Name>
			</Config></System>
		</YAMAHA_AV>"

		  
		 */
	}
	
	//===============================================	
	private float StringVolToFloat(String sVOL)
	{
		float fVol = Float.valueOf(sVOL.trim()).floatValue(); 
		fVol = fVol/10;
		return fVol;
	}
	private String FloatVolToString( float fVOL)
	{
		String sVol =  Integer.toString((int)(fVOL*10));
		return sVol;
	}
	private String NodesToString( NodeList oNL)
	{
		String sResult;int i;
		sResult = "Nombre de noeuds : " + oNL.getLength() +"\n";
		
		for(i=0;i<oNL.getLength();i++)
		{
			sResult+="Noeud "+i;
			sResult+=" NOM "+oNL.item(0).getNodeName();
			sResult+=" TEXT " + oNL.item(0).getTextContent();
			sResult+=" VALUE "+ oNL.item(0).getNodeValue()+"\n";
		}
		return sResult;
	}
	//====================================================
	/*
	 * <YAMAHA_AV rsp="GET" RC="0">
	 * 	<NET_RADIO>
	 * 		<List_Info>
	 * 			<Menu_Status>Busy</Menu_Status>
	 * 			<Menu_Layer>1</Menu_Layer>
	 * 			<Menu_Name></Menu_Name>
	 * 			<Current_List>
	 * 				<Line_1><Txt></Txt><Attribute>Unselectable</Attribute></Line_1>
	 * 				<Line_2><Txt></Txt><Attribute>Unselectable</Attribute></Line_2>
	 * 				<Line_3><Txt></Txt><Attribute>Unselectable</Attribute></Line_3>
	 * 				<Line_4><Txt></Txt><Attribute>Unselectable</Attribute></Line_4>
	 * 				<Line_5><Txt></Txt><Attribute>Unselectable</Attribute></Line_5>
	 * 				<Line_6><Txt></Txt><Attribute>Unselectable</Attribute></Line_6>
	 * 				<Line_7><Txt></Txt><Attribute>Unselectable</Attribute></Line_7>
	 * 				<Line_8><Txt></Txt><Attribute>Unselectable</Attribute></Line_8>
	 * 			</Current_List>
	 * 			<Cursor_Position>
	 * 				<Current_Line>1</Current_Line>
	 * 				<Max_Line>0</Max_Line>
	 * 			</Cursor_Position>
	 * 		</List_Info>
	 * </NET_RADIO>
	 * </YAMAHA_AV>

<YAMAHA_AV cmd="PUT"><NET_RADIO><List_Control><Cursor>Return</Cursor></List_Control></NET_RADIO></YAMAHA_AV>
<YAMAHA_AV rsp="PUT" RC="0"><NET_RADIO><List_Control><Cursor></Cursor></List_Control></NET_RADIO></YAMAHA_AV>
------------------------------------------------------------------------
<YAMAHA_AV cmd="PUT"><NET_RADIO><List_Control><Direct_Sel>Line_6</Direct_Sel></List_Control></NET_RADIO></YAMAHA_AV>
<YAMAHA_AV rsp="PUT" RC="0"><NET_RADIO><List_Control><Direct_Sel></Direct_Sel></List_Control></NET_RADIO></YAMAHA_AV>

	 * */
	//=================================================================
	//"<YAMAHA_AV cmd=\"GET\"><NET_RADIO><List_Info>GetParam</List_Info></NET_RADIO></YAMAHA_AV>"
	public void NETRADIO_ListInfo()
	{
		String sTemp;
		int iTemp;
		Element XmlDoc = SendGETRequest("<YAMAHA_AV cmd=\"GET\"><NET_RADIO><List_Info>GetParam</List_Info></NET_RADIO></YAMAHA_AV>");
		//--------------------------------------
		
		
	     try {
             NodeList properties = XmlDoc.getElementsByTagName("NET_RADIO");
             XPath xpath = XPathFactory.newInstance().newXPath();
             
             String expression = "./List_Info/Menu_Status";
             NodeList nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             this.nradMenuStatus = nodes.item(0).getTextContent();
             Log.d("XML", "NET_RADIO Menu Status : " +this.nradMenuStatus);
             
             expression = "./List_Info/Current_List/*";
            // xpath = XPathFactory.newInstance().newXPath();
             nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             Log.d("XML", "Xpath = " + expression +" retourne " + nodes.getLength() + "noeuds");
             /*  */
             for (iTemp=0;iTemp<nodes.getLength();iTemp++)
             {
            	 sTemp =  iTemp +" : " + nodes.item(iTemp).getChildNodes().item(0).getTextContent()+ " : " + nodes.item(iTemp).getChildNodes().item(1).getTextContent()+"\n";
            	 Log.d("XML", sTemp);
             }
            
             
             
	     }
             catch (Exception e) { 
              	Log.d("XML", "NETRADIO_ListInfo ERROR: " + e.getMessage());
              	//throw new RuntimeException(e); 
              	} 
	}
	//=================================================================
	//"<YAMAHA_AV cmd=\"GET\"><NET_RADIO><Play_Info>GetParam</Play_Info></NET_RADIO></YAMAHA_AV>"
	public void NETRADIO_PlayInfo()
	{
		String sTemp;
		String sTemp2;
		Element XmlDoc = SendGETRequest("<YAMAHA_AV cmd=\"GET\"><NET_RADIO><Play_Info>GetParam</Play_Info></NET_RADIO></YAMAHA_AV>");
		//--------------------------------------
		
		
	     try {
             NodeList properties = XmlDoc.getElementsByTagName("NET_RADIO");
             XPath xpath = XPathFactory.newInstance().newXPath();
          
             String expression = "./Play_Info/Feature_Availability";
             NodeList nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             sTemp2 = nodes.item(0).getTextContent();
             Log.d("XML", "NETRADIO_PlayInfo  : ./Play_Info/Feature_Availability = " +sTemp2);
             
             if(sTemp2.compareTo("Ready") == 0)
             	{
            	 expression = "./Play_Info/Playback_Info";
            	 nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
                 sTemp ="Etat " +  nodes.item(0).getTextContent();
                 Log.d("XML", "NETRADIO_PlayInfo  : ./Play_Info/Feature_Availability = " +sTemp);
                 
                 expression = "./Play_Info/Meta_Info/*";
            	 nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
                 sTemp +="; Station " +  nodes.item(0).getTextContent();
                 sTemp +="; Album " +  nodes.item(1).getTextContent();
                 sTemp +="; Song " +  nodes.item(2).getTextContent();
                 Log.d("XML", "NETRADIO_PlayInfo  : " +sTemp);
             	}
             
             
             }
             catch (Exception e) { 
              	Log.d("XML", "NETRADIO_PlayInfo ERROR : " + e.getMessage());
              	//throw new RuntimeException(e); 
              	} 
	}
	//=================================================================
	//"<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Input><Input_Sel>NET RADIO</Input_Sel></Input></Main_Zone></YAMAHA_AV>"
	public void NETRADIO_Select()
	{
		SendPUTRequest("<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Input><Input_Sel>NET RADIO</Input_Sel></Input></Main_Zone></YAMAHA_AV>");//ok
		
	}
	//=================================================================
	public void DebugProtocol()
	{
		/* commande put car ecriture ???*/
		//this.DebugRequest("<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Volume><Mute>On</Mute></Volume></Main_Zone></YAMAHA_AV>");//ok
		//this.DebugRequest("<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Volume><Mute>Off</Mute></Volume></Main_Zone></YAMAHA_AV>");//ok
		
		/*
		 * repond tjs busy si  pas dans le mode net radio et si pas dans lsite de selection
		 */
		//this.DebugRequest("<YAMAHA_AV cmd=\"GET\"><NET_RADIO><List_Info>GetParam</List_Info></NET_RADIO></YAMAHA_AV>");//ok
		
		//this.DebugRequest("<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Input><Input_Sel>NET RADIO</Input_Sel></Input></Main_Zone></YAMAHA_AV>");//ok
		
		/*fonctionne quand lecture en cours*/
		//this.DebugRequest("<YAMAHA_AV cmd=\"GET\"><NET_RADIO><Play_Info>GetParam</Play_Info></NET_RADIO></YAMAHA_AV>");//ok
		/*
		 on fait un <YAMAHA_AV cmd="GET"><NET_RADIO><List_Info>GetParam</List_Info></NET_RADIO></YAMAHA_AV>
		 l amp repond
		 <YAMAHA_AV rsp="GET" RC="0"><NET_RADIO><List_Info><Menu_Status>Ready</Menu_Status><Menu_Layer>1</Menu_Layer><Menu_Name>NET RADIO</Menu_Name><Current_List><Line_1><Txt>Favoris</Txt><Attribute>Container</Attribute></Line_1><Line_2><Txt>Pays</Txt><Attribute>Container</Attribute></Line_2><Line_3><Txt>Genres</Txt><Attribute>Container</Attribute></Line_3><Line_4><Txt>Nouvelles stations</Txt><Attribute>Container</Attribute></Line_4><Line_5><Txt>Stations populaires</Txt><Attribute>Container</Attribute></Line_5><Line_6><Txt>Podcasts</Txt><Attribute>Container</Attribute></Line_6><Line_7><Txt>Aide</Txt><Attribute>Container</Attribute></Line_7><Line_8><Txt></Txt><Attribute>Unselectable</Attribute></Line_8></Current_List><Cursor_Position><Current_Line>1</Current_Line><Max_Line>7</Max_Line></Cursor_Position></List_Info></NET_RADIO></YAMAHA_AV>
		 puis on choisit 
		 YAMAHA_AV cmd="PUT"><NET_RADIO><List_Control><Direct_Sel>Line_2</Direct_Sel></List_Control></NET_RADIO></YAMAHA_AV>
		 
		 <YAMAHA_AV cmd="PUT"><NET_RADIO><List_Control><Cursor>Down</Cursor></List_Control></NET_RADIO></YAMAHA_AV>
		 <YAMAHA_AV cmd="PUT"><NET_RADIO><List_Control><Cursor>Return</Cursor></List_Control></NET_RADIO></YAMAHA_AV>
		 
		 */
		Element XmlDoc = SendGETRequest("<YAMAHA_AV cmd=\"GET\"><Main_Zone><Basic_Status>GetParam</Basic_Status></Main_Zone></YAMAHA_AV>");
		//--------------------------------------
		
		
	     try {
             NodeList properties = XmlDoc.getElementsByTagName("Main_Zone");
             XPath xpath = XPathFactory.newInstance().newXPath();
             String expression = "//Volume/Lvl/Val";
             NodeList nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);
             Log.d("XML", NodesToString(nodes) );
             Log.d("XML", "Volume (dB) " + StringVolToFloat(nodes.item(0).getTextContent()) );
	     	}
         
         catch (Exception e) { 
         	Log.d("XML", "DebugProtocol ERROR : " + e.getMessage());
         	//throw new RuntimeException(e); 
         	} 
		
		//---------------------------------------
		
		
		
	}
	//=================================================================
	
	private void DebugRequest( String sReqXML)
	{
		
		try 
		{			
			StringEntity se = new StringEntity( sReqXML, HTTP.UTF_8);se.setContentType("text/xml");
            httppost.setEntity(se);httppost.setHeader("Content-Type", "text/xml");httppost.setHeader("Accept", "*/*");
            HttpResponse response = httpclient.execute(httppost);// Execute HTTP Post Request
            Log.d("XML",response.getAllHeaders().toString());
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) 
            {
            	is = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = null;
                try { while ((line = reader.readLine()) != null) { sb.append(line + "\n"); } } catch (IOException e) { e.printStackTrace();} 
                String sTemp = sb.toString();Log.d("XML", "Result: " + sTemp);
            }
			is.close();
		}
		catch (ClientProtocolException e) { }
        catch (IOException e) { }   
		
	}
	
	//================================================================
	//requete retourant un resultat
	private Element SendGETRequest( String sReqXML )
	{
		 Element root = null;
		 
	        try {
	            StringEntity se = new StringEntity( sReqXML, HTTP.UTF_8);
	            se.setContentType("text/xml");
	            httppost.setEntity(se);httppost.setHeader("Content-Type", "text/xml");httppost.setHeader("Accept", "*/*");
	            HttpResponse response = httpclient.execute(httppost);// Execute HTTP Post Request
	            HttpEntity httpEntity = response.getEntity();
	         
	            if (httpEntity != null) {
	                is = httpEntity.getContent();
	                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	                StringBuilder sb = new StringBuilder();
	                String line = null;
	                try { while ((line = reader.readLine()) != null) { sb.append(line + "\n"); } }
	                catch (IOException e) { e.printStackTrace();} 

	                String sTemp = sb.toString();
	                Log.d("XML", "Result: " + sTemp);
	                try {
	                    if (httpEntity != null) {
	                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	                    String sTemp2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+sTemp;
	                    DocumentBuilder builder = factory.newDocumentBuilder();
	                   Document dom = builder.parse(new InputSource(new StringReader(sTemp2)));
	                    root = dom.getDocumentElement();
	                   /* NodeList properties = root.getElementsByTagName("Main_Zone");
	                    XPath xpath = XPathFactory.newInstance().newXPath();
	                    String expression = "//Volume/Lvl/Val";
	                    NodeList nodes = (NodeList) xpath.evaluate(expression, properties.item(0), XPathConstants.NODESET);*/
	                    }
	                }
	                
	                catch (Exception e) { 
	                	Log.d("XML", "SendGETRequest ERROR : " + e.getMessage());
	                	//throw new RuntimeException(e); 
	                	} 
		
	            }
	        }
		 
	        catch (ClientProtocolException e) { }
	        catch (IOException e) { }  
		return root;
	}
	
	
	private void SendPUTRequest( String sReqXML)
{
		
		try 
		{			
			StringEntity se = new StringEntity( sReqXML, HTTP.UTF_8);se.setContentType("text/xml");
            httppost.setEntity(se);httppost.setHeader("Content-Type", "text/xml");httppost.setHeader("Accept", "*/*");
            HttpResponse response = httpclient.execute(httppost);// Execute HTTP Post Request
		}
		catch (ClientProtocolException e) { }
        catch (IOException e) { }   
	}
	
}
