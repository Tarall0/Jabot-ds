package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class Commands extends ListenerAdapter{


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        System.out.println(event.getName());
        String command = event.getName();
        switch (command) {
            case "info-bot" -> {
                EmbedBuilder embed = new EmbedBuilder()
                        .setFooter("Developed with much love by Tarallo")
                        .setTitle(":heart: About Jabot")
                        .setDescription("Jabot is a nice project. I'll tell you more. This bot is developed in Java using Java Discord API")
                        .setAuthor("Jabot Website", "https://tarallo.dev/jabot/")
                        .setColor(0X9900FF);
                event.replyEmbeds(embed.build()).queue();
            }
            case "roll-dice" -> {
                    try{
                        int sides = Objects.requireNonNull(event.getOption("number", OptionMapping::getAsInt));
                        int diceRoll = rollDice(sides);
                        if(sides <=1 ){
                            event.reply("I still don't know a dice with 1 face").queue();
                        } else{
                            event.reply("**D"+sides+"** :game_die: Nice roll! Result: " + diceRoll).queue();
                        }
                    } catch (NullPointerException e){
                        int diceRoll = rollDice(6);
                        event.reply(":game_die: Nice roll! Result: " + diceRoll).queue();
                        e.printStackTrace();
                    }
            }
            case "stats" -> {
                int count = Objects.requireNonNull(event.getGuild()).getMemberCount();
                int boosts = event.getGuild().getBoostCount();
                String stats = ":bust_in_silhouette: Users: " + count + "\n\n:butterfly: Boosts: " + boosts;
                event.reply(stats).queue();
            }

            case "ban" ->{
                if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.BAN_MEMBERS)) {
                    event.reply("You cannot ban members! Nice try ;)").setEphemeral(true).queue();
                    break;
                }
                User target = event.getOption("user", OptionMapping::getAsUser);
                // optionally check for member information
                Member member = event.getOption("user", OptionMapping::getAsMember);
                if (!event.getMember().canInteract(Objects.requireNonNull(member))) {
                    event.reply("You cannot ban this user.").setEphemeral(true).queue();
                    break;
                }
                // Before starting our ban request, tell the user we received the command
                // This sends a "Bot is thinking..." message which is later edited once we finished
                event.deferReply().queue();
                String reason = event.getOption("reason", OptionMapping::getAsString);
                AuditableRestAction<Void> action = Objects.requireNonNull(event.getGuild()).ban(Objects.requireNonNull(target), 3, TimeUnit.MILLISECONDS); // Start building our ban request
                if (reason != null) // reason is optional
                    action = action.reason(reason); // set the reason for the ban in the audit logs and ban log
                action.queue(v -> {
                    // Edit the thinking message with our response on success
                    event.getHook().editOriginal("**" + target.getAsMention() + "** was banned by **" + event.getUser().getAsMention() + "**!").queue();
                }, error -> {
                    // Tell the user we encountered some error
                    event.getHook().editOriginal("Some error occurred, try again!").queue();
                    error.printStackTrace();
                });
            }

            case "adm-news" -> {
                String msg = event.getOption("message", OptionMapping::getAsString);
                EmbedBuilder admnews = new EmbedBuilder()
                        .setAuthor(Objects.requireNonNull(event.getMember()).getEffectiveName())
                        .setTitle("Admin message")
                        .setDescription(msg)
                        .setColor(0X9900FF);
                event.replyEmbeds(admnews.build()).queue();
            }

            case "del" -> {
                int n = Objects.requireNonNull(event.getOption("number", OptionMapping::getAsInt));
                TextChannel textChannel = event.getChannel().asTextChannel();
                // retrieve the last n messages from the channel
                List<Message> messagesToDelete = textChannel.getHistory().retrievePast(n).complete();
                // Delete the messages in bulk
                textChannel.deleteMessages(messagesToDelete).queue();
                // send message to the current channel
                event.reply("Deleted " + n + " messages from this channel.").queue();
            }

            default -> throw new IllegalStateException("Unexpected value: " + command);
        }


    }

    public int rollDice(int sides) {
        // Simulate rolling a n sided dice
        return (int) (Math.random() * sides) + 1;
    }
}
