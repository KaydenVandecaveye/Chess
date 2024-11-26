package CSCI1933P2;
public class Board {
    // Instance variables (add more if you need)
    private final Piece[][] board;

    //default constructor
    public Board() {
        // initialize the board to chessboard dimensions.
        this.board = new Piece[8][8];
    }
    // Accessor Methods
    /**
     * Gets the piece at a particular row and column of the board.
     * @param row       The row of the piece to be accessed.
     * @param col       The column of the piece to be accessed.
     * @return          The piece at the specified row and column of the board.
     */
    public Piece getPiece(int row, int col) {
        return this.board[row][col];
    }
    /**
     * Sets the piece at a particular row and column of the board.
     * @param row       The row to place the piece at.
     * @param col       The column to place the piece at.
     * @param piece     The piece to place at the specified row and column.
     */
    public void setPiece(int row, int col, Piece piece) {
        piece.col = col;
        piece.row = row;
        board[row][col] = piece;
    }

    // Movement helper functions
    /**
     * Verifies that the source and destination of a move are valid by performing the following checks:
     *  1. ALL rows and columns provided must be >= 0.
     *  2. ALL rows and columns provided must be < 8.
     *  3. The start position of the move must contain a piece.
     *  4. The piece at the starting position must be the correct color.
     *  5. The destination must be empty OR must contain a piece of the opposite color.
     * @param startRow  The starting row of the move.
     * @param startCol  The starting column of the move.
     * @param endRow    The ending row of the move.
     * @param endCol    The ending column of the move.
     * @param isBlack   The expected color of the starting piece.
     * @return True if the above conditions are met, false otherwise.
     */
    public boolean verifySourceAndDestination(int startRow, int startCol, int endRow, int endCol, boolean isBlack) {
        // verify rows and columns are correct inputs
        if ((startRow >= 0 && startRow < 8) && (endRow >= 0 && endRow < 8) &&
                (startCol >= 0 && startCol < 8) && (endCol >= 0 && endCol < 8)) {
            // verify there is a piece at the given starting position
            if (getPiece(startRow,startCol) != null) {
                // verify piece at starting position is correct color
                if ((board[startRow][startCol]).isBlack == isBlack) {
                    // verify destination is of opposite color or is empty
                    return ((board[endRow][endCol] == null)) || (board[endRow][endCol]).isBlack != isBlack;
                }
            }
        }
        return false;
    }

    /**
     * Verifies that the source and destination of a move are adjacent squares (within 1 square of each other)
     * Example, Piece P is adjacent to the spots marked X:
     * OOOOO
     * OXXXO
     * OXPXO
     * OXXXO
     * OOOOO
     * @param startRow  The starting row of the move.
     * @param startCol  The starting column of the move.
     * @param endRow    The ending row of the move.
     * @param endCol    The ending column of the move.
     * @return True if the source and destination squares are adjacent, false otherwise.
     */

    public boolean verifyAdjacent(int startRow, int startCol, int endRow, int endCol) {
        if ((startRow == endRow) && (startCol == endCol)) {
            return true;
        }
        else {
            return ((startRow - 1 <= endRow && endRow <= startRow + 1) && (startCol - 1 <= endCol && endCol <= startCol + 1));
        }
    }

