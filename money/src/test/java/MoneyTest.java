import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTest {
    //TODO: 当瑞士法郎与美元的兑换率为2:1的时候，5美元 + 10 瑞士法郎 = 10美元
    //TODO: 5美元 * 2 = 10 美元
    //TODO: 将 “amount” 定义为私有
    //TODO: Dollar 类有副作用？
    //TODO: 钱数必须是整数吗？

    @Test
    public void testMultiplication() {
        Dollar five = new Dollar(5);
        five.times(2);
        assertEquals(10, five.amount);
    }
}
