package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

public class HelpCommandTest {
    @Test
    void testNameMatchesData() {
        HelpCommand helpCommand = new HelpCommand();
        String name = helpCommand.getName();
        CommandData commandData = helpCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
