package SnakeGame;

public class Lobby {
    private String host_IP;
    private int host_port;
    private boolean master = false;

    public Lobby(boolean master){
        this.master = master;
        this.host_IP = "";
        this.host_port = 0;
    }

    public Lobby(String host_IP, int host_port, boolean master){
        this.host_IP = host_IP;
        this.host_port = host_port;
        this.master = master;
    }

    public String getHost_IP() { return host_IP; }
    public int getHost_port() { return host_port; }
    public boolean isMaster() { return master; }
}
