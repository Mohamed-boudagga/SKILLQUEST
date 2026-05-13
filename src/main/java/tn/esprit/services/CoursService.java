package tn.esprit.services;

import tn.esprit.enties.Cours;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CoursService implements IService<Cours> {
    private Connection connection = MyDataBase.getInstance().getConnection();

    public CoursService() {
        if (connection != null) {
            ensureContenueColumn();
            ensureIdAjouteurColumn();
            ensureDateDeCreationColumn();
        }
    }

    @Override
    public void add(Cours cours) throws SQLException {
        validateNiveau(cours.getNiveau());
        validateIdAjouteur(cours.getIdAjouteur());
        String sql = "INSERT INTO `cours` (`titre`, `description`, `niveau`, `contenue`, `idAjouteur`, `dateDeCreation`) VALUES (?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, cours.getTitre());
        preparedStatement.setString(2, cours.getDescription());
        preparedStatement.setString(3, cours.getNiveau());
        preparedStatement.setString(4, cours.getContenue());
        preparedStatement.setInt(5, cours.getIdAjouteur());
        preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Cours cours) throws SQLException {
        validateNiveau(cours.getNiveau());
        validateIdAjouteur(cours.getIdAjouteur());
        String sql = "UPDATE `cours` SET `titre`=?, `description`=?, `niveau`=?, `contenue`=?, `idAjouteur`=? WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, cours.getTitre());
        ps.setString(2, cours.getDescription());
        ps.setString(3, cours.getNiveau());
        ps.setString(4, cours.getContenue());
        ps.setInt(5, cours.getIdAjouteur());
        ps.setInt(6, cours.getId());
        ps.executeUpdate();
    }


    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `cours` WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Cours> getAll() throws SQLException {
        List<Cours> list = new ArrayList<>();
        String sql = "SELECT * FROM `cours`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            list.add(mapCours(rs));
        }
        return list;
    }

    public Cours getById(int id) throws SQLException {
        String sql = "SELECT * FROM `cours` WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapCours(rs);
        }
        return null;
    }

    public Cours getByTitre(String titre) throws SQLException {
        String sql = "SELECT * FROM `cours` WHERE `titre`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, titre);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapCours(rs);
        }
        return null;
    }

    public Cours getByNiveau(String niveau) throws SQLException {
        String sql = "SELECT * FROM `cours` WHERE `niveau`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, niveau);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapCours(rs);
        }
        return null;
    }

    public Cours getByDescription(String description) throws SQLException {
        String sql = "SELECT * FROM `cours` WHERE `description`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, description);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapCours(rs);
        }
        return null;
    }

    private Cours mapCours(ResultSet rs) throws SQLException {
        Cours c = new Cours();
        c.setId(rs.getInt("id"));
        c.setTitre(rs.getString("titre"));
        c.setDescription(rs.getString("description"));
        c.setNiveau(rs.getString("niveau"));
        c.setContenue(rs.getString("contenue"));
        c.setIdAjouteur(rs.getInt("idAjouteur"));
        c.setDateDeCreation(rs.getTimestamp("dateDeCreation"));
        return c;
    }

    private void ensureContenueColumn() {
        String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cours' AND COLUMN_NAME = 'contenue'";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                statement.executeUpdate("ALTER TABLE `cours` ADD COLUMN `contenue` TEXT NULL");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void ensureIdAjouteurColumn() {
        String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cours' AND COLUMN_NAME = 'idAjouteur'";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                statement.executeUpdate("ALTER TABLE `cours` ADD COLUMN `idAjouteur` INT NULL");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void ensureDateDeCreationColumn() {
        String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cours' AND COLUMN_NAME = 'dateDeCreation'";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                statement.executeUpdate("ALTER TABLE `cours` ADD COLUMN `dateDeCreation` DATETIME NULL");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void validateNiveau(String value) throws SQLException {
        int niveau;
        try {
            niveau = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new SQLException("Le niveau doit etre un entier entre 1 et 6.");
        }

        if (niveau < 1 || niveau > 6) {
            throw new SQLException("Le niveau doit etre entre 1 et 6.");
        }
    }

    private void validateIdAjouteur(int idAjouteur) throws SQLException {
        if (idAjouteur <= 0) {
            throw new SQLException("L'id de l'ajouteur doit etre un entier positif.");
        }
    }
}
