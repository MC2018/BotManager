package botmanager.generic.commands;

import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface IButtonClickCommand extends ICommand {

    default void run(Event event) {
        if (event instanceof ButtonClickEvent) {
            runOnButtonClick((ButtonClickEvent) event);
        }
    }

    void runOnButtonClick(ButtonClickEvent event);

}
