package edu.northeastern.cs5500.starterbot;

import dagger.Component;
import edu.northeastern.cs5500.starterbot.auctionExpirationHandler.AuctionExpirationHandlerModule;
import edu.northeastern.cs5500.starterbot.command.CommandModule;
import edu.northeastern.cs5500.starterbot.listener.AuctionExpirationEventListener;
import edu.northeastern.cs5500.starterbot.listener.MessageListener;
import edu.northeastern.cs5500.starterbot.repository.RepositoryModule;
import edu.northeastern.cs5500.starterbot.service.ServiceModule;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

@Component(
        modules = {
            AuctionExpirationHandlerModule.class,
            CommandModule.class,
            RepositoryModule.class,
            ServiceModule.class
        })
@Singleton
interface BotComponent {
    public Bot bot();
}

public class Bot {

    @Inject
    Bot() {}

    @Inject MessageListener messageListener;
    @Inject AuctionExpirationEventListener auctionExpirationEventListener;
    @Inject JDA jda;

    void start() {
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(messageListener.allCommandData());
        commands.queue();
    }
}
