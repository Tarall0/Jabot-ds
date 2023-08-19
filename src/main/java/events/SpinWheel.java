package events;

import db.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SpinWheel extends ListenerAdapter {

    private final Dotenv config = Dotenv.configure().directory("./").filename(".env").load();
    private final String dbHost = config.get("HOST");
    private final String dbName = config.get("DB");
    private final String dbUsername = config.get("USER");
    private final String dbPassword = config.get("PSW");

    private final DatabaseManager databaseManager;
    private final Map<String, Integer> wheelRewards;

    public SpinWheel() {
        // Initialize the DatabaseManager with your database configuration
        databaseManager = new DatabaseManager(dbHost, dbName, dbUsername, dbPassword);
        databaseManager.connect(); // Connect to the database

        // Define the rewards and their corresponding XP values
        wheelRewards = new HashMap<>();
        wheelRewards.put("You won a cute puppy!", 100); // 100 XP for winning a puppy
        wheelRewards.put("Congratulations, you get a free coffee!", 50); // 50 XP for a free coffee
        wheelRewards.put("You landed on a bag of gold coins!", 200); // 200 XP for gold coins
        wheelRewards.put("Oh no, better luck next time!", 10); // 10 XP for no win
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if (event.getAuthor().isBot()) return;

        String messageContent = event.getMessage().getContentRaw();
        if (messageContent.equalsIgnoreCase("!spin")) {
            spinWheel(event.getChannel().asTextChannel(), Objects.requireNonNull(event.getMember()).getId());
        }
    }

    private void spinWheel(TextChannel channel, String userId) {
        Random random = new Random();
        List<String> rewardsList = new ArrayList<>(wheelRewards.keySet());
        String selectedReward = rewardsList.get(random.nextInt(rewardsList.size()));

        // Calculate the XP to be awarded based on the selected reward
        int xpWin = wheelRewards.get(selectedReward);

        int currentLevel = databaseManager.getUserLevel(userId);

        databaseManager.updateUserLevel(userId, currentLevel);
        // Update the user's XP in the database with the new total XP
        databaseManager.updateUserXP(userId, xpWin);

        // Send the reward as a message in the channel
        channel.sendMessage("The wheel is spinning... 🌀").queue();
        channel.sendMessage("Results: **" + selectedReward + "** ").queue();
        channel.sendMessage("User with ID " + userId + " received " + xpWin + " XP.").queue();
    }
}
