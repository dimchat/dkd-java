package chat.dim.dkd.content;

import chat.dim.dkd.Utils;

import java.util.Map;

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

    public VideoContent(Map<String, Object> dictionary) {
        super(dictionary);
        Object snapshot = dictionary.get("snapshot");
        if (snapshot == null) {
            this.snapshot = null;
        } else {
            this.snapshot = Utils.base64Decode((String) snapshot);
        }
    }

    public VideoContent(byte[] data, String filename) {
        super(VIDEO, data, filename);
        this.snapshot = null;
    }

    public void setSnapshot(byte[] snapshot) {
        this.snapshot = snapshot;
        if (snapshot == null) {
            this.dictionary.remove("snapshot");
        } else {
            this.dictionary.put("snapshot", Utils.base64Encode(snapshot));
        }
    }
}
