package db;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MemberInsertion {

    public static void insertAllMembers(JDA jda, DatabaseManager dbManager) throws SQLException {
        Guild guild = jda.getGuildById("1133308584755744778");

        if(guild != null){
            Connection connection = dbManager.getConnection();
            String insertQuery = "INSERT INTO users(user_id, username) VALUES (?, ?) ";

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            for (Member member : guild.getMembers()){
                String userId = member.getUser().getId();
                String username = member.getUser().getGlobalName();

                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, username);
            }

            preparedStatement.close();

            System.out.println("All members are inserted in DB");
        }
    }
}
