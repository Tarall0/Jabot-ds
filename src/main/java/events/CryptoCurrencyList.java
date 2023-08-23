package events;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CryptoCurrencyList extends ListenerAdapter {

    String currencyName = "Bitcoin"; // The cryptocurrency name you want to retrieve

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);

        if (!event.getAuthor().isBot()){
            if(event.getMessage().getContentRaw().equalsIgnoreCase("!cryptoinfo")){

                String currencies = getCryptoCurrencies().toString();

                String bitcoinInfo = getCryptoInfo("bitcoin");
                event.getChannel().asTextChannel().sendMessage("Bitcoin Info:\n" + bitcoinInfo).queue();

                //StringBuilder currenciesList = new StringBuilder("**Crypto Currencies**");

                //event.getChannel().asTextChannel().sendMessage(currencies).queue();
            }
        }
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



            return "** \uD83E\uDE99 " + name + " current nfo**\n" +
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

    public static List<String> getCryptoCurrencies(){
        List<String> cryptoCurrencies = new ArrayList<>();
        // CoinGecko API endpoint for listing cryptocurrencies
        try{
            String currencyListUrl = "https://api.coingecko.com/api/v3/coins/list?include_platform=true";
            HttpResponse<JsonNode> response = Unirest.get(currencyListUrl).asJson();
            JsonNode responseBody = response.getBody();
            // Parse the JSON response to extract cryptocurrency names
            // The CoinGecko API returns an array of objects with a "name" field.
            for (int i = 0; i < 10; i++) {
                String currencyName = responseBody.getArray().getJSONObject(i).getString("name");
                cryptoCurrencies.add(currencyName);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return cryptoCurrencies;
    }

}

