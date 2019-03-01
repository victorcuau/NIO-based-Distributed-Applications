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
import ricm.channels.nio.Reader.State;

public class Broker implements IBroker {
	
	enum State {READ_LENGTH, READ_PAYLOAD, WRITE_LENGTH, WRITE_PAYLOAD};
	State state;
	private Selector selector;
	private SocketChannel sc;
	private ServerSocketChannel ssc;
	private SelectionKey scKey;
	ByteBuffer buffLen;
	ByteBuffer buffData;
	int size;
	
	public Broker() {
		
	}

	public void setListener(IBrokerListener l) {
		
	}

	// Préparer les choses pour pouvoir recevoir l'événement
	public boolean connect(String host, int port) {
		

		return false;
	}

	//Préparer les choses pour pouvoir recevoir l'événement
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
					scKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

		}
		catch (IOException e) {
		
		}
		
		return false;
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
						handleRead(key);
					
					if (key.isWritable())
						handleWrite(key);
					
					if (key.isConnectable())
						handleConnect(key);
				}
			}
		}
	}
	
	public void handleAccept(SelectionKey key) throws IOException {
		
	}
	
	public void handleConnect(SelectionKey key) throws IOException {
		
	}
	
	public void handleRead(SelectionKey key) throws IOException {
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
	
	public void handleWrite(SelectionKey key) throws IOException {
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

	//Les 4 handle
			// 
			// handle read et write depuis reader et writer
	//createchannel
	//Setlistener

}