package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class AuctionWithdrawConfirmButtonHandler implements ButtonHandler {

    @Inject AuctionController auctionController;
    @Inject JDA jda;

    /** Instantiates a new Auction withdraw confirm button handler. */
    @Inject
    public AuctionWithdrawConfirmButtonHandler() {}

    @Override
    @Nonnull
    public String getName() {
        return AuctionHelper.AuctionWithdrawConfirmButtonLabel;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String itemId = event.getComponentId().split(":", 2)[1];
        ObjectId objId = new ObjectId(itemId);
        AuctionItem item = auctionController.getItemById(objId);

        log.info("Dealing with button-WithDraw Confirm in the auction item add page. ");

        if (item.getCurrentBidUserId() != null) {
            event.getHook()
                    .sendMessage("You cannot withdraw an item that has already been bid on!")
                    .setEphemeral(true)
                    .queue();
            event.editMessage(AuctionHelper.buildSellerEditMessage(item)).queue();
            return;
        }

        jda.getTextChannelById(item.getPublicChannelId())
                .deleteMessageById(item.getPublicMessageId())
                .queue();

        jda.getTextChannelById(item.getPublicChannelId())
                .sendMessage(buildWithdrawNotification(item))
                .queue();

        event.getChannel().deleteMessageById(event.getMessageId()).queue();

        auctionController.removeItemById(new ObjectId(itemId));
        event.getHook()
                .sendMessage("Item has been withdrawn successfully!")
                .setEphemeral(true)
                .queue();
    }

    /**
     * Build withdraw notification string.
     *
     * @param auctionItem the auction item
     * @return the string
     */
    String buildWithdrawNotification(AuctionItem auctionItem) {
        return String.format(
                "Auction item %s has been withdrawn by Seller %s",
                auctionItem.getItemName(),
                AuctionHelper.buildUserMentionString(auctionItem.getSellerId()));
    }
}
