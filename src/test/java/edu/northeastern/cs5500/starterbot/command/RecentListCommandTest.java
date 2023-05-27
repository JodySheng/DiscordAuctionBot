package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

public class RecentListCommandTest {
    @Test
    void testNameMatchesData() {
        RecentListCommand recentListCommand = new RecentListCommand();
        String name = recentListCommand.getName();
        CommandData commandData = recentListCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
