package CSCI1933P2;

public class King extends Piece {
    private boolean hasMoved = false;

    public King(int row, int col, boolean isBlack) {
        super.col = col;
        super.row = row;
        super.isBlack = isBlack;

        if (super.isBlack) {
            super.representation = '\u265A';
        }
        else {
            super.representation = '\u2654';
        }
    }

    public void setHasMoved(boolean moved) {
        hasMoved = moved;
    }

    @Override
    public boolean canMoveTo(Board board, int endRow, int endCol) {
        if (board.verifySourceAndDestination(this.row, this.col, endRow, endCol, isBlack)) {
            return (board.verifyAdjacent(this.row, this.col, endRow, endCol));
        }
        return false;
    }


    // New method to check and perform castling
    public boolean canCastle(Board board, int endRow, int endCol) {
        if (hasMoved || this.row != endRow) {
            return false;
        }

        // Verify if destination column is for castling (either king side or queen side)
        if (endCol == this.col + 2) {  // King side
            // Verify that the king can move two squares to the right
            return canCastleKingside(board);
        }

        else if (endCol == this.col - 2) {  // Queen side
            // Verify that the king can move two squares to the left
            return canCastleQueenside(board);
        }

        return false;
    }

    // Check if king side castling is possible
    private boolean canCastleKingside(Board board) {
        // Check if the square between the king and rook is empty
        if (board.getPiece(row, col + 1) != null || board.getPiece(row, col + 2) != null) {
            return false;  // There are pieces blocking the way
        }

        // Check if the king's path is not under attack
        if (board.isSquareUnderAttack(row, col + 1, isBlack) || board.isSquareUnderAttack(row, col + 2, isBlack)) {
            return false;  // The king cannot pass through an attacked square
        }

        // Check if the rook has not moved
        Piece rook =  board.getPiece(row, col + 3);  // Assuming rook is on the same row and to the right
        if (!(rook instanceof Rook)) {
            return false;  // Rook has moved or is missing
        }

        return !((Rook) rook).hasMoved();   // All conditions for kingside castling are met
    }

    // Check if queenside castling is possible
    private boolean canCastleQueenside(Board board) {
        // Check if the square between the king and rook is empty
        if (board.getPiece(row, col - 1) != null || board.getPiece(row, col - 2) != null || board.getPiece(row, col - 3) != null) {
            return false;  // There are pieces blocking the way
        }

        // Check if the kings path is not under attack
        if (board.isSquareUnderAttack(row, col - 1, isBlack) || board.isSquareUnderAttack(row, col - 2, isBlack) || board.isSquareUnderAttack(row, col - 3, isBlack)) {
            return false;
        }

        // Check if the rook has not moved
        Piece rook =  board.getPiece(row, col - 4);  // Assuming rook is on the same row and to the left
        if (!(rook instanceof Rook)) {
            return false;  // Rook has moved or is missing
        }

        return !((Rook) rook).hasMoved();   // All conditions for queenside castling are met
    }

    // Method to perform the castling move
    public void performCastling(Board board, int endRow, int endCol) {

        // King side Castle
        if (endCol == this.col + 2) {
            board.movePiece(endRow, col + 3, endRow, endCol - 1, false );
        }
        // Queen side Castle
        else if (endCol == this.col - 2) {
            board.movePiece(endRow, col - 4, endRow, endCol + 1, false );
        }

        // Move the king to the appropriate location
        this.row = endRow;
        this.col = endCol;

        // Mark the king as having moved
        this.hasMoved = true;

    }
}

