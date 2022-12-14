package org.gamenet.kata.untestable;

import javax.xml.stream.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.text.DecimalFormat;

public class TransactionLoader {
    boolean includeItems = false;
    boolean includeCancelled = false;
    public void init(String xml) {
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
            while(reader.hasNext()) {
                if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                    if ("items".equals(reader.getLocalName())) {
                        includeItems = "1".equals(reader.getTextCharacters());
                    } else if ("cancelled".equals(reader.getLocalName())) {
                        includeCancelled = "1".equals(reader.getTextCharacters());
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public String load() throws SQLException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(
                    new OutputStreamWriter(os, "utf-8"));
            writer.writeStartElement("res");
            writer.writeStartElement("transactions");
            try (Connection tConn = getConnection("trandb");
                 Connection cConn = getConnection("custdb")) {
                PreparedStatement ps = tConn.prepareStatement("select customer_id, order_id, price, status, from transaction where status != 'cancelled'");
                if (includeCancelled) {
                    ps = tConn.prepareStatement("select customer_id, order_id, price, status, from transaction");
                }
                ResultSet rs = ps.executeQuery();
                ResultSet rs2;
                while (rs.next()) {
                    writer.writeStartElement("t");
                    writer.writeAttribute("s", rs.getString(4).toUpperCase());
                    ps = cConn.prepareStatement("select name from customer where id = ?");
                    ps.setInt(1, rs.getInt("customer_id"));
                    ResultSet crs = ps.executeQuery();
                    if (!crs.next()) {
                        writer.writeStartElement("n");
                        writer.writeCharacters("UNK");
                        writer.writeEndElement();
                    } else {
                        writer.writeStartElement("n");
                        writer.writeCharacters(crs.getString(1));
                        writer.writeEndElement();
                    }
                    ps = tConn.prepareStatement("select name, category from order_item where order_id = ?");
                    ps.setInt(1, rs.getInt(2));
                    rs2 = ps.executeQuery();
                    if (includeItems) {
                        while (rs2.next()) {
                            writer.writeStartElement("i");
                            writer.writeAttribute("name", rs2.getString(1));
                            writer.writeAttribute("cat", rs2.getString(2).toUpperCase());
                            writer.writeEndElement();
                        }
                    }
                    rs.getString("order_id");
                    writer.writeStartElement("p");
                    writer.writeCharacters(new DecimalFormat("0.00").format(rs.getInt("price") / 100.0));
                    writer.writeEndElement();
                    writer.writeEndElement();
                }
                rs.close();
            }
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
            return os.toString("utf-8");
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "<error/>";
    }

    private Connection getConnection(String dbName) throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1", "", "");
    }
}
