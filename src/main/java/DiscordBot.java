import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {

    public static void main(String[] args) {

        System.out.println("Running..");
        final String TOKEN = "MTEzMDgxODk1NTA2NjQ4Njc4Ng.GMwSne.d86IbLIeuuIxl-dSlfo0PcxUkJZ7U2BcyxMoro";
        JDABuilder bot = JDABuilder.createDefault(TOKEN);
        JDA jda = bot
                .setActivity(Activity.playing("Java Code"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .addEventListeners(new ReadyEventL(), new MessageEventL(), new InteractionsEventL())
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("info-bot", "Get info about this bot").setGuildOnly(true),
                Commands.slash("stats", "Shows the information about current server"),
                Commands.slash("roll-dice", "Perfect set of dices for your D&D sessions")
                        .addOption(OptionType.INTEGER, "number", "Enter the number of the sides"),
                Commands.slash("ban", "Ban a user from the server")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)) // only usable with ban permissions
                        .setGuildOnly(true) // Ban command only works inside a guild
                        .addOption(OptionType.USER, "user", "The user to ban", true) // required option of type user (target to ban)
                        .addOption(OptionType.STRING, "reason", "The ban reason") // optional reason
        ).queue();

    }
}
