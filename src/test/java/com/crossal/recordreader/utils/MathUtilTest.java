package com.crossal.recordreader.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class MathUtilTest {

    @InjectMocks
    private MathUtil mathUtil;

    @Test
    public void distanceInKms_isOk() {
        double distance = mathUtil.distanceInKms(53.339428, -6.257664, 53.241595, -6.244606);
        assertEquals(10.9, distance, .1);
    }

    @Test
    public void distanceWithinKms_distanceIsWithinKms_returnsTrue() {
        assertTrue(mathUtil.distanceWithinKms(53.339428, -6.257664, 53.241595, -6.244606, 15));
    }

    @Test
    public void distanceWithinKms_distanceIsNotWithinKms_returnsFalse() {
        assertFalse(mathUtil.distanceWithinKms(53.339428, -6.257664, 53.241595, -6.244606, 5));
    }
}
