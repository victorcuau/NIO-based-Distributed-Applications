package ricm.channels.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import ricm.channels.IChannel;
import ricm.channels.IChannelListener;

public class Channel implements IChannel {
	
	static int chunk = 10;
	
	Reader reader;
	Writer writer;
	
	IChannelListener listener;
	boolean closed;
	
	Channel(SelectionKey key){
		this.writer = new Writer(key);
		this.reader = new Reader(key, writer);
		closed = false;
	}
	
	public void handleRead() throws IOException {
		reader.handleRead();
	}
	
	public void handleWrite() throws IOException {
		writer.handleWrite();
	}

	public void setListener(IChannelListener l) {
		listener = l;
		reader.l = l;
	}

	public void send(byte[] bytes, int offset, int count) throws IllegalStateException {
//		byte[] tmp = new byte[count];
//		System.arraycopy(bytes, offset, tmp, 0, count);
//		writer.sendMsg(tmp);
		
		byte[] tmp = new byte[chunk];
		
		if (count < chunk) {
			System.arraycopy(bytes, offset, tmp, 0, count);
			writer.sendMsg(tmp);
		}
		else {
			int reste = count;
			int i = 0; // BUG ICI ?
			while (i*chunk < count) {
				System.arraycopy(bytes, offset, tmp, 0, Math.min(chunk, reste));
				writer.sendMsg(tmp);
				i++;
				reste = reste - Math.min(chunk, reste);
				System.out.println("Reste " + reste + " à envoyer.");
			}
		}
		
	}
	
	public void send(byte[] msg) {
			writer.sendMsg(msg);
	}

	public void close() {
		if (!closed) {
			closed = true;
			listener.closed(this, null);
		}
	}

	public boolean closed() {
		return closed;
	}

}