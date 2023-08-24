package events;

import kong.unirest.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RandomMeme {

   public static String fetchRandomMeme(){
       try{
           URL url = new URL("https://meme-api.com/gimme");
           HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           int responseCode = connection.getResponseCode();

           if (responseCode == HttpURLConnection.HTTP_OK) {
               BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               StringBuilder response = new StringBuilder();
               String line;

               while ((line = reader.readLine()) != null) {
                   response.append(line);
               }
               reader.close();

               JSONObject memeJson = new JSONObject(response.toString());
               return memeJson.getString("url");
           }

       } catch (IOException e) {
           throw new RuntimeException(e);
       }

       return null;
   }
}
