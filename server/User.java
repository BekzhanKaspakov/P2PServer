package server;

public class User {
    private int Port;
    private String IP;
    private boolean check;

    public User(int Port, String IP){
        this.IP = IP;
        this.Port = Port;
        this.check = false;
    }


    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String  IP) {
        this.IP = IP;
    }

    public boolean getCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        /*if (Port != user.Port){
            return false;
        }
        if (check != user.check){
            return false;
        }*/
        return IP.equals(user.IP);
    }

    @Override
    public int hashCode() {
        int result = Port;
        result = 31 * result + (IP != null ? IP.hashCode() : 0);
        return result;
    }
}
