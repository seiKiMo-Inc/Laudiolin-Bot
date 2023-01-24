package moe.seikimo.laudiolin.objects.audio;

import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import org.jetbrains.annotations.NotNull;

public final class LaudiolinVoiceInterceptor implements VoiceDispatchInterceptor {
    @Override
    public void onVoiceServerUpdate(@NotNull VoiceServerUpdate voiceServerUpdate) {

    }

    @Override
    public boolean onVoiceStateUpdate(@NotNull VoiceStateUpdate voiceStateUpdate) {
        return false;
    }
}
