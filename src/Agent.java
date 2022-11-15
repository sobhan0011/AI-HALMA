import java.util.LinkedList;
import java.util.List;

public class Agent {
    private final Board board;
    private byte playerTurn;
    public Agent(Board board) {
        this.board = board;
    }

    public Move doMinMaxAlphaBeta(Tile[][] tiles, byte playerTurn) {
        TranspositionTable transpositionTable = new TranspositionTable();
        Pair temp = max(tiles, playerTurn, (byte) (0), Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.playerTurn = playerTurn;
        return temp.move;
    }

    private Pair max(Tile[][] currentBoard, byte currentColor, byte depth, int alpha, int beta) {
        if (depth != 0)
            playerTurn = (byte) (3 - playerTurn);
        byte MAX_DEPTH = 3;
        int maxValue = Integer.MIN_VALUE, value, eval;
        Move bestMove = null;
        List<Move> possibleMoves = createPossibleMoves(currentBoard, currentColor);
        boolean cutOFFIsReached = (depth + 1) >= MAX_DEPTH;

        if (checkTerminal(currentBoard))
            return new Pair(null, Integer.MIN_VALUE); //MAX MAX -- MIN MAX

            for (Move possibleMove : possibleMoves) {
                eval = evaluate(board.doMove(possibleMove, currentBoard), currentBoard.clone(), currentColor);
                if (cutOFFIsReached)
                    value = eval;
                else
                    value = min(board.doMove(possibleMove, currentBoard), (byte) (currentColor == 0 ? 1 : 0), (byte) (depth + 1), alpha, beta).value;
                if (value > maxValue)
                {
                    maxValue = value;
                    bestMove = possibleMove;
                }
                if (maxValue >= beta)
                    return new Pair(bestMove, maxValue);
                alpha = Math.max(alpha, maxValue);
            }
            return new Pair(bestMove, maxValue);
    }

    private Pair min(Tile[][] currentBoard, byte currentColor, byte depth, int alpha, int beta) {
        playerTurn = (byte) (3 - playerTurn);
        byte MAX_DEPTH = 3;
        int minValue = Integer.MAX_VALUE, value, eval;
        Move worstMove = null;
        List<Move> possibleMoves = createPossibleMoves(currentBoard, currentColor);
        boolean cutOFFIsReached = (depth + 1) >= MAX_DEPTH;

        if (checkTerminal(currentBoard))
            return new Pair(null, Integer.MAX_VALUE);

        for (Move possibleMove : possibleMoves) {
            eval = evaluate(board.doMove(possibleMove, currentBoard), currentBoard.clone(),currentColor);
            if (cutOFFIsReached)
                value = eval;
            else
                value = max(board.doMove(possibleMove, currentBoard), (byte) (currentColor == 0 ? 1 : 0), (byte) (depth + 1), alpha, beta).value;
            if (value < minValue)
            {
                minValue = value;
                worstMove = possibleMove;
            }
           if (minValue <= alpha)
                return new Pair(worstMove, minValue);
            beta = Math.min(beta, minValue);
        }
        return new Pair(worstMove, minValue);
    }

    private int evaluate(Tile[][] currentBoard, Tile[][] parentBoard, byte currentColor) {
        short score = 0;
        for (byte i = 0; i < currentBoard.length; i++)
            for (byte j = 0; j < currentBoard.length; j++)
                if (currentBoard[i][j].color == playerTurn) {
                    score += (7 - i);
                    score += (7 - j);
                } else if (currentBoard[i][j].color == (3 - playerTurn)) {
                    score -= i;
                    score -= j;
                }
        return score;
    }

    private int ourEvaluate(Tile[][] currentBoard, Tile[][] parentBoard, byte currentColor) {
        int groundCapturedCurrent = capturedEnemyGround(currentBoard, currentColor);
        int groundCapturedParent = capturedEnemyGround(parentBoard, currentColor);
        int groundLostCurrent = capturedEnemyGround(parentBoard, (byte) (3 - currentColor));
        if (groundCapturedCurrent > groundCapturedParent)
            return 2000;
        else
            return sumOfEuclideanDistanceFromEnemyGround(currentBoard, (byte) (3 - currentColor)) / 2 + 100 * (groundCapturedCurrent - groundLostCurrent) - sumOfEuclideanDistanceFromEnemyGround(currentBoard, currentColor);
     }

    private int capturedEnemyGround(Tile[][] currentBoard, byte currentColor) {
        int startXEnemyGround = ((currentColor == 1) ? 0 : 4), startYEnemyGround = ((currentColor == 1) ? 0 : 4),
                finishXEnemyGround = ((currentColor == 1) ? 3 : 7), finishYEnemyGround = ((currentColor == 1) ? 3 : 7);
        int enemySideCaptured = 0, enemyColor = (currentColor == 1) ? 2 : 1;
        for (int y = startYEnemyGround; y <= finishYEnemyGround; y++)
            for (int x = startXEnemyGround; x <= finishXEnemyGround; x++)
                if (currentBoard[y][x].zone == enemyColor && currentBoard[y][x].color == currentColor && currentColor == 2 && x + y >= 11)
                    enemySideCaptured++;
                else if (currentBoard[y][x].zone == enemyColor && currentBoard[y][x].color == currentColor && currentColor == 1 && x + y <= 3)
                    enemySideCaptured++;
        return enemySideCaptured;
    }

    private int sumOfEuclideanDistanceFromEnemyGround(Tile[][] currentBoard, byte currentColor) {
        int startXEnemyGround = ((currentColor == 1) ? 0 : 4), startYEnemyGround = ((currentColor == 1) ? 0 : 4),
                finishXEnemyGround = ((currentColor == 1) ? 3 : 7), finishYEnemyGround = ((currentColor == 1) ? 3 : 7);
        double sumOfEuclideanDistance = 0;
        for (int i = 0; i < currentBoard.length; i++)
            for (int j = 0; j < currentBoard[i].length; j++)
                if (currentBoard[i][j].color == currentColor)
                    for (int y = startYEnemyGround; y <= finishYEnemyGround; y++)
                        for (int x = startXEnemyGround; x <= finishXEnemyGround; x++) {
                            double euclideanDistance = Math.sqrt((Math.pow(i - y, 2) + (Math.pow(j - x, 2))));
                            if (currentColor == 2 && x + y >= 11 && currentBoard[y][x].color == 0)
                                sumOfEuclideanDistance += euclideanDistance;
                            else if (currentColor == 1 && x + y <= 3 && currentBoard[y][x].color == 0)
                                sumOfEuclideanDistance += euclideanDistance;
                        }

        return (int) (sumOfEuclideanDistance);
    }

    public List<Move> createPossibleMoves(Tile[][] newBoard, int currentColor) {
        List<Move> possibleMoves = new LinkedList<>();
        for (byte i = 0; i < 8; i++)
            for (byte j = 0; j < 8; j++)
                if (newBoard[i][j].color == currentColor) {
                    List<Tile> legalTiles = new LinkedList<>();
                    board.findPossibleMoves(newBoard, newBoard[i][j], legalTiles, newBoard[i][j], true);
                    for (Tile tile : legalTiles)
                        possibleMoves.add(new Move(newBoard[i][j], tile));
                }
        return possibleMoves;
    }

    public boolean checkTerminal(Tile[][] currentTiles) {
        byte redCounter, blueCounter;
        redCounter = blueCounter = 0;

        for (byte x = 0; x < 8; x++)
            for (byte y = 0; y < 8; y++)
                if (currentTiles[x][y].zone == 1 && currentTiles[x][y].color == 2)
                    if (++redCounter >= 10)
                        return true;
                    else if (currentTiles[x][y].zone == 2 && currentTiles[x][y].color == 1)
                        if (++blueCounter >= 10)
                            return true;
        return false;
    }
}