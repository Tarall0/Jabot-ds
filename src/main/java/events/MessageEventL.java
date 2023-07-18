package events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEventL extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if (event.getAuthor().equals(event.getJDA().getSelfUser())) {
            // Ignore bot's own messages
            return;
        }
        System.out.println(event.getMessage().getAuthor().getName() + " sent '" + event.getMessage().getContentDisplay() + "'");
    }


}
