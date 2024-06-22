package geektime.tdd.args;

public interface OptionParser {
    Object parse(Class type, String[] values);
}
