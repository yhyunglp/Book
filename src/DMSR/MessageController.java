package DMSR;

import java.net.*;
import java.io.*;
import org.json.simple.parser.*;
import org.json.simple.*;
import java.util.*;

public class MessageController
{
    public String PrintMessage(String[] messageList, String srcMRN)
    {
        String url = "https://sr.maritimecloud.net/api";
        
        switch (messageList[0])
        {
            case "SearchInstanceByLocation":
                if (messageList.length != 3)
                    return "ERROR";
                url += "/_searchLocation/serviceInstance?latitude=" + messageList[1] + "&longitude=" + messageList[2] + "&includeDoc=false";
                break;
            case "LookupService":
                url += "/serviceInstance?includeDoc=false";
                url += "&page=0&size=1000";
                break;
            case "GetInstance":
                if (messageList.length != 3)
                    return "ERROR";
                url += "/serviceInstance/" + messageList[1] + "/" + messageList[2] + "/?includeDoc=false";
                break;
            case "GetAllInstanceById":
                if (messageList.length != 2)
                    return "ERROR";
                url += "/serviceInstance/" + messageList[1] + "?includeDoc=false";
                break;
        }
        
        try 
        {
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
	        
            if (messageList[0].equals("GetInstance"))
            {
                JSONParser Jpar = new JSONParser();
                JSONObject Jobj1 = (JSONObject)Jpar.parse(data);
                JSONObject Jobj2 = new JSONObject();
                
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

                System.out.println(Jobj2.toString());
                System.out.println();
                System.out.println();
                System.out.println("=============== GetInstance ===============");
                System.out.println("Converted data: " + Jobj2.toJSONString());
                System.out.println("srcMRN: " + srcMRN);
                System.out.println("===========================================");
                System.out.println();
                return Jobj2.toJSONString();
            }
            int i = 0;
            final JSONParser Jpar2 = new JSONParser();
            final JSONArray Jdata = (JSONArray)Jpar2.parse(data);
            final JSONArray Jarr = new JSONArray();
            final Iterator<JSONObject> iterator = Jdata.iterator();
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
            
            System.out.println();
            System.out.println("============== " + messageList[0] + " ==============");
            System.out.println("Converted data: " + Jarr.toJSONString());
            System.out.println("srcMRN: " + srcMRN);
            System.out.println("===========================================");
            System.out.println();
            return Jarr.toJSONString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return "OK";
    }
}
