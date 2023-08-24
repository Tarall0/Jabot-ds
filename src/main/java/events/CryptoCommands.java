package events;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoCommands extends ListenerAdapter {

    String currencyName = "Bitcoin"; // The cryptocurrency name you want to retrieve

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);


        if (!event.getAuthor().isBot()) {
            if (event.getMessage().getContentRaw().equalsIgnoreCase("!cryptolist")) {

                Map<String, String> commonCryptos = getCryptoInfoList();

                if (!commonCryptos.isEmpty()) {
                    StringBuilder messageBuilder = new StringBuilder("Crypto Currency Information:\n");
                    for (Map.Entry<String, String> entry : commonCryptos.entrySet()) {
                        messageBuilder.append(entry.getKey()).append(": $").append(entry.getValue()).append("\n");
                    }
                    event.getChannel().sendMessage(messageBuilder.toString()).queue();
                } else {
                    event.getChannel().sendMessage("No cryptocurrency information found.").queue();
                }
            }


        }

    }

        public static Map<String, String> stampHashMap(Map<String, String> inputMap, String stamp) {
            Map<String, String> stampedMap = new HashMap<>();

            for (Map.Entry<String, String> entry : inputMap.entrySet()) {
                String stampedValue = stamp + entry.getValue();
                stampedMap.put(entry.getKey(), stampedValue);
            }

            return stampedMap;
        }



    public static String getCryptoInfo(String cryptoName){
        try{
            // CoinGecko API endpoint for retrieving cryptocurrency information
            String apiUrl = "https://api.coingecko.com/api/v3/coins/" + cryptoName;
            HttpResponse<JsonNode> response = Unirest.get(apiUrl).asJson();
            JsonNode responseBody = response.getBody();

            // Extract specific information about Bitcoin (or any other cryptocurrency)
            String name = responseBody.getObject().getString("name");
            String symbol = responseBody.getObject().getString("symbol");
            String price = responseBody.getObject().getJSONObject("market_data").getJSONObject("current_price").getString("usd");
            String priceChange = responseBody.getObject().getJSONObject("market_data").getString("price_change_24h");
            String priceChangePerc = responseBody.getObject().getJSONObject("market_data").getString("price_change_percentage_24h");
            String lastChange = responseBody.getObject().getJSONObject("market_data").getString("last_updated");



            return "** \uD83E\uDE99 " + name + " currency current details**\n" +
                    "\n**Name**: " + name + "\n" +
                    "**Symbol**: " + symbol + "\n" +
                    "**Price (USD)**: " + price + "\n"+
                    "**Last 24h**: " + priceChange + "("+priceChangePerc+"%)"+"\n"+
                    "**Updated**: " + lastChange + "\n";

        } catch (Exception e){
            e.printStackTrace();
        }

        return "Failed to retrieve cryptocurrency information";
    }


    public static HashMap<String, String> getCommonCryptos() {
        HashMap<String, String> commonCryptos = new HashMap<>();

        try {
            // CoinGecko API endpoint for listing cryptocurrencies
            String apiUrl = "https://api.coingecko.com/api/v3/coins/markets";

            // Define the parameters for the request
            String vsCurrency = "usd"; // You can change this to a different currency if needed
            String order = "market_cap_desc";
            int perPage = 10; // Number of cryptocurrencies to fetch

            HttpResponse<JsonNode> response = Unirest.get(apiUrl)
                    .queryString("vs_currency", vsCurrency)
                    .queryString("order", order)
                    .queryString("per_page", perPage)
                    .asJson();

            JsonNode responseBody = response.getBody();

            // Iterate through the JSON array to extract cryptocurrency names
            for (int i = 0; i < responseBody.getArray().length(); i++) {
                String cryptoName = responseBody.getArray().getJSONObject(i).getString("name");
                String cryptoValue = responseBody.getArray().getJSONObject(i).getJSONObject("market_data").getJSONObject("current_price").getString("usd");
                commonCryptos.put(cryptoName, cryptoValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return commonCryptos;
    }

    public static Map<String, String> getCryptoInfoList(){
        Map<String, String> cryptoInfoList = new HashMap<>();

        try{
            // CoinGecko API endpoint for listing cryptocurrencies
            String apiUrl = "https://api.coingecko.com/api/v3/coins/";
            // Define the parameters for the request
            String vsCurrency = "usd"; // You can change this to a different currency if needed
            String order = "market_cap_desc";
            int perPage = 10; // Number of cryptocurrencies to fetchg

            HttpResponse<JsonNode> response = Unirest.get(apiUrl)
                    .queryString("vs_currency", vsCurrency)
                    .queryString("order", order)
                    .queryString("per_page", perPage)
                    .asJson();

            JsonNode responseBody = response.getBody();

            // Iterate through the JSON array to extract cryptocurrency names
            for (int i = 0; i < responseBody.getArray().length(); i++) {
                String cryptoName = responseBody.getArray().getJSONObject(i).getString("name");
                String cryptoValue = responseBody.getArray().getJSONObject(i).getJSONObject("market_data").getJSONObject("current_price").getString("usd");
                cryptoInfoList.put(cryptoName, cryptoValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cryptoInfoList;
    }

}

