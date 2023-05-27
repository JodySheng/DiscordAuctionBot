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

@Slf4j
@Singleton
public class AuctionEditButtonHandler implements ButtonHandler {

    @Inject AuctionController auctionController;

    @Inject
    public AuctionEditButtonHandler() {}

    @Override
    @Nonnull
    public String getName() {
        return AuctionHelper.AuctionEditButtonLabel;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String itemId = event.getComponentId().split(":", 2)[1];
        ObjectId objId = new ObjectId(itemId);
        log.info("event.getMessageId()" + event.getMessageId());
        log.info("Dealing with button-Edit in the auction item add page.");
        Modal modal = createEditModal(":" + itemId, objId);
        event.replyModal(modal).queue();
    }

    @Nonnull
    private Modal createEditModal(String itemId, ObjectId objId) {
        TextInput subject =
                AuctionHelper.createTextInput(
                        "item",
                        "Item",
                        auctionController.getItemById(objId).getItemName(),
                        TextInputStyle.SHORT);
        TextInput description =
                AuctionHelper.createTextInput(
                        "description",
                        "Description",
                        auctionController.getItemById(objId).getDescription(),
                        TextInputStyle.PARAGRAPH);

        TextInput url =
                AuctionHelper.createTextInput(
                        "url",
                        "Url",
                        auctionController.getItemById(objId).getImgUrl(),
                        TextInputStyle.SHORT);

        return Modal.create(this.getName() + ":editInfo" + itemId, "Edit")
                .addActionRow(subject)
                .addActionRow(description)
                .addActionRow(url)
                .build();
    }
}
