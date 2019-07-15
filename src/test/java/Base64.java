
public class Base64 {

    public static String encode(byte[] data) {
        return coder.encode(data);
    }

    public static byte[] decode(String string) {
        return coder.decode(string);
    }

    // default coder
    public static BaseCoder coder = new BaseCoder() {

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
