package it.ipramodsinghrawat.aurids;

public class IntrusionNotification {
    String key;
    String details;
    String faceFile;
    String faceFrameFile;
    String notification;
    String timestamp;

    IntrusionNotification(String key, String details,String faceFile,String faceFrameFile,
                          String notification,String timestamp){
        this.key = key;
        this.details = details;
        this.faceFile = faceFile;
        this.faceFrameFile = faceFrameFile;
        this.notification = notification;
        this.timestamp = timestamp;
    }

}
