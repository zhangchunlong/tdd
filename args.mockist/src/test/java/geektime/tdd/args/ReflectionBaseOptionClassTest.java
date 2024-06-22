package geektime.tdd.args;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ReflectionBaseOptionClassTest {
    @Test
    public void should_treat_parameter_with_option_annotation_as_option() {
        OptionClass<IntOption> optionClass = new ReflectionBaseOptionClass(IntOption.class);
        assertArrayEquals(new String[]{"p"}, optionClass.getOptionNames());
    }
    static record IntOption(@Option("p") int port) {}
}
