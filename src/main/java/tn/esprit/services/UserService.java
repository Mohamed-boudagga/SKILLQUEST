package tn.esprit.services;

import tn.esprit.enties.Role;
import tn.esprit.enties.User;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    private Connection connection = MyDataBase.getInstance().getConnection();

    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO `user`( `name`, `email`, `role`) VALUES ('" + user.getName() + "','" + user.getEmail() + "','" + user.getRole() + "')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        System.out.println("user ajouter wererererererereryyyy !!! ");
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE `user` SET `name`=?,`email`=?,`role`=? WHERE `id`=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getEmail());
        preparedStatement.setString(3, user.getRole().toString());
        preparedStatement.setInt(4, user.getId());
        preparedStatement.executeUpdate();

    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `user` WHERE id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();


    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM `user`";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getInt(1));
            user.setName(resultSet.getNString(2));
            user.setEmail(resultSet.getNString(3));
            user.setRole(Role.valueOf(resultSet.getNString(4)));
            users.add(user);


        }
        return users;
    }

    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM `user` WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User u = new User();
            u.setId(rs.getInt(1));
            u.setName(rs.getString(2));
            u.setEmail(rs.getString(3));
            u.setRole(Role.valueOf(rs.getString(4)));
            return u;
        }
        return null;
    }

    public User getByName(String name) throws SQLException {
        String sql = "SELECT * FROM `user` WHERE `name`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User u = new User();
            u.setId(rs.getInt(1));
            u.setName(rs.getString(2));
            u.setEmail(rs.getString(3));
            u.setRole(Role.valueOf(rs.getString(4)));
            return u;
        }
        return null;
    }

    public User getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM `user` WHERE `email`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User u = new User();
            u.setId(rs.getInt(1));
            u.setName(rs.getString(2));
            u.setEmail(rs.getString(3));
            u.setRole(Role.valueOf(rs.getString(4)));
            return u;
        }
        return null;
    }

    public List<User> getByRole(Role role) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM `user` WHERE `role`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, role.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt(1));
            u.setName(rs.getString(2));
            u.setEmail(rs.getString(3));
            u.setRole(Role.valueOf(rs.getString(4)));
            users.add(u);
        }
        return users;
    }
}
//hedhi user
