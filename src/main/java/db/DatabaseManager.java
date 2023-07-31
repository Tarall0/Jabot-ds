package db;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        String query = "INSERT INTO users (user_id, username) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, member.getId());
            statement.setString(2, member.getUser().getName());

            statement.executeUpdate();

            System.out.println("Inserted member with ID " + member.getId() + " into the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void populateUserTable(JDA jda) {

        connect();

        // Get all the guilds (servers) the bot is a member of
        List<Guild> guilds = jda.getGuilds();

        for (Guild guild : guilds) {
            try {
                List<Member> members = guild.getMembers();
                for (Member member : members) {
                    // Insert member details into the database using the insertMember method
                    insertMember(member);
                }
            } catch (ErrorResponseException e) {
                e.printStackTrace();
            }
        }


    }

}
