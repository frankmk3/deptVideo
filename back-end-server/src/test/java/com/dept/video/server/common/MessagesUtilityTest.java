package com.dept.video.server.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

public class MessagesUtilityTest {

    private MessagesUtility messagesUtility;
    private MessageSource messageSource;

    @Before
    public void init() {
        messageSource = Mockito.mock(MessageSource.class);
        messagesUtility = new MessagesUtility(messageSource);
    }

    @Test
    public void whenTheMessageIdIsPresentReturnTheProperMessage() {
        String messageValue = "message value";
        String sourceId = "source.id";
        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq(sourceId), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(messageValue);

        String responseMessage = messagesUtility.getMessage(sourceId);

        Assert.assertEquals(messageValue, responseMessage);
    }

    @Test
    public void whenTheMessageIdIsNotPresentReturnMessageId() {
        String sourceId = "source.id";
        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq(sourceId), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new NoSuchMessageException(sourceId));

        String responseMessage = messagesUtility.getMessage(sourceId);

        Assert.assertEquals(sourceId, responseMessage);
    }


    @Test
    public void whenTheMessageIdIsNotPresentAndReturnDefaultValue() {
        String sourceId = "source.id";
        String defaultValue = "translate value";
        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq(sourceId), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new NoSuchMessageException(sourceId));

        String responseMessage = messagesUtility.getMessage(sourceId, defaultValue);

        Assert.assertEquals(defaultValue, responseMessage);
    }


}
