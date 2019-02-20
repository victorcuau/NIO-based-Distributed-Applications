package ricm.distsys.nio.babystep2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Reader {
	
	enum State {READ_LENGTH, READ_PAYLOAD};
	State state;
	ByteBuffer buffLen;
	ByteBuffer buffData;
	SocketChannel sc;
	SelectionKey key;
	
	int size;
	
	Writer writer;
	
	Reader(SocketChannel sc, SelectionKey key, Writer writer){
		this.sc = sc;
		this.key = key;
		this.writer = writer;
		buffLen = ByteBuffer.allocate(4);
		state = State.READ_LENGTH;
	}
	
	public void handleRead() throws IOException {
		if (state == State.READ_LENGTH) {
			sc.read(buffLen);
			if (buffLen.remaining() == 0) {
				buffLen.rewind();
				size = buffLen.getInt();
				System.out.println("READER : size = " + size);
				buffData = ByteBuffer.allocate(size);
				state = State.READ_PAYLOAD;
			}
		}
		else if (state == State.READ_PAYLOAD) {
			sc.read(buffData);
			if (buffData.remaining() == 0) {
				processMsg(new byte[size]);
				state = State.READ_LENGTH;
				//key.interestOps(SelectionKey.OP_WRITE);
			}
			
		}
	}
	
	public void processMsg(byte[] msg) {
		buffData.get(msg, 0, size);
		System.out.println("READER : Le message complet a été reçu.");
		writer.sendMsg(msg);
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
}
