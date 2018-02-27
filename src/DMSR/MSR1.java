package DMSR;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : ServiceRegistry.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-02-01
Version : 0.3.01

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-02
Version : 0.5.4
	Added setting response header
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class MSR1{
	
	public static void main(String args[]) throws Exception{
		//==========================================
		//============= MRN & PORT =================
		//==========================================
		
		//===== prkim =====
		//String myMRN = "urn:mrn:smart-navi:device:msr1-20171117";
		//int port = 8982;
		
		//===== kilee =====
		//String myMRN = "urn:mrn:smart-navi:device:msr2-20171117";
		//int port = 8992;
		
		//===== workstation1(default) =====
		//String myMRN = "urn:mrn:smart-navi:device:msr3-20171117";
		//int port = 8977;

		//===== workstation2 =====
		String myMRN = "urn:mrn:smart-navi:device:dummy-msr1";
		int port = 8958;

		//===== server =====
		//String myMRN = "urn:mrn:smart-navi:device:msr5-20171117";
		//int port = 8952;

		//==========================================
		//============= MMS Server =================
		//==========================================

		//===== Local =====
		//MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		//===== Kaist(default) =====
		MMSConfiguration.MMS_URL="mms-kaist.com:8088";
		
		System.out.println("==========================================");
		System.out.println("====== Service Registry(Dummy SR) ========");
		System.out.println("==========================================");
		
		//==========================================
		//============= Server =====================
		//==========================================

		MMSClientHandler server = new MMSClientHandler(myMRN);
		server.setServerPort(port, new MMSClientHandler.RequestCallback() {
			//Request Callback from the request message
			//it is called when client receives a message
			String add_header = "";
			@Override
			public String respondToClient(Map<String,List<String>>  headerField, String message) {
				try {
					String[] messageList = message.split("#:");
                    if (messageList.length < 1)
                        return "ERROR";
                    
					System.out.println("Request : " + message);
					
					List<String> valueList = headerField.get("Srcmrn");
					String srcMRN = valueList.get(0);				
					add_header = messageList[0];
					
					MessageController messageController = new MessageController();
                    return messageController.PrintMessage(messageList, srcMRN);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				/*
				Iterator<String> iter = headerField.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					System.out.println(key+":"+headerField.get(key).toString());
				}
				System.out.println(message);
				*/
				return "OK";
			}

			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}

			@Override
			public Map<String, List<String>> setResponseHeader() {
				// TODO Auto-generated method stub	
				if(add_header != "")
				{
					Map<String,List<String>> headerField = new HashMap<String,List<String>>();
					List<String> serviceType = new ArrayList<String>();
					serviceType.add(add_header);
					headerField.put("Servicetype", serviceType);
					add_header = "";
					
					return headerField;	
				}
				else
				{
					return null;
				}
			}
		});
	}
}
