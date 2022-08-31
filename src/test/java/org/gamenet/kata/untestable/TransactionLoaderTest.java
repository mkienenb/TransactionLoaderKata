package org.gamenet.kata.untestable;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

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

    @Test
    void testApplesauce() throws XMLStreamException, SQLException, UnsupportedEncodingException {
        TransactionLoader tl = new TransactionLoader();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        tl.applesauce(os);
        Approvals.verify(os.toString());
    }
}