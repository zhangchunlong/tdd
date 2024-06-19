package geektime.tdd;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArgsTest {
    @Test
    public void should_parse_bool_option() {
        Function<String[], Map<String, String[]>> optionParser = Mockito.mock(Function.class);
        Mockito.when(optionParser.apply(new String[]{"-l"})).thenReturn(Map.of("l", new String[]{}));

        Args<BooleanOption> args = new Args<>(BooleanOption.class, Map.of(boolean.class, ArgsTest::parseBool),optionParser);
        BooleanOption option = args.parse("-l");
        assertTrue(option.logging());
    }

    static record BooleanOption(@Option("l") boolean logging){}
    private static boolean parseBool(String[] values) {
        checkSize(values, 0);
        return values != null;
    }

    @Test
    public void should_parse_int_option() {
        Function<String[], Map<String, String[]>> optionParser = Mockito.mock(Function.class);
        Mockito.when(optionParser.apply(new String[]{"-p", "8080"})).thenReturn(Map.of("p", new String[]{"8080"}));

        Args<IntOption> args = new Args<>(IntOption.class, Map.of(int.class, ArgsTest::parseInt), optionParser);
        IntOption option = args.parse("-p", "8080");
        assertEquals(8080, option.port());
    }
    static record IntOption(@Option("p")int port) {}
    private static int parseInt(String[] values) {
        checkSize(values, 1);
        return Integer.parseInt(values[0]);
    }
    private static void checkSize(String[] values, int size) {
        if (values!=null && values.length !=size) throw new RuntimeException();
    }
}
