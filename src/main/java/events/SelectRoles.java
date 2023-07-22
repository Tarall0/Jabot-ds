package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Objects;

public class SelectRoles extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if(!event.getAuthor().equals(event.getJDA().getSelfUser())){
            if(event.getMessage().getContentRaw().contains("!roles")){
                sendEmbedWithButton(event);
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        Guild guild = event.getGuild();

        if(event.getComponentId().equals("btn1")){
            String roleId = "1095851964719444039";
            assert guild != null;
            Role userRole = guild.getRoleById(roleId);
            if (userRole != null) {
                guild.addRoleToMember(Objects.requireNonNull(event.getUser()), userRole).queue();
                event.reply("Role ' "+userRole.getName()+" ' added, check it out").queue();
            }
        } else if (event.getComponentId().equals("btn2")) {
            String roleId = "1095851301323161670";
            assert guild != null;
            Role userRole = guild.getRoleById(roleId);
            if (userRole != null) {
                guild.addRoleToMember(Objects.requireNonNull(event.getUser()), userRole).queue();
                event.reply("Role ' "+userRole.getName()+" ' added, check it out").queue();
            }
        }
    }


    public void sendEmbedWithButton(MessageReceivedEvent event) {
        String username = event.getAuthor().getGlobalName();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setFooter("You can add as much as you wish");
        embedBuilder.setTitle("Choose your roles ");
        embedBuilder.setDescription("Feel free to select the role you like most.\n\n");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).addActionRow(Button.secondary("btn1", "\uD83D\uDCBB"), Button.secondary("btn2", "\uD83C\uDF41")).queue();


    }

}
