package ricm.distsys.nio.babystep2;

import java.nio.ByteBuffer;

public class Writer {
	
	enum State {IDLE}; // Utile ici ???
	
	public void sendMsg(byte[] msg) {
		ByteBuffer b = ByteBuffer.wrap(msg);
	}

}