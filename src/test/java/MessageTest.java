import chat.dim.dkd.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MessageTest {

    private void log(String msg) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String method = traces[2].getMethodName();
        int line = traces[2].getLineNumber();
        System.out.println("[" + method + ":" + line + "] " + msg);
    }

    public static String sender = "moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk";
    public static String receiver = "hulk@4YeVEN3aUnvC1DNUufCq1bs9zoBSJTzVEj";

    @Test
    public void testEnvelope() {

        Envelope envelope = new Envelope(sender, receiver);
        log("envelope:" + envelope);

        Envelope env = Envelope.getInstance(envelope);
        Assert.assertSame(envelope, env);
    }

    @Test
    public void testInstantMessage() throws ClassNotFoundException {

        Message message;

        Content content = new TextContent("Hello world!");

        InstantMessage iMsg = new InstantMessage(content, sender, receiver);
        log("instant message:" + iMsg);

        message = InstantMessage.getInstance(iMsg);
        log("message:" + message);
        Assert.assertSame(iMsg, message);
    }

    @Test
    public void testSecureMessage() throws NoSuchFieldException {

        byte[] data = new byte[64];

        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("sender", sender);
        dictionary.put("receiver", receiver);
        dictionary.put("data", Utils.base64Encode(data));

        SecureMessage sMsg = SecureMessage.getInstance(dictionary);
        log("secure message:" + sMsg);

        sMsg.setGroup("group12345");
        log("group:" + sMsg.getGroup());

        dictionary.put("signature", Utils.base64Encode(data));
        ReliableMessage rMsg = ReliableMessage.getInstance(dictionary);
        log("reliable message:" + rMsg);
    }
}
