package geektime.tdd;

public interface OptionParser<T> {
    T parse(String[] values);
}
