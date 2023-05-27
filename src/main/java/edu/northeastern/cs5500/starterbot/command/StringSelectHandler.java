package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/** The interface String select handler. */
public interface StringSelectHandler {

    /**
     * Gets name.
     *
     * @return the name
     */
    @Nonnull
    public String getName();

    /**
     * On string select interaction.
     *
     * @param event the event
     */
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event);
}
