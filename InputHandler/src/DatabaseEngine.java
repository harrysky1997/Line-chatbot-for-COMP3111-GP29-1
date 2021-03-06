/* 
 * Created by Samson on 2/11/2017.
 */

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.*;

import java.io.File;
import java.io.FileInputStream;

public class DatabaseEngine {
    private Connection connection;

    public DatabaseEngine() throws URISyntaxException, SQLException {
//        String url = "postgres://agztmngmcxsmtw:53c37a0e5fc1b17138f39cedb6447395069d3e91c10fea104207c4b779a61b7d@ec2-54-221-244-196.compute-1.amazonaws.com:5432/ddgig904mq3sj6";
        String url = "postgres://kntfvvdzhkanyz:69f7ad5e8e5021c8f38d2419efbb6504dbdad4aa93f423a971ddad9245724e37@ec2-23-21-235-142.compute-1.amazonaws.com:5432/dft5f6k5thrk4u";
        URI dbUri = new URI(url);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

        this.connection = DriverManager.getConnection(dbUrl, username, password);
    }

    // For csv or txt input
    public void addTour() throws URISyntaxException, SQLException {
        Statement stmt = connection.createStatement();

        String sqlDelete = "delete from tour";
        stmt.executeUpdate(sqlDelete);

        String sqlInsert = "insert into tour values (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);


        String csvFile = "./tour plan.txt";
        String line = null;
        String cvsSplitBy = "\t";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] attribute = line.split(cvsSplitBy);

                if (attribute.length < 5) continue;
                preparedStatement.setString(1, attribute[0]);

                preparedStatement.setString(2, attribute[1].trim());
                preparedStatement.setString(3, attribute[2]);
                preparedStatement.setInt(4, Integer.parseInt(attribute[3]));

                String input = attribute[5];
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(input);
                int weekDayPrice = 0;
                int weekEndPrice = 0;
                int count = 0;
                while (m.find()){
                    if (count == 0) {
                        weekDayPrice = Integer.parseInt(m.group());
                        weekEndPrice = Integer.parseInt(m.group());
                    }
                    else weekEndPrice = Integer.parseInt(m.group());
                    count++;
                }

