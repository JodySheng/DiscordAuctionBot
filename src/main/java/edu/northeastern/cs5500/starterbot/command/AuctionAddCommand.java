package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.auctionExpirationHandler.AuctionExpirationEvent;
import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.listener.AuctionExpirationEventListener;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.model.DiscordUser;
import edu.northeastern.cs5500.starterbot.util.ImageHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Singleton
@Slf4j
public class AuctionAddCommand implements SlashCommandHandler {

    @Inject AuctionController auctionController;
    @Inject UserController userController;
    @Inject AuctionExpirationEventListener auctionExpirationListener;
    @Inject JDA jda;

    /** Instantiates a new Auction add command. */
    @Inject
    public AuctionAddCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "auction_add_item";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        OptionData conditionOption =
                new OptionData(OptionType.STRING, "condition", "Choose a condition", true);
        conditionOption.addChoice("New", "New");
        conditionOption.addChoice("Like new", "Like new");
        conditionOption.addChoice("Good", "Good");
        conditionOption.addChoice("Average", "Average");
        conditionOption.addChoice("Broken", "Broken");

        OptionData durationOption =
                new OptionData(OptionType.INTEGER, "duration", "Choose a duration", true);
        durationOption.addChoice("1 Minute", 60);
        durationOption.addChoice("5 Minutes", 300);
        durationOption.addChoice("1 Hour", 3600);
        durationOption.addChoice("5 Hours", 18000);
        durationOption.addChoice("1 Day", 86400);
        durationOption.addChoice("2 Days", 172800);
        durationOption.addChoice("3 Days", 259200);
        durationOption.addChoice("1 Week", 604800);

        return Commands.slash(getName(), "📦 Add an item for auction")
                .addOptions(
                        new OptionData(
                                OptionType.STRING, "itemname", "The name of auction item", true),
                        new OptionData(
                                OptionType.STRING,
                                "description",
                                "The description of auction item",
                                true),
                        new OptionData(
                                OptionType.NUMBER,
                                "startprice",
                                "The startPrice of auction item",
                                true),
                        new OptionData(
                                OptionType.NUMBER, "minbid", "The minBid of auction item", true),
                        conditionOption,
                        durationOption,
                        new OptionData(OptionType.ATTACHMENT, "image", "Original PNG Only", true));
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

        var guild = event.getGuild();
        if (guild == null) {
            event.reply(
                            "This command is not allowed in direct messages. Please use it in a guild text channel.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        log.info("event: /auction add item");

        String severId = guild.getId();
        String channelId = event.getChannel().getId();
        log.info("event.getGuild().getId()" + severId);
        log.info("event.getChannel().getId()" + channelId);

        Attachment attachment = Objects.requireNonNull(event.getOption("image")).getAsAttachment();
        String attachType = attachment.getContentType();

        if (attachType == null
                || !attachment.isImage()
                || (!attachType.startsWith("image/png") && !attachType.startsWith("image/jpeg"))) {
            event.reply("Please upload an Original PNG image less than 8MB")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        InputStream fileStream = attachment.retrieveInputStream().join();
        String imgUrl;
        try {
            imgUrl = ImageHandler.uploadToImgur(fileStream);
            log.info("img uploaded: " + imgUrl);
        } catch (IOException e) {
            event.reply("Failed to upload image, please try again").setEphemeral(true).queue();
            return;
        }

        if (imgUrl == null) {
            event.reply("Failed to upload image, please try again").setEphemeral(true).queue();
            return;
        }

        User user = event.getUser();
        String discordUserId = user.getId();
        log.info("this is the auction seller id: " + discordUserId);
        String discordChannelId = event.getChannel().getId();
        log.info("this is the event channel id: " + discordChannelId);

        String itemName = Objects.requireNonNull(event.getOption("itemname")).getAsString();
        String description = Objects.requireNonNull(event.getOption("description")).getAsString();
        String condition = Objects.requireNonNull(event.getOption("condition")).getAsString();
        Double startPrice = Objects.requireNonNull(event.getOption("startprice")).getAsDouble();
        Double minBid = Objects.requireNonNull(event.getOption("minbid")).getAsDouble();
        Integer duration = Objects.requireNonNull(event.getOption("duration")).getAsInt();

        if (startPrice < 0.01) {
            event.reply("⚠️ Start price shouldn't be less than 0.01 $").setEphemeral(true).queue();
            return;
        }
        if (minBid < 0.01) {
            event.reply("⚠️ Minimum bid shouldn't be less than 0.01 $").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        AuctionItem item =
                auctionController.createAuctionItem(
                        itemName,
                        description,
                        condition,
                        startPrice,
                        minBid,
                        duration,
                        imgUrl,
                        discordUserId);

        log.info("save item to repo successfully");
        AuctionExpirationEvent auctionExpirationEvent = new AuctionExpirationEvent(jda, item);

        auctionExpirationEvent.fireEventAfterDelay(duration);
        log.info("Set auction expiration successfully");

        String itemID = ":" + auctionController.getObjectID(item).toString();
        log.info("item Id in slash interaction: " + itemID);

        MessageEmbed messageEmbedItem = AuctionHelper.buildItemEmbed(item);
        MessageCreateBuilder messageBuilder =
                new MessageCreateBuilder()
                        .setEmbeds(messageEmbedItem)
                        .addActionRow(AuctionHelper.buildBuyerButtons(item.getId()));

        MessageCreateData message = messageBuilder.build();

        MessageCreateBuilder sellerMessageBuilder =
                new MessageCreateBuilder()
                        .setEmbeds(messageEmbedItem)
                        .addActionRow(AuctionHelper.buildSellerButtons(item.getId()));

        user.openPrivateChannel()
                .queue(
                        channel -> {
                            channel.sendMessage(sellerMessageBuilder.build())
                                    .queue(
                                            response -> {
                                                log.info(
                                                        "event.getMessageId(): "
                                                                + response.getId());
                                                item.setSellerChannelId(channel.getId());
                                                item.setSellerChannelMessageId(response.getId());
                                                auctionController.updateOneItem(item);
                                            });
                        });

        event.getHook()
                .sendMessage(message)
                .queue(
                        response -> {
                            log.info("event.getMessageId(): " + response.getId());
                            String jumpUrl = response.getJumpUrl();
                            item.setDiscordMessageUrl(jumpUrl);
                            item.setPublicMessageId((response.getId()));
                            item.setPublicChannelId(channelId);

                            auctionController.updateOneItem(item);
                            log.info("Message url: " + jumpUrl);
                        });

        DiscordUser itemOwner = userController.getDiscordUserByUserId(discordUserId);
        userController.addItemToSellerInventory(itemOwner, item);
    }
}
