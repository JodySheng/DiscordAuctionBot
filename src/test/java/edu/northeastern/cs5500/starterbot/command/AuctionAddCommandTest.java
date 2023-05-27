package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

public class AuctionAddCommandTest {
    @Test
    void testNameMatchesData() {
        AuctionAddCommand auctionAddCommand = new AuctionAddCommand();
        String name = auctionAddCommand.getName();
        CommandData commandData = auctionAddCommand.getCommandData();
        assertThat(name).isEqualTo(commandData.getName());
    }

    @Test
    void testCommandDataTest() {
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

        AuctionAddCommand auctionAddCommand = new AuctionAddCommand();
        SlashCommandData slashCommandData =
                Commands.slash(auctionAddCommand.getName(), "📦 Add an item for auction")
                        .addOptions(
                                new OptionData(
                                        OptionType.STRING,
                                        "itemname",
                                        "The name of auction item",
                                        true),
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
                                        OptionType.NUMBER,
                                        "minbid",
                                        "The minBid of auction item",
                                        true),
                                conditionOption,
                                durationOption,
                                new OptionData(
                                        OptionType.ATTACHMENT, "image", "Original PNG Only", true));
        assertThat(slashCommandData.getOptions().size()).isEqualTo(7);
    }
}
