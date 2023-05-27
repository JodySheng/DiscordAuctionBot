package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Singleton
public class SellListCommand extends AbstractItemListCommand {

    /** Instantiates a new Sell list command. */
    @Inject
    public SellListCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "sell_list";
    }

    @Override
    @Nonnull
    public String getListTitle() {
        return "My Auction Item List:\n\n";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "🔍 List all your items for auction");
    }

    @Override
    public List<AuctionItem> getItemList(String userId) {
        return auctionController.getSellList(userId);
    }
}
