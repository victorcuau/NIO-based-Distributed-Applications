package ricm.distsys.nio.babystep3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Reader {
	
	enum State {READ_LENGTH, READ_PAYLOAD};
	State state;
	ByteBuffer buffLen;
	ByteBuffer buffData;
	SocketChannel sc;
	
	int size;
	
	Writer writer;
	
	Reader(SocketChannel sc, Writer writer){
		this.sc = sc;
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
		
		// On double la taille du message à chaque échange.
		String new_msg = new String(msg, Charset.forName("UTF-8"));
		new_msg = new_msg + new_msg;
		if(new_msg.length() < Math.pow(2, 24)) {
			writer.sendMsg(new_msg.getBytes());
		}
		else {
			System.out.println("READER : La taille maximale a été atteinte. On arrête d'envoyer des messages.");
		}
	}
	
}
