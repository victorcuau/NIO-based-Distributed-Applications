package ricm.distsys.nio.babystep3;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Channel {
	Reader reader;
	Writer writer;
	
	Channel(SelectionKey key){
		this.writer = new Writer(key);
		this.reader = new Reader(key, writer);
	}
	
	public void handleRead() throws IOException {
		reader.handleRead();
	}
	
	public void handleWrite() throws IOException {
		writer.handleWrite();
	}
	
	public void sendMsg(byte[] msg) throws ClosedChannelException {
		writer.sendMsg(msg);
	}
	
}