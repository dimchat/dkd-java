package chat.dim.dkd.content;

import chat.dim.dkd.Utils;

import java.util.HashMap;

/**
 *  Web Page message: {
 *      type : 0x20,
 *      sn   : 123,
 *
 *      URL   : "https://github.com/moky/dimp", // Page URL
 *      icon  : "...",                          // base64_encode(icon)
 *      title : "...",
 *      desc  : "..."
 *  }
 */
public class PageContent extends Content {

    public String url;
    public String title;
    public String desc;
    public byte[] icon;

    public PageContent(PageContent content) {
        super(content);
        this.url   = content.url;
        this.title = content.title;
        this.desc  = content.desc;
        this.icon  = content.icon;
    }

    public PageContent(HashMap<String, Object> dictionary) {
        super(dictionary);
        this.url   = (String) dictionary.get("URL");
        this.title = (String) dictionary.get("title");
        this.desc  = (String) dictionary.get("desc");
        Object icon = dictionary.get("icon");
        if (icon != null) {
            this.icon = Utils.base64Decode((String) icon);
        } else {
            this.icon = null;
        }
    }

    public PageContent(String url, String title, String desc, byte[] icon) {
        super(PAGE);
        setURL(url);
        setTitle(title);
        setDesc(desc);
        setIcon(icon);
    }

    public void setURL(String url) {
        this.url = url;
        this.dictionary.put("URL", url);
    }

    public void setTitle(String title) {
        this.title = title;
        this.dictionary.put("title", title);
    }

    public void setDesc(String desc) {
        this.desc = desc;
        this.dictionary.put("desc", desc);
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
        if (icon != null && icon.length > 0) {
            this.dictionary.put("icon", Utils.base64Encode(icon));
        } else {
            this.dictionary.remove("icon");
        }
    }
}
