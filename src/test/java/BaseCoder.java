
public interface BaseCoder {

    String encode(byte[] data);

    byte[] decode(String string);
}
