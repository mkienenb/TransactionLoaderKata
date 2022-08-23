package org.gamenet.kata.untestable;

import java.sql.*;
import java.text.DecimalFormat;

public class TransactionLoader {
    // This is intended as a refactoring / testing kata for improving separation of concerns.
    // Goal might be to introduce JSON output in addition to XML.
    public String load() throws SQLException {
        // Produces result like: 
        // "<res><transactions><t><n>Loch Ness</n><p>3.50</p></t></transactions></res>";
        StringBuilder sb = new StringBuilder();
        sb.append("<res>");
        sb.append("<transactions>");
        try (Connection tConn = getConnection("trandb");
             Connection cConn = getConnection("custdb")) {
            PreparedStatement ps = tConn.prepareStatement("select customer_id, order_id, price, status, from transaction");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sb.append("<t>");
                ps = cConn.prepareStatement("select name from customer where id = ?");
                ps.setInt(1, rs.getInt("customer_id"));
                ResultSet crs = ps.executeQuery();
                if (!crs.next()) {
                    sb.append("<n>UNK</n>");
                } else {
                    sb.append("<n>" + crs.getString(1) + "</n>");
                }
                rs.getString("order_id");
                sb.append("<p>" + new DecimalFormat("0.00").format(rs.getInt("price") / 100.0) + "</p>");;
                sb.append("</t>");
            }
            rs.close();
        }
        sb.append("</transactions>");
        sb.append("</res>");
        return sb.toString();
    }

    private Connection getConnection(String dbName) throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1", "", "");
    }
}
