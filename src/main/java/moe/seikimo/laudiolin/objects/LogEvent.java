package moe.seikimo.laudiolin.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum LogEvent {
    DEPLOY_COMMANDS("%s %s commands %s");

    final String template;
}
