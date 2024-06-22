package geektime.tdd.args;

import java.util.Arrays;

public class Args<T> {
    private ValueRetriever retriever;
    private OptionParser parser;
    private OptionClass<T> optionClass;

    public Args(ValueRetriever retriever, OptionParser parser, OptionClass<T> optionClass) {
        this.retriever = retriever;
        this.parser = parser;
        this.optionClass = optionClass;
    }

    public T parse(String... args) {
        return optionClass.create(Arrays.stream(optionClass.getOptionNames())
                .map(name -> parser.parse(optionClass.getOptionType(name), retriever.getValue(name, args)))
                .toArray(Object[]::new));
    }
}
