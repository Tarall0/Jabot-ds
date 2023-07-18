package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;


public class InteractionsEventL extends ListenerAdapter{



    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        System.out.println(event.getName());
        String command = event.getName();
        switch (command) {
            case "info" -> {
                EmbedBuilder embed = new EmbedBuilder()
                        .setFooter("Developed with much love by Tarallo")
                        .setTitle(":heart: About Jabot")
                        .setDescription("Jabot is a nice project. I'll tell you more. This bot is developed in Java using Java Discord API")
                        .setAuthor("Jabot Website", "https://tarallo.dev/jabot/")
                        .setColor(0X00B0FF);
                event.replyEmbeds(embed.build()).queue();
            }
            case "roll-dice" -> {
                    int sides = event.getOption("number", OptionMapping::getAsInt);
                    int diceRoll = rollDice(sides);
                    if(sides <=1 ){
                        event.reply("I still don't know a dice with 1 face").queue();
                    } else{
                        event.reply(":game_die: You rolled a " + diceRoll).queue();
                    }
            }
        }


    }

    public int rollDice(int sides) {
        // Simulate rolling a 6-sided dice
        return (int) (Math.random() * sides) + 1;
    }
}
