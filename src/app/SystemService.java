package app;

public interface SystemService {

    void pull(String key, String fileName);
    void add(String fileName);
    void remove(String fileName);

}
