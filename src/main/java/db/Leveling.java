package db;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Leveling extends ListenerAdapter {

    private final Dotenv config = Dotenv.configure().directory("./").filename(".env").load();
    private final String dbHost = config.get("HOST");
    private final String dbName = config.get("DB");
    private final String dbUsername = config.get("USER");
    private final String dbPassword = config.get("PSW");

    private final DatabaseManager databaseManager;

    private final Map<Integer, String> levelRoles = new HashMap<>();

    public Leveling() {
        databaseManager = new DatabaseManager(dbHost, dbName, dbUsername, dbPassword);

        levelRoles.put(5, "1142772431027699754");
        levelRoles.put(10, "1142774781586972684");
        levelRoles.put(15, "1142774872674676838");
        levelRoles.put(20, "1142774984217993266");
        // Level X Roles
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);

        User author = event.getAuthor();
        long userId = Long.parseLong(event.getAuthor().getId());

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
                   int newLevel = currentLevel + 1;

                   if (xp >= xpRequiredForUp) {
                       // Level up
                       statement = databaseManager.getConnection().prepareStatement("UPDATE users SET level = level + 1 WHERE user_id = ?");
                       statement.setLong(1, userId);
                       statement.executeUpdate();

                       event.getChannel().sendMessage(author.getAsMention() + " has leveled up to level " + (currentLevel + 1) + "!").queue();

                       // Check if the user has an old role at the same level and remove it
                       if (levelRoles.containsKey(newLevel - 5)) {
                           String oldRoleId = levelRoles.get(newLevel - 5);
                           Role oldRole = event.getGuild().getRoleById(oldRoleId);
                           if (oldRole != null && event.getMember().getRoles().contains(oldRole)) {
                               event.getGuild().removeRoleFromMember(event.getMember(), oldRole).queue();
                           }
                       }


                       // Check if the new level corresponds to a role in the mapping
                       if (levelRoles.containsKey(newLevel)) {
                           String roleId = levelRoles.get(newLevel);
                           Role role = event.getGuild().getRoleById(roleId);

                           if (role != null) {
                               // Assign the role to the user
                               event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), role).queue();
                           }
                       }

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
