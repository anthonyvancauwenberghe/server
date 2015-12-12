package org.hyperion.rs2.model.content.checkers;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;

import java.util.ArrayList;

public class GameHandler {

    private static final ArrayList<CheckersGame> games = new ArrayList<CheckersGame>();

    public static void movePiece(final Player p, final int slotFrom, final int slotTo) {
        for(final CheckersGame g : games){
            if(g.getPlayerOne() == p || g.getPlayerTwo() == p){
                if(!g.playerTurn.equalsIgnoreCase(p.getName())){
                    p.getActionSender().sendMessage("Please wait until your turn.");
                    updatePieces(p, g);
                    return;
                }
                if(g.getPiece(slotFrom) != g.playerPiece[g.getPlayerOne().getName().equalsIgnoreCase(p.getName()) ? 0 : 1]){
                    p.getActionSender().sendMessage("This is not your piece.");
                    updatePieces(p, g);
                    return;
                }
                if(!validMove(g, slotFrom, slotTo, g.getPlayerOne().getName().equalsIgnoreCase(p.getName()) ? 0 : 1)){
                    p.getActionSender().sendMessage("You can not move that piece here.");
                    updatePieces(p, g);
                    return;
                }
                g.setPiece(slotFrom, slotTo);
                final Player o = p.challengedBy;
                if(!g.getPieceTaken()){
                    g.playerTurn = g.getPlayerOne().getName().equalsIgnoreCase(p.getName()) ? g.getPlayerTwo().getName() : g.getPlayerOne().getName();
                    p.getActionSender().sendString(g.getPlayerOne().getName().equalsIgnoreCase(p.getName()) ? g.getPlayerTwo().getName() : g.getPlayerOne().getName(), 10479);
                    o.getActionSender().sendString(g.getPlayerOne().getName().equalsIgnoreCase(p.getName()) ? g.getPlayerTwo().getName() : g.getPlayerOne().getName(), 10479);
                }
                g.setPieceTaken(false);
                updatePieces(o, g);
                updatePieces(p, g);
                checkWon(g);
            }
        }
    }

    public static void checkWon(final CheckersGame g) {
        final Player p = g.playerOne;
        final Player o = g.playerTwo;
        if(g.getPiecesLeft(0) == 0 && g.getPiecesLeft(1) > 0){
            p.getActionSender().sendMessage("You have lost the game of Draughts!");
            o.getActionSender().sendMessage("You have won the game of Draughts!");
            p.checkersRecord[1]++;
            o.checkersRecord[0]++;
            p.getActionSender().removeAllInterfaces();
            o.getActionSender().removeAllInterfaces();
            games.remove(g);
            return;
        }
        if(g.getPiecesLeft(1) == 0 && g.getPiecesLeft(0) > 0){
            o.getActionSender().sendMessage("You have lost the game of Draughts!");
            p.getActionSender().sendMessage("You have won the game of Draughts!");
            o.checkersRecord[1]++;
            p.checkersRecord[0]++;
            p.getActionSender().removeAllInterfaces();
            o.getActionSender().removeAllInterfaces();
            games.remove(g);
        }
    }


    public static void updatePieces(final Player p, final CheckersGame g) {
        for(int i = 0; i < 64; i++){
            p.getActionSender().sendUpdateItem(10494, g.getPiece(i), new Item(i, 1));
        }
    }

    public static void takePiece(final CheckersGame g, final int i, final int p) {
        g.setPieceSlot(i, -1);
        g.subtractPiece(p);
        g.setPieceTaken(true);
    }

