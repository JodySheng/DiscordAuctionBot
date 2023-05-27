package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/** The interface Button handler. */
public interface ButtonHandler {

    /**
     * Gets name.
     *
     * @return the name
     */
    @Nonnull
    public String getName();

    /**
     * On button interaction.
     *
     * @param event the event
     */
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event);
}
