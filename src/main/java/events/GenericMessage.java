package events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GenericMessage extends ListenerAdapter {

    private final Set<String> bannedWords = new HashSet<>(Arrays.asList("nigga", "whore", "faggot", "https://", "www.")); // Add your banned words here

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

            if(event.getMessage().getContentRaw().contains("jabot")){
                event.getMessage().addReaction(Emoji.fromUnicode("U+2665")).queue();
                event.getMessage().getAuthor().getAsMention();
            }
        }

        // Get the user's roles
        List<Role> userRoles = Objects.requireNonNull(event.getMember()).getRoles();

        // Check for banned words based on user roles
        if (userRoles.isEmpty()) {
            checkBannedWords(event.getMessage(), "DEFAULT");
        } else {
            for (Role role : userRoles) {
                if(role.getName().equalsIgnoreCase("white role")){
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
            checkBannedWords(event.getMessage(), "USER");
        }
        System.out.println(event.getMessage().getAuthor().getName() + " sent '" + event.getMessage().getContentDisplay() + "'");
    }


    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);


        if (!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
            String username = Objects.requireNonNull(event.getUser()).getGlobalName();
            String emoji = event.getReaction().getEmoji().getAsReactionCode();
            String channelName = event.getChannel().getAsMention();

            String message = username + " reacted to a message with " +" "+ emoji + " "+ " in "+channelName;

            try{
                Objects.requireNonNull(event.getGuild().getDefaultChannel()).asTextChannel().sendMessage(message).queue();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }


    }

    private void checkBannedWords(Message message, String userGroup) {
        String content = message.getContentRaw().toLowerCase();
        for (String bannedWord : bannedWords) {
            if (content.contains(bannedWord)) {
                // Depending on the user group, you can choose the appropriate moderation action here
                switch (userGroup) {
                    case "DEFAULT":
                        // For default users, you can delete the message and warn them, or take other actions
                        message.delete().queue();
                        message.getChannel().sendMessage("Your message contains inappropriate content and has been deleted. Please review our rules. ").queue();
                        break;
                    case "USER":
                        // For users with roles, you can choose a different moderation action here
                        // For example, you may want to notify moderators or log the incident
                        break;
                }
                break; // Exit the loop after finding one banned word
            }
        }
    }


}
