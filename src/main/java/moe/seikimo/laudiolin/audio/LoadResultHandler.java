package moe.seikimo.laudiolin.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.seikimo.laudiolin.objects.enums.MessageType;
import moe.seikimo.laudiolin.objects.constants.Messages;
import moe.seikimo.laudiolin.utils.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import tech.xigam.cch.utils.Interaction;

public final class LoadResultHandler implements AudioLoadResultHandler {
    private final GuildAudioManager manager;
    private final Interaction interaction;
    private final String query;

    private final Member member;

    public LoadResultHandler(
        GuildAudioManager manager, Interaction interaction, String query
    ) {
        this.manager = manager;
        this.interaction = interaction;
        this.query = query;

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
        // Check if the playlist was a search result.
        if (audioPlaylist.isSearchResult()) {
            this.trackLoaded(audioPlaylist.getTracks().stream()
                .findFirst().orElseThrow());
            return;
        }

        this.manager.getScheduler().queue(audioPlaylist);

        // Process the loaded playlist.
        var fullTitle = audioPlaylist.getName();
        var title = fullTitle.length() > 50
            ? fullTitle.substring(0, 50) + "..."
            : fullTitle;

        // Reply to the interaction.
        this.interaction.reply(new EmbedBuilder()
            .setColor(MessageType.INFO.getColor())
            .setDescription(String.format("**Queued Playlist:** [%s](%s)", title, this.query))
            .build(), false);
    }

    @Override
    public void noMatches() {
        this.interaction.reply(Messages.NO_RESULTS_FOUND, false);
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        LogUtil.log(exception);
        this.interaction.reply(Messages.UNABLE_TO_PLAY, false);
    }
}
