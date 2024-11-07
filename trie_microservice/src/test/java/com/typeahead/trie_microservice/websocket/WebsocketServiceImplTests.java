package com.typeahead.trie_microservice.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
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
import com.typeahead.trie_microservice.exception.KafkaException;
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
    public void setup() {
        wordTyped = new StringBuilder();
    }

    @Test
    void whenSendResponseToClientCalled_shouldSendMessageSuccessfully() throws IOException {
        websocketService.sendResponseToClient(session, new TextMessage("test"));

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, atLeastOnce()).sendMessage(captor.capture());

        TextMessage sentMessage = captor.getValue();
        assertEquals("test", sentMessage.getPayload());
    }

    @Test
    void whenSendResponseToClientThrowsIOException_shouldHandleException() throws IOException {
        doThrow(new IOException("Failed to send to websocket client")).when(session).sendMessage(new TextMessage("t"));
        websocketService.sendResponseToClient(session, new TextMessage("t"));
        verify(session).sendMessage(any(TextMessage.class));
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
    void whenEndOfWordCharacterIsTyped_shouldSendWordToKafka() throws Exception {
        websocketService.queryTrie(session, "t");
        websocketService.queryTrie(session, "e");
        websocketService.queryTrie(session, "s");
        websocketService.queryTrie(session, "t");

        // Simulate pressing a space (end of word)
        websocketService.queryTrie(session, " ");

        verify(kafkaService).sendMessageToKafka("test");
        assertEquals(0, wordTyped.length());
    }

    @Test
    void whenQueryTrieThrowsKafkaException_shouldHandleException() throws Exception {
        doThrow(new KafkaException("Kafka exception occurred")).when(kafkaService).sendMessageToKafka("test");

        websocketService.queryTrie(session, "t");
        websocketService.queryTrie(session, "e");
        websocketService.queryTrie(session, "s");
        websocketService.queryTrie(session, "t");

        // Simulate pressing a space (end of word)
        websocketService.queryTrie(session, " ");

        assertThrows(KafkaException.class, () -> kafkaService.sendMessageToKafka("test"));

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, atLeastOnce()).sendMessage(captor.capture());

        TextMessage sentMessage = captor.getValue();
        assertEquals("Error sending data to Kafka.", sentMessage.getPayload());

        assertEquals(0, wordTyped.length());
    }
}
