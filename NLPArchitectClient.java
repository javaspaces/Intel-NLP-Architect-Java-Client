
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;


public class NLPArchitectClient {
	
	static String nlpArchitectUrl = "http://192.168.1.60:9000/inference";
	static long startTime, stopTime = 0;

	/*
		PROGRAMMER NOTE: This is the what the JSON body looks like in "pretty" JSON format
	
	{
	    "model_name": "intent_extraction",
	    "docs": [
	        {
	            "id": 1,
	            "doc": "What’s the weather in San Diego, California?"
	        }
	    ]
	}
	
	*/	
	
	//
	// ...And this is what the JSON body needs to look like with the quotes (") escaped
	//
	static String jsonBody1 = "{\r\n" + 
			"    \"model_name\": \"intent_extraction\",\r\n" + 
			"    \"docs\": [\r\n" + 
			"        {\r\n" + 
			"            \"id\": 1,\r\n" + 
			"            \"doc\": \"What’s the weather like today in London?\"\r\n" + 
			"        }\r\n" + 
			"    ]\r\n" + 
			"}";

	static String jsonBody2 = "{\r\n" + 
			"    \"model_name\": \"intent_extraction\",\r\n" + 
			"    \"docs\": [\r\n" + 
			"        {\r\n" + 
			"            \"id\": 1,\r\n" + 
			"            \"doc\": \"Can you give me the forecast for Chicago, Illinois?\"\r\n" + 
			"        }\r\n" + 
			"    ]\r\n" + 
			"}";
	
    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpclient = HttpClients.custom().build();

        StringEntity requestEntity1 = new StringEntity(jsonBody1, ContentType.APPLICATION_JSON);
        StringEntity requestEntity2 = new StringEntity(jsonBody2, ContentType.APPLICATION_JSON);
        
        try {

            HttpPost postRequest1 = new HttpPost(nlpArchitectUrl);
            postRequest1.setEntity(requestEntity1);
            postRequest1.setHeader("Content-Type", "application/json");
            postRequest1.setHeader("Response-Format", "application/json");
            
            HttpPost postRequest2 = new HttpPost(nlpArchitectUrl);
            postRequest2.setEntity(requestEntity2);
            postRequest2.setHeader("Content-Type", "application/json");
            postRequest2.setHeader("Response-Format", "application/json");
            
            System.out.println("Invoking the Intel NLP Architect Server: \n" + postRequest1.getRequestLine() + "\n");
            
            // Create a custom response handler
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            startTime = System.currentTimeMillis();
            String responseBody = httpclient.execute(postRequest1, responseHandler);
            stopTime = System.currentTimeMillis();
            
            System.out.println(responseBody);
            
            long processingTime =  (stopTime - startTime);
            System.out.println("Processing Time for NLP Statement #1: " + processingTime + " ms \n");
            
            startTime = System.currentTimeMillis();
             responseBody = httpclient.execute(postRequest2, responseHandler);
            stopTime = System.currentTimeMillis();
            
            System.out.println(responseBody);
            
             processingTime =  (stopTime - startTime);
            System.out.println("Processing Time NLP Statement #2: " + processingTime + " ms \n");
        } finally {
            httpclient.close();
        }
    }
}