package CSCI1933P2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

/**
 * Class responsible for the GUI of the chess board & GUI game functionality.
 */
public class ChessGame extends JFrame {
    // board representation fields
    private final JPanel boardPanel;
    private final JPanel[][] squares = new JPanel[8][8]; // 2d array of GUI representation of the boards squares
    private final Board chessBoard; // game logic representation of ches game
    private final JTextArea log;

    // piece/square selection fields
    private JLabel selectedPiece = null;
    private int selectedPieceRow = -1;
    private int selectedPieceCol = -1;
    private JPanel selectedSquare = null;

    private boolean colorToMove = true; // T for W, F for B

    public ChessGame(Board board) {
        chessBoard = board;

        // build frame
        setSize(800,600);
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // build container
        JPanel container = new JPanel(new BorderLayout());

        // build chess board
        boardPanel = new JPanel(new GridLayout(8,8));
        initializeBoard();
        initializePieces();
        container.add(boardPanel, BorderLayout.CENTER);

        // build row coords (1 - 8)
        JPanel rowCoords = new JPanel(new GridLayout(8,1));
        for (int i = 8; i > 0; i--) {
            JLabel label = new JLabel(String.valueOf(i),SwingConstants.CENTER);
            rowCoords.add(label);
        }
        rowCoords.setPreferredSize(new Dimension(15,600));

        // build col coords (a - h)
        JPanel colCoords = new JPanel(new GridLayout(1,8));
        for (char c = 'a'; c <= 'h'; c++) {
            JLabel label = new JLabel(String.valueOf(c),SwingConstants.CENTER);
            colCoords.add(label);
        }
        colCoords.setPreferredSize(new Dimension(600,15));

        // build move log
        log = new JTextArea();
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(log);
        scrollPane.setPreferredSize(new Dimension(125, 100));

        // build title for move log
        JLabel title = new JLabel("Move Log",SwingConstants.CENTER);
        JPanel logPanel = new JPanel(new BorderLayout());
        title.setFont(new Font("Arial",Font.BOLD, 16));
        logPanel.add(title,BorderLayout.NORTH);
        logPanel.add(scrollPane,BorderLayout.CENTER);

        // add rows
        container.add(rowCoords,BorderLayout.WEST);
        // add cols
        container.add(colCoords,BorderLayout.NORTH);

        add(container,BorderLayout.CENTER);
        add(logPanel,BorderLayout.WEST);
        setVisible(true);
    }

