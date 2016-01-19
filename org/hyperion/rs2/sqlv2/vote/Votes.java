package org.hyperion.rs2.sqlv2.vote;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sqlv2.dao.SqlDaoManager;
import org.hyperion.rs2.sqlv2.db.Db;
import org.hyperion.rs2.sqlv2.db.DbConfig;

import java.util.List;

public class Votes extends SqlDaoManager<VoteDao> {

    public Votes(final Db db) {
        super(db, VoteDao.class);
    }

    public List<WaitingVote> waiting(final String playerName) {
        try{
            return dao.waiting(playerName);
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return null;
        }
    }

    public List<WaitingVote> waiting(final Player player) {
        return waiting(player.getName().toLowerCase());
    }

    public boolean processRunelocus(final VoteDao dao, final WaitingVote vote) {
        try{
            return dao.processRunelocus(vote.index()) == 1;
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }

    public boolean processTopg(final VoteDao dao, final WaitingVote vote) {
        try{
            return dao.processTopg(vote.index()) == 1;
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }

    public boolean processRspslist(final VoteDao dao, final WaitingVote vote) {
        try{
            return dao.processRspslist(vote.index()) == 1;
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }

    public boolean process(final VoteDao dao, final WaitingVote vote) {
        try{
            return dao.process(vote.index()) == 1;
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }

    public boolean delete(final VoteDao dao, final WaitingVote vote) {
        try{
            return dao.delete(vote.index()) == 1;
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }

    public boolean insertVote(final VoteDao dao, final String name, final int runelocus, final int top100, final int topg) {
        try{
            return dao.insertVote(name, runelocus, top100, topg) == 1;
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }

    public boolean insertVote(final VoteDao dao, final Player player, final int runelocus, final int top100, final int topg) {
        return insertVote(dao, player.getName().toLowerCase(), runelocus, top100, topg);
    }
}
