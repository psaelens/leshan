package com.skylaneoptics.ipso;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by pierre on 30/09/16.
 */
public class SmartObjectsTest {

    @Test
    public void testThatAllWorks() {
        List<SmartObject> all = SmartObjects.all();

        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertEquals(60, all.size());

        System.out.println(new Gson().toJson(all.get(0)));

    }
}