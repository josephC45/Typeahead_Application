package com.typeahead.trie_microservice.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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

    private StringBuilder wordTyped;

    @BeforeEach()
    public void setup(){
        wordTyped = new StringBuilder();
    }

    @Test
    void givenValidPrefix_whenWebsocketQueriesTrie_shouldReturnPopularPrefixesToClient() throws IOException {
        List<String> mockPrefixes = Arrays.asList("testing", "testosterone");
        lenient().when(trieService.getPopularPrefixes("test")).thenReturn(mockPrefixes);

        // Simulate typing characters to form the word "test"
        websocketService.queryTrie(session, "t");
        websocketService.queryTrie(session, "e");
        websocketService.queryTrie(session, "s");
        websocketService.queryTrie(session, "t");

        verify(trieService).getPopularPrefixes("test");

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, atLeastOnce()).sendMessage(captor.capture());
        TextMessage sentMessage = captor.getAllValues().get(captor.getAllValues().size() - 1);
        assertEquals("testing,testosterone", sentMessage.getPayload());
    }

    @Test
    void whenEndOfWordCharacterIsTyped_shouldSendWordToKafka() throws Exception{
        websocketService.queryTrie(session, "t");
        websocketService.queryTrie(session, "e");
        websocketService.queryTrie(session, "s");
        websocketService.queryTrie(session, "t");
        
        // Simulate pressing a space (end of word)
        websocketService.queryTrie(session, " ");
        
        verify(kafkaService).sendMessageToKafka("test");
        assertEquals(0, wordTyped.length());
    }
}
