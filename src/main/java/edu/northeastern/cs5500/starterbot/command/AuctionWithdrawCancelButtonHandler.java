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
public class AuctionWithdrawCancelButtonHandler implements ButtonHandler {

    @Inject AuctionController auctionController;

    /** Instantiates a new Auction withdraw cancel button handler. */
    @Inject
    public AuctionWithdrawCancelButtonHandler() {
        // empty for dagger
    }

    @Override
    @Nonnull
    public String getName() {
        return AuctionHelper.AuctionWithdrawCancelButtonLabel;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String itemId = event.getComponentId().split(":", 2)[1];
        ObjectId objId = new ObjectId(itemId);
        AuctionItem item = auctionController.getItemById(objId);

        log.info("event.getMessageId()" + event.getMessageId());

        log.info("item Id in button interaction: " + itemId);

        log.info("Dealing with button-WithDraw Cancel in the auction item add page. ");
        MessageEditData messageEditData = AuctionHelper.buildSellerEditMessage(item);
        event.editMessage(messageEditData).queue();
    }
}
