package Enumeration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TestMessageType {

    @Test
    public void testGetMessageType() {
        assertEquals(MessageType.getMessageType("/leave"),MessageType.LEAVE_MESSAGE);
        assertEquals(MessageType.getMessageType("message"),MessageType.RAW_MESSAGE);
    }

}
