package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

/** The interface Modal handler. */
public interface ModalHandler {

    /**
     * Gets name.
     *
     * @return the name
     */
    @Nonnull
    public String getName();

    /**
     * On modal interaction.
     *
     * @param event the event
     */
    public void onModalInteraction(@Nonnull ModalInteractionEvent event);
}
