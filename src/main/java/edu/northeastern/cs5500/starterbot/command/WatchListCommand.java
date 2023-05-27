package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Singleton
public class WatchListCommand extends AbstractItemListCommand {

    @Inject
    public WatchListCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "watch_list";
    }

    @Override
    @Nonnull
    public String getListTitle() {
        return "My Watch List:\n\n";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "🔍 All items in your Watch List");
    }

    @Override
    public List<AuctionItem> getItemList(String userId) {
        return userController.getWatchListByUserId(userId);
    }
}
