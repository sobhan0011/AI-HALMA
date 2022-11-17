import java.util.LinkedList;
import java.util.List;

public class Agent {
    private final Board board;
    private byte playerTurn;
    private final TranspositionTable transpositionTable = new TranspositionTable();
    public Agent(Board board) {
        this.board = board;
    }

    public Move doMinMaxAlphaBeta(Tile[][] tiles, byte playerTurn) {
        Pair temp = max(tiles, playerTurn, (byte) (0), Integer.MIN_VALUE, Integer.MAX_VALUE/*, transpositionTable*/);
        this.playerTurn = playerTurn;
        return temp.move;
    }

    private Pair max(Tile[][] currentBoard, byte currentColor, byte depth, int alpha, int beta/*, TranspositionTable transpositionTable*/) {
        if (depth != 0)
            playerTurn = (byte) (3 - playerTurn);
        byte MAX_DEPTH = 4;
        int maxValue = Integer.MIN_VALUE, value, eval;
        Move bestMove = null;
//
        boolean cutOFFIsReached = (depth + 1) >= MAX_DEPTH;
        Tile[][] newBoard;

        if (checkTerminal(currentBoard))
            return new Pair(null, Integer.MIN_VALUE); //MAX MAX -- MIN MAX

        long zobristHash = Zobrist.getZobristHash(currentBoard, playerTurn);

        NodeInfo nodeInfo = null, childInfo = null;
        if (transpositionTable.contains(currentBoard, playerTurn))
            nodeInfo = transpositionTable.getNodes().get(zobristHash);

        if (nodeInfo != null && nodeInfo.getDepth() == 3 && nodeInfo.getBestMove() != null)
            return new Pair(transpositionTable.getNodes().get(zobristHash).getBestMove(), transpositionTable.getNodes().get(zobristHash).getAlpha());

        List<Move> possibleMoves = createPossibleMoves(currentBoard, currentColor);

        for (Move possibleMove : possibleMoves) {
            newBoard = board.doMove(possibleMove, currentBoard);
            if (transpositionTable.contains(newBoard, playerTurn))
                childInfo = transpositionTable.getNodes().get(zobristHash);
            eval = (childInfo != null) ? childInfo.getEval() : evaluate(newBoard);

            if (cutOFFIsReached)
                value = eval;
            else if (childInfo != null && childInfo.getWorstMove() != null)
                value = childInfo.getBeta();
            else
                value = min(newBoard, (byte) (currentColor == 0 ? 1 : 0), (byte) (depth + 1), alpha, beta).value;
            if (value > maxValue)
            {
                maxValue = value;
                bestMove = possibleMove;
            }
            if (maxValue >= beta)
                return new Pair(bestMove, maxValue);
            alpha = Math.max(alpha, maxValue);
        }

        if (depth == 3 && nodeInfo == null)
            transpositionTable.addNode(currentBoard, evaluate(currentBoard), alpha, 0, bestMove, null, playerTurn, 3);
        else if (nodeInfo == null)
            transpositionTable.addNode(currentBoard, evaluate(currentBoard), 0, 0, null, null, playerTurn,  0);
        else if (depth == 3 && nodeInfo.getBestMove() == null)
        {
            nodeInfo.setDepth(3);
            nodeInfo.setAlpha(alpha);
            nodeInfo.setBestMove(bestMove);
        }


        return new Pair(bestMove, maxValue);
    }

    private Pair min(Tile[][] currentBoard, byte currentColor, byte depth, int alpha, int beta) {
        playerTurn = (byte) (3 - playerTurn);
        byte MAX_DEPTH = 4;
        int minValue = Integer.MAX_VALUE, value, eval;
        Move worstMove = null;
        boolean cutOFFIsReached = (depth + 1) >= MAX_DEPTH;
        Tile[][] newBoard;
        if (checkTerminal(currentBoard))
            return new Pair(null, Integer.MAX_VALUE);

        long zobristHash = Zobrist.getZobristHash(currentBoard, playerTurn), childZobrist;

        NodeInfo nodeInfo = null, childInfo = null;
        if (transpositionTable.contains(currentBoard, playerTurn))
            nodeInfo = transpositionTable.getNodes().get(zobristHash);

        if (nodeInfo != null && nodeInfo.getDepth() == 3 && nodeInfo.getWorstMove() != null)
            return new Pair(transpositionTable.getNodes().get(zobristHash).getWorstMove(), transpositionTable.getNodes().get(zobristHash).getBeta());

        List<Move> possibleMoves = createPossibleMoves(currentBoard, currentColor);

        for (Move possibleMove : possibleMoves) {

            newBoard = board.doMove(possibleMove, currentBoard);
            if (transpositionTable.contains(newBoard, playerTurn))
                childInfo = transpositionTable.getNodes().get(zobristHash);
            eval = (childInfo != null) ? childInfo.getEval() : evaluate(newBoard);

            if (cutOFFIsReached)
                value = eval;
            else if (childInfo != null && childInfo.getBestMove() != null)
                value = childInfo.getAlpha();
            else
                value = max(newBoard, (byte) (currentColor == 0 ? 1 : 0), (byte) (depth + 1), alpha, beta).value;
            if (value < minValue)
            {
                minValue = value;
                worstMove = possibleMove;
            }
           if (minValue <= alpha)
                return new Pair(worstMove, minValue);
            beta = Math.min(beta, minValue);
        }

        if (depth == 3 && nodeInfo == null)
            transpositionTable.addNode(currentBoard, evaluate(currentBoard), 0, beta, null, worstMove, playerTurn, 3);
        else if (nodeInfo == null)
            transpositionTable.addNode(currentBoard, evaluate(currentBoard), 0, 0, null, null, playerTurn,  0);
        else if (depth == 3 && nodeInfo.getWorstMove() == null)
        {
            nodeInfo.setDepth(3);
            nodeInfo.setBeta(beta);
            nodeInfo.setWorstMove(worstMove);
        }


        return new Pair(worstMove, minValue);
    }

    private int evaluate(Tile[][] currentBoard) {
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