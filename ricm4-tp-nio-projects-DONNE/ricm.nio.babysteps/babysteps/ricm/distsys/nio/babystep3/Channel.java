package ricm.distsys.nio.babystep3;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Channel {
	SocketChannel sc;
	Selector selector;
	Reader reader;
	Writer writer;
	
	Channel(SocketChannel sc, Selector selector){
		this.sc = sc;
		this.selector = selector;
		this.writer = new Writer(sc, selector);
		this.reader = new Reader(sc, writer);
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