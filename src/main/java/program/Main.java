package program;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.io.IOException;


public class Main {
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        String strConn = "jdbc:mariadb://localhost:3306/vpd912java";
        int menu = 0;
        while (menu != 5)
        {
            System.out.println("1. Показати всі продукти\n" +
                    "2. Створити\n" +
                    "3. Видалити\n" +
                    "4. Змінити\n" +
                    "5. Вийти\n" +
                    "6. Показати всі новини");
            try {
                menu = Integer.parseInt(in.nextLine());
            }
            catch(Exception ex) {
                System.out.println("Невірно введенний формат!");
                break;
            }

            switch (menu) {
                case 1: {
                    select(strConn);
                    break;
                }
                case 2: {
                    insert(strConn);
                    break;
                }
                case 3: {
                    delete(strConn);
                    break;
                }
                case 4: {
                    update(strConn);
                }
                case 5: {
                    System.out.println("Вихід!");
                    menu = 5;
                    break;
                }
                case 6: {
                    selectNews(strConn);
                    break;
                }
                default: {
                    System.out.println("Невірний пункт меню!");
                    break;
                }
            }
        }
    }

    private static void insert(String strConn) {
        try(Connection con = DriverManager.getConnection(strConn, "root", ""))
        {
            System.out.println("Connection is good");
            String query = "INSERT INTO products (name, price, description) " +
                    "VALUES (?, ?, ?);";
            try(PreparedStatement ps = con.prepareStatement(query)) {
                String name, description;
                double price;
                System.out.print("Enter name: ");
                name = in.nextLine();
                if(name != null && !name.isEmpty())
                    ps.setString(1, name);
                else{
                    System.out.println("Невірне ім'я!");
                    return;
                }

                System.out.print("Enter price: ");
                try {
                    price = Double.parseDouble(in.nextLine());
                    ps.setBigDecimal(2, new BigDecimal(price));
                }
                catch (Exception ex) {
                    System.out.println("Невірна ціна!\n");
                    return;
                }

                System.out.print("Enter description: ");
                description = in.nextLine();
                if(description != null && !description.isEmpty())
                    ps.setString(3, description);
                else {
                    System.out.println("Невірний опис!");
                    return;
                }

                int rows = ps.executeUpdate();
                System.out.println("Update rows: " +rows);
            }
            catch(Exception ex) {
                System.out.println("error statment");
            }
        }
        catch(Exception ex) {
            System.out.println("Error connection");
        }
    }

    private static void select(String strConn) {
        try(Connection con = DriverManager.getConnection(strConn, "root", ""))
        {
            System.out.println("Connection is good");
            String query = "SELECT * FROM products";
            try(PreparedStatement ps = con.prepareStatement(query)) {
                ResultSet resultSet = ps.executeQuery();
                while(resultSet.next())
                {
                    System.out.print("{ id = "+ resultSet.getInt("id")+", ");
                    System.out.print("name = "+ resultSet.getString("name")+", ");
                    System.out.print("price = "+ resultSet.getBigDecimal("price")+", ");
                    System.out.println("description = "+ resultSet.getString("description")+" } ");
                }
            }
            catch(Exception ex) {
                System.out.println("erro statment");
            }
        }
        catch(Exception ex) {
            System.out.println("Error connection");
        }
    }

    private static void selectNews(String strConn)
    {
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
            System.out.println("Connection is good");
            String query = "SELECT * FROM `news`, `categories` WHERE categoryId = `categories`.`id`;";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    System.out.print("{ id = " + resultSet.getInt("id") + ", ");
                    System.out.print("name = " + resultSet.getString("name") + ", ");
                    System.out.print("description = " + resultSet.getString("description") + ", ");
                    System.out.println("category = " + resultSet.getString("category") + " }");
                }
            }
        }
        catch(Exception ex) {
            System.out.println("Помилка selectNews");
        }
    }

    private static Product getById(String strConn, int id) {
        try(Connection con = DriverManager.getConnection(strConn, "root", ""))
        {
            System.out.println("Connection is good");
            String query = "SELECT * FROM products where id = ?";
            try(PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, id);
                ResultSet resultSet = ps.executeQuery();
                if(resultSet.next())
                {
                    Product p = new Product();
                    p.setId(resultSet.getInt("id"));
                    p.setName(resultSet.getString("name"));
                    p.setPrice(resultSet.getDouble("price"));
                    p.setDescription(resultSet.getString("description"));
                    return p;
                }
            }
            catch(Exception ex) {
                System.out.println("erro statment");
            }
        }
        catch(Exception ex) {
            System.out.println("Error connection");
        }
        return null;
    }
    private static void delete(String strConn) {
        try(Connection con = DriverManager.getConnection(strConn, "root", ""))
        {
            System.out.println("Connection is good");
            String query = "DELETE FROM products " +
                    "WHERE id = ?;";
            try(PreparedStatement ps = con.prepareStatement(query)) {
                int id;
                System.out.print("Enter id: ");
                id = Integer.parseInt(in.nextLine());
                Product product = getById(strConn, id);
                if (product == null) {
                    System.out.println("Такого продукта не існує!");
                    return;
                }
                ps.setInt(1, id);

                int rows = ps.executeUpdate();
                System.out.println("Update rows: " +rows);
            }
            catch(Exception ex) {
                System.out.println("Виникла помилка!");
            }
        }
        catch(Exception ex) {
            System.out.println("Error connection");
        }
    }

    private static void update(String strConn) {
        try(Connection con = DriverManager.getConnection(strConn, "root", ""))
        {
            System.out.println("Connection is good");
            String query = "UPDATE products SET name = ?, price=?, description=? " +
                    "WHERE id = ?;";
            try(PreparedStatement ps = con.prepareStatement(query)) {
                int id;
                System.out.print("Enter id: ");
                id = Integer.parseInt(in.nextLine());
                Product p = getById(strConn, id);
                System.out.print("Enter new name: ");
                String tmp = in.nextLine();
                if(tmp != null && !tmp.isEmpty()) {
                    p.setName(tmp);
                }
                System.out.print("Enter price: ");
                tmp = in.nextLine();
                if(tmp != null && !tmp.isEmpty()) {
                    try {
                        p.setPrice(Double.parseDouble(tmp));
                    }
                    catch(Exception ex){
                        System.out.println("Невірна ціна!");
                        return;
                    }
                }
                System.out.print("Enter description: ");
                tmp = in.nextLine();
                if(tmp != null && !tmp.isEmpty()) {
                    p.setDescription(tmp);
                }

                ps.setString(1, p.getName());
                ps.setDouble(2, p.getPrice());
                ps.setString(3,p.getDescription());
                ps.setInt(4, id);
                int rows = ps.executeUpdate();
                System.out.println("Update rows: " +rows);
            }
            catch(Exception ex) {
                System.out.println("Помилка!");
            }
        }
        catch(Exception ex) {
            System.out.println("Error connection");
        }
    }
}