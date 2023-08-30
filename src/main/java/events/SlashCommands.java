package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static events.CurrentMoon.getCurrentMoon;


public class SlashCommands extends ListenerAdapter{

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
                        .setDescription("Jabot is a free project. As a Virtual Community Manager, Jabot helps with server management using great commands and Gandalf's magic. This bot is developed in Java using Java Discord API"+
                                "\n\nJabot has implemented a system of levels of users interacting in the Discord server."+
                                "\n\n\n**Slash Commands**"+
                                "\n\n**/moonphase**: Get the current moon details"+
                                "\n\n**/roll-dice**: Roll a dice of n faces, perfect for your D&D nights"+
                                "\n\n**/stats**: Get server statistics"+
                                "\n\n**/cryptoinfo**: Get crypto currency current info from CoinGecko API"+
                                "\n\n**/ranmeme**: Generate a Randome meme from Meme API"+
                                "\n\n**Other Commands**"+
                                "\n\n**!spin**: Spin a wheel and defies fortune, 3 attempts per day"+
                                "\n\n**!xp**: Shows current level and experience"+
                                "\n\n**!leaderboard**: Returns the top 5 users per level"+
                                "\n\nMore in the future")
                        .setAuthor("Jabot Github repo", "https://github.com/Tarall0/Jabot-ds")
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
            case "cryptoinfo" -> {
                    try {
                        String cryptoName = event.getOption("name", OptionMapping::getAsString);
                        String cryptoInfo = CryptoCommands.getCryptoInfo(cryptoName);
                        String img = CryptoCommands.getCryptoImg(cryptoName);
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(0X9900FF);
                        embed.setThumbnail(img);
                        embed.setDescription(cryptoInfo);
                        event.replyEmbeds(embed.build()).queue();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
            }

            case "moonphase" ->{

                try{
                    EmbedBuilder moon = getCurrentMoon(event);
                    String phase = moon.build().getTitle();
                    switch (Objects.requireNonNull(phase)){
                        case "Full Moon" -> {
                            moon.setTitle("Full Moon \uD83C\uDF15");
                        }

                        case "Waxing Cresent" -> {
                            moon.setTitle("Waxing Cresent Moon \uD83C\uDF12");
                        }

                        case "Waning Cresent" -> {
                            moon.setTitle("Waning Cresent Moon \uD83C\uDF18");

                        }

                        case "New Moon" -> {
                            moon.setTitle("New Moon \uD83C\uDF11");
                        }

                    }

                    event.replyEmbeds(moon.build()).queue();
                }
                catch (Exception e){
                    e.printStackTrace();
                    event.reply("error fetching the moon").queue();
                }

            }
            case "ranmeme" -> {
                try {
                    String memeUrl;
                    memeUrl = RandomMeme.fetchRandomMeme();
                    if (memeUrl != null) {
                        event.reply("Here a meme for you!").setEphemeral(true).queue();
                        TextChannel channel = event.getChannel().asTextChannel();
                        channel.sendMessage(memeUrl).queue();
                    } else {
                        event.getChannel().sendMessage("Failed to fetch a meme.").queue();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            case "stats" -> {
                int count = Objects.requireNonNull(event.getGuild()).getMemberCount();
                int boosts = event.getGuild().getBoostCount();
                String test = event.getGuild().getTimeCreated().toString();
                String date = test.substring(0, 10);
                String stats = ":bust_in_silhouette: Users: " + count + "\n:butterfly: Boosts: " + boosts;
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(0X9900FF);
                embed.setTitle("Server Statistics");
                embed.setDescription(stats);
                embed.setFooter("Created: " +date);
                event.replyEmbeds(embed.build()).queue();
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
                event.deferReply().queue();
                String reason = event.getOption("reason", OptionMapping::getAsString);
                AuditableRestAction<Void> action = Objects.requireNonNull(event.getGuild()).ban(Objects.requireNonNull(target), 3, TimeUnit.MILLISECONDS); // Start building our ban request
                if (reason == null) action = action.reason("Not specified"); // set the reason for default reason
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
                // Send message to the current channel
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
