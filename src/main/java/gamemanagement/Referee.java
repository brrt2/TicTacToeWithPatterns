package gamemanagement;
import gamemanagement.configuration.TilesToWin;
import gamemanagement.moveManagement.Move;
import gamemanagement.tiles.TakenTileSign;
import gamemanagement.tiles.Tile;
import gamemanagement.validation.Score;
import players.Player;

import java.util.ArrayList;
import java.util.List;

public class Referee implements Observer{

    private Board board;
    private TilesToWin tilesToWin;
    private Score score;
    GameState gameState;


    public Referee(Board board,TilesToWin tilesToWin,Score score) {
        this.board = board;
        this.tilesToWin = tilesToWin;
        this.score=score;
    }

    @Override
    public void update(Move move) {
        checkIfWonHorizontally(move.getPlayerThatMadeTheMove(),move.getIndex());
    }

    void checkIfWonHorizontally(Player currentPlayer, int index) {
        int row = (index-1)/board.getColumn();
        ArrayList<Tile> tilesHorizontal = new ArrayList<>(board.getColumn());

        for(int i=0; i<board.getColumn(); i++){
            tilesHorizontal.add(board.getPlayBoard().get(row*board.getColumn()+i));
        }

        if(horizontalIterator(tilesHorizontal,currentPlayer)){
            gameState=GameState.WIN;
            return;
        }
        else checkIfWonVertically(currentPlayer,index);

    }

    private boolean horizontalIterator(List<Tile> tilesHorizontal,Player currentPlayer){

        int counter =0;
        for (int i = 0; i <tilesHorizontal.size(); i++) {
            if (currentPlayer.getTakenTileSign().equals(tilesHorizontal.get(i).getTakenTileSign())) counter++;
            else counter = 0;
            if (counter == tilesToWin.getValue()) {
                score.increaseScore(currentPlayer);
                board.setMoveCounter(0);
                return true;
            }
        }
        return false;
    }

    void checkIfWonVertically(Player currentPlayer, int number) {

        int counter = 0;
        for (int i = number - 1; i >= 0; i -= board.getColumn()) {
            if (currentPlayer.getTakenTileSign().equals(board.getPlayBoard().get(i).getTakenTileSign())) counter++;
            else if (counter >= 2) {
                counter += 0;
            } else counter = 0;
        }
        for (int i = number - 1; i < board.getPlayBoard().size(); i += board.getColumn()) {
            if (currentPlayer.getTakenTileSign().equals(board.getPlayBoard().get(i).getTakenTileSign())) counter++;
            else counter = 0;
            if (counter - 1 == tilesToWin.getValue()) {
                score.increaseScore(currentPlayer);
                board.setMoveCounter(0);
                gameState=GameState.WIN;
                return;
            }
        }
        checkDiagonalLeftToRight(currentPlayer,number);
    }

    void checkDiagonalLeftToRight(Player currentPlayer, int number) {
        int index =number;
        ArrayList<Tile> tilesOnDiagonal = new ArrayList<>();
        while (index - board.getColumn() - 1 > 0) {
            index = index - board.getColumn() - 1;
            if (index % board.getColumn() == 1) {
                break;
            }
        }
        while (index <= board.getPlayBoard().size()) {
            tilesOnDiagonal.add(board.getPlayBoard().get(index - 1));
            index += (board.getColumn() + 1);
        }

        if(iterate(tilesOnDiagonal,currentPlayer)){
            gameState=GameState.WIN;
            return;
        }
        else checkDiagonalRL(currentPlayer,number);
    }

    void checkDiagonalRL(Player currentPlayer, int index) {
        ArrayList<Tile> tiles = new ArrayList<>();
        while (index - board.getColumn() > 0) {
            index = index - board.getColumn() + 1;
            if (index % board.getColumn() == 0) {
                break;
            }
        }
        while (index < board.getPlayBoard().size()) {
            tiles.add(board.getPlayBoard().get(index - 1));
            if (index % board.getColumn() == 1) {
                break;
            }
            index += (board.getColumn() - 1);
        }
        if(iterate(tiles,currentPlayer)){
            gameState=GameState.WIN;
            return;
        }
        else checkIfDraw();

    }

    private boolean iterate(List<Tile> tiles,Player currentPlayer){
        int counter =0;
        for (int i = 0; i < tiles.size(); i++) {
            if (currentPlayer.getTakenTileSign().equals(tiles.get(i).getTakenTileSign())) counter++;
            if (counter == tilesToWin.getValue()) {
                score.increaseScore(currentPlayer);
                board.setMoveCounter(0);
                return true;
            }
        }
        return false;

    }

    void checkIfDraw() {
        if (board.getMoveCounter() == board.getPlayBoard().size()) {
            board.setMoveCounter(0);
            score.increaseScoreDraw();
            gameState=GameState.WIN;
            return;
        }
    }

    boolean checkIfWonMatch(Player currentPlayer) {

        if (score.getRoundNumber() == score.getNumberOfRounds().getValue()) {
            if (currentPlayer.getTakenTileSign().equals(TakenTileSign.CROSS) && score.getCrossPlayerPoints() > score.getNoughtPlayerPoints()) {
                return true;
            } else if (currentPlayer.getTakenTileSign().equals(TakenTileSign.NOUGHT) && score.getCrossPlayerPoints() < score.getNoughtPlayerPoints()) {
                return true;
            }
        }
        return false;
    }

    boolean checkIfDrawEndMatch() {
        if (score.getRoundNumber() == score.getNumberOfRounds().getValue()) {
            if (score.getNoughtPlayerPoints() == score.getCrossPlayerPoints()) {
                return true;
            }
        }
        return false;
    }

    Score getScore() {
        return score;
    }

    public Board getBoard() {
        return board;
    }


}
