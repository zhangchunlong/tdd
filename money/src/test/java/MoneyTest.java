import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    //TODO: 当瑞士法郎与美元的兑换率为2:1的时候，5美元 + 10 瑞士法郎 = 10美元
    //5美元 * 2 = 10 美元
    //TODO: 将 “amount” 定义为私有
    //Dollar 类有副作用？
    //TODO: 钱数必须是整数吗？
    //实现equals()方法
    //TODO： 实现hashcode方法
    //TODO: 与空对象判等
    //TODO: 与非同类对象判等

    @Test
    public void testMultiplication() {
        Dollar five = new Dollar(5);
        assertEquals(new Dollar(10), five.times(2));
        assertEquals(new Dollar(15), five.times(3));
    }

    @Test
    public void testEquality() {
        assertTrue(new Dollar(5).equals(new Dollar(5)));
        assertFalse(new Dollar(5).equals(new Dollar(6)));
    }
}
