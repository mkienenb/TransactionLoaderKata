package org.gamenet.kata.untestable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionLoaderTest {

    private String xml_string1;

    @Test
    void shouldNoticeItemsAndNoCancelledInXmlStream() {
        TransactionLoader tl = new TransactionLoader();
        String xml_string = "<items/>\n";
        tl.init(xml_string);

        assertTrue(tl.includeItems);
        assertFalse(tl.includeCancelled);
    }

    @Test
    void shouldNoticeCancelledAndNoItemsInXmlStream() {
        TransactionLoader tl = new TransactionLoader();
        String xml_string = "<cancelled/>\n";
        tl.init(xml_string);

        assertFalse(tl.includeItems);
        assertTrue(tl.includeCancelled);
    }
}