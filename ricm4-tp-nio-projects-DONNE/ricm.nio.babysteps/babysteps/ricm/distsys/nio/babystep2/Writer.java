package ricm.distsys.nio.babystep2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Writer {
	
	enum State {WRITE_LENGTH, WRITE_PAYLOAD};
	State state;
	ByteBuffer buffLen;
	ByteBuffer buffData;
	SocketChannel sc;
	SelectionKey key;
	
	//ArrayList<ByteBuffer> messages;
	
	Writer(SocketChannel sc, SelectionKey key){
		this.sc = sc;
		this.key = key;
		buffLen = ByteBuffer.allocate(4);
		state = State.WRITE_LENGTH;
	}

	public void handleWrite() throws IOException {
		if (state == State.WRITE_LENGTH) {
			sc.write(buffLen);
			if (buffLen.remaining() == 0) {
				state = State.WRITE_PAYLOAD;
			}
		}
		else if (state == State.WRITE_PAYLOAD) {
			sc.write(buffData);
			if (buffData.remaining() == 0) {
				state = State.WRITE_LENGTH;
				key.interestOps(SelectionKey.OP_READ);
			}
			
		}
	}
	
	public void sendMsg(byte[] msg) {
		buffData = ByteBuffer.wrap(msg, 0, msg.length);
		System.out.println("WRITER : Le message complet a été envoyé.");
		buffLen.putInt(msg.length);
		buffLen.rewind();
		System.out.println("WRITER : msg.length = " + msg.length);
		// messages.add(buffData);

		key.interestOps(SelectionKey.OP_WRITE);
	}
	
}