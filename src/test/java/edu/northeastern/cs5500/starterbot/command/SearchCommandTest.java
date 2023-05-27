package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

public class SearchCommandTest {
    @Test
    void testNameMatchesData() {
        SearchCommand searchCommand = new SearchCommand();
        String name = searchCommand.getName();
        CommandData commandData = searchCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
