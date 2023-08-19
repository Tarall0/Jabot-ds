package db;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.*;
import java.util.List;

public class DatabaseManager {
    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;

    public DatabaseManager(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }
    public void connect() {
        String url = "jdbc:mysql://"+host+"/"+database;
        try{
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database successfully");


        }catch (SQLException e){
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Error occurred trying connect to the database");

        }
    }
    public void disconnect() throws SQLException{
        if(connection != null){
            connection.close();
            System.out.println("Connection closed with db");
        }
    }
    public Connection getConnection() {
        return connection;
    }

    public void insertMember(Member member){
        String query = "INSERT INTO users (user_id, username, points, level, experience) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, member.getId());
            statement.setString(2, member.getUser().getName());
            statement.setInt(3, 0); // Set default value for points
            statement.setInt(4, 1); // Set default value for level
            statement.setInt(5, 0); // Set default value for experience

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Inserted member with ID " + member.getId() + " into the database.");
            } else {
                System.out.println("No rows were inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isMemberInDatabase(String userId) {
        String query = "SELECT user_id FROM users WHERE user_id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {

            statement.setString(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                // If the query returns a result, the member exists in the database
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of an exception or error
        }
    }
    public void insertAllMemberInDB(JDA bot){
        Guild guild = null;
        for (Guild g : bot.getGuilds()) {
            guild = g;
            break; // Take the first guild and break out of the loop
        }

        if (guild != null) {
            // Retrieve all users member as List
            List<Member> members = guild.loadMembers().get();

            System.out.println(members);

            for(Member member : members){
                String id = member.getId();
                if (!isMemberInDatabase(id)){
                    insertMember(member);
                }
            }

        } else {
            System.out.println("The bot is not a member of any guilds.");
        }
    }

    public void awardExperience(Member member, int experiencePoints) {
        // Get the user's current XP from the database
        int currentXP = getUserExperience(member.getId());

        // Update the user's XP
        int newXP = currentXP + experiencePoints;
        updateUserXP(member.getId(), newXP);

        // Calculate the user's new level
        int newLevel = calculateUserLevel(newXP);

        // Update the user's level in the database
        updateUserLevel(member.getId(), newLevel);

        // Send a message to notify the user of their new level
        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Congratulations! You've reached level " + newLevel + " in the bot.").queue());
    }

    public int getUserExperience(String userId) {
        // Retrieve the user's XP from the database

        String query = "SELECT experience FROM users WHERE user_id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve and return the user's experience points
                    return resultSet.getInt("experience");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return 0;
    }

    public void updateUserXP(String userId, int xp) {
        // Update the user's XP in the database
        String query = "UPDATE users SET experience = ? WHERE user_id = ?";

        try(PreparedStatement statement = getConnection().prepareStatement(query)){

            int currentXp = getUserExperience(userId);
            int newXp = currentXp + xp;
            statement.setInt(1, newXp);
            statement.setString(2, userId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("User's experience points updated.");
            } else {
                System.out.println("No rows were updated.");
            }


        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUserLevel(String userId) {
        // Retrieve the user's XP from the database

        String query = "SELECT level FROM users WHERE user_id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve and return the user's experience points
                    return resultSet.getInt("level");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return 0;
    }

    public int calculateUserLevel(int xp) {
        // Implement your leveling algorithm here
        System.out.println(xp);
        return xp / 1000;


    }

    public void updateUserLevel(String userId, int level) {
        // Update the user's level in the database
        // Implement SQL update statement to set the new level value
        String query = "UPDATE users SET level = ? WHERE user_id = ?";
        try(PreparedStatement statement = getConnection().prepareStatement(query)){
            statement.setInt(1, level);
            statement.setString(2, userId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("User's level updated.");
            } else {
                System.out.println("No rows were updated.");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}
