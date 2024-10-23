import asyncio
import websockets
import keyboard

async def send_keyboard_input(websocket):
    print("Type a prefix (press ESC to exit): ")
    while(True):
        key_event = keyboard.read_event()

        if(key_event.event_type == keyboard.KEY_DOWN):
            if(key_event.name == "esc"):
                print("\nExiting websocket...")
                break;
            
            await websocket.send(key_event.name)

async def receive_suggestions(websocket):
    try:
        while(True):
            popular_suggestions = await websocket.recv()
            print(f"\nPopular suggestions: {popular_suggestions}")
    except websockets.ConnectionClosed:
        print("Websocket connection closed.")

async def interact_with_websocket():
    uri = 'ws://localhost:8080/ws'
    async with websockets.connect(uri) as websocket:
        await asyncio.gather(
            send_keyboard_input(websocket),
            receive_suggestions(websocket)
        )

asyncio.run(interact_with_websocket())