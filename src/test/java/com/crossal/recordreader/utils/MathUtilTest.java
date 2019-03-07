package com.crossal.recordreader.utils;

import org.junit.Test;

public class MathUtilTest {
    @Test
    public void distance_isOk() {
        double distance = MathUtil.distanceInKms(53.339428, -6.257664, 53.339428, -6.311039);
        System.out.println(distance);
    }
}
