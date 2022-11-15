public class NodeInfo {
    private int eval, alpha, beta;
    Move bestMove;
    public NodeInfo(int eval, int alpha, int beta, Move bestMove) {
        eval = this.eval;
        alpha = this.alpha;
        beta = this.beta;
        this.bestMove = bestMove;
    }
}
