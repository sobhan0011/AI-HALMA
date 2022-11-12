import java.util.LinkedList;
import java.util.List;

public class Agent2 extends Agent {
    private byte playerTurn;

    public Agent2(Board board) {
        super(board);
    }


//    public Move doMinMaxP2(Tile[][] tiles, byte playerTurn) {
//        Pair temp = max2(tiles, playerTurn, (byte) (0));
//        this.playerTurn = playerTurn;
//        return temp.move;
//    }
//
//
//    private Pair max2(Tile[][] currentBoard, byte currentColor, byte depth) {
//        byte MAX_DEPTH = 3;
//        int maxValue = Integer.MIN_VALUE, value;
//        Move bestMove = null;
//        List<Move> possibleMoves = createPossibleMoves(currentBoard, currentColor);
//        boolean cutOFFIsReached = (depth + 1) >= MAX_DEPTH;
//
//        if (checkTerminal(currentBoard))
//            return new Pair(null, Integer.MIN_VALUE); //
//
//        for (Move possibleMove : possibleMoves) {
//            value = cutOFFIsReached ? opponent_evaluate(board.doMove(possibleMove, currentBoard), currentBoard.clone(), currentColor) : min2(board.doMove(possibleMove, currentBoard), (byte) (currentColor == 0 ? 1 : 0), (byte) (depth + 1)).value;
//            if (value > maxValue) {
//                maxValue = value;
//                bestMove = possibleMove;
//            }
//        }
//        return new Pair(bestMove, maxValue);
//        // return new Pair(null, 0);
//    }

    //    private Pair min2(Tile[][] currentBoard, byte currentColor, byte depth) {
//        byte MAX_DEPTH = 3;
//        int minValue = Integer.MAX_VALUE, value;
//        Move worstMove = null;
//        List<Move> possibleMoves = createPossibleMoves(currentBoard, currentColor);
//        boolean cutOFFIsReached = (depth + 1) >= MAX_DEPTH;
//
//        if (checkTerminal(currentBoard))
//            return new Pair(null, Integer.MAX_VALUE); //
//
//        for (Move possibleMove : possibleMoves) {
//
//            value = cutOFFIsReached ? opponent_evaluate(board.doMove(possibleMove, currentBoard), currentBoard.clone(),currentColor) : max2(board.doMove(possibleMove, currentBoard), (byte) (currentColor == 0 ? 1 : 0), (byte) (depth + 1)).value;
//            if (value < minValue)
//            {
//                minValue = value;
//                worstMove = possibleMove;
//            }
//        }
//        return new Pair(worstMove, minValue);
//        // return new Pair(null, 0);
//    }
//


    @Override
    public int evaluate(Tile[][] currentBoard, Tile[][] parentBoard, byte currentColor) {
        short score = 0;
        for (byte i = 0; i < currentBoard.length; i++) {
            for (byte j = 0; j < currentBoard.length; j++) {
                if (currentBoard[i][j].color == playerTurn) {
                    score += (7 - i);
                    score += (7 - j);
                } else if (currentBoard[i][j].color == (3 - playerTurn)) {

                    score -= i;
                    score -= j;

                }
            }
        }
        return score;
    }



}