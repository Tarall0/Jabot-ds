package events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HourlyMessage extends ListenerAdapter {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);
        scheduler.scheduleAtFixedRate(() -> sendHourlyMessage(event), 0, 1, TimeUnit.HOURS);

    }

    private void sendHourlyMessage(ReadyEvent event) {
        List<Guild> guilds = event.getJDA().getGuilds();
        for (Guild guild : guilds) {
            TextChannel defaultChannel = Objects.requireNonNull(guild.getDefaultChannel()).asTextChannel();
            String[] hrsmessage = {"Come Virtual Community Manager, contribuisco alla gestione del server utilizzando vari comandi e la magia di Gandalf. Digita /info-bot", "Siamo esseri in continuo sviluppo e impariamo cose nuove ogni momento. Io lo faccio!",":eyes:",":eyes:",":eyes:","Sono attualmente un Bot in fase di sviluppo, sono creato in Java!"};
            Random ran = new Random();
            defaultChannel.sendMessage(hrsmessage[ran.nextInt(hrsmessage.length)]).queue();
        }
    }

}
