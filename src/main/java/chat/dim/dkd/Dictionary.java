package chat.dim.dkd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Dictionary implements Map<String, Object> {

    protected final Map<String, Object> dictionary;

    public Dictionary() {
        super();
        dictionary = new HashMap<>();
    }

    public Dictionary(Map<String, Object> map) {
        super();
        dictionary = map;
    }

    public boolean equals(Map<String, Object> map) {
        return dictionary.equals(map);
    }

    public String toString() {
        return dictionary.toString();
    }

    @Override
    public int size() {
        return dictionary.size();
    }

    @Override
    public boolean isEmpty() {
        return dictionary.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return dictionary.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return dictionary.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return dictionary.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return dictionary.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return dictionary.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        dictionary.putAll(m);
    }

    @Override
    public void clear() {
        dictionary.clear();
    }

    @Override
    public Set<String> keySet() {
        return dictionary.keySet();
    }

    @Override
    public Collection<Object> values() {
        return dictionary.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return dictionary.entrySet();
    }
}
