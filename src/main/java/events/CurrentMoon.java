package events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;


public class CurrentMoon extends ListenerAdapter {

    public static EmbedBuilder getCurrentMoon(SlashCommandInteractionEvent event) throws RuntimeException {
        // Current moon details based on the time
        long date = getCurrentDate(event);
        String apiUrl = "https://api.farmsense.net/v1/moonphases/?d=" + date;
        EmbedBuilder moonEmbed = new EmbedBuilder();

        try {
            URL url = new URI(apiUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code from farmsense Astro API
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                // Create a BufferedReader to read the API response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                // Read the response line by line
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response
                JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();

                // Check if the JSON array is not empty
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                    // Extract specific information from the JSON object
                    String moonPhase = jsonObject.get("Phase").getAsString();
                    double moonAge = jsonObject.get("Age").getAsDouble();
                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                    String formattedAge = decimalFormat.format(moonAge);
                    double illumination = jsonObject.get("Illumination").getAsDouble();
                    String moonName = jsonObject.getAsJsonArray("Moon").get(0).getAsString();
                    moonEmbed.setDescription(moonName).setTitle(moonPhase);
                    event.getChannel().sendMessage("The current **" + moonPhase+"** is "+formattedAge+" days old. It is called *'"+moonName+"'*. The current illumination reach "+ illumination*100+"%").queue();

                    return moonEmbed;

                }
            }
            // Close the connection
            connection.disconnect();


        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return moonEmbed;
    }

    public static long getCurrentDate(SlashCommandInteractionEvent event) {
        // Get the current time
        OffsetDateTime unixTime = event.getTimeCreated();

        // Convert to seconds (Unix date) and returns
        return unixTime.toEpochSecond();
    }
}
