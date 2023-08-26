package db;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import java.sql.*;
import java.util.ArrayList;
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
    public Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://"+host+"/"+database;
        return DriverManager.getConnection(url, username, password);
    }

    public void insertMember(Member member){
        String query = "INSERT INTO users (user_id, username, points, level, experience, warnings) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, member.getId());
            statement.setString(2, member.getUser().getName());
            statement.setInt(3, 0); // Set default value for points
            statement.setInt(4, 1); // Set default value for level
            statement.setInt(5, 0); // Set default value for experience
            statement.setInt(6, 0); // Set default value for warnings

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

    public String getUserName(String id){
        String query = "SELECT username FROM users WHERE user_id = ? ";
        try(PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "no-name";
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

    public int getWarnings(String userId){

        String query = "SELECT warnings FROM users WHERE user_id = ?";
        try(PreparedStatement statement = getConnection().prepareStatement(query)){
            statement.setString(1, userId);
               try(ResultSet resultSet = statement.executeQuery()){
                   if(resultSet.next()){
                       return resultSet.getInt("warnings");
                   }
               }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public void updateWarnings(String userId){
        // update warnings
        String query = "UPDATE users SET warnings = ? WHERE user_id = ?";
        int warnings = getWarnings(userId);
        int addWarn = warnings + 1;
        try(PreparedStatement statement = getConnection().prepareStatement(query)){
            statement.setInt(1, addWarn);
            statement.setString(2, userId); // Set the user_id parameter
            statement.executeUpdate(); // Execute the update statement

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void initializeMemberSpin(String userId){
        String query = "INSERT INTO daily_spins (user_id, spin_count) VALUES (?, 0?)";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, userId);
            statement.setInt(2, 0);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Inserted member with ID " + userId + " into the database table for DailySpins.");
            } else {
                System.out.println("No rows were inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeDailySpins() throws SQLException {

        PreparedStatement statement = null;

        try {
            connection = getConnection();

            // SQL query to update daily spin counts for all users
            String sql = "UPDATE daily_spins SET spin_count = 0";

            statement = connection.prepareStatement(sql);

            // select all user IDs from the 'users' table
            String selectSql = "SELECT user_id FROM users;";
            PreparedStatement selectStatement = getConnection().prepareStatement(selectSql);
            ResultSet resultSet = selectStatement.executeQuery();

            // Prepare the insert statement
            String insertSql = "INSERT INTO daily_spins (user_id, spin_count) VALUES (?, 0);";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);

            while (resultSet.next()) {
                String userId = resultSet.getString("user_id");

                // Check if the user ID already exists in the 'daily_spins' table
                String checkSql = "SELECT user_id FROM daily_spins WHERE user_id = ?;";
                PreparedStatement checkStatement = connection.prepareStatement(checkSql);
                checkStatement.setString(1, userId);
                ResultSet checkResult = checkStatement.executeQuery();

                if (!checkResult.next()) {
                    // The user ID doesn't exist in 'daily_spins', so insert it
                    insertStatement.setString(1, userId);
                    insertStatement.executeUpdate();
                }

                // Close the checkStatement and checkResult
                checkStatement.close();
                checkResult.close();
            }

            // Close the resources
            resultSet.close();
            selectStatement.close();
            insertStatement.close();

            // Execute the update query
            statement.executeUpdate();
        } finally {
            // Close the resources (statement and connection)
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public int getSpinCount(String userId){
        // Retrieve the user's XP from the database
        String query = "SELECT spin_count FROM daily_spins WHERE user_id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve and return the user's experience points
                    return resultSet.getInt("spin_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;


    }

    public void updateDailySpinCount(String userId, int newSpinCount) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        int oldSpinCount = getSpinCount(userId);

        try {
            connection = getConnection();
            // SQL query to update the daily spin count for a specific user
            String sql = "UPDATE daily_spins SET spin_count = ? WHERE user_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, newSpinCount + oldSpinCount);
            statement.setString(2, userId);

            // Execute the update query
            statement.executeUpdate();
        } finally {
            // Close the resources (statement and connection)
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void RetrieveMembersLeaderboard(TextChannel textChannel){
        String query = "SELECT * FROM users ORDER BY level DESC LIMIT 5"; // Query to retrieve up to 5 members for leaderboard
        List<Member> members = new ArrayList<>();
        try(PreparedStatement statement = getConnection().prepareStatement(query)){
            ResultSet resultSet = statement.executeQuery();
            // Iterate through the results
            while (resultSet.next()) {
                String memberId = resultSet.getString("user_id");
                String memberName = resultSet.getString("username");
                int memberLvl = resultSet.getInt("level");
                Member member = new User(memberId, memberName, memberLvl);
                members.add(member);
            }
            StringBuilder memberList = new StringBuilder();
            for(Member member : members){
                memberList.append("** \uD83D\uDD39").append(getUserName(member.getId())).append("**").append(" ~ lvl ").append(getUserLevel(member.getId())).append(" / ").append("*").append(getUserExperience(member.getId())).append("xp *").append("\n");
            }
            // Sending an embed message to return members as list
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(0X9900FF);
            embed.setTitle("⭐ Top Members");
            embed.setDescription(memberList);

            textChannel.sendMessageEmbeds(embed.build()).queue();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}





