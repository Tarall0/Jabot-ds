package events;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinWheel extends ListenerAdapter {
    private final List<String> wheelRewards;

    public SpinWheel(){
        wheelRewards = new ArrayList<>();
        wheelRewards.add("You won a cute puppy!");
        wheelRewards.add("Congratulations, you get a free coffee!");
        wheelRewards.add("You landed on a bag of gold coins!");
        wheelRewards.add("Oh no, better luck next time!");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if(event.getAuthor().isBot()) return;

        String messageContent = event.getMessage().getContentRaw();
        if(messageContent.equalsIgnoreCase("!spin")){
            spinWheel((TextChannel) event.getChannel());
        }
    }

    private void spinWheel(TextChannel channel){
        Random random = new Random();
        String reward = wheelRewards.get(random.nextInt(wheelRewards.size()));

        // Send the reward as a message in the channel
        channel.sendMessage("The wheel is spinning... 🌀").queue();
        channel.sendMessage("Results: **" + reward + "** ").queue();
    }
}
