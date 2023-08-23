package events;

import db.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GenericMessage extends ListenerAdapter {

    private final DatabaseManager databaseManager;
    private final Set<String> bannedWords = new HashSet<>(Arrays.asList("nigga", "whore", "faggot", "www.")); // Add your banned words here

   public GenericMessage(){
       // Initialize the DatabaseManager
       Dotenv config = Dotenv.configure().directory("./").filename(".env").load();
       String dbHost = config.get("HOST");
       String dbName = config.get("DB");
       String dbUsername = config.get("USER");
       String dbPassword = config.get("PSW");
       databaseManager = new DatabaseManager(dbHost, dbName, dbUsername, dbPassword);
       databaseManager.connect(); // Connect to the database
   }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        var emojis = new String[]{"U+1F601", "U+1F604", "U+1F643", "U+1F60A", "U+1F607", "U+1F970", "U+1F929", "U+263A"};
        Random ran = new Random();
        int j = ran.nextInt(emojis.length);
        // Exit the method if the event objects are null

        if (!event.getAuthor().equals(event.getJDA().getSelfUser())){


            if (event.getMessage().getContentRaw().contains("weed")) {
                event.getMessage().addReaction(Emoji.fromUnicode("U+1F601")).queue(); // Add a reaction
                event.getMessage().addReaction(Emoji.fromUnicode("U+1F96C")).queue();
            }

            if(event.getMessage().getContentRaw().contains("hello")){
                event.getMessage().addReaction(Emoji.fromUnicode(emojis[j])).queue();

            }

            if(event.getMessage().getContentRaw().toLowerCase().contains("jabot")){
                event.getMessage().addReaction(Emoji.fromUnicode("U+2665")).queue();
                event.getMessage().getAuthor().getAsMention();
            }
        }

        // Get the user's roles
        List<Role> userRoles = Objects.requireNonNull(event.getMember()).getRoles();

        // Check for banned words based on user roles
        if (userRoles.isEmpty()) {
            checkBannedWords(event.getMessage(), "DEFAULT", event);
        } else {
            for (Role role : userRoles) {
                if(role.getName().equalsIgnoreCase("white role")){
                    return;
                }
                if(role.getName().equalsIgnoreCase("jabot")){
                    return;
                }
                if (role.getName().equalsIgnoreCase("Moderator")) {
                    return;
                } else if (role.getName().equalsIgnoreCase("Admin")) {
                    // Admins have no word filtering, but you can add other actions here if needed
                    return;
                }
            }
            // If the user has roles but is not an Admin or Moderator, check for banned words
            checkBannedWords(event.getMessage(), "USER", event);
        }
        System.out.println(event.getMessage().getAuthor().getName() + " sent '" + event.getMessage().getContentDisplay() + "'");
    }
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);

        if (!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
            try{
                int xpWin = 5;
                // Update the user's XP in the database with the new total XP
                databaseManager.updateUserXP(event.getUserId(), xpWin);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    private void checkBannedWords(Message message, String userGroup, MessageReceivedEvent event) {
        String content = message.getContentRaw().toLowerCase();
        for (String bannedWord : bannedWords) {
            if (content.contains(bannedWord)) {
                // Depending on the user group
                switch (userGroup) {
                    case "DEFAULT", "USER" -> {
                        String userId = message.getAuthor().getId();
                        databaseManager.updateWarnings(userId);
                        int warnings = databaseManager.getWarnings(userId);
                        message.delete().queue();
                        message.getChannel().sendMessage(message.getAuthor().getAsMention()+" your message contains inappropriate content and has been deleted. Please review our rules. ").queue();
                        // at the third warn the user will be banned
                        if (warnings > 3){
                            AuditableRestAction<Void> action = Objects.requireNonNull(event.getGuild()).ban(Objects.requireNonNull(message.getAuthor()), 3, TimeUnit.MILLISECONDS);
                            action.queue( v -> { message.getChannel().sendMessage(" The user was banned as several warnings were ignored ").queue(); },
                                          error -> { message.getChannel().sendMessage("An error occurred while trying to ban the user").queue(); }
                            );
                        }
                    }
                }
                break; // Exit the loop after finding one banned word
            }
        }
    }


}
