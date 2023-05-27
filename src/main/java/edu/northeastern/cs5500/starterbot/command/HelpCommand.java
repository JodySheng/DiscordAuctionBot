package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Singleton
@Slf4j
public class HelpCommand implements SlashCommandHandler {

    /** Instantiates a new Help command. */
    @Inject
    public HelpCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "help";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "❓ Check how to use the bot: ");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /help");
        if (event.getName().equals("help")) {
            event.deferReply(true).queue();

            EmbedBuilder embed =
                    new EmbedBuilder()
                            .setTitle("💡  Help")
                            .setDescription(
                                    "🤖️Hello! This is a document to help you use this bot.")
                            .addField(
                                    "👉  /auction_add_item",
                                    "AuctionAddCommand is a slash command that allows users to add the auction item in the general channel. We allow users to upload their item with their item name, description, starting price, minimum bidding price, condition, duration of the auction, image of the product.",
                                    false)
                            .addField(
                                    "👉  /recent_list",
                                    "Will return the most recent added items in a descending order of items added time. Each item information includes the item’s name, current price, auction end time, and its picture.",
                                    false)
                            .addField(
                                    "👉  /search",
                                    "Search items by itemname, price range, or condition(New, Like New, Good, Average, Broken).",
                                    false)
                            .addField("👉  /sell_list", "Show items that were sold by you.", false)
                            .addField("👉  /watch_list", "Show items that you have bid on.", false);
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
