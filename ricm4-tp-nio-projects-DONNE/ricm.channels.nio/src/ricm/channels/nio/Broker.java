package ricm.channels.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

import ricm.channels.IBroker;
import ricm.channels.IBrokerListener;

public class Broker implements IBroker {
	
	
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
			sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		
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

	
	//Les 4 handle
			// 
			// handle read et write depuis reader et writer
	//createchannel
	//Setlistener

}