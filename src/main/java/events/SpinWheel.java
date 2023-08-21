package events;

import db.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpinWheel extends ListenerAdapter {
    private final DatabaseManager databaseManager;
    private final Map<String, Integer> wheelRewards;
    private final Map<String, Integer> dailySpinCounts;

    public SpinWheel() {

        // Initialize the DatabaseManager
        Dotenv config = Dotenv.configure().directory("./").filename(".env").load();
        String dbHost = config.get("HOST");
        String dbName = config.get("DB");
        String dbUsername = config.get("USER");
        String dbPassword = config.get("PSW");
        databaseManager = new DatabaseManager(dbHost, dbName, dbUsername, dbPassword);
        databaseManager.connect(); // Connect to the database
        dailySpinCounts = new ConcurrentHashMap<>(); //initialize the daily spins coutner
        initializeDailySpins();

        // Define the rewards and their corresponding XP values
        wheelRewards = new HashMap<>();
        wheelRewards.put("You won a cute puppy! \uD83D\uDC15", 100); // 100 XP for winning a puppy
        wheelRewards.put("Congratulations, you get a free coffee! ☕", 50); // 50 XP for a free coffee
        wheelRewards.put("You landed on a bag of gold coins! \uD83D\uDCB0", 200); // 200 XP for gold coins
        wheelRewards.put("Oh no, better luck next time!", 10); // 10 XP for no win
        wheelRewards.put("Whoops! Nothing this time.", 10); // 10 XP for no win
        wheelRewards.put("Nope, that's a flop", 10); // 10 XP for no win
        wheelRewards.put("You got a candy, its fruity \uD83C\uDF6C", 20); // 20 XP for a candy
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if (event.getAuthor().isBot()) return;

        String userId = event.getMember().getId();
        String messageContent = event.getMessage().getContentRaw();

        if (messageContent.equalsIgnoreCase("!spin")) {
            if (canSpin(userId)) {
                spinWheel(event.getChannel().asTextChannel(), userId, event.getMember());
            } else {
                event.getChannel().sendMessage("Sorry, you've reached the daily spin limit.").queue();
            }
        }
    }

    private void initializeDailySpins() {
        // Query the database for all users and initialize their daily spins to zero
        // This should be done at the start of each day or bot startup
        try {
            databaseManager.initializeDailySpins();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean canSpin(String userId) {

        int spinCount = databaseManager.getSpinCount(userId);
        // Limit each user to 3 spins per day
        int spinsPerDay = 3;
        if (spinCount < spinsPerDay) {
            // Update the database with the new daily spin count
            try {
                databaseManager.updateDailySpinCount(userId, spinCount + 1);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return true;
        }

        // User has reached the daily spin limit
        return false;
    }

    private void spinWheel(TextChannel channel, String userId, Member member) {
        Random random = new Random();
        List<String> rewardsList = new ArrayList<>(wheelRewards.keySet());
        String selectedReward = rewardsList.get(random.nextInt(rewardsList.size()));

        // Calculate the XP to be awarded based on the selected reward
        int xpWin = wheelRewards.get(selectedReward);

        // Update the user's XP in the database with the new total XP
        databaseManager.updateUserXP(userId, xpWin);

        // Send the reward as a message in the channel
        channel.sendMessage("The wheel is spinning... 🌀").queue();
        channel.sendMessage("Results: **" + selectedReward + "** ").queue();
        channel.sendMessage(member.getEffectiveName() + " just received " + xpWin + " XP").queue();
    }
}