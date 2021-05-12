package org.vgb.eaf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageProcessorTest {
    @Test
    public void test_filenameEscape() {
        String testString = "Testmessage 08/15 : Content here ../..\\..";
        String escaped = MessageProcessor.filenameEscape(testString);

        System.out.printf("%s ---> %s%n", testString, escaped);

        Assertions.assertEquals(-1, escaped.indexOf('.'));
        Assertions.assertEquals(-1, escaped.indexOf('\\'));
        Assertions.assertEquals(-1, escaped.indexOf('/'));
        Assertions.assertEquals(-1, escaped.indexOf(':'));
        Assertions.assertEquals(-1, escaped.indexOf('?'));

        Assertions.assertTrue(escaped.indexOf("Testmessage") >= 0, "should be present");
    }
}
