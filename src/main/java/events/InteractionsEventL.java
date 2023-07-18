package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;



public class InteractionsEventL extends ListenerAdapter{



    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
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
                        int sides = event.getOption("number", OptionMapping::getAsInt);
                        int diceRoll = rollDice(sides);
                        if(sides <=1 ){
                            event.reply("I still don't know a dice with 1 face").queue();
                        } else{
                            event.reply("**D"+sides+"** :game_die: Nice roll! Result: " + diceRoll).queue();
                        }
                    } catch (NullPointerException e){
                        int diceRoll = rollDice(6);
                        event.reply(":game_die: Nice roll! Result: " + diceRoll).queue();
                        System.out.println(e);
                    }
            }
            case "stats " -> {
                int count = event.getGuild().getMemberCount();
                System.out.println(count);
                String stats = "Users: "  + count ;
                event.reply(stats).queue();
            }

            case "adm-news" -> {
                String msg = event.getOption("message", OptionMapping::getAsString);
                EmbedBuilder admnews = new EmbedBuilder()
                        .setAuthor(event.getMember().getEffectiveName())
                        .setTitle("Admin message")
                        .setDescription(msg)
                        .setColor(0X9900FF);
                event.replyEmbeds(admnews.build()).queue();
            }
        }


    }

    public int rollDice(int sides) {
        // Simulate rolling a 6-sided dice
        return (int) (Math.random() * sides) + 1;
    }
}
