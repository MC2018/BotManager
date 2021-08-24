package botmanager.bots.masspoll.generic;

import botmanager.bots.masspoll.MassPoll;
import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class MassPollCommandBase {

    protected MassPoll bot;

    public MassPollCommandBase(MassPoll bot) {
        this.bot = bot;
    }

    public MassPoll getBot() {
        return bot;
    }

}
