package server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class someFile {
    private String Type;
    private int Size;
    private Date LastModified;
    private String IP;
    private int Port;
    String Path;


    public someFile(String Type, String Path, int Size, Date LastModified, String IP, int Port){
        this.Type = Type;
        this.Size = Size;
        this.LastModified = LastModified;
        this.IP = IP;
        this. Port = Port;
        this.Path = Path;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public Date getLastModified() {
        return LastModified;
    }

    public void setLastModified(Date lastModified) {
        LastModified = lastModified;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        someFile someFile = (someFile) o;

        if (Size != someFile.Size) return false;
        if (Port != someFile.Port) return false;
        if (!Type.equals(someFile.Type)) return false;
        if (!LastModified.equals(someFile.LastModified)) return false;
        return IP.equals(someFile.IP);
    }


    public String getAll(){
        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yy");
        //System.out.println("Current Date: " + ft.format(LastModified));
        return Type + ", " + Path+ ", " + Size +", "+ ft.format(LastModified) + ", "+ IP+ ", " + Port;
    }
}
