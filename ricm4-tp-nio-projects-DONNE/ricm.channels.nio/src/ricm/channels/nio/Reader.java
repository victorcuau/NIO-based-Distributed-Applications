package ricm.channels.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ricm.channels.IChannelListener;

public class Reader {
	
	enum State {READ_LENGTH, READ_PAYLOAD};
	State state;
	ByteBuffer buffLen;
	ByteBuffer buffData;
	SocketChannel sc;
	SelectionKey key;
	IChannelListener l;
	
	int size;
	
	Writer writer;
	
	Reader(SelectionKey key, Writer writer){
		this.key = key;
		this.sc = (SocketChannel)(key.channel());
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
				buffLen.rewind();
				state = State.READ_PAYLOAD;
			}
		}
		else if (state == State.READ_PAYLOAD) {
			sc.read(buffData);
			if (buffData.remaining() == 0) {
				processMsg(new byte[size]);
				state = State.READ_LENGTH;
			}
		}
	}
	
	public void processMsg(byte[] msg) throws ClosedChannelException {
		buffData.rewind();
		buffData.get(msg, 0, size);
		System.out.println("READER : Le message complet a été reçu.");
		writer.sendMsg(msg);
		l.received((Channel)key.attachment(), msg);
	}
	
}
