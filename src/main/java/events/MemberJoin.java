package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class MemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        super.onGuildMemberJoin(event);
        String[] str = new String[]{"It’s a privilege to have you around!", "It is an honor to have such a fellow like you join us!", "We have waited so long to have you among us. At last, the time has come.", "arrived at the party just in time :partying_face:", "just brought some cake... didn't them?"};
        String[] rls = {" I am just going to PM our community rules. ", " Sending a PM with all our rules for you.", " We just have some simple rules here. Check your PM"};
        Random ran = new Random();
        int i = ran.nextInt(str.length);
        int r = ran.nextInt(rls.length);
        String welcome = event.getUser().getAsMention()+" "+str[i];
        User user = event.getUser();

        String roleName = "Member";
        Guild guild = event.getGuild();
        Role memberRole = guild.getRolesByName(roleName, true).stream().findFirst().orElse(null);

        if (memberRole != null) {
            guild.addRoleToMember(event.getMember(), memberRole).queue();
        }




        try {
            Objects.requireNonNull(event.getGuild().getDefaultChannel()).asTextChannel().sendMessage(welcome+rls[r]).queue();
            String[] gifUrl = {"https://tenor.com/view/tony-bennett-gif-17991077", "https://tenor.com/view/wink-wink-agnes-agatha-harkness-kathryn-hahn-wandavision-gif-22927975", "https://tenor.com/view/omg-wow-really-surprised-feeling-it-gif-15881647", "https://tenor.com/view/bhibatsam-girls-dance-sisters-dance-happy-dance-kids-dance-gif-26527005" };
            int g = ran.nextInt(gifUrl.length);
            event.getGuild().getDefaultChannel().asTextChannel().sendMessage(gifUrl[g]).queue();
        }catch (NullPointerException e){
            System.out.println(e);
        }

        try {
            Thread.sleep(3000); // 3000 milliseconds (3 seconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sending an embed message
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(0X9900FF); // Set the color of the embed
        embed.setTitle("Welcome to the Server!");
        String head = "Welcome to our Discord community! We are delighted to have you here and look forward to creating an inclusive and vibrant space for all members. To ensure that everyone can enjoy their time here and engage in meaningful discussions, we have established some basic community rules that we kindly ask you to follow. These rules are designed to maintain a positive, respectful, and safe environment for all members.\n \n";
        String rules = "1- **Be Respectful**: Treat all members with respect, regardless of their background, beliefs, or opinions. Harassment, hate speech, and offensive language are not tolerated. \n \n" +
                "2- **No Spamming**: Avoid excessive posting of messages, images, or links that disrupt the flow of conversation or flood the channels.\n \n" +
                "3- **No Advertising or Self-Promotion**: Avoid advertising or promoting personal projects, products, or services unless explicitly permitted by the community guidelines.\n \n" +
                "4- **No NSFW Content**: Do not share explicit, sexually explicit, or NSFW (Not Safe For Work) content. \n \n" +
                "5- **Respect Privacy**: Do not share personal information about yourself or others without consent. This includes real names, addresses, phone numbers, etc. \n \n" +
                "6- **Reporting Issues**: Report any violations of the rules or concerning behavior to the moderators or community staff privately.\n \n" +
                "7- **Have Fun and Be Positive**: Foster a welcoming and enjoyable atmosphere for everyone. Encourage positive interactions and help newcomers feel comfortable.";

        embed.setDescription(head+rules);
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessageEmbeds(embed.build()).queue());
        user.openPrivateChannel().queue(privateChannel ->  privateChannel.sendMessage("For any questions or concerns, ask our Staff available :smile:").queue());


        System.out.println(event.getUser().getName()+"joined");
    }
}