    /**
     * Initializes GUI chess boards starting position.
     */
    private void initializeBoard() {
        boolean isBlack = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final int row = i;
                final int col = j;
                JPanel square = new JPanel(new BorderLayout());
                if (isBlack) {
                    square.setBackground(Color.LIGHT_GRAY);
                }
                else {
                    square.setBackground(Color.WHITE);
                }

                isBlack = !isBlack;
                boardPanel.add(square);
                squares[i][j] = square;

                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        handleSquareClick(square, row, col);
                    }
                });
            }
            isBlack = !isBlack;
        }
    }

    /**
     * Once there the game is over, we iterate through the board to determine who won.
     * @return T for White won, B for Black won.
     */
    public boolean decideWinner() {
        return !colorToMove;
    }

    public void showCheckmateDialog(boolean whiteWon) {
        String message;
        // Icon image;
        if (whiteWon) {
            message = "Checkmate, White Wins!";
            //image = new ImageIcon("/Users/kaydenvandecaveye/PersonalProjects/SWE/Chess/whiteking.png");
        }
        else {
            message = "Checkmate, Black Wins!";
            //image = new ImageIcon("/Users/kaydenvandecaveye/PersonalProjects/SWE/Chess/blackking.png");
        }
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Gets (row,col) position of a passed in Jpanel.
     * @param panel (square on board)
     * @return position of panel
     */
    private int[] getPosition(JPanel panel) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (squares[i][j] == panel) {
                    return new int[] {i, j};
                }
            }
        }
        return null;
    }

    /**
     * Moves a piece on both the GUI and underlying board for game logic
     * @param piece visual representation of piece being moved.
     * @param source GUI square piece begins on.
     * @param destination GUI square piece ends on.
     */
    private void movePiece(JLabel piece, JPanel source, JPanel destination) {
        // initialize coords
        int sourceRow = getPosition(source)[0];
        int sourceCol = getPosition(source)[1];
        int destRow = getPosition(destination)[0];
        int destCol = getPosition(destination)[1];

        source.remove(piece);
        source.repaint();

        if (destination.getComponentCount() > 0) { // move to occupied square
            destination.remove(0);
        }

        // pawn promotion check
        if (chessBoard.getPiece(sourceRow,sourceCol) instanceof Pawn) {
            Piece pawn = chessBoard.getPiece(sourceRow,sourceCol);
            if (pawn.isBlack && destRow == 7) {
                addPiece(destRow,destCol,"♛");
            }
            else if (!pawn.isBlack && destRow == 0) {
                addPiece(destRow,destCol,"♕");
            }
            else {
                destination.add(piece);
                destination.revalidate();
            }
        }
        else {
            destination.add(piece);
        }
        destination.revalidate();
        destination.repaint();

        // move piece for game logic
        chessBoard.movePiece(sourceRow, sourceCol, destRow, destCol,false);
        // add move to game log
        logMove(sourceCol,sourceRow,destCol,destRow);

        // for debug
        // System.out.println(chessBoard.toString());
    }

    /**
     * Converts a move to proper chess notation and logs it into the game log.
     * @param sourceCol Starting Col.
     * @param sourceRow Starting Row.
     * @param destCol Ending Col.
     * @param destRow Ending Row.
     */
    private void logMove(int sourceCol,int sourceRow,int destCol, int destRow) {
        log.append((colorToMove ? "White:" : "Black:") + " " + mapCoords(sourceCol,true) + mapCoords(sourceRow,false) + "," +
                mapCoords(destCol,true) + mapCoords(destRow,false) + '\n');
    }

    /**
     * Maps a given integer column/row value to the appropriate value. (Used for move logging)
     * @param val Passed in column/row value that is being mapped.
     * @param col Boolean for if the input is a col or a row. (T for col, F for row)
     * @return returns the mapped column/row coordinate.
     */
    public String mapCoords(int val,boolean col) {
        String s;
        if (col) { // passed in val corresponds to column
             s = switch (val) {
                case 0 -> "a";
                case 1 -> "b";
                case 2 -> "c";
                case 3 -> "d";
                case 4 -> "e";
                case 5 -> "f";
                case 6 -> "g";
                case 7 -> "h";
                default -> "invalid";
            };
        }
        else { // passed in val corresponds to row
             s = switch (val) {
                case 0 -> "8";
                case 1 -> "7";
                case 2 -> "6";
                case 3 -> "5";
                case 4 -> "4";
                case 5 -> "3";
                case 6 -> "2";
                case 7 -> "1";
                default -> "invalid";
            };
        }
        return s;
    }

    /**
     * Shows all legal moves for a clicked piece or moves an already clicked piece.
     * @param square square the clicked piece is on.
     * @param row
     * @param col
     */
    private void handleSquareClick(JPanel square, int row, int col) {
        // Case 1: PLayer wants to move to a highlighted square. (move previously selected piece)
        if (square.getBackground().equals(new Color(255, 110, 75)) && selectedPiece != null) {
            if (chessBoard.getPiece(selectedPieceRow,selectedPieceCol).isMoveLegal(chessBoard,row,col)) {
                movePiece(selectedPiece,selectedSquare,squares[row][col]);
            }
            colorToMove = !colorToMove;
            resetHighlights();
            chessBoard.checkOnBoard();
        }
        // Case 2: Player clicks on a non highlighted square. (reveal available moves)
        else if (square.getComponentCount() > 0) {
            // reset highlighting
            resetHighlights();
            Component component = square.getComponent(0);
            if (component instanceof JLabel) {
                // reset selected piece field to newly pressed piece
                selectedPiece = (JLabel) component;
                selectedPieceRow = row;
                selectedPieceCol = col;
                selectedSquare = squares[row][col];

                if (chessBoard.getPiece(selectedPieceRow,selectedPieceCol).isBlack != colorToMove) {
                    // generate legal moves and highlight
                    int[][] legalMoves = chessBoard.board[row][col].generateLegalMoves(chessBoard);
                    highlightMoves(legalMoves);
                }
            }
        }
    }

    /**
     * "highlights" moves from a passed in coordinate array.
     * @param moves 2d array containing moves.
     */
    private void highlightMoves(int[][] moves) {
        for (int[] move : moves) {
            int moveRow = move[0];
            int moveCol = move[1];
            squares[moveRow][moveCol].setBackground(new Color(255, 110, 75));
        }
    }

    /**
     * removes all highlights from the GUI board representation.
     */
    private void resetHighlights() {
        boolean isBlack = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isBlack) {
                    squares[i][j].setBackground(Color.LIGHT_GRAY);
                }
                else {
                    squares[i][j].setBackground(Color.WHITE);
                }
                isBlack = !isBlack;
            }
            isBlack = !isBlack;
        }
    }
    private boolean isGameOver() {
        return chessBoard.isGameOver();
    }

    /**
     * Initializes pieces on GUI board representation in the normal starting chess position.
     */
    private void initializePieces() {
        String[] blackBackRow = {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"};
        String blackPawn = "♟";
        String[] whiteBackRow = {"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"};
        String whitePawn = "♙";

        for (int col = 0; col < 8; col++) {
            // black pieces
            addPiece(0,col,blackBackRow[col]);
            addPiece(1,col,blackPawn);
            // white pieces
            addPiece(7,col,whiteBackRow[col]);
            addPiece(6,col,whitePawn);
        }
    }

    /**
     * Adds a given piece to GUI board representation based on passed in coordinates.
     * @param row
     * @param col
     * @param piece
     */
    public void addPiece(int row, int col, String piece) {
        JLabel pieceLabel = new JLabel(piece, SwingConstants.CENTER);
        pieceLabel.setFont(new Font("Serif",Font.BOLD,60));
        squares[row][col].add(pieceLabel);
    }

    public static void main(String[] args) {
        Board board = new Board();
        Fen.load("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", board);
        ChessGame chess = new ChessGame(board);

        Timer timer = new Timer(100, e -> {
            if (chess.isGameOver()) {
                chess.showCheckmateDialog(chess.decideWinner());
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }
}
