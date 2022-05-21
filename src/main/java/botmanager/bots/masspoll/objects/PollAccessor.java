package botmanager.bots.masspoll.objects;

import botmanager.bots.masspoll.MassPoll;
import botmanager.utils.IOUtils;

import java.io.IOException;
import java.util.NoSuchElementException;

public class PollAccessor implements AutoCloseable {

    public enum PollAccessType {
        POLL_CREATOR_ID,
        UUID,
        SEND
    }

    final MassPoll massPoll;
    final long pollID;
    final PollAccessType accessType;
    final String id;

    public PollAccessor(MassPoll massPoll, long pollID, PollAccessType accessType, String id) {
        this.massPoll = massPoll;
        this.pollID = pollID;
        this.accessType = accessType;
        this.id = id;
    }

    public PollAccessor(MassPoll massPoll, long pollID, PollAccessType accessType) {
        this(massPoll, pollID, accessType, "");
    }

    public Poll getPoll() throws NoSuchElementException, IOException {
        Poll poll;

        while (massPoll.isPollInProcess(pollID)) {
            // wait until poll is free
        }

        massPoll.addPollInProcess(pollID);
        poll = IOUtils.readGson(Poll.getFileLocation(massPoll, pollID), Poll.class);

        if (
                (accessType == PollAccessType.UUID && !poll.getUUID().equals(id)) ||
                (accessType == PollAccessType.POLL_CREATOR_ID && !poll.getCreatorID().equals(id))) {
            massPoll.removePollInProcess(pollID);
            throw new NoSuchElementException("The " + accessType.name() + " does not match; this poll was likely made with a different bot.");
        } else if (accessType == PollAccessType.SEND) {
            return null;
        }

        return poll;
    }

    @Override
    public void close() throws Exception {
        massPoll.removePollInProcess(pollID);
    }

}
