package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.model.Paginator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class SearchCommand extends AbstractItemListCommand {

    @Inject AuctionController auctionController;

    /** Instantiates a new Search command. */
    @Inject
    public SearchCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "search";
    }

    @Override
    public String getListTitle() {
        return "🔍 Search Result:\n\n";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        OptionData conditionOption =
                new OptionData(OptionType.STRING, "condition", "Choose a condition", false);
        conditionOption.addChoice("New", "New");
        conditionOption.addChoice("Like new", "Like new");
        conditionOption.addChoice("Good", "Good");
        conditionOption.addChoice("Average", "Average");
        conditionOption.addChoice("Broken", "Broken");

        OptionData priceOrderOption =
                new OptionData(OptionType.STRING, "price", "Choose price range", false);
        priceOrderOption.addChoice("0$ - 30$", "0-30");
        priceOrderOption.addChoice("30$ - 100$", "30-100");
        priceOrderOption.addChoice("100$ - 500$", "100-500");
        priceOrderOption.addChoice("500$ - 1000$", "500-1000");
        priceOrderOption.addChoice("1000$ - 5000$", "1000-5000");
        priceOrderOption.addChoice("more than 5000$", "5000");

        return Commands.slash(getName(), "🔍 Search items:")
                .addOptions(
                        new OptionData(
                                OptionType.STRING, "name", "The name of auction item", false),
                        conditionOption,
                        priceOrderOption);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event:" + getName());
        event.deferReply(true).queue();

        OptionMapping itemName = event.getOption("name");
        OptionMapping condition = event.getOption("condition");
        OptionMapping priceRange = event.getOption("price");

        if (itemName == null && condition == null && priceRange == null) {
            event.getHook().sendMessage("Please provide at least one search term!").queue();
            return;
        }

        String parsedItemName = itemName == null ? null : itemName.getAsString();
        String parsedCondition = condition == null ? null : condition.getAsString();
        String parsedPriceRange = priceRange == null ? null : priceRange.getAsString();

        Double startPrice = Double.MIN_VALUE;
        Double endPrice = Double.MAX_VALUE;

        if (parsedPriceRange != null) {
            String[] priceRangeTuple = priceRange == null ? null : parsedPriceRange.split("-");
            startPrice = Double.parseDouble(priceRangeTuple[0]);
            if (priceRangeTuple.length > 1) {
                endPrice = Double.parseDouble(priceRangeTuple[1]);
            }
        }

        List<AuctionItem> auctionItems =
                auctionController.getSearchList(
                        parsedItemName, parsedCondition, startPrice, endPrice);

        Paginator paginator = paginatorController.createPaginator(auctionItems, 3);
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

    @Override
    public List<AuctionItem> getItemList(String userId) {
        throw new UnsupportedOperationException(
                "Should not call 'getItemList() on 'SearchCommand'");
    }
}
