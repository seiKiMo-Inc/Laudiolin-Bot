package moe.seikimo.laudiolin.objects.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import lombok.Getter;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.Buffer;
import java.nio.ByteBuffer;

@Getter
public final class LaudiolinAudioSender implements AudioSendHandler {
    private final AudioPlayer player;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;

    public LaudiolinAudioSender(AudioPlayer player) {
        this.player = player;
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();

        this.frame.setBuffer(this.buffer);
    }

    @Override
    public boolean canProvide() {
        return player.provide(this.frame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        ((Buffer) this.buffer).flip();
        return this.buffer;
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
