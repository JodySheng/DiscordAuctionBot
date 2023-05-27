package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/** The interface Slash command handler. */
public interface SlashCommandHandler {

    /**
     * Gets name.
     *
     * @return the name
     */
    @Nonnull
    public String getName();

    /**
     * Gets command data.
     *
     * @return the command data
     */
    @Nonnull
    public CommandData getCommandData();

    /**
     * On slash command interaction.
     *
     * @param event the event
     */
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event);
}
