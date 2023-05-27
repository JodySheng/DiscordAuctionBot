package edu.northeastern.cs5500.starterbot.auctionExpirationHandler;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Slf4j
public class AuctionExpirationHelper {

    /** The Auction controller. */
    @Inject AuctionController auctionController;

    /**
     * Build item embed message embed.
     *
     * @param item the item
     * @return the message embed
     */
    public static MessageEmbed buildItemEmbed(AuctionItem item) {
        AuctionItem updatedItem = AuctionController.getAuctionItem(item.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatStartTime = updatedItem.getAuctionStartTime().format(formatter);
        String formatEndTime = updatedItem.getAuctionEndTime().format(formatter);
        log.info("SellerId: " + updatedItem.getSellerId());
        log.info("Final buyer Id: " + updatedItem.getCurrentBidUserId());

        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle("Here is the final sell detail for item: " + item.getItemName())
                        .addField(
                                "StartPrice 💵",
                                updatedItem.getStartPrice().toString()
                                        + "$"
                                        + (updatedItem.getSellerId() != ""
                                                ? ("\n" + "<@" + updatedItem.getSellerId() + ">")
                                                : ""),
                                true)
                        .addField(
                                "Final buyer bid 💵",
                                updatedItem.getCurrentBid().toString()
                                        + "$"
                                        + ((updatedItem.getCurrentBidUserId() != null
                                                        && updatedItem
                                                                        .getCurrentBidUserId()
                                                                        .length()
                                                                != 0)
                                                ? ("\n"
                                                        + "<@"
                                                        + updatedItem.getCurrentBidUserId()
                                                        + ">")
                                                : ("\n" + "No Buyer")),
                                true)
                        .addField("Condition 🏷️", updatedItem.getCondition(), true)
                        .addField("StarTime 🕒", formatStartTime, true)
                        .addField("EndTime 🕒", formatEndTime, true)
                        .addField("Description 📝", updatedItem.getDescription(), false);

        return embedBuilder.build();
    }
}
