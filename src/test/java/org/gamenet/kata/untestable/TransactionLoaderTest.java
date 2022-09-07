package org.gamenet.kata.untestable;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionLoaderTest {

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
    void emptyCustomerResultSet() throws XMLStreamException, SQLException, UnsupportedEncodingException {
        TransactionLoader tl = new TransactionLoader() {
            @Override
            Connection getConnection(String dbName) {
                return null;
            }

            @Override
            ResultSet getCustomerResultSet_AndStuff(Connection tConn) {
                ResultSet emptyResultSet;
                emptyResultSet = mock(ResultSet.class);
                return emptyResultSet;
            }
        };
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        tl.applesauce(os);
        Approvals.verify(os.toString());
    }

    @Test
    void NonEmptyCustomerResultSet() throws XMLStreamException, SQLException, UnsupportedEncodingException {
        TransactionLoader tl = new TransactionLoader() {
            @Override
            Connection getConnection(String dbName) {
                return null;
            }

            @Override
            ResultSet getCustomerResultSet_AndStuff(Connection tConn) throws SQLException {
                ResultSet nonEmptyResultSet;
                nonEmptyResultSet = mock(ResultSet.class);
                when(nonEmptyResultSet.next()).
                        thenReturn(true).
                        thenReturn(false);
                return nonEmptyResultSet;
            }
        };
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        tl.applesauce(os);
        Approvals.verify(os.toString());
    }
}