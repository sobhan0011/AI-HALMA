import java.security.SecureRandom;

public class Zobrist {
    static long[][] zobristArray = new long[2][64];
    static long zobristBlueMove;
    private static long random64() {
        SecureRandom random = new SecureRandom();
        return random.nextLong();
    }

    public static void zobristFillArray() {
        for (int color = 0; color < 2; color++)
            for (int square = 0; square < 64; square++)
                zobristArray[color][square] = random64();

        zobristBlueMove = random64();
    }
    
    public static long getZobristHash(Tile[][] currentBoard, int blueToMove) {
        long zobristKey = 0;
        for (int row = 0; row <= 7; row++) {
            for (int column = 0; column <= 7; column++) {
                if (currentBoard[row][column].color == 1)
                    zobristKey ^= zobristArray[0][row * 8 + column];
                else
                    zobristKey ^= zobristArray[1][row * 8 + column];
            }
        }
        if (blueToMove == 1)
            zobristKey ^= zobristBlueMove;

        return zobristKey;
    }
}
