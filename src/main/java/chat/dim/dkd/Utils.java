package chat.dim.dkd;

import java.util.Base64;

public class Utils {

    public static String base64Encode(byte[] data) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }

    public static byte[] base64Decode(String string) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(string);
    }
}
