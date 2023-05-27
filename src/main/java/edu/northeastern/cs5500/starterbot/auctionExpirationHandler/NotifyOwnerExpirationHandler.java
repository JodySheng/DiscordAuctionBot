package edu.northeastern.cs5500.starterbot.auctionExpirationHandler;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Slf4j
@Singleton
public class NotifyOwnerExpirationHandler implements AuctionExpirationHandler {

    @Inject JDA jda;

    /** Instantiates a new Notify owner expiration handler. */
    @Inject
    public NotifyOwnerExpirationHandler() {
        // public and empty for dagger
    }

    @Override
    public void onAuctionExpiration(@Nonnull AuctionItem auctionItem) {
        if (auctionItem.getAuctionStatus().equals("WITHDRAW")) {
            log.info("Item has been withdrawn, give up operation.");
            return;
        }

        log.info(
                "Notifying the seller "
                        + auctionItem.getSellerId()
                        + " the auction is expired, and delete the auction post in the private channel, and post the auction sell details");

        MessageEmbed messageEmbedItem = AuctionExpirationHelper.buildItemEmbed(auctionItem);
        MessageCreateBuilder messageBuilder =
                new MessageCreateBuilder().setEmbeds(messageEmbedItem);
        MessageCreateData message = messageBuilder.build();

        jda.retrieveUserById(auctionItem.getSellerId())
                .queue(
                        user -> {
                            user.openPrivateChannel()
                                    .queue(
                                            channel -> {
                                                channel.deleteMessageById(
                                                                auctionItem
                                                                        .getSellerChannelMessageId())
                                                        .queue();

                                                channel.sendMessage(
                                                                "The auction for your item [ "
                                                                        + auctionItem.getItemName()
                                                                        + " ] is over! Here is the final auction sale details.")
                                                        .queue();
                                                channel.sendMessage(message).queue();
                                            });
                        });
    }
}
