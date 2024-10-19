package com.typeahead.trie_microservice.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.typeahead.trie_microservice.domain.TrieService;
import com.typeahead.trie_microservice.infrastructure.KafkaProducerService;

@ExtendWith(MockitoExtension.class)
public class WebsocketServiceImplTests {

    @Mock
    private TrieService trieService;

    @Mock
    private KafkaProducerService kafkaService;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private WebsocketServiceImpl websocketService;

    @Test
    void givenValidPrefix_whenWebsocketQueriesTrie_shouldReturnPopularPrefixesToClient() throws IOException {
        List<String> mockPrefixes = Arrays.asList("testing", "testosterone");
        when(trieService.getPopularPrefixes("test")).thenReturn(mockPrefixes);

        websocketService.queryTrie(session, "test");
        verify(trieService).getPopularPrefixes("test");

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        TextMessage sentMessage = captor.getValue();
        assertEquals("testing,testosterone", sentMessage.getPayload());
    }
}
