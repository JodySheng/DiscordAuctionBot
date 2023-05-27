package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.controller.PaginatorController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.model.Paginator;
import java.awt.Color;
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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public abstract class AbstractItemListCommand implements SlashCommandHandler, ButtonHandler {
    private static final int ITEMS_PER_PAGE = 3;

    @Inject AuctionController auctionController;
    @Inject UserController userController;
    @Inject PaginatorController paginatorController;

    @Override
    @Nonnull
    public abstract String getName();

    @Override
    @Nonnull
    public abstract CommandData getCommandData();

    /**
     * Gets item list.
     *
     * @param userId the user id
     * @return the item list
     */
    public abstract List<AuctionItem> getItemList(String userId);

    /**
     * Gets list title.
     *
     * @return the list title
     */
    public abstract String getListTitle();

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /", getName());

        event.deferReply(true).queue();

        String currentUserId = event.getUser().getId();

        List<AuctionItem> auctionItems = getItemList(currentUserId);

        Paginator paginator = paginatorController.createPaginator(auctionItems, ITEMS_PER_PAGE);
        List<AuctionItem> currentPageData = paginator.getCurrentPageData();

        if (auctionItems.isEmpty() || auctionItems.size() == 0) {
            event.getHook().sendMessage("No items in this list!").queue();
            return;
        }

        MessageCreateBuilder message =
                new MessageCreateBuilder()
                        .setContent(
                                getListTitle()
                                        + "📚 Page: "
                                        + (paginator.getCurrentPage() + 1)
                                        + " / "
                                        + paginator.getTotalPages())
                        .setEmbeds(buildItemsEmbed(currentPageData))
                        .addActionRow(
                                buildPageNavigationButtons(
                                        paginator.getId(),
                                        paginator.getCurrentPage(),
                                        paginator.getTotalPages()));
        event.getHook().sendMessage(message.build()).setEphemeral(true).queue();
    }

    /**
     * Build items embed list.
     *
     * @param items the items
     * @return the list
     */
    public List<MessageEmbed> buildItemsEmbed(List<AuctionItem> items) {
        List<MessageEmbed> embeds = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (AuctionItem item : items) {
            EmbedBuilder listEmbed = new EmbedBuilder();
            listEmbed.setColor(Color.WHITE);
            listEmbed.setTitle("🔗Item: " + item.getItemName(), item.getDiscordMessageUrl());
            listEmbed.addField("💰Current Bid: ", Double.toString(item.getCurrentBid()), true);
            listEmbed.addField(
                    "⏰Auction End Time: ", item.getAuctionEndTime().format(formatter), true);
            listEmbed.setThumbnail(item.getImgUrl());
            embeds.add(listEmbed.build());
        }

        return embeds;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        log.info("event: ", event);
        String[] buttonId = event.getComponentId().split(":");
        String paginatorId = buttonId[1];
        int targetPage = Integer.parseInt(buttonId[2]);

        Paginator paginator = paginatorController.getPaginator(paginatorId);

        paginator.setCurrentPage(targetPage);
        event.editMessage(editCurrentPage(paginator)).queue();
    }

    /**
     * Edit current page message edit data.
     *
     * @param paginator the paginator
     * @return the message edit data
     */
    public MessageEditData editCurrentPage(Paginator paginator) {
        MessageEditBuilder messageEditBuilder = new MessageEditBuilder();
        List<AuctionItem> currentPageData = paginator.getCurrentPageData();
        messageEditBuilder.setContent(
                getListTitle()
                        + "📚 Page: "
                        + (paginator.getCurrentPage() + 1)
                        + " / "
                        + paginator.getTotalPages());
        messageEditBuilder.setEmbeds(buildItemsEmbed(currentPageData));
        messageEditBuilder.setActionRow(
                buildPageNavigationButtons(
                        paginator.getId(), paginator.getCurrentPage(), paginator.getTotalPages()));

        return messageEditBuilder.build();
    }

    /**
     * Build page navigation buttons collection.
     *
     * @param paginatorId the paginator id
     * @param currentPage the current page
     * @param totalPages the total pages
     * @return the collection
     */
    Collection<ItemComponent> buildPageNavigationButtons(
            ObjectId paginatorId, int currentPage, int totalPages) {
        List<ItemComponent> buttons = new ArrayList<>();
        buttons.add(
                Button.primary(
                                String.format("%s:%s:%d", getName(), paginatorId, currentPage - 1),
                                "PREV")
                        .withDisabled(currentPage == 0));
        buttons.add(
                Button.primary(
                                String.format("%s:%s:%d", getName(), paginatorId, currentPage + 1),
                                "NEXT")
                        .withDisabled(currentPage == totalPages - 1));
        return buttons;
    }
}
