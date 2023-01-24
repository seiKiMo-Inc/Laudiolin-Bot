package moe.seikimo.laudiolin.objects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum LogEvent {
    DEPLOY_COMMANDS("%s %s commands %s"),
    BACKEND_PING("An error occurred while trying to ping the backend."),
    BACKEND_QUERY_ERROR("An error occurred while trying to query the backend.");

    final String template;
}
