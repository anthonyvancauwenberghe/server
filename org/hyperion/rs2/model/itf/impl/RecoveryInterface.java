package org.hyperion.rs2.model.itf.impl;

import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.requests.QueryRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jet on 1/2/2015.
 */
public class RecoveryInterface extends Interface {

    private static final String[] QUESTIONS = {
            "What is your first name?",
            "What is your last name?",
            "What is your mother's maiden name?",
            "What is your birth date?",
            "What is your first pet's name?",
            "What is your mother's name?",
            "What is your father's name?",
            "What is your bestfriend's name?",
            "What is your dog's name?",
            "What is your cat's name?",
            "What is your hamster's name?"
    };

    public static final int ID = 1;

    private static final int ERROR = 1;

    public RecoveryInterface(){
        super(ID);
    }

    public void handle(final Player player, final Packet pkt){
        final String email = pkt.getRS2String().trim();
        final int question1Idx = pkt.getByte();
        final String answer1 = pkt.getRS2String().trim();
        final int question2Idx = pkt.getByte();
        final String answer2 = pkt.getRS2String().trim();
        player.sendf("Email: " + email);
        player.sendf("Q1 Index: " + question1Idx);//nigger
        player.sendf("A1: " + answer1);
        player.sendf("Q2 Index: " + question2Idx);
        player.sendf("A2: " + answer2);
        if(email.isEmpty() || answer1.isEmpty() || answer2.isEmpty()){
            sendResponse(player, ERROR);
            return;
        }
        if(!isValidIndex(question1Idx) || !isValidIndex(question2Idx) || question1Idx == question2Idx){
            sendResponse(player, ERROR);
            return;
        }
        //save recovery info
        /*final String query = String.format("INSERT INTO recovery(name, mail, security_id1, security_id2, security_answer_1, security_answer_2) " +
                "VALUES(%s, %s, %d, %d, %s, %s, %s)",
                player.getName(), email, question1Idx, question2Idx, answer1, answer2);
        World.getWorld().getLogsConnection().offer(new QueryRequest(query) {
            @Override
            public void process(SQLConnection sql) throws SQLException {
                final String check = "SELECT * FROM recovery WHERE name="+player.getName();
                final ResultSet set = sql.query(check);
                if(set.next())
                    return;
                sql.query(query);
            }
        });   */
        hide(player);
    }

    private boolean isValidIndex(final int idx){
        return idx > -1 && idx < QUESTIONS.length;
    }

    public static RecoveryInterface get(){
        return InterfaceManager.get(ID);
    }
}
