package moe.seikimo.laudiolin.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class LaudiolinAudioSender implements AudioSendHandler {
    private final AudioPlayer player;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;

    public LaudiolinAudioSender(AudioPlayer player) {
        this.player = player;
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();

        this.frame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
        return player.provide(this.frame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        ((Buffer) buffer).flip();
        return buffer;
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    public AudioPlayer getPlayer() {
        return this.player;
    }

    public MutableAudioFrame getFrame() {
        return this.frame;
    }
}
