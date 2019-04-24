package chat.dim.dkd.content;

import chat.dim.dkd.Utils;

import java.util.Map;

/**
 *  Image message: {
 *      type : 0x12,
 *      sn   : 123,
 *
 *      URL       : "http://", // upload to CDN
 *      data      : "...",     // if (!URL) base64_encode(image)
 *      thumbnail : "...",     // base64_encode(smallImage)
 *      filename  : "..."
 *  }
 */
public class ImageContent extends FileContent {

    public byte[] thumbnail;

    public ImageContent(ImageContent content) {
        super(content);
        this.thumbnail = content.thumbnail;
    }

    public ImageContent(Map<String, Object> dictionary) {
        super(dictionary);
        Object thumbnail = dictionary.get("thumbnail");
        if (thumbnail == null) {
            this.thumbnail = null;
        } else {
            this.thumbnail = Utils.base64Decode((String) thumbnail);
        }
    }

    public ImageContent(byte[] data, String filename) {
        super(IMAGE, data, filename);
        this.thumbnail = null;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
        if (thumbnail == null) {
            this.dictionary.remove("thumbnail");
        } else {
            this.dictionary.put("thumbnail", Utils.base64Encode(thumbnail));
        }
    }
}
