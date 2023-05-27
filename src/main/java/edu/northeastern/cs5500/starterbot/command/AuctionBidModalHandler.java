package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class AuctionBidModalHandler implements ModalHandler {

    @Inject AuctionController auctionController;
    @Inject UserController userController;
    @Inject JDA jda;

    /** Instantiates a new Auction bid modal handler. */
    @Inject
    public AuctionBidModalHandler() {
        // empty for dagger
    }

    @Override
    @Nonnull
    public String getName() {
        return AuctionHelper.AuctionBidButtonLabel;
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        log.info("Entering Modal Interaction: {}", event.getModalId());

        String eventName = event.getModalId().split(":", 3)[1];
        log.info("eventname - " + eventName);
        String itemId = event.getModalId().split(":", 3)[2];
        ObjectId objId = new ObjectId(itemId);
        AuctionItem item = auctionController.getItemById(objId);
        Double buyerBidPrice = Double.parseDouble(event.getValue("bid").getAsString());

        log.info("seller Id:" + auctionController.getItemById(objId).getSellerId());
        log.info("get User id: " + event.getUser().getId());

        String currentBidUserId = event.getUser().getId();
        String sellerId = auctionController.getItemById(objId).getSellerId();

        String itemName = item.getItemName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatStartTime = item.getAuctionStartTime().format(formatter);
        String formatEndTime = item.getAuctionEndTime().format(formatter);

        if (currentBidUserId.equals(item.getCurrentBidUserId())) {
            event.reply(
                            "You should not to bid on the item for which you currently hold the highest bid")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!auctionController.isSeller(objId, currentBidUserId)) {
            if (buyerBidPrice > auctionController.getItemById(objId).getCurrentBid()) {
                AuctionItem updatedItem =
                        auctionController.updateCurrentBidByID(
                                objId, buyerBidPrice, currentBidUserId);
                userController.addItemToWatchList(event.getUser().getId(), objId);

                event.editMessage(AuctionHelper.buildBuyerEditMessage(updatedItem)).queue();
                updateSellerChannelMessage(updatedItem);

                log.info("Exit success bid");
                bidReply(
                        currentBidUserId,
                        sellerId,
                        itemName,
                        buyerBidPrice,
                        formatStartTime,
                        formatEndTime,
                        event);
            } else {
                event.reply("Your bid has to be larger than currrent bid.")
                        .setEphemeral(true)
                        .queue();
            }
        } else {
            event.reply("Seller cannot put a bid").setEphemeral(true).queue();
        }
    }

    private void updateSellerChannelMessage(AuctionItem updatedItem) {
        jda.retrieveUserById(updatedItem.getSellerId())
                .queue(
                        user -> {
                            user.openPrivateChannel()
                                    .queue(
                                            channel -> {
                                                channel.editMessageById(
                                                                updatedItem
                                                                        .getSellerChannelMessageId(),
                                                                AuctionHelper
                                                                        .buildSellerEditMessage(
                                                                                updatedItem))
                                                        .queue();
                                            });
                        });
    }

    private void bidReply(
            String discordUserId,
            String sellerId,
            String itemName,
            Double price,
            String formatStartTime,
            String formatEndTime,
            @Nonnull ModalInteractionEvent event) {
        MessageEmbed BidmessageEmbedItem =
                bidReplyEmbed(
                        discordUserId, sellerId, itemName, price, formatStartTime, formatEndTime);
        MessageCreateBuilder BidmessageBuilder =
                new MessageCreateBuilder().setEmbeds(BidmessageEmbedItem);
        MessageCreateData messageBid = BidmessageBuilder.build();

        event.getHook()
                .sendMessage(messageBid)
                .queue(
                        success -> log.info("Interaction response sent!"),
                        error ->
                                log.info(
                                        "Interaction response failed to send: "
                                                + error.getMessage()));
    }

    private MessageEmbed bidReplyEmbed(
            String discordUserId,
            String sellerId,
            String itemName,
            Double price,
            String formatStartTime,
            String formatEndTime) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.WHITE);
        embed.addField(
                "New Bid Message: ",
                String.format(
                        "New Bid Message: %s offered %s for %s hosted by %s",
                        AuctionHelper.buildUserMentionString(discordUserId),
                        price,
                        itemName,
                        AuctionHelper.buildUserMentionString(sellerId)),
                false);
        embed.addField("AuctionStartTime: ", formatStartTime, true);
        embed.addField("AuctionEndTime: ", formatEndTime, true);

        return embed.build();
    }
}
