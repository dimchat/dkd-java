import chat.dim.dkd.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MessageTest {

    public static String sender = "moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk";
    public static String receiver = "hulk@4YeVEN3aUnvC1DNUufCq1bs9zoBSJTzVEj";

    @Test
    public void testEnvelope() {

        Envelope envelope = new Envelope(sender, receiver);
        Log.info("envelope:" + envelope);

        Envelope env = Envelope.getInstance(envelope);
        Assert.assertSame(envelope, env);
    }

    @Test
    public void testInstantMessage() throws ClassNotFoundException {

        Message message;

        Content content = new TextContent("Hello world!");

        InstantMessage iMsg = new InstantMessage(content, sender, receiver);
        Log.info("instant message:" + iMsg);

        message = InstantMessage.getInstance(iMsg);
        Log.info("message:" + message);
        Assert.assertSame(iMsg, message);
    }

    @Test
    public void testSecureMessage() throws NoSuchFieldException {

        byte[] data = new byte[64];

        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("sender", sender);
        dictionary.put("receiver", receiver);
        dictionary.put("data", Base64.encode(data));

        SecureMessage sMsg = SecureMessage.getInstance(dictionary);
        Log.info("secure message:" + sMsg);

        sMsg.setGroup("group12345");
        Log.info("group:" + sMsg.getGroup());

        dictionary.put("signature", Base64.encode(data));
        ReliableMessage rMsg = ReliableMessage.getInstance(dictionary);
        Log.info("reliable message:" + rMsg);
    }
}
