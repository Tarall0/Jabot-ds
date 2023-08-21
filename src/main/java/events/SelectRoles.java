package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelectRoles extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if(!event.getAuthor().equals(event.getJDA().getSelfUser())){
            if(event.getMessage().getContentRaw().contains("!rolesx")){
                sendEmbedWithButton(event);
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        Guild guild = event.getGuild();

        if(event.getComponentId().equals("btn1")){
            String roleId = "1095851964719444039";
            assert guild != null;
            Role userRole = guild.getRoleById(roleId);
            if (userRole != null) {
                guild.addRoleToMember(Objects.requireNonNull(event.getUser()), userRole).queue();
                String message = "Role ' "+userRole.getName()+" ' added, check it out";
                event.reply(message).queue(reply ->{
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(() -> {
                        reply.deleteOriginal().queue();
                        executorService.shutdown();
                    },3, TimeUnit.SECONDS);
                });
            }
        } else if (event.getComponentId().equals("btn2")) {
            String roleId = "1095851301323161670";
            assert guild != null;
            Role userRole = guild.getRoleById(roleId);
            if (userRole != null) {
                guild.addRoleToMember(Objects.requireNonNull(event.getUser()), userRole).queue();
                String message = "Role ' "+userRole.getName()+" ' added, check it out";
                event.reply(message).queue(reply ->{
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(() -> {
                        reply.deleteOriginal().queue();
                        executorService.shutdown();
                    },3, TimeUnit.SECONDS);
                });

            }
        } else if (event.getComponentId().equals("btn3")) {
            String roleId = "1132581219138285638";
            assert guild != null;
            Role userRole = guild.getRoleById(roleId);
            if (userRole != null) {
                guild.addRoleToMember(Objects.requireNonNull(event.getUser()), userRole).queue();
                String message = "Role ' "+userRole.getName()+" ' added, check it out";
                event.reply(message).queue(reply ->{
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(() -> {
                        reply.deleteOriginal().queue();
                        executorService.shutdown();
                    },3, TimeUnit.SECONDS);
                });

            }

        } else if (event.getComponentId().equals("btn4")) {
            String roleId = "1132581683460321390";
            assert guild != null;
            Role userRole = guild.getRoleById(roleId);
            if (userRole != null) {
                guild.addRoleToMember(Objects.requireNonNull(event.getUser()), userRole).queue();
                String message = "Role ' " + userRole.getName() + " ' added, check it out";
                event.reply(message).queue(reply -> {
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(() -> {
                        reply.deleteOriginal().queue();
                        executorService.shutdown();
                    }, 3, TimeUnit.SECONDS);
                });
            }
        }
    }

    public void sendEmbedWithButton(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setFooter("You can add as much as you wish");
        embedBuilder.setTitle("Choose your roles ");
        embedBuilder.setDescription("Feel free to select the role you like most.\n\n");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).addActionRow(Button.secondary("btn1", "\uD83D\uDCBB"), Button.secondary("btn2", "\uD83C\uDF41"), Button.secondary("btn3", "\uD83C\uDF43"), Button.secondary("btn4", "\uD83C\uDFA8")).queue();


    }

}
