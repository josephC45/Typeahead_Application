package com.typeahead.trie_microservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "websockethandler")
class WebsocketProperties {

    private String handlerPath;
    private String handlerOrigins;

    String getHandlerPath() {
        return handlerPath;
    }

    void setHandlerPath(String handlerPath) {
        this.handlerPath = handlerPath;
    }

    String getHandlerOrigins() {
        return handlerOrigins;
    }

    void setHandlerOrigins(String handlerOrigins) {
        this.handlerOrigins = handlerOrigins;
    }

}
