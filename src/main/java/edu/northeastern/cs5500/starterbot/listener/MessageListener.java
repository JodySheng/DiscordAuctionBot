package edu.northeastern.cs5500.starterbot.listener;

import edu.northeastern.cs5500.starterbot.command.ButtonHandler;
import edu.northeastern.cs5500.starterbot.command.ModalHandler;
import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@Slf4j
public class MessageListener extends ListenerAdapter {

    @Inject Set<SlashCommandHandler> commands;
    @Inject Set<ButtonHandler> buttons;
    @Inject Set<ModalHandler> modals;

    /**
     * Instantiates a new Message listener.
     *
     * @param jda the jda
     */
    @Inject
    public MessageListener(JDA jda) {
        super();
        log.info("Set up event listener for jda now");
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("get slash command event!");
        for (SlashCommandHandler command : commands) {
            if (command.getName().equals(event.getName())) {
                command.onSlashCommandInteraction(event);
                return;
            }
        }
    }

    /**
     * All command data collection.
     *
     * @return the collection
     */
    public @Nonnull Collection<CommandData> allCommandData() {
        Collection<CommandData> commandData =
                commands.stream()
                        .map(SlashCommandHandler::getCommandData)
                        .collect(Collectors.toList());
        if (commandData == null) {
            return new ArrayList<>();
        }
        return commandData;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        log.info("onButtonInteraction: {}", event.getButton().getId());
        String id = event.getButton().getId();
        Objects.requireNonNull(id);
        String handlerName = id.split(":", 2)[0];

        for (ButtonHandler buttonHandler : buttons) {
            log.info("List button handler name: " + buttonHandler.getName());
        }
        log.info("current button handler name: " + handlerName);

        for (ButtonHandler buttonHandler : buttons) {
            log.info(buttonHandler.getName() + " ** " + handlerName);
            if (buttonHandler.getName().equals(handlerName)) {
                buttonHandler.onButtonInteraction(event);
                return;
            }
        }

        log.error("Unknown button handler: {}", handlerName);
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        log.info("onModalInteraction: {}", event.getModalId());

        for (ModalHandler modalHandler : modals) {
            log.info("List modal handler name: " + modalHandler.getName());
        }

        String handlerName = event.getModalId().split(":", 2)[0];

        for (ModalHandler modalHandler : modals) {
            if (modalHandler.getName().equals(handlerName)) {
                modalHandler.onModalInteraction(event);
                return;
            }
        }

        log.error("Unknown modal handler: {}", handlerName);
    }
}
