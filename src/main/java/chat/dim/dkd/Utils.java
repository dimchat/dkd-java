package chat.dim.dkd;

import com.alibaba.fastjson.JSON;

import java.util.Base64;
import java.util.Map;

public class Utils {

    public static String jsonEncode(Map<String, Object> dictionary) {
        return JSON.toJSONString(dictionary);
    }

    public static Map<String, Object> jsonDecode(String jsonString) {
        return JSON.parseObject(jsonString);
    }

    public static String base64Encode(byte[] data) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }

    public static byte[] base64Decode(String string) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(string);
    }
}