                preparedStatement.setInt(5, weekDayPrice);
                preparedStatement.setInt(6, weekEndPrice);
                preparedStatement.setString(7, attribute[4]);
                preparedStatement .executeUpdate();
//                System.out.println(attribute[0] + " " + attribute[1] + " " + attribute[2]
//                        + " " + attribute[3] + " " + attribute[4] + " " + attribute[5]);

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        preparedStatement.close();
        printTour();
    }

    public void addCustomer()  throws URISyntaxException, SQLException{
        Statement stmt = connection.createStatement();

        String sqlDelete = "delete from customer";
        stmt.executeUpdate(sqlDelete);

        String sqlInsert = "insert into customer values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);

        String csvFile = "./customer.csv";
        String line = null;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            boolean first = true;
            while ((line = br.readLine()) != null) {

                //ignore first row
                if (first) {
                    first = false;
                    continue;
                }

                // use comma as separator
                String[] attribute = line.split(cvsSplitBy);
                if (attribute.length < 10) continue;

                preparedStatement.setString(1, attribute[0]);
                preparedStatement.setString(2, attribute[1]);
                preparedStatement.setInt(3, 81777778);
                preparedStatement.setInt(4, Integer.parseInt(attribute[3]));
                preparedStatement.setString(5, attribute[4]);
                preparedStatement.setInt(6, Integer.parseInt(attribute[5]));
                preparedStatement.setInt(7, Integer.parseInt(attribute[6]));
                preparedStatement.setInt(8, Integer.parseInt(attribute[7]));
                preparedStatement.setDouble(9, Double.parseDouble(attribute[8]));
                preparedStatement.setDouble(10, Double.parseDouble(attribute[9]));

                preparedStatement .executeUpdate();
//                System.out.println(attribute[0] + " " + attribute[1] + " " + attribute[2]
//                        + " " + attribute[3] + " " + attribute[4] + " " + attribute[5]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String strSelect = "select * from customer";
        ResultSet rset = stmt.executeQuery(strSelect);
        while(rset.next()) {   // Move the cursor to the next row
            System.out.println(rset.getString("name") + ", "
                    + rset.getString("id") + ", "
                    + rset.getInt("phoneNo") + ", "
                    + rset.getInt("age") + ", "
                    + rset.getString("tour") + ", "
                    + rset.getInt("adultNo") + ", "
                    + rset.getInt("childrenNo") + ", "
                    + rset.getInt("toodlerNo") + ", "
                    + rset.getDouble("tourFee") + ", "
                    + rset.getDouble("paid"));
        }
        preparedStatement.close();
        rset.close();
    }

    public void addFAQData()  throws URISyntaxException, SQLException{
        Statement stmt = connection.createStatement();

        String sqlDelete = "delete from faq";
        stmt.executeUpdate(sqlDelete);

        String sqlInsert = "insert into faq values (?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);

        String csvFile = "./faq.txt";
        String line = null;
        String cvsSplitBy = "\t";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] attribute = line.split(cvsSplitBy);

                preparedStatement.setString(1, attribute[0].trim());
                preparedStatement.setString(2, attribute[1].trim());

                preparedStatement .executeUpdate();

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String strSelect = "select * from faq";
        ResultSet rset = stmt.executeQuery(strSelect);
        while(rset.next()) {   // Move the cursor to the next row
            System.out.println(rset.getString("keywords"));
            System.out.println(rset.getString("respond"));
        }
        preparedStatement.close();
        rset.close();
    }

    public void addBooking() throws URISyntaxException, SQLException{
        Statement stmt = connection.createStatement();

        String sqlDelete = "delete from booking";
        stmt.executeUpdate(sqlDelete);

        String sqlInsert = "insert into booking values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);

        String csvFile = "./booking.csv";
        String line = null;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String bookingID = null;
            while ((line = br.readLine()) != null) {
                // use comma as separator

                String[] attribute = line.split(cvsSplitBy);
                if (attribute.length < 2) {
                    bookingID = attribute[0];
                    continue;
                }
                preparedStatement.setString(1, attribute[0]);
                preparedStatement.setString(2, bookingID);
                preparedStatement.setString(3, attribute[1]);
                preparedStatement.setString(4, attribute[2]);
                preparedStatement.setString(5, attribute[3]);
                preparedStatement.setString(6, attribute[4]);
                preparedStatement.setInt(7, Integer.parseInt(attribute[5]));
                preparedStatement.setInt(8, Integer.parseInt(attribute[6]));
                preparedStatement.setInt(9, 0);

                preparedStatement .executeUpdate();
//                System.out.println(bookingID + " " + attribute[0] + " " + attribute[1] + " " + attribute[2]
//                        + " " + attribute[3] + " " + attribute[4] + " " + attribute[5]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String strSelect = "select * from booking";
        ResultSet rset = stmt.executeQuery(strSelect);
        while(rset.next()) {   // Move the cursor to the next row
            System.out.println(rset.getString("id") + ", "
                    + rset.getString("tourId") + ", "
                    + rset.getString("dates") + ", "
                    + rset.getString("tourGuide") + ", "
                    + rset.getString("lineAcc") + ", "
                    + rset.getString("hotel") + ", "
                    + rset.getInt("capacity") + ", "
                    + rset.getInt("miniCustomer") + ", "
                    + rset.getDouble("currentCustomer"));
        }
        preparedStatement.close();
        rset.close();
    }

    // For GUI input
    public void addTour(String id, String name, String descrip, String dura, String days, String dayCost, String endCost)
            throws URISyntaxException, SQLException {
        String sqlInsert = "insert into tour select ?, ?, ?, ?, ?, ?, ? where not exists (select id from tour where id = ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, descrip);
        preparedStatement.setInt(4, Integer.parseInt(dura));
        preparedStatement.setString(7, days);
        if (dayCost.isEmpty()) preparedStatement.setInt(5, 0);
        else preparedStatement.setInt(5, Integer.parseInt(dayCost));
        if (endCost.isEmpty()) preparedStatement.setInt(6, 0);
        else preparedStatement.setInt(6, Integer.parseInt(endCost));
        preparedStatement.setString(8, id);

        preparedStatement.executeUpdate();

        printTour();
        preparedStatement.close();
    }

    public void printTour() throws URISyntaxException, SQLException{
        Statement stmt = connection.createStatement();
        String strSelect = "select * from tour";
        ResultSet rset = stmt.executeQuery(strSelect);
        while(rset.next()) {   // Move the cursor to the next row
            System.out.println(rset.getString("id") + ", "
                    + rset.getString("name") + ", "
                    + rset.getString("attraction") + ", "
                    + rset.getInt("duration") + ", "
                    + rset.getInt("weekDayPrice") + ", "
                    + rset.getInt("weekEndPrice"));
        }
        rset.close();
        stmt.close();
    }

    public void addFaq(String keywords, String respond) throws URISyntaxException, SQLException{
        String sqlInsert = "insert into faq values (?,?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);
        preparedStatement.setString(1, keywords);
        preparedStatement.setString(2, respond);


        preparedStatement.executeUpdate();

        preparedStatement.close();

        Statement stmt = connection.createStatement();
        String strSelect = "select * from faq";
        ResultSet rset = stmt.executeQuery(strSelect);
        while(rset.next()) {   // Move the cursor to the next row
            System.out.println(rset.getString("keywords"));
            System.out.println(rset.getString("respond"));
        }
        rset.close();
        stmt.close();
    }
}