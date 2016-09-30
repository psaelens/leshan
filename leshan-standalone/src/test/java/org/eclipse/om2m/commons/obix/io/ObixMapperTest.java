package org.eclipse.om2m.commons.obix.io;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Pierre Saelens
 */
public class ObixMapperTest {

    @Test
    public void getInstance() throws Exception {
        ObixMapper mapper = ObixMapper.getInstance();
        assertNotNull(mapper);
        assertNotNull(mapper.getJAXBContext());
    }

}