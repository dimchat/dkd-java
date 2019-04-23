package chat.dim.dkd.content;

import chat.dim.dkd.Utils;

import java.util.HashMap;

/**
 *  Video message: {
 *      type : 0x16,
 *      sn   : 123,
 *
 *      URL      : "http://", // upload to CDN
 *      data     : "...",     // if (!URL) base64_encode(video)
 *      snapshot : "...",     // base64_encode(smallImage)
 *      filename : "..."
 *  }
 */
public class VideoContent extends FileContent {

    public byte[] snapshot;

    public VideoContent(VideoContent content) {
        super(content);
        this.snapshot = content.snapshot;
    }

    public VideoContent(HashMap<String, Object> dictionary) {
        super(dictionary);
        String snapshot = (String) dictionary.get("snapshot");
        if (snapshot != null) {
            this.snapshot = Utils.base64Decode(snapshot);
        } else {
            this.snapshot = null;
        }
    }

    public VideoContent(byte[] data, String filename) {
        super(VIDEO, data, filename);
        this.snapshot = null;
    }

    public void setSnapshot(byte[] snapshot) {
        this.snapshot = snapshot;
        if (snapshot != null && snapshot.length > 0) {
            this.dictionary.put("snapshot", Utils.base64Encode(snapshot));
        } else {
            this.dictionary.remove("snapshot");
        }
    }
}
