package chat.dim.dkd.content;

import chat.dim.dkd.Utils;

import java.util.HashMap;

/**
 *  File message: {
 *      type : 0x10,
 *      sn   : 123,
 *
 *      URL      : "http://", // upload to CDN
 *      data     : "...",     // if (!URL) base64_encode(fileContent)
 *      filename : "..."
 *  }
 */
public class FileContent extends Content {

    public String url;
    public byte[] data;
    public String filename;

    public HashMap<String, Object> password;

    public FileContent(FileContent content) {
        super(content);
        this.url      = content.url;
        this.data     = content.data;
        this.filename = content.filename;
        this.password = content.password;
    }

    public FileContent(HashMap<String, Object> dictionary) {
        super(dictionary);
        this.url = (String) dictionary.get("URL");
        this.data = null; // NOTICE: file data should not exists here
        this.filename = (String) dictionary.get("filename");
        this.password = (HashMap<String, Object>) dictionary.get("password");
    }

    public FileContent(int type, byte[] data, String filename) {
        super(type);
        setUrl(null);
        setData(data);
        setFilename(filename);
        setPassword(null);
    }

    public FileContent(byte[] data, String filename) {
        this(FILE, data, filename);
    }

    public void setUrl(String url) {
        this.url = url;
        this.dictionary.put("URL", url);
    }

    public void setData(byte[] data) {
        this.data = data;
        // NOTICE: do not set file data in dictionary, which will be post onto the DIM network
    }

    public void setFilename(String filename) {
        this.filename = filename;
        this.dictionary.put("filename", filename);
    }

    public void setPassword(HashMap<String, Object> password) {
        this.password = password;
        this.dictionary.put("password", password);
    }
}
