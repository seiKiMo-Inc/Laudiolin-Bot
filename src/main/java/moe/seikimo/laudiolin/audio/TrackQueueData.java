package moe.seikimo.laudiolin.audio;

import net.dv8tion.jda.api.entities.Member;

public record TrackQueueData(
    Member queuedBy
) {
}