    public static boolean validMove(final CheckersGame g, final int slotFrom, final int slotTo, final int type) {
        final int[] leftPieces = {0, 8, 16, 24, 32, 40, 48, 56};
        final int[] rightPieces = {7, 15, 23, 31, 39, 47, 55, 63};
        boolean leftPiece = false;
        boolean rightPiece = false;
        /**
         * Piece is on far left of board?
         **/
        for(int i = 0; i < leftPieces.length; i++){
            if(slotFrom == leftPieces[i]){
                leftPiece = true;
            }
        }
        /**
         * Piece is on far right of board?
         **/
        for(int i = 0; i < rightPieces.length; i++){
            if(slotFrom == rightPieces[i]){
                rightPiece = true;
            }
        }
        /**
         * New position is occupied?
         **/
        if(g.getPiece(slotTo) > 0)
            return false;
        /**
         * Taking pieces
         **/
        switch(type){
            case 0:
                if(slotFrom + 18 == slotTo){
                    if(g.getPiece(slotFrom + 9) == g.getPlayerPiece(1)){
                        takePiece(g, slotFrom + 9, 1);
                        return true;
                    }
                }
                if(slotFrom + 14 == slotTo){
                    if(g.getPiece(slotFrom + 7) == g.getPlayerPiece(1)){
                        takePiece(g, slotFrom + 7, 1);
                        return true;
                    }
                }
                break;
            case 1:
                if(slotFrom - 18 == slotTo){
                    if(g.getPiece(slotFrom - 9) == g.getPlayerPiece(0)){
                        takePiece(g, slotFrom - 9, 0);
                        return true;
                    }
                }
                if(slotFrom - 14 == slotTo){
                    if(g.getPiece(slotFrom - 9) == g.getPlayerPiece(0)){
                        takePiece(g, slotFrom - 7, 0);
                        return true;
                    }
                }
                break;
        }
        /**
         * Movement is valid?
         **/
        switch(type){
            case 0:
                if(leftPiece){
                    if((slotFrom + 9) == slotTo)
                        return true;
                }
                if(rightPiece){
                    if((slotFrom + 7) == slotTo)
                        return true;
                }
                if(!leftPiece && !rightPiece){
                    if(((slotFrom + 7) == slotTo) || ((slotFrom + 9) == slotTo))
                        return true;
                }
                break;
            case 1:
                if(leftPiece){
                    if((slotFrom - 7) == slotTo)
                        return true;
                }
                if(rightPiece){
                    if((slotFrom - 9) == slotTo)
                        return true;
                }
                if(!leftPiece && !rightPiece){
                    if(((slotFrom - 7) == slotTo) || ((slotFrom - 9) == slotTo))
                        return true;
                }
                break;
        }
        return false;
    }

    public static boolean inGame(final Player p) {
        for(final CheckersGame cg : games){
            if(cg.getPlayerOne() == p || cg.getPlayerTwo() == p){
                return true;
            }
        }
        return false;
    }

    public static void newGame(final Player p, final Player o) {
        final CheckersGame g = new CheckersGame(p, o);
        games.add(g);
        p.getActionSender().showInterface(10380);
        o.getActionSender().showInterface(10380);
        p.inGame = true;
        o.inGame = true;
    }

    public static void sendRequest(final Player p, final Player o) {
        if(inGame(p)){
            p.getActionSender().sendMessage("You are already in a game.");
            return;
        }
        if(inGame(o)){
            p.getActionSender().sendMessage("The other player is busy at the moment.");
            return;
        }
        p.getActionSender().sendMessage("Sending draughts request...");
        o.getActionSender().sendMessage(p.getName() + ":checkreq:");
        o.challengedBy = p;
    }

    public static void acceptRequest(final Player p) {
        if(p.challengedBy == null){
            return;
        }
        final Player o = p.challengedBy;
        if(o == null){
            return;
        }
        if(inGame(p)){
            p.getActionSender().sendMessage("You are already in a game.");
            return;
        }
        if(inGame(o)){
            p.getActionSender().sendMessage("The other player is busy at the moment.");
            return;
        }
        newGame(p, o);
    }

    public void endGame(final Player p) {
        for(final CheckersGame cg : games){
            if(cg.getPlayerOne() == p || cg.getPlayerTwo() == p){
                p.getActionSender().sendMessage("You resign from the game");
                final Player o = p.challengedBy;
                o.getActionSender().sendMessage("Your opponent has resigned from the game.");
                o.getActionSender().removeAllInterfaces();
                games.remove(cg);
                p.challengedBy = null;
                return;
            }
        }
    }
}
