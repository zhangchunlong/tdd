package geektime.tdd.args;

public interface OptionClass<T> {
    String[] getOptionNames();
    Class getOptionType(String name);
    T create(Object[] values);
}
