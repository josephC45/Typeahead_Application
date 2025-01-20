package com.typeahead.trie_microservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "websockethandler")
public class WebSocketProperties {

    private String handlerPath;
    private String handlerOrigins;

    public String getHandlerPath() {
        return handlerPath;
    }

    public void setHandlerPath(String handlerPath) {
        this.handlerPath = handlerPath;
    }

    public String getHandlerOrigins() {
        return handlerOrigins;
    }

    public void setHandlerOrigins(String handlerOrigins) {
        this.handlerOrigins = handlerOrigins;
    }

}
