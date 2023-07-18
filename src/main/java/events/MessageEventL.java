package events;


import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageEventL extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if (!event.getAuthor().equals(event.getJDA().getSelfUser())){
            if (event.getMessage().getContentRaw().contains("weed")) {
                event.getMessage().addReaction(Emoji.fromUnicode("U+1F601")).queue(); // Add a reaction
                event.getMessage().addReaction(Emoji.fromUnicode("U+1F96C")).queue();
            }
        }
        System.out.println(event.getMessage().getAuthor().getName() + " sent '" + event.getMessage().getContentDisplay() + "'");
    }


    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);

        if (!event.getUser().equals(event.getJDA().getSelfUser())){
            User user = event.getUser();
            String username = user.getName();
            String emoji = event.getReaction().getEmoji().getAsReactionCode();
            String channelName = event.getChannel().getAsMention();

            String message = username + " reacted to a message with " +" ' "+ emoji + " ' "+ "in "+channelName;
            event.getGuild().getDefaultChannel().asTextChannel().sendMessage(message).queue();

        }

    }
}
