import chat.dim.dkd.ReliableMessage;
import chat.dim.dkd.Utils;
import chat.dim.dkd.content.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ContentTest {

    private void log(String msg) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String method = traces[2].getMethodName();
        int line = traces[2].getLineNumber();
        System.out.println("[" + method + ":" + line + "] " + msg);
    }

    @Test
    public void testTextContent() throws ClassNotFoundException {

        // text
        TextContent text = new TextContent("Hello world!");
        log("text msg:" + text.getText());
        log("text:" + text);
        Assert.assertEquals(Content.TEXT, text.type);

        Map<String, Object> map = new HashMap<>(text);
        log("dictionary:" + map);
        Content content = Content.getInstance(map);
        log("content:" + content);
        Assert.assertEquals(Content.TEXT, text.type);

        content.setGroup("group456");
        log("group content:" + content);

        content = new TextContent(map);
        log("content:" + content);
        Assert.assertEquals(Content.TEXT, text.type);

        Assert.assertTrue(content.equals(map));
    }

    @Test
    public void testFileContent() {
        Map<String, Object> key = new HashMap<>();
        key.put("algorithm", "AES");

        byte[] data = new byte[64];

        // file
        FileContent file = new FileContent(data, "a.zip");
        file.setUrl("https://www.beva.com/");
        file.setPassword(key);
        log("file msg:" + file);
        Assert.assertEquals(Content.FILE, file.type);

        log("password:" + file.getPassword());
        log("URL:" + file.getUrl());

        // image
        ImageContent image = new ImageContent(data, "a.jpeg");
        image.setThumbnail(data);
        log("thumbnail:" + Utils.base64Encode(image.getThumbnail()));
        log("image content:" + image);
        Assert.assertEquals(Content.IMAGE, image.type);

        file = new ImageContent(image);
        log("file  content:" + file);

        // audio
        AudioContent audio = new AudioContent(data, "a.mp3");
        log("audio content:" + audio);
        Assert.assertEquals(Content.AUDIO, audio.type);

        file = new AudioContent(audio);
        log("file  content:" + file);

        // video
        VideoContent video = new VideoContent(data, "a.mp4");
        video.setSnapshot(data);
        log("snapshot:" + Utils.base64Encode(video.getSnapshot()));
        log("video content:" + video);
        Assert.assertEquals(Content.VIDEO, video.type);

        file = new VideoContent(video);
        log("file  content:" + file);
    }

    @Test
    public void testForward() throws NoSuchFieldException {
        ReliableMessage rMsg = null;

        ForwardContent forward = new ForwardContent(rMsg);
        log("forward:" + forward);

        Content content = new ForwardContent(forward);
        log("content:" + content);
    }

    @Test
    public void testCommand() {
        CommandContent cmd = new CommandContent("hello");
        log("cmd:" + cmd);
    }

    @Test
    public void testHistory() {
        HistoryCommand history = new HistoryCommand(HistoryCommand.REGISTER);
        log("history:" + history);

        Content content = new HistoryCommand(history);
        log("content:" + content);
    }
}
