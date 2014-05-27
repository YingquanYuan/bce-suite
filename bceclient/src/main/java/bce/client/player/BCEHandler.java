package bce.client.player;

import java.io.IOException;

public interface BCEHandler {

	void handleResponse(byte[] responseData) throws IOException;
}
