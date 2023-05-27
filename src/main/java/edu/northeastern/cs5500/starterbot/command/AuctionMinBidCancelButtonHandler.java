package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class AuctionMinBidCancelButtonHandler implements ButtonHandler {

    @Inject AuctionController auctionController;

    /** Instantiates a new Auction min bid cancel button handler. */
    @Inject
    public AuctionMinBidCancelButtonHandler() {}

    @Override
    @Nonnull
    public String getName() {
        return AuctionHelper.AuctionMinBidCancelButtonLabel;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String itemId = event.getComponentId().split(":", 2)[1];
        ObjectId objId = new ObjectId(itemId);
        AuctionItem item = auctionController.getItemById(objId);

        log.info("event.getMessageId()" + event.getMessageId());

        log.info("item Id in button interaction: " + itemId);

        log.info("On click bid-cancel button in the auction item add page.");
        MessageEditData message = AuctionHelper.buildBuyerEditMessage(item);
        event.editMessage(message).queue();
    }
}
