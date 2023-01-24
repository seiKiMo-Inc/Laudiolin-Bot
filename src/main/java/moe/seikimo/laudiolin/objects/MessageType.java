package moe.seikimo.laudiolin.objects;

import lombok.Getter;

import java.awt.*;
import java.util.Objects;

/**
 * Message types.
 */
@Getter
public enum MessageType {
    INFO(null),
    ERROR("#ed4245");

    final Color color;

    MessageType(String color) {
        this.color = Color.decode(Objects.requireNonNullElse(color, "#2771d3"));
    }
}
