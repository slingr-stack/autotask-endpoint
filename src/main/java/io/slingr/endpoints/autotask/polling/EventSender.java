package io.slingr.endpoints.autotask.polling;

import io.slingr.endpoints.utils.Json;

public interface EventSender {
    void sendEvent(String eventName, Json data);
}
