package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class AuctionBidButtonHandler implements ButtonHandler {

    @Inject AuctionController auctionController;

    @Inject
    public AuctionBidButtonHandler() {}

    @Override
    @Nonnull
    public String getName() {
        return AuctionHelper.AuctionBidButtonLabel;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String itemId = event.getComponentId().split(":", 2)[1];
        ObjectId objId = new ObjectId(itemId);

        log.info("Dealing with button-Bid in the auction item add page.");
        Modal bidModal = createBidModal(this.getName(), ":" + itemId, objId);
        event.replyModal(bidModal).queue();
    }

    @Nonnull
    private Modal createBidModal(String handlerName, String itemID, ObjectId objId) {
        TextInput bidPrice =
                AuctionHelper.createTextInput(
                        "bid",
                        "BidPrice",
                        "Place your bid here, current bid is: "
                                + auctionController.getItemById(objId).getCurrentBid(),
                        TextInputStyle.SHORT);
        return Modal.create(handlerName + ":bidPrice" + itemID, "BidPrice")
                .addActionRow(bidPrice)
                .build();
    }
}
