
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.format.Base64;
import chat.dim.Entity;
import chat.dim.Transceiver;
import chat.dim.dkd.EncryptedMessage;
import chat.dim.dkd.PlainMessage;
import chat.dim.dkd.RelayMessage;
import chat.dim.protocol.ID;
import chat.dim.protocol.InstantMessage;
import chat.dim.protocol.Meta;
import chat.dim.protocol.SecureMessage;
import chat.dim.protocol.TextContent;

public class MessageTest {

    private static ID sender = Entity.parseID("gsp-s001@x5Zh9ixt8ECr59XLye1y5WWfaX4fcoaaSC");
    private ID receiver = Entity.parseID("pony@4Zc1ax3bVBMm4AXky8rmj6pGn8Hmb8pn5C");
    private long time = 1561195412L;
    private String data = "4j92H0lmiRrHcf4uQK3hwLSarmD9s2wY6b8Kt/gbNiOA7K/qst9W7kYAjkXMRzlCZ/IqMAy97nhvPKzzIuorkudpWDLAOWIVeoRTkDop+JTzReiXVh51zRtcXgVTmmaGfpU/STGhMC1f9L+mzc9WjA==";
    private String key = "h1964ruibFD4o9B2Oye//Ycer9xjC7T8oNSNriEP1k2AQJ6c2hvL7Lqvmb/NPqRJ9wdqC2RRUeIIYKamb6IwN6+k6iyy+qJ18Yawtz/kBLn0aHgIt/Ujo9W6jo9KpC6f/rWtnHyMW/wdfSlufvGhE1WZxApmNVPCoSvzoLeRidw=";
    private String signature = "glwiQAP9HQ08iKK6DZM3aL1qnYUNYFl0nyZLuw77YLTXBVc3/mw/TnlDpEBtiTS9kvk85ucGoe2uMAg6CMfQ+256TPSmYitCmwZ+rTM2EYnjA1bS04Po3PPtnmlpIVKgKNNEseUe8uIRMqnhPsIgUu3SCM/FxnMD/hhfCKu9hu8=";

    private Transceiver transceiver = new Transceiver();

    @Test
    public void testInstantMessage() {

        TextContent content = new TextContent("Hello world!");

        PlainMessage iMsg;
        iMsg = new PlainMessage(content, null, null);
        Log.info("instant message: " + iMsg);
        iMsg = new PlainMessage(content, sender, receiver, null);
        Log.info("instant message: " + iMsg);
        iMsg = new PlainMessage(content, iMsg.getEnvelope());
        Log.info("instant message: " + iMsg);

        iMsg = new PlainMessage(content, sender, receiver, 0);
        Log.info("instant message: " + iMsg);
        Log.info("envelope: " + iMsg.getEnvelope());
        Log.info("content: " + iMsg.getContent());

        Assert.assertEquals(content.get("text"), content.getText());

        iMsg.setDelegate(transceiver);
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

        EncryptedMessage sMsg = new EncryptedMessage(dictionary);
        Log.info("secure message: " + sMsg);

        sMsg.setDelegate(transceiver);
        byte[] key = sMsg.getKey();
        Map keys = sMsg.getKeys();
        Log.info("key: " + Base64.encode(key));
        Log.info("keys: " + keys);

        sMsg.getEnvelope().setGroup(Entity.parseID("everyone@everywhere"));
        Log.info("group: " + sMsg.getGroup());

        InstantMessage iMsg = sMsg.decrypt();
        Log.info("instant msg: " + iMsg);

        dictionary.put("signature", signature);
        RelayMessage rMsg = new RelayMessage(dictionary);
        Log.info("reliable message: " + rMsg);

        rMsg.setDelegate(transceiver);
        byte[] signature = rMsg.getSignature();
        Log.info("signature: " + Base64.encode(signature));

        Meta meta = rMsg.getMeta();
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

        RelayMessage rMsg = new RelayMessage(dictionary);
        Log.info("reliable message: " + rMsg);

        rMsg.setDelegate(transceiver);

        List<ID> members = new ArrayList<>();
        List<SecureMessage> messages = rMsg.split(members);
        Log.info("split: " + messages);

        SecureMessage sMsg = rMsg.trim(receiver);
        Log.info("trim: " + sMsg);
    }
}
