package edu.northeastern.cs5500.starterbot.auctionExpirationHandler;

import edu.northeastern.cs5500.starterbot.command.AuctionHelper;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Slf4j
@Singleton
public class NotifyChannelExpirationHandler implements AuctionExpirationHandler {

    @Inject JDA jda;

    /** Instantiates a new Notify channel expiration handler. */
    @Inject
    public NotifyChannelExpirationHandler() {
        // public and empty for dagger
    }

    @Override
    public void onAuctionExpiration(@Nonnull AuctionItem auctionItem) {
        if (auctionItem.getAuctionStatus().equals("WITHDRAW")) {
            log.info("Item has been withdrawn, give up operation.");
            return;
        }

        log.info("Notify the channel the auction is expired");

        String notificationMessage =
                String.format(
                        "Auction item %s has ended🔨 %nSeller: %s     Start bid: %s $ %nBuyer: %s     Final bid: %s $",
                        auctionItem.getItemName(),
                        AuctionHelper.buildUserMentionString(auctionItem.getSellerId()),
                        auctionItem.getStartPrice().toString(),
                        AuctionHelper.buildUserMentionString(auctionItem.getCurrentBidUserId()),
                        auctionItem.getCurrentBid().toString());

        TextChannel channel = jda.getTextChannelById(auctionItem.getPublicChannelId());
        if (channel == null) {
            log.warn("Unable to find a text channel for item {}", auctionItem.getPublicChannelId());
            return;
        }

        channel.sendMessage(notificationMessage).queue();

        log.info("Now try to delete the auaction post in general channel");
        channel.deleteMessageById(auctionItem.getPublicMessageId()).queue();

        log.info("Now posting finalized post");
        MessageEmbed messageEmbedItem = AuctionExpirationHelper.buildItemEmbed(auctionItem);
        MessageCreateBuilder messageBuilder =
                new MessageCreateBuilder().setEmbeds(messageEmbedItem);
        MessageCreateData message = messageBuilder.build();
        channel.sendMessage(message).queue();
    }
}