    /**
     * Verifies that a source and destination are in the same row and that there are no pieces on squares
     * between the source and the destination.
     * @param startRow  The starting row of the move.
     * @param startCol  The starting column of the move.
     * @param endRow    The ending row of the move.
     * @param endCol    The ending column of the move.
     * @return True if source and destination are in same row with no pieces between them, false otherwise.
     */
    // y = y and x != x
    public boolean verifyHorizontal(int startRow, int startCol, int endRow, int endCol) {
        if ((startRow == endRow) && (startCol == endCol)) {
            return true;
        }
        if((this.board[startCol] != this.board[endCol]) && (this.board[endRow] == this.board[startRow])) {
            //check for obstructions in pieces path
            if (startCol < endCol) {
                for (int i = startCol + 1;i < endCol; i++) {
                    if (this.board[endRow][i] != null) {
                        return false;
                    }
                }
                return true;
            }
            else if (startCol > endCol) {
                for (int i = endCol + 1;i < startCol; i++) {
                    if (this.board[endRow][i] != null) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies that a source and destination are in the same column and that there are no pieces on squares
     * between the source and the destination.
     * @param startRow  The starting row of the move.
     * @param startCol  The starting column of the move.
     * @param endRow    The ending row of the move.
     * @param endCol    The ending column of the move.
     * @return True if source and destination are in same column with no pieces between them, false otherwise.
     */
    // y != y and x = x
    public boolean verifyVertical(int startRow, int startCol, int endRow, int endCol) {
        if ((startRow == endRow) && (startCol == endCol)) {
            return true;
        }
        if((this.board[startCol] == this.board[endCol]) && (this.board[endRow] != this.board[startRow])) {
            // check for obstructions in pieces path
            if (startRow < endRow) {
                for (int i = startRow + 1;i < endRow; i++) {
                    if (this.board[i][startCol] != null) {
                        return false;
                    }
                }
                return true;
            }
            else if (startRow > endRow) {
                for (int i = endRow + 1;i < startRow; i++) {
                    if (this.board[i][startCol] != null) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies that a source and destination are on the same diagonal and that there are no pieces on squares
     * between the source and the destination.
     * @param startRow  The starting row of the move.
     * @param startCol  The starting column of the move.
     * @param endRow    The ending row of the move.
     * @param endCol    The ending column of the move.
     * @return True if source and destination are on the same diagonal with no pieces between them, false otherwise.
     */
    // abs(delta x) = abs(delta y)
    public boolean verifyDiagonal(int startRow, int startCol, int endRow, int endCol) {
        if ((startRow == endRow) && (startCol == endCol)) {
            return true;
        }
        if (Math.abs(endRow - startRow) == Math.abs(endCol - startCol)) {
            // up right direction
            if ((endRow > startRow) && (endCol > startCol)) {
                startRow ++;
                startCol ++;
                // iterate through pieces path
                while (startRow < endRow && startCol < endCol) {
                    if (this.board[startRow][startCol] != null) {
                        return false;
                    }
                    startRow ++;
                    startCol ++;
                }
                return true;
            }
            // up left direction
            else if ((endRow < startRow) && (endCol > startCol)) {
                // iterate through pieces path
                startRow --;
                startCol ++;
                while (startRow > endRow && startCol < endCol) {
                    if (this.board[startRow][startCol] != null) {
                        return false;
                    }
                    startRow --;
                    startCol ++;
                }
                return true;
            }
            // down right direction
            else if ((endRow > startRow) && (endCol < startCol)) {
                // iterate through pieces path
                startRow ++;
                startCol --;
                while (startRow < endRow && startCol > endCol) {
                    if (this.board[startRow][startCol] != null) {
                        return false;
                    }
                    startRow ++;
                    startCol --;
                }
                return true;
            }
            // down left direction
            else if ((endRow < startRow) && (endCol < startCol)) {
                // iterate through pieces path
                startRow --;
                startCol --;
                while (startRow > endRow && startCol > endCol) {
                    if (this.board[startRow][startCol] != null) {
                        return false;
                    }
                    startRow --;
                    startCol --;
                }
                return true;
            }
        }
        return false;
    }

    public boolean verifyKnight(int startRow, int startCol, int endRow, int endCol) {
        // vertically "branching" knight moves (ex: down 2 left 1 or up 2 right 1)
        if ((startCol == endCol + 2) || (startCol == endCol - 2)) {
            return (startRow == endRow + 1) || (startRow == endRow - 1);
        }
        // horizontally "branching" knight moves (ex: right 2 down 1 or left 2 up 1)
        else if ((startRow == endRow + 2) || (startRow == endRow - 2)) {
            return (startCol == endCol + 1 || (startCol == endCol - 1));
        }
        return false;
    }

    // Game functionality methods
    /**
     * Moves the piece from startRow, startCol to endRow, endCol if it is legal to do so.
     * IMPORTANT: Make sure to update the internal position of the piece, and the starting position of the piece to null!
     * @param startRow  The starting row of the move.
     * @param startCol  The starting column of the move.
     * @param endRow    The ending row of the move.
     * @param endCol    The ending column of the move.
     * @return Whether the move was successfully completed or not. (Moves are not completed if they are not legal.)
     */
    public boolean movePiece(int startRow, int startCol, int endRow, int endCol) {
        if (board[startRow][startCol] != null) {
            if (verifySourceAndDestination(startRow, startCol, endRow, endCol, board[startRow][startCol].isBlack) && board[startRow][startCol].isMoveLegal(this, endRow,endCol)) {
                Piece piece = board[startRow][startCol];
                board[endRow][endCol] = piece;
                board[startRow][startCol] = null;
                piece.setPosition(endRow, endCol);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there are fewer than TWO kings on the board.
     * @return If the game is in a game over state.
     */
    public boolean isGameOver() {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    if (board[i][j] instanceof King) {
                        count++;
                    }
                }
            }
        }
        return !(count == 2);
    }

    /**
     * Sets all indexes in the board to null
     */
    public void clear() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = null;
            }
        }
    }

    public void display() {
        System.out.print("\t\t\t");
        for (int i = 0; i < 8; i++) {
            System.out.print(i + "\t\t");
        }
        System.out.print("\n");
        for (int i = 0; i < 8; i++) {
            System.out.print("\t" + i + "\t");
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null) {
                    System.out.print("|\t\t");
                } else {
                    System.out.print("|\t" + board[i][j] + "\t");
                }
            }
            System.out.print("|\n");
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(" ");
        for(int i = 0; i < 8; i++){
            out.append(" ");
            out.append(i);
        }
        out.append('\n');
        for(int i = 0; i < board.length; i++) {
            out.append(i);
            out.append("|");
            for(int j = 0; j < board[0].length; j++) {
                out.append(board[i][j] == null ? "\u2001|" : board[i][j] + "|");
            }
            out.append("\n");
        }
        return out.toString();
    }
}

