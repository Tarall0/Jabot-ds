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

    private final Set<String> bannedWords = new HashSet<>(Arrays.asList("nigga", "whore", "faggot", "https://", "www."));
    private Map<String, Integer> messageCountMap = new HashMap<>();
    private static final int MESSAGE_THRESHOLD = 5;
    private static final String ROLE_NAME = "Verified";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        var emojis = new String[]{"U+1F601", "U+1F604", "U+1F643", "U+1F60A", "U+1F607", "U+1F970", "U+1F929", "U+263A"};
        Random ran = new Random();
        int j = ran.nextInt(emojis.length);
        String userId = event.getAuthor().getId();
        int currentMessageCount = messageCountMap.getOrDefault(userId, 0) + 1;
        messageCountMap.put(userId, currentMessageCount);


        if (!event.getAuthor().equals(event.getJDA().getSelfUser())){

            if (currentMessageCount == MESSAGE_THRESHOLD) {
                assignRoleToUser(event, userId);
            }

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

        List<Role> userRoles = Objects.requireNonNull(event.getMember()).getRoles();

        // Check for banned words based on user roles
        if (userRoles.isEmpty()) {
            checkBannedWords(event.getMessage(), "DEFAULT");
        } else {
            for (Role role : userRoles) {
                if(role.getName().equalsIgnoreCase("White Role")){
                    // White role have special permissions
                    return;
                } else if (role.getName().equalsIgnoreCase("Moderator")) {
                    // Moderators have no word filtering
                    return;
                } else if (role.getName().equalsIgnoreCase("Admin")) {
                    // Admins have no word filtering
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
                System.out.println(e);
            }

        }


    }

    private void assignRoleToUser(MessageReceivedEvent event, String userId) {
        String roleName = ROLE_NAME;
        Role role = event.getGuild().getRolesByName(roleName, true).stream().findFirst().orElse(null);

        if (role != null) {
            event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), role).queue();
            System.out.println(userId);


        }
    }

    private void checkBannedWords(Message message, String userGroup) {
        String content = message.getContentRaw().toLowerCase();
        for (String bannedWord : bannedWords) {
            if (content.contains(bannedWord)) {

                switch (userGroup) {
                    case "DEFAULT":
                        message.delete().queue();
                        message.getChannel().sendMessage("Your message contains inappropriate content and has been deleted. Please review our rules. ").queue();
                        break;
                    case "USER":
                        // User handling
                        break;
                }
                break;
            }
        }
    }


}
