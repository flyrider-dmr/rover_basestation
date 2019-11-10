package Comm;

import java.io.Serializable;

public class SocketConnectionParameter implements Serializable
{
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	private static final long serialVersionUID = 1L;
	String host;
	int port;

}
