import keyboard
import pytest
from unittest.mock import AsyncMock, patch

from websockets.exceptions import ConnectionClosed
from typeahead_websocket_interaction import send_keyboard_input, receive_suggestions 

@pytest.mark.asyncio
async def test_send_keyboard_input():
    
    mock_websocket = AsyncMock()

    with patch('keyboard.read_event') as mock_read_event:

        mock_read_event.side_effect = [
            keyboard.KeyboardEvent(event_type="down", name="a", scan_code=0),
            keyboard.KeyboardEvent(event_type="down", name="b", scan_code=0),
            keyboard.KeyboardEvent(event_type="down", name="esc", scan_code=0)
        ]
    
        await send_keyboard_input(mock_websocket)

        mock_websocket.send.assert_any_call("a")
        mock_websocket.send.assert_any_call("b")

        assert mock_websocket.send.call_count == 2

@pytest.mark.asyncio
async def test_negative_receive_suggestions():

    mock_websocket = AsyncMock()
    mock_websocket.recv.side_effect = RuntimeError("Simulated connection closed")

    with pytest.raises(RuntimeError) as exception:
        await mock_websocket.recv()
    
    assert "Simulated connection closed" in str(exception.value)

@pytest.mark.asyncio
async def test_receive_suggestions():

    mock_websocket = AsyncMock()
    mock_websocket.recv.side_effect = [
        "suggestion",
        StopAsyncIteration
    ]

    try:
        await receive_suggestions(mock_websocket)
    except StopAsyncIteration:
        pass

    assert mock_websocket.recv.call_count == 2
    assert mock_websocket.recv.call_args_list[0][0] == ()