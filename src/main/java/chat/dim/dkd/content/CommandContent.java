package chat.dim.dkd.content;

import java.util.HashMap;

/**
 *  Command message: {
 *      type : 0x88,
 *      sn   : 123,
 *
 *      command : "...", // command name
 *      extra   : info   // command parameters
 *  }
 */
public class CommandContent extends Content {

    //-------- command names begin --------
    public static final String HANDSHAKE = "handshake";
    public static final String BROADCAST = "broadcast";
    public static final String RECEIPT   = "receipt";
    public static final String META      = "meta";
    public static final String PROFILE   = "profile";
    //-------- command names end --------

    public String command;

    public CommandContent(CommandContent content) {
        super(content);
        this.command = content.command;
    }

    public CommandContent(HashMap<String, Object> dictionary) {
        super(dictionary);
        this.command = (String) dictionary.get("command");
    }

    public CommandContent(int type, String command) {
        super(type);
        setCommand(command);
    }

    public CommandContent(String command) {
        this(COMMAMD, command);
    }

    public void setCommand(String command) {
        this.command = command;
        this.dictionary.put("command", command);
    }
}
