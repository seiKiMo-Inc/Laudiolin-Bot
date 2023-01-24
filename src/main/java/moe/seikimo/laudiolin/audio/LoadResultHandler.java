package moe.seikimo.laudiolin.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.seikimo.laudiolin.objects.enums.MessageType;
import moe.seikimo.laudiolin.objects.constants.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import tech.xigam.cch.utils.Interaction;

public final class LoadResultHandler implements AudioLoadResultHandler {
    private final GuildAudioManager manager;
    private final Interaction interaction;

    private final Member member;

    public LoadResultHandler(
        GuildAudioManager manager, Interaction interaction, String query
    ) {
        this.manager = manager;
        this.interaction = interaction;

        this.member = interaction.getMember();
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        audioTrack.setUserData(new TrackQueueData(this.member));
        this.manager.getScheduler().queue(audioTrack);

        // Process the loaded track.
        var info = audioTrack.getInfo();
        var fullTitle = info.title;
        var title = fullTitle.length() > 50
            ? fullTitle.substring(0, 50) + "..."
            : fullTitle;

        // Reply to the interaction.
        this.interaction.reply(new EmbedBuilder()
            .setColor(MessageType.INFO.getColor())
            .setDescription(String.format("**Queued:** [%s](%s)", title, info.uri))
            .build(), false);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {

    }

    @Override
    public void noMatches() {
        this.interaction.reply(Messages.NO_RESULTS_FOUND, false);
    }

    @Override
    public void loadFailed(FriendlyException e) {
        this.interaction.reply(Messages.UNABLE_TO_PLAY, false);
    }
}
