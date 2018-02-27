import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
			boolean add_header = false;
			@Override
			public String respondToClient(Map<String,List<String>>  headerField, String message) {
				try {
					System.out.println("Request : " + message);
					
					switch(message){
						case "LookupService":
							try {
								String url = "https://sr.maritimecloud.net/api/serviceInstance?includeDoc=false";
								
								URL obj = new URL(url);
								HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
								conn.setRequestProperty("Content-Type", "application/json");
					            conn.setDoOutput(true);
					            conn.setRequestMethod("GET");
					 
					            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
					            
					            String data = "";
					            String inputLine;
					 
					            while ((inputLine = in.readLine()) != null) {
					            	data += inputLine;
					            }
					            in.close();
					            
					            int i = 0;
					            JSONParser Jpar = new JSONParser();
					            JSONArray Jdata = (JSONArray) Jpar.parse(data);
					            JSONArray Jarr = new JSONArray();
								Iterator<JSONObject> iterator = Jdata.iterator();
								
								System.out.println();
								System.out.println("===== JsonList(data) =====");
								
								while(iterator.hasNext()){
									JSONObject Jobj1 = iterator.next();
									JSONObject Jobj2 = new JSONObject();
									
									++i;
									System.out.println();
									System.out.println("===== " + i + " =====");
									//System.out.println(i + "======" + Jobj1.toString());
									
									Iterator<String> iter = Jobj1.keySet().iterator();
									while (iter.hasNext()){
										String key = iter.next();
										Object val = Jobj1.get(key);
										
										if(!key.equals("instanceAsXml")){
											Jobj2.put(key, val);
											if(val == null)
												System.out.println(key+":"+ "null");
											else
												System.out.println(key+":"+Jobj1.get(key).toString());
										}
									}
									
									System.out.println("===== " + i + " =====");
									System.out.println(Jobj2.toString());
									System.out.println();
									
									Jarr.add(Jobj2);
								}

					            List<String> valueList = headerField.get("Srcmrn");
								String srcMRN = valueList.get(0);
								
					            System.out.println();
						        System.out.println("============== LookupService ==============");
						        //System.out.println("Original data: " + data);
						        //System.out.println("Jdata-toString: " + Jdata.toString());
						        //System.out.println("Jdata-toJSONString: " + Jdata.toJSONString());
						        System.out.println("Converted data: " + Jarr.toJSONString());
						        System.out.println("srcMRN: " + srcMRN);
						        System.out.println("===========================================");
						        System.out.println();
						        
						        add_header = true;
						        
						        return Jarr.toJSONString();
						        //return Jdata.toJSONString();
					            //sender.sendPostMsg(srcMRN, "Test");
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
					}
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
				if(add_header)
				{
					add_header = false;
					Map<String,List<String>> headerField = new HashMap<String,List<String>>();
					List<String> serviceType = new ArrayList<String>();
					serviceType.add("LookupService");
					headerField.put("Servicetype", serviceType);
					
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
