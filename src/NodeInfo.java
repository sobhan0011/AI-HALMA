public class NodeInfo {
    private final int eval;
    private int alpha, beta, depth;
    private Move bestMove, worstMove;

    public NodeInfo(int eval, int alpha, int beta, Move bestMove, int depth, Move worstMove) {
        this.eval = eval;
        this.alpha = alpha;
        this.beta = beta;
        this.depth = depth;
        this.bestMove = bestMove;
        this.worstMove = worstMove;
    }

    public void setBestMove(Move bestMove) {
        this.bestMove = bestMove;
    }

    public void setWorstMove(Move worstMove) {
        this.worstMove = worstMove;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public Move getBestMove() {
        return bestMove;
    }

    public Move getWorstMove() {
        return worstMove;
    }

    public int getAlpha() {
        return alpha;
    }

    public int getBeta() {
        return beta;
    }

    public int getEval() {
        return eval;
    }
}
