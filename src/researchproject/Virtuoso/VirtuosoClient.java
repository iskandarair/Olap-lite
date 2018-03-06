/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.Virtuoso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import researchproject.models.Triple;
/**
 *
 * @author Iskandar
 */
public class VirtuosoClient {

    private static HttpClient httpClient = HttpClientBuilder.create().build(); 
    
    public static JSONObject sendRequest(String query,String endpoint)
    {
        try 
        {
            HttpPost request = new HttpPost("http://localhost:8890/sparql");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("query", query));
            nameValuePairs.add(new BasicNameValuePair("format", "application/sparql-results+json"));
            nameValuePairs.add(new BasicNameValuePair("default-graph-uri", endpoint));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            ResponseHandler<String> responseHandler=new BasicResponseHandler();
            String responseBody = httpClient.execute(request, responseHandler);
            JSONObject response=new JSONObject(responseBody);
            return response;
        }
        catch(Exception ex)
        {
            System.out.println("Exception occured: Message " + ex.getMessage());
        }
        return null;

    }
    
}
