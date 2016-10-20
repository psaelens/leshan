package com.skylaneoptics.ipso;

import com.google.gson.Gson;
import org.eclipse.leshan.core.model.ObjectModel;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by pierre on 30/09/16.
 */
public class ObjectModelsTest {

    @Test
    public void testThatAllWorks() {
        List<ObjectModel> all = ObjectModels.all();

        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertEquals(60, all.size());

        System.out.println(new Gson().toJson(all.get(0)));

    }
}