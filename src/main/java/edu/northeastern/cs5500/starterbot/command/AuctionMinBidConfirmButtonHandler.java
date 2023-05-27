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
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class AuctionMinBidConfirmButtonHandler implements ButtonHandler {

    @Inject AuctionController auctionController;
    @Inject UserController userController;
    @Inject JDA jda;

    /** Instantiates a new Auction min bid confirm button handler. */
    @Inject
    public AuctionMinBidConfirmButtonHandler() {}

    @Override
    @Nonnull
    public String getName() {
        return AuctionHelper.AuctionMinBidConfirmButtonLabel;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {

        User user = event.getUser();
        String discordUserId = user.getId();
        String itemId = event.getComponentId().split(":", 2)[1];
        ObjectId objId = new ObjectId(itemId);
        AuctionItem item = auctionController.getItemById(objId);

        String sellerId = item.getSellerId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatStartTime = item.getAuctionStartTime().format(formatter);
        String formatEndTime = item.getAuctionEndTime().format(formatter);
        String itemName = item.getItemName();
        log.info("event.getMessageId()" + event.getMessageId());

        log.info("item Id in button interaction: " + itemId);

        log.info("On click minbid-confirm button in the auction item add page.");

        String currentBidUserId = event.getUser().getId();
        log.info("Min bid user: " + currentBidUserId);
        Double minBid =
                auctionController.getItemById(objId).getMinBid()
                        + auctionController.getItemById(objId).getCurrentBid();
        if (currentBidUserId.equals(item.getCurrentBidUserId())) {
            event.reply(
                            "You should not to bid on the item for which you currently hold the highest bid")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (!auctionController.isSeller(objId, currentBidUserId)) {
            AuctionItem updatedItem =
                    auctionController.updateCurrentBidByID(objId, minBid, currentBidUserId);
            log.info(
                    "Fetch the current bid user from db: "
                            + auctionController.getItemById(objId).getCurrentBidUserId());
            event.editMessage(AuctionHelper.buildBuyerEditMessage(updatedItem)).queue();
            updateSellerChannelMessage(updatedItem);

            userController.addItemToWatchList(event.getUser().getId(), objId);

            minBidReply(
                    discordUserId,
                    sellerId,
                    itemName,
                    updatedItem.getCurrentBid(),
                    formatStartTime,
                    formatEndTime,
                    event);
            log.info("Exit success bid");

        } else {
            event.getHook()
                    .sendMessage("You are the seller of this item")
                    .setEphemeral(true)
                    .queue();
            event.editMessage(AuctionHelper.buildBuyerEditMessage(item)).queue();
        }
    }

    private void minBidReply(
            String discordUserId,
            String sellerId,
            String itemName,
            Double price,
            @Nonnull String formatStartTime,
            @Nonnull String formatEndTime,
            @Nonnull ButtonInteractionEvent event) {
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
            @Nonnull String formatStartTime,
            @Nonnull String formatEndTime) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.WHITE);
        embed.addField(
                "New Bid Message: ",
                (discordUserId != "" ? ("<@" + discordUserId + ">") : "")
                        + " offered "
                        + price
                        + " for "
                        + itemName
                        + " hosted by "
                        + (sellerId != "" ? ("<@" + sellerId + ">") : ""),
                false);
        embed.addField("AuctionStartTime", formatStartTime, true);
        embed.addField("AuctionEndTime: ", formatEndTime, true);

        return embed.build();
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
}
