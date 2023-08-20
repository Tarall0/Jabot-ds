package db;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Leveling extends ListenerAdapter {

    private final Dotenv config = Dotenv.configure().directory("./").filename(".env").load();
    private final String dbHost = config.get("HOST");
    private final String dbName = config.get("DB");
    private final String dbUsername = config.get("USER");
    private final String dbPassword = config.get("PSW");

    private final DatabaseManager databaseManager;

    public Leveling() {
        databaseManager = new DatabaseManager(dbHost, dbName, dbUsername, dbPassword);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);

        User author = event.getAuthor();
        long userId = event.getMember().getIdLong();

       if(!event.getAuthor().isBot()){
           try {
               PreparedStatement statement = databaseManager.getConnection().prepareStatement("SELECT * FROM users WHERE user_id = ?");
               statement.setLong(1, userId);
               ResultSet resultSet = statement.executeQuery();

               if (!resultSet.next()) {
                   // if the user doesn't exist, insert a new record
                   statement = databaseManager.getConnection().prepareStatement("INSERT INTO users (user_id) VALUES (?)");
                   statement.setLong(1, userId);
                   statement.executeUpdate();
               }

               int xpGained = 10;

               statement = databaseManager.getConnection().prepareStatement("UPDATE users SET experience = experience + ? WHERE user_id = ? ");
               statement.setInt(1, xpGained);
               statement.setLong(2, userId);
               statement.executeUpdate();

               // Check for level up
               statement = databaseManager.getConnection().prepareStatement("SELECT experience, level FROM users WHERE user_id = ?");
               statement.setLong(1, userId);
               resultSet = statement.executeQuery();

               if(resultSet.next()){
                   int xp = resultSet.getInt("experience");
                   int currentLevel = resultSet.getInt("level");
                   int xpRequiredForUp = calculateXpRequiredForLevel(currentLevel + 1);

                   if (xp >= xpRequiredForUp) {
                       // Level up
                       statement = databaseManager.getConnection().prepareStatement("UPDATE users SET level = level + 1 WHERE user_id = ?");
                       statement.setLong(1, userId);
                       statement.executeUpdate();

                       event.getChannel().sendMessage(author.getAsMention() + " has leveled up to level " + (currentLevel + 1) + "!").queue();
                   }
               }

           } catch(SQLException e){
               e.printStackTrace();
           }
       }

    }

    private int calculateXpRequiredForLevel(int level) {
        // XP calculation
        return 1000 * level;
    }
}
