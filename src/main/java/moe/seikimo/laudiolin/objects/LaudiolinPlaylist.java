package moe.seikimo.laudiolin.objects;

import lombok.Getter;

import java.util.List;

@Getter
public final class LaudiolinPlaylist {
    private String owner, id, name, description, icon;
    private boolean isPrivate;
    private List<LaudiolinTrackInfo> tracks;
}
