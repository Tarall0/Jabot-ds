import events.*;
import db.*;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {

    public static void main(String[] args) throws InterruptedException {

        Dotenv config = Dotenv.configure().directory("./").filename(".env").load();
        String token = config.get("TOKEN");
        String dbHost = config.get("HOST");
        String dbName = config.get("DB");
        String dbUsername = config.get("USER");
        String dbPassword = config.get("PSW");

        DatabaseManager dbManager = new DatabaseManager(dbHost, dbName, dbUsername, dbPassword);
        dbManager.connect();

        System.out.println("Running..");

        JDABuilder builder = JDABuilder.createDefault(token);


        JDA bot = builder
                .setActivity(Activity.playing("Java Code"))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES,GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .addEventListeners(new GenericMessage(), new SlashCommands(), new MemberJoin(), new SelectRoles(), new SpinWheel(), new HourlyMessage(), new Leveling(), new CryptoCommands(), new CurrentMoon())
                .build()
                .awaitReady();

        dbManager.insertAllMemberInDB(bot);


        // Slash commands

        bot.updateCommands().addCommands(
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("info-bot", "Get info about Jabot").setGuildOnly(true),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("stats", "Shows the information about current server"),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("moonphase", "Get the current Moon details"),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("cryptoinfo", "Get current info of a Crypto Currency")
                        .addOption(OptionType.STRING, "name", "Insert the valid name of the currency"),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("ranmeme", "Generate a random meme"),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("roll-dice", "Perfect set of dices for your D&D sessions")
                        .addOption(OptionType.INTEGER, "number", "Enter the number of the sides"),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("adm-news", "Send an important message to the current channel")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)) // only usable with Admin permissions
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "message", "Insert the message you want to send", true),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("del", "Delete n messages from current channel")
                        .addOption(OptionType.INTEGER, "number", "Enter the number of messages to remove")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)), // only usable with ban permissions
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("ban", "admin command")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))  // only usable with ban permissions
                        .setGuildOnly(true) // Ban command only works inside a guild
                        .addOption(OptionType.USER, "user", "The user to ban", true) // required option of type user (target to ban)
                        .addOption(OptionType.STRING, "reason", "The ban reason") // optional reason
        ).queue();






    }


}
