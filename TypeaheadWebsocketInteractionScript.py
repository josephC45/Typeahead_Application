import asyncio
import websockets

async def interact_with_websocket():
    uri = 'ws://localhost:8080'
    async with websockets.connect(uri) as websocket:
        while True:
            prefix = input("Type a prefix: ")
            await websocket.send(prefix)
            popularSuggestions = await websocket.recv()
            print(f"Popular suggestions: {popularSuggestions}")

asyncio.get_event_loop().run_until_complete(interact_with_websocket())