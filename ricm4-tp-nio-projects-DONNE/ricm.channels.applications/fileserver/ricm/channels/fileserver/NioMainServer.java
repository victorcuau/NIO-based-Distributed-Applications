package ricm.channels.fileserver;

import java.io.IOException;

import ricm.channels.nio.Broker;

public class NioMainServer {
	static Broker bs;
	static FileServer fs;
	static String folder = "echo";
	static int port = 1234;
	
	public static void main(String[] args) throws Exception, IOException {
		bs = new Broker();
		fs = new FileServer(bs,folder,port);
		bs.setListener(bs.l);
		bs.loop();
	}
}