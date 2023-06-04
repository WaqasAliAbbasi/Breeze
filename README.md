# Breeze

Breeze is a chromium extension allowing you to easily send text, images and sketches from your phone or tablet to your browser. Just scan the QR code with your phone, beam the data over and it will be available in your browser to use.

## Components

1. **Extension:** Shows a QR code, talks to the server and copies data to your clipboard once received.
1. **Server:** Acts as the gateway between your browser and phone.
1. **Website:** Allows your phone to add text, upload an image or make a sketch.

## Development

```bash
docker build . -t breeze-server-local --build-arg SERVER_HOSTNAME=http://localhost:8080
docker run --rm -p 8080:8080 breeze-server-local
```
