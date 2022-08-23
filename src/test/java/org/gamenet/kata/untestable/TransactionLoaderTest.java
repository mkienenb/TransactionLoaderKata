package org.gamenet.kata.untestable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionLoaderTest {


    @Test
    void shouldNoticeItemsAndNoCancelledInXmlStream() {
        TransactionLoader tl = new TransactionLoader();
        String xml_string = "<items/>\n";

        tl.init(xml_string);

        assertTrue(tl.includeItems);
        assertFalse(tl.includeCancelled);
    }
}