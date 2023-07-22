package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MessageEventL extends ListenerAdapter {

    private final Set<String> bannedWords = new HashSet<>(Arrays.asList("nigga", "whore", "faggot", "https://", "www.")); // Add your banned words here

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        var emojis = new String[]{"U+1F601", "U+1F604", "U+1F643", "U+1F60A", "U+1F607", "U+1F970", "U+1F929", "U+263A"};
        Random ran = new Random();
        int j = ran.nextInt(emojis.length);
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
                    // Moderators have no word filtering, but you can add other actions here if needed
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
                System.out.println(e);
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

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        super.onGuildMemberJoin(event);
        String[] str = new String[]{"It’s a privilege to have you around!", "It is an honor to have such a fellow like you join us!", "We have waited so long to have you among us. At last, the time has come.", "arrived at the party just in time :partying_face:", "just brought some cake... didn't them?"};
        String[] rls = {"\n I am just going to send our community rules.", "\n Sending all our rules for you", "\n We just have some simple rules here"};
        Random ran = new Random();
        int i = ran.nextInt(str.length);
        int r = ran.nextInt(rls.length);
        String welcome = event.getUser().getAsMention()+" "+str[i];

        try {
            Objects.requireNonNull(event.getGuild().getDefaultChannel()).asTextChannel().sendMessage(welcome+rls[r]).queue();
        }catch (NullPointerException e){
            System.out.println(e);
        }

        try {
            Thread.sleep(3000); // 3000 milliseconds (3 seconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sending an embed message
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(0X9900FF); // Set the color of the embed
        embed.setTitle("Welcome to the Server!");
        String head = "Welcome to our Discord community! We are delighted to have you here and look forward to creating an inclusive and vibrant space for all members. To ensure that everyone can enjoy their time here and engage in meaningful discussions, we have established some basic community rules that we kindly ask you to follow. These rules are designed to maintain a positive, respectful, and safe environment for all members.\n \n";
        String rules = "1- **Be Respectful**: Treat all members with respect, regardless of their background, beliefs, or opinions. Harassment, hate speech, and offensive language are not tolerated. \n \n" +
                "2- **No Spamming**: Avoid excessive posting of messages, images, or links that disrupt the flow of conversation or flood the channels.\n \n" +
                "3- **No Advertising or Self-Promotion**: Avoid advertising or promoting personal projects, products, or services unless explicitly permitted by the community guidelines.\n \n" +
                "4- **No NSFW Content**: Do not share explicit, sexually explicit, or NSFW (Not Safe For Work) content. \n \n" +
                "5- **Respect Privacy**: Do not share personal information about yourself or others without consent. This includes real names, addresses, phone numbers, etc. \n \n" +
                "6- **Reporting Issues**: Report any violations of the rules or concerning behavior to the moderators or community staff privately.\n \n" +
                "7- **Have Fun and Be Positive**: Foster a welcoming and enjoyable atmosphere for everyone. Encourage positive interactions and help newcomers feel comfortable.";

        embed.setDescription(head+rules);

        Objects.requireNonNull(event.getGuild().getDefaultChannel()).asTextChannel().sendMessageEmbeds(embed.build())
                .queue();


        System.out.println(event.getUser().getName()+"joined");
    }
}
