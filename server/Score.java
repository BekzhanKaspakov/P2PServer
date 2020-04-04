package server;

public class Score {
    int NumRequests;
    int NumUploads;

    public Score(){
        NumRequests =0;
        NumUploads = 0;
    }

    public void RequestsInc(){
        NumRequests++;
    }

    public void UploadsInc(){
        NumUploads++;
    }

    public int getNumRequests() {
        return NumRequests;
    }

    public int getNumUploads() {
        return NumUploads;
    }
}
