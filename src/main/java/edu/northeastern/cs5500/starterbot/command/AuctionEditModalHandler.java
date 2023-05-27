package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class AuctionEditModalHandler implements ModalHandler {

    @Inject AuctionController auctionController;

    /** Instantiates a new Auction edit modal handler. */
    @Inject
    public AuctionEditModalHandler() {
        // empty for dagger
    }

    @Override
    @Nonnull
    public String getName() {
        return AuctionHelper.AuctionEditButtonLabel;
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        log.info("Entering Modal Interaction: {}", event.getModalId());

        String eventName = event.getModalId().split(":", 3)[1];
        log.info("eventname - " + eventName);
        String itemId = event.getModalId().split(":", 3)[2];
        ObjectId objId = new ObjectId(itemId);
        AuctionItem item = auctionController.getItemById(objId);
        log.info("itemID - " + itemId + "here after convert to objid: " + objId);

        List<ModalMapping> records = event.getValues();
        log.info("getModal: {}", event);
        for (ModalMapping instance : records) {
            log.info("getModalMapping: {}", instance.getAsString());
        }

        // fetch the user input
        String itemName = records.get(0).getAsString();
        String description = records.get(1).getAsString();
        String url = records.get(2).getAsString();

        log.info("itemName:" + itemName);
        log.info("description:" + description);
        log.info("url:" + url);

        if (itemName == null || itemName.length() == 0) {
            itemName = auctionController.getItemById(objId).getItemName();
        }

        if (description == null || description.length() == 0) {
            description = auctionController.getItemById(objId).getDescription();
        }

        if (url == null || url.length() == 0) {
            url = auctionController.getItemById(objId).getImgUrl();
        }

        log.info("updated itemName:" + itemName);
        log.info("updated description:" + description);
        log.info("updated url:" + url);

        AuctionItem updatedItem =
                auctionController.updateItemUsingEdit(objId, itemName, description, url);

        event.editMessage(AuctionHelper.buildSellerEditMessage(updatedItem)).queue();

        log.info("updatedItemName:" + updatedItem.getItemName());
        log.info("updatedItemDescription:" + updatedItem.getDescription());

        event.getJDA()
                .getTextChannelById(updatedItem.getPublicChannelId())
                .editMessageById(
                        updatedItem.getPublicMessageId(),
                        AuctionHelper.buildBuyerEditMessage(updatedItem))
                .queue();

        String discordUserId = event.getUser().getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatStartTime = item.getAuctionStartTime().format(formatter);
        String formatEndTime = item.getAuctionEndTime().format(formatter);

        MessageEmbed messageEmbedItem =
                editReplyEmbed(
                        discordUserId,
                        itemName,
                        item.getCurrentBid(),
                        formatStartTime,
                        formatEndTime);
        MessageCreateBuilder messageBuilder =
                new MessageCreateBuilder().setEmbeds(messageEmbedItem);
        MessageCreateData messageEdit = messageBuilder.build();

        event.getHook()
                .sendMessage(messageEdit)
                .setEphemeral(true)
                .queue(
                        success -> System.out.println("Interaction response sent!"),
                        error ->
                                System.out.println(
                                        "Interaction response failed to send: "
                                                + error.getMessage()));
    }

    private MessageEmbed editReplyEmbed(
            String discordUserId,
            String itemName,
            Double price,
            String formatStartTime,
            String formatEndTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.WHITE);
        embed.addField(
                "Edit reply: ",
                (discordUserId != "" ? ("<@" + discordUserId + ">") : "")
                        + " listed "
                        + price
                        + " for "
                        + itemName,
                false);
        embed.addField("AuctionStartTime", formatStartTime, true);
        embed.addField("AuctionEndTime: ", formatEndTime, true);

        return embed.build();
    }
}
