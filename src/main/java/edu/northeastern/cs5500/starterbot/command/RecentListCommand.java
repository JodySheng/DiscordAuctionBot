package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Singleton
public class RecentListCommand extends AbstractItemListCommand {

    /** Instantiates a new Recent list command. */
    @Inject
    public RecentListCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "recent_list";
    }

    @Override
    @Nonnull
    public String getListTitle() {
        return "Most Recent Selling Items List:\n\n";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "🔍 Check most recent selling items list: ");
    }

    @Override
    public List<AuctionItem> getItemList(String userId) {
        List<AuctionItem> items = auctionController.getAllItems();
        return items.stream()
                .filter(item -> item.getAuctionStatus() != null)
                .filter(item -> item.getAuctionStatus().equals("IN_PROGRESS"))
                .filter(item -> item.getAuctionStartTime() != null)
                .sorted(Comparator.comparing(AuctionItem::getAuctionStartTime).reversed())
                .collect(Collectors.toList());
    }
}
