package chat.dim.dkd.content;

import java.util.Map;

/**
 *  Audio message: {
 *      type : 0x14,
 *      sn   : 123,
 *
 *      URL      : "http://", // upload to CDN
 *      data     : "...",     // if (!URL) base64_encode(audio)
 *      text     : "...",     // Automatic Speech Recognition
 *      filename : "..."
 *  }
 */
public class AudioContent extends FileContent {

    public AudioContent(AudioContent content) {
        super(content);
    }

    public AudioContent(Map<String, Object> dictionary) {
        super(dictionary);
    }

    public AudioContent(byte[] data, String filename) {
        super(AUDIO, data, filename);
    }
}
