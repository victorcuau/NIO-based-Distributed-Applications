package ricm.channels.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import ricm.channels.IBroker;
import ricm.channels.IBrokerListener;
import ricm.channels.IChannelListener;

public class Broker implements IBroker {
	
	public IBrokerListener l;
	IChannelListener lc;
	int port;
	String host;
	Selector selector;
	
	ServerSocketChannel ssc;
	
	ByteBuffer buffLen = ByteBuffer.allocate(4);
	ByteBuffer buffData;
	int size;
	
	// The message to send to the server
	byte[] first;
	byte[] digest;
	int nloops;
	
	public Broker() {

	}

	public void setListener(IBrokerListener l) {
		this.l = l;
	}

	public boolean connect(String host, int port) {
		try {
			// create a new selector
			selector = SelectorProvider.provider().openSelector();

			// create a new non-blocking server socket channel
			SocketChannel sc = SocketChannel.open();
			sc = SocketChannel.open();
			sc.configureBlocking(false);

			// register an connect interested in order to get a
			// connect event, when the connection will be established
			sc.register(selector, SelectionKey.OP_CONNECT);

			// request a connection to the given server and port
			InetAddress addr;
			addr = InetAddress.getByName(host);
			sc.connect(new InetSocketAddress(addr, port));
		}
		catch (IOException e) {
			l.refused("localhost", port);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public boolean accept(int port) {
		try {
				// create a new selector
					selector = SelectorProvider.provider().openSelector();

					// create a new non-blocking server socket channel
					ssc = ServerSocketChannel.open();
					ssc.configureBlocking(false);

					// bind the server socket to the given address and port
					InetAddress hostAddress;
					hostAddress = InetAddress.getByName("localhost");
					InetSocketAddress isa = new InetSocketAddress(hostAddress, port);
					ssc.socket().bind(isa);

					// be notified when connection requests arrive
					ssc.register(selector, SelectionKey.OP_ACCEPT);

		}
		catch (IOException e) {
			l.refused("localhost", port);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public void loop() throws IOException {
		System.out.println("NioServer running");
		while (true) {
			selector.select();

			Iterator<?> selectedKeys = this.selector.selectedKeys().iterator();

			while (selectedKeys.hasNext()) {

				SelectionKey key = (SelectionKey) selectedKeys.next();
				selectedKeys.remove();

				if (key.isValid()) {
					if (key.isAcceptable())
						handleAccept(key);
					
					if (key.isReadable())
						((Channel)(key.attachment())).handleRead();
					
					if (key.isWritable())
						((Channel)(key.attachment())).handleWrite();
					
					if (key.isConnectable())
						handleConnect(key);
				}
			}
		}
	}
	
	public void handleAccept(SelectionKey key) throws IOException {
		SocketChannel sc;

		// do the actual accept on the server-socket channel
		sc = ssc.accept();
		sc.configureBlocking(false);

		// register the read interest for the new socket channel
		// in order to know when there are bytes to read
		SelectionKey k = sc.register(this.selector, SelectionKey.OP_READ);
		Channel channel = new Channel(k);
		channel.setListener(lc);
		k.attach(channel);
		l.accepted(channel);
	}
	
	public void handleConnect(SelectionKey key) throws IOException {
		SocketChannel sc = (SocketChannel)(key.channel());
		sc.finishConnect();
		key.interestOps(SelectionKey.OP_READ);
		
		Channel channel = new Channel(key);
		channel.setListener(lc);
		key.attach(channel);
		l.connected(channel);
	}

}