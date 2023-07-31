package events;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.nio.ByteBuffer;
import java.util.StringTokenizer;

public class MusicCommands extends ListenerAdapter implements AudioSendHandler, AudioReceiveHandler {

    private Guild guild;
    private VoiceChannel voiceChannel;
    private TextChannel textChannel;

    public MusicCommands() {
        this.guild = guild;
        this.voiceChannel = voiceChannel;
        this.textChannel = textChannel;
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);

        String message = event.getMessage().getContentRaw();

        if (message.startsWith("!music")){
            StringTokenizer tokenizer = new StringTokenizer(message.substring("!music ".length()));
            String[] args = new String[tokenizer.countTokens()];
            int index = 0;
            while (tokenizer.hasMoreTokens()) {
                args[index] = tokenizer.nextToken();
                index++;
            }
            handleMusicCommand(event, args);
        }
    }

    private void handleMusicCommand(MessageReceivedEvent event, String[] args){
        if(args.length == 0){
            event.getChannel().sendMessage("Usage: !music play <song URL>").queue();
        }

        String command = args[0].toLowerCase();
        switch (command) {
            case "play":
                if (args.length < 2) {
                    event.getChannel().sendMessage("Please provide a song URL.").queue();
                    return;
                }
                String songUrl = args[1];
              //  event.play(event.getGuild(), event.getTextChannel(), songUrl);
                break;
            case "stop":
              //  musicBot.stop(event.getGuild());
                break;
            case "skip":
               // musicBot.skip(event.getGuild());
                break;
            // Add more music-related commands as needed
            default:
                event.getChannel().sendMessage("Unknown command. Available commands: play, stop, skip").queue();
        }
    }

    @Override
    public boolean canProvide() {
        return false;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return null;
    }
}
