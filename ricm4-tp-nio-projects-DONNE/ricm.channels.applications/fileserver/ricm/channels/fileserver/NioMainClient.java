package ricm.channels.fileserver;

import java.io.IOException;

import ricm.channels.nio.Broker;

public class NioMainClient {
	static Broker bc;
	static FileDownloader fd;
	
	public static void main(String[] args) throws Exception, IOException {
		bc = new Broker();
		fd = new FileDownloader(bc);
		bc.setListener(bc.l);
		fd.download("localhost", 1234, "echo/ricm/channels/echo/EchoClient.java", true);
		bc.loop();
	}
}