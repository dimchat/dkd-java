
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import chat.dim.dkd.*;

public class MessageTest {

    private static String sender = "gsp-s001@x5Zh9ixt8ECr59XLye1y5WWfaX4fcoaaSC";
    private String receiver = "pony@4Zc1ax3bVBMm4AXky8rmj6pGn8Hmb8pn5C";
    private long time = 1561195412L;
    private String data = "4j92H0lmiRrHcf4uQK3hwLSarmD9s2wY6b8Kt/gbNiOA7K/qst9W7kYAjkXMRzlCZ/IqMAy97nhvPKzzIuorkudpWDLAOWIVeoRTkDop+JTzReiXVh51zRtcXgVTmmaGfpU/STGhMC1f9L+mzc9WjA==";
    private String key = "h1964ruibFD4o9B2Oye//Ycer9xjC7T8oNSNriEP1k2AQJ6c2hvL7Lqvmb/NPqRJ9wdqC2RRUeIIYKamb6IwN6+k6iyy+qJ18Yawtz/kBLn0aHgIt/Ujo9W6jo9KpC6f/rWtnHyMW/wdfSlufvGhE1WZxApmNVPCoSvzoLeRidw=";
    private String signature = "glwiQAP9HQ08iKK6DZM3aL1qnYUNYFl0nyZLuw77YLTXBVc3/mw/TnlDpEBtiTS9kvk85ucGoe2uMAg6CMfQ+256TPSmYitCmwZ+rTM2EYnjA1bS04Po3PPtnmlpIVKgKNNEseUe8uIRMqnhPsIgUu3SCM/FxnMD/hhfCKu9hu8=";

    private Transceiver transceiver = new Transceiver();

    @Test
    public void testEnvelope() {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("sender", sender);
        dictionary.put("receiver", receiver);
        dictionary.put("time", time);
        Envelope env = Envelope.getInstance(dictionary);

        Log.info("envelope: " + env);
        Log.info("sender: " + env.sender);
        Log.info("receiver: " + env.receiver);
        Log.info("time: " + env.time);
    }

    @Test
    public void testContent() {

        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("type", 9527);
        dictionary.put("sn", 123412341234L);
        dictionary.put("text", "Hello world!");

        Content content = Content.getInstance(dictionary);

        Log.info("content: "+ content);
        Log.info("type: " + content.type);
        Log.info("sn: " + content.serialNumber);
        Log.info("group: " + content.getGroup());

        content = new TextContent("Hello world!");
        content.setGroup("Group-12345@qq.com");

        Log.info("content: "+ content);
        Log.info("type: " + content.type);
        Log.info("sn: " + content.serialNumber);
        Log.info("group: " + content.getGroup());
    }

    @Test
    public void testInstantMessage() {

        Content content = new TextContent("Hello world!");

        InstantMessage iMsg;
        iMsg = new InstantMessage(content, null, null);
        Log.info("instant message: " + iMsg);
        iMsg = new InstantMessage(content, sender, receiver, null);
        Log.info("instant message: " + iMsg);
        iMsg = new InstantMessage(content, iMsg.envelope);
        Log.info("instant message: " + iMsg);

        iMsg = new InstantMessage(content, sender, receiver, 0);
        Log.info("instant message: " + iMsg);
        Log.info("envelope: " + iMsg.envelope);
        Log.info("content: " + iMsg.content);

        InstantMessage message = InstantMessage.getInstance(iMsg);
        Log.info("message: " + message);
        Assert.assertSame(iMsg, message);

//        Map dictionary = (Map) message.get("content");
        Content cnt = Content.getInstance(new HashMap<>(content));
        Assert.assertEquals(cnt.get("text"), ((TextContent) content).getText());

        iMsg.delegate = transceiver;
    }

    @Test
    public void testSecureMessage() {

        byte[] decode = Base64.decode(data);
        String encode = Base64.encode(decode);
        Assert.assertEquals(data, encode);

        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("sender", sender);
        dictionary.put("receiver", receiver);
        dictionary.put("data", data);
        dictionary.put("key", key);

        SecureMessage sMsg = SecureMessage.getInstance(dictionary);
        Log.info("secure message: " + sMsg);

        sMsg.delegate = transceiver;
        byte[] key = sMsg.getKey();
        Map keys = sMsg.getKeys();

        sMsg.setGroup("group12345");
        Log.info("group: " + sMsg.getGroup());

//        InstantMessage iMsg = sMsg.decrypt();
//        Log.info("instant msg: " + iMsg);

        dictionary.put("signature", signature);
        ReliableMessage rMsg = ReliableMessage.getInstance(dictionary);
        Log.info("reliable message: " + rMsg);

        rMsg.delegate = transceiver;
        byte[] signature = rMsg.getSignature();

        Map<String, Object> meta = rMsg.getMeta();
        Log.info("meta: " + meta);

        SecureMessage sMsg2 = rMsg.verify();
        Log.info("secure message: " + sMsg2);

        rMsg.setMeta(meta);
    }

    @Test
    public void testReliableMessage() {

        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("sender", sender);
        dictionary.put("receiver", receiver);
        dictionary.put("time", time);
        dictionary.put("data", data);
        dictionary.put("key", key);
        dictionary.put("signature", signature);

        ReliableMessage msg = (ReliableMessage) Message.getInstance(dictionary);
        Log.info("reliable message: " + msg);

        List<Object> members = new ArrayList<>();
        List<SecureMessage> messages = msg.split(members);
        Log.info("split: " + messages);

        SecureMessage sMsg = msg.trim(receiver);
        Log.info("trim: " + sMsg);
    }

    static {
        Base64.coder = new BaseCoder() {

            @Override
            public String encode(byte[] data) {
                return java.util.Base64.getEncoder().encodeToString(data);
            }

            @Override
            public byte[] decode(String string) {
                return java.util.Base64.getDecoder().decode(string);
            }
        };
    }
}
