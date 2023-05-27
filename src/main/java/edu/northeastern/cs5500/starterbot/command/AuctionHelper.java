package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class AuctionHelper {

    @Inject AuctionController auctionController;

    public static final String AuctionEditButtonLabel = "auction_edit_item";
    public static final String AuctionWithdrawButtonLabel = "auction_withdraw_item";
    public static final String AuctionWithdrawCancelButtonLabel = "auction_withdraw_item_cancel";
    public static final String AuctionWithdrawConfirmButtonLabel = "auction_withdraw_item_confirm";
    public static final String AuctionBidButtonLabel = "auction_bid_item";
    public static final String AuctionMinBidButtonLabel = "auction_minbid_item";
    public static final String AuctionMinBidCancelButtonLabel = "auction_minbid_cancel_item";
    public static final String AuctionMinBidConfirmButtonLabel = "auction_minbid_confirm_item";

    /**
     * Create text input text input.
     *
     * @param id the id
     * @param label the label
     * @param placeholder the placeholder
     * @param textInputStyle the text input style
     * @return the text input
     */
    @Nonnull
    public static TextInput createTextInput(
            @Nonnull String id,
            @Nonnull String label,
            @Nonnull String placeholder,
            @Nonnull TextInputStyle textInputStyle) {
        return TextInput.create(id, label, textInputStyle)
                .setPlaceholder(placeholder)
                .setMinLength(1)
                .setMaxLength(100)
                .setRequired(false)
                .build();
    }

    /**
     * Build item embed message embed.
     *
     * @param item the item
     * @return the message embed
     */
    public static MessageEmbed buildItemEmbed(AuctionItem item) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formatStartTime = item.getAuctionStartTime().format(formatter);
        String formatEndTime = item.getAuctionEndTime().format(formatter);
        log.info("SellerId: " + item.getSellerId());
        log.info("curBidUserId: " + item.getCurrentBidUserId());

        Double currentBid = item.getCurrentBid();
        if (currentBid == null) {
            throw new IllegalStateException("Cannot render a bid embed with no bids!");
        }

        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(String.format("📌 %s", item.getItemName()))
                        .addField("Start Time 🕒", formatStartTime, true)
                        .addField("End Time 🕒", formatEndTime, true)
                        .addField("Condition 🏷️", item.getCondition(), true)
                        .addField(
                                "Start Price 💵",
                                String.format(
                                        "$%.2f%n%s",
                                        item.getStartPrice(),
                                        buildUserMentionString(item.getSellerId())),
                                true)
                        .addField(
                                "Current Bid 💵",
                                String.format(
                                        "$%.2f%n%s",
                                        currentBid,
                                        buildUserMentionString(item.getCurrentBidUserId())),
                                true)
                        .addField("Min Bid 💵", String.format("+$%.2f", item.getMinBid()), true)
                        .addField("Description 📝", item.getDescription(), false)
                        .setImage(item.getImgUrl());

        return embedBuilder.build();
    }

    /**
     * Build user mention string string.
     *
     * @param discordUserId the discord user id
     * @return the string
     */
    public static String buildUserMentionString(String discordUserId) {
        if (discordUserId == null || discordUserId.isEmpty()) {
            return "No Bidder 👤";
        }
        return String.format("<@%s> 👤", discordUserId);
    }

    /**
     * Build buyer buttons collection.
     *
     * @param itmObjectId the itm object id
     * @return the collection
     */
    @Nonnull
    public static Collection<ItemComponent> buildBuyerButtons(ObjectId itmObjectId) {
        String itemID = ":" + itmObjectId.toString();
        AuctionItem item = AuctionController.getAuctionItem(itmObjectId);
        List<ItemComponent> buttons = new ArrayList<>();

        buttons.add(
                Button.primary(
                        AuctionMinBidButtonLabel + itemID,
                        String.format("Min Bid  (+$%.2f)", item.getMinBid())));
        buttons.add(Button.primary(AuctionBidButtonLabel + itemID, "Bid"));
        return buttons;
    }

    /**
     * Build seller buttons collection.
     *
     * @param itmObjectId the itm object id
     * @return the collection
     */
    @Nonnull
    public static Collection<ItemComponent> buildSellerButtons(ObjectId itmObjectId) {
        String itemID = ":" + itmObjectId.toString();
        List<ItemComponent> buttons = new ArrayList<>();

        buttons.add(Button.danger(AuctionWithdrawButtonLabel + itemID, "Withdraw"));
        buttons.add(Button.primary(AuctionEditButtonLabel + itemID, "Edit Info"));
        return buttons;
    }

    /**
     * Build seller edit message message edit data.
     *
     * @param item the item
     * @return the message edit data
     */
    @Nonnull
    public static MessageEditData buildSellerEditMessage(AuctionItem item) {
        MessageEditBuilder messageEditBuilder = new MessageEditBuilder();
        messageEditBuilder.setEmbeds(AuctionHelper.buildItemEmbed(item));
        messageEditBuilder.setActionRow(buildSellerButtons(item.getId()));
        return messageEditBuilder.build();
    }

    /**
     * Build buyer edit message message edit data.
     *
     * @param item the item
     * @return the message edit data
     */
    @Nonnull
    public static MessageEditData buildBuyerEditMessage(AuctionItem item) {
        MessageEditBuilder messageEditBuilder = new MessageEditBuilder();
        messageEditBuilder.setEmbeds(AuctionHelper.buildItemEmbed(item));
        messageEditBuilder.setActionRow(buildBuyerButtons(item.getId()));
        return messageEditBuilder.build();
    }

    /**
     * Build confirmation message message edit data.
     *
     * @param item the item
     * @param itemId the item id
     * @param confirmButtonLabel the confirm button label
     * @param cancelButtonLabel the cancel button label
     * @return the message edit data
     */
    public static MessageEditData buildConfirmationMessage(
            AuctionItem item, String itemId, String confirmButtonLabel, String cancelButtonLabel) {
        MessageEditBuilder messageEditBuilder = new MessageEditBuilder();
        messageEditBuilder.setEmbeds(AuctionHelper.buildItemEmbed(item));

        Button confirmButton =
                Button.primary(
                        AuctionWithdrawConfirmButtonLabel + ":" + itemId.toString(),
                        "Confirm Withdraw");
        Button cancelButton =
                Button.danger(
                        AuctionWithdrawCancelButtonLabel + ":" + itemId.toString(),
                        "Cancel WithDraw");

        List<ItemComponent> buttons = new ArrayList<>();
        buttons.add(confirmButton);
        buttons.add(cancelButton);

        messageEditBuilder.setActionRow(buttons);

        return messageEditBuilder.build();
    }
}
