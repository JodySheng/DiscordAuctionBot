package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class AuctionMinBidButtonHandler implements ButtonHandler {

    @Inject AuctionController auctionController;

    /** Instantiates a new Auction min bid button handler. */
    @Inject
    public AuctionMinBidButtonHandler() {
        // empty for dagger
    }

    @Override
    @Nonnull
    public String getName() {
        return AuctionHelper.AuctionMinBidButtonLabel;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String itemId = event.getComponentId().split(":", 2)[1];
        ObjectId objId = new ObjectId(itemId);
        AuctionItem item = auctionController.getItemById(objId);

        log.info("item Id in button interaction: " + itemId);

        log.info("Dealing with button-Min Bid in the auction item add page.");
        event.editMessage(
                        buildConfirmationMessage(
                                item,
                                itemId,
                                AuctionHelper.AuctionMinBidConfirmButtonLabel,
                                AuctionHelper.AuctionMinBidCancelButtonLabel))
                .queue();
    }

    private MessageEditData buildConfirmationMessage(
            AuctionItem item, String itemId, String confirmButtonLabel, String cancelButtonLabel) {
        MessageEditBuilder messageEditBuilder = new MessageEditBuilder();
        messageEditBuilder.setEmbeds(AuctionHelper.buildItemEmbed(item));

        Button confirmButton =
                Button.primary(
                        AuctionHelper.AuctionMinBidConfirmButtonLabel + ":" + itemId.toString(),
                        "Confirm Min Bid");
        Button cancelButton =
                Button.danger(
                        AuctionHelper.AuctionMinBidCancelButtonLabel + ":" + itemId.toString(),
                        "Cancel Min Bid");

        List<ItemComponent> buttons = new ArrayList<>();
        buttons.add(confirmButton);
        buttons.add(cancelButton);

        messageEditBuilder.setActionRow(buttons);

        return messageEditBuilder.build();
    }
}
