import java.util.Hashtable;

public class TranspositionTable {
    Hashtable<Long, NodeInfo> nodes = new Hashtable<>();

    public TranspositionTable() {
        Zobrist.zobristFillArray();
    }

    public Hashtable<Long, NodeInfo> getNodes() {
        return nodes;
    }

    public void addNode(Tile[][] board, int eval, int alpha, int beta, Move bestMove, int playerTurn) {
        long zobrist = Zobrist.getZobristHash(board, playerTurn);
        NodeInfo nodeInfo = new NodeInfo(eval, alpha, beta, bestMove);
        nodes.put(zobrist, nodeInfo);
    }

}

