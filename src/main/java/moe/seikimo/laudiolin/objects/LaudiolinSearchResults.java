package moe.seikimo.laudiolin.objects;

import lombok.Getter;

import java.util.List;

@Getter
public final class LaudiolinSearchResults {
    private LaudiolinTrackInfo top;
    private List<LaudiolinTrackInfo> results;
}
