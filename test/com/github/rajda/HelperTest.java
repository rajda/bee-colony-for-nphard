package com.github.rajda;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jacek on 30.01.2016.
 */
public class HelperTest {
    @Test
    public void testGetCeilFromDouble() throws Exception {
        assertEquals(-1, Helper.getCeilFromDouble(-1));
        assertEquals(-1, Helper.getCeilFromDouble(-1.1));
        assertEquals(-1, Helper.getCeilFromDouble(-1.9));
        assertEquals(0, Helper.getCeilFromDouble(-0.9));
        assertEquals(0, Helper.getCeilFromDouble(-0.1));
        assertEquals(0, Helper.getCeilFromDouble(0));
        assertEquals(0, Helper.getCeilFromDouble(0.0));
        assertEquals(1, Helper.getCeilFromDouble(0.1));
        assertEquals(1, Helper.getCeilFromDouble(1));
    }

    @Test
    public void testRandomWithReferencedValue() throws Exception {
        assertEquals(1, Helper.random(0, 1, 0));
        assertEquals(0, Helper.random(0, 1, 1));
    }

    @Test
    public void testRandom() throws Exception {
        assertEquals(-1, Helper.random(-1, -1));
        assertEquals(0, Helper.random(0, 0));
        assertEquals(1, Helper.random(1, 1));
    }

    @Test
    public void testShowCurrentSolutionsList() throws Exception {

    }
}