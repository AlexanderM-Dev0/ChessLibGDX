package Chess.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Engine
{

    /*
    White = (Black - 6)
    --------
    Rook =   1
    Knight = 2
    Bishop = 3
    Queen =  4
    King =   5
    Pawn =   6

    Black = (White + 6)
     */

    //Used Hiero v5 to generate the proper .fnt and .png assets


    private int tileSelectedIndex = -1;
    private long lastInput =   System.currentTimeMillis();
    private ArrayList<String> turnHistory = new ArrayList<String>();

    public ArrayList<Square> initializeBoard(ArrayList<Square> board)
    {
        board = new ArrayList<Square>();
        String fen = "1,2,3,4,5,3,2,1,6,6,6,6,6,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,12,12,12,12,12,12,12,12,7,8,9,10,11,9,8,7";
        turnHistory.add(fen);
        System.out.print(fen);
        String[] delimentedFen = fen.split(",");
        for(int i = 0; i < 64; i++)
        {
            board.add(new Square(200+(i%8)*Square.size + Square.size/2,50+(i/8)*Square.size, Integer.parseInt(delimentedFen[i])));
        }
        return board;
    }

    public ArrayList<Square> setBoard(ArrayList<Square> board)
    {
        String fen = turnHistory.get(turnHistory.size()-2);
        System.out.println("Last move: " + turnHistory.get(turnHistory.size()-2));
        String[] delimentedFen = fen.split(",");
        for(int i = 0; i < 64; i++)
        {
            board.get(i).piece = Integer.parseInt(delimentedFen[i]);
        }
        turnHistory.add(fen);
        return board;
    }

    private String exportBoard(ArrayList<Square> board)
    {
        String boardToString = "";
        for(int i = 0; i < board.size(); i++)
        {
            boardToString += (board.get(i).piece + ",");
        }
        //System.out.println(boardToString);
        return boardToString;
    }

    public boolean timeCheck(int variation)
    {
        boolean check = false;
        if(System.currentTimeMillis() > lastInput+variation)
        {
            lastInput = System.currentTimeMillis();
            check = true;
        }
        return check;
    }


    public void selectionCheck(ArrayList<Square> board, OrthographicCamera cam)
    {
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) &&  timeCheck(500))
        {
            lastInput = System.currentTimeMillis();
            if(tileSelectedIndex == -1)
            {
                for (int i = 0; i < board.size(); i++)
                {
                    //Vector2 mousePos = new Vector2(getWorldPositionFromScreen(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0), cam));
                    Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY());
                    if (board.get(i).isClicked(mousePos)) {
                        board.get(i).isSelected = true;
                        tileSelectedIndex = i;
                    }
                }
            }
            else
            {
                for (int i = 0; i < board.size(); i++)
                {
                    //Vector2 mousePos = new Vector2(getWorldPositionFromScreen(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0), cam));
                    Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY());
                    if (board.get(i).isClicked(mousePos) && !board.get(i).isSelected && board.get(tileSelectedIndex).piece != 0) {

                        board.get(tileSelectedIndex).takes(board.get(i));
                        board.get(tileSelectedIndex).isSelected = false;
                        tileSelectedIndex = -1;
                        turnHistory.add(exportBoard(board));
                        System.out.println(turnHistory.get(turnHistory.size()-1));
                    }
                }
            }
        }
        if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
        {
            for (Square square : board) {
                square.isSelected = false;
                tileSelectedIndex = -1;
            }
        }
    }
    public boolean wasClicked(ArrayList<Square> board)
    {
        for(int i = 0; i < 64; i++)
        {
            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            {
                //System.out.println(Gdx.input.getX());
                //System.out.println(board.get(i).coords().x);
                if(board.get(i).coords().x*Square.size > Gdx.input.getX())
                {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean turn(ArrayList<Square> board, boolean turn)
    {
        ArrayList<Square> boardCopy = deepCopyBoard(board);
        turn = wasClicked(board);

        if(!boardCopy.equals(board))
        {
            return !turn;
        }
        else
        {
            return turn;
        }
    }
    public ArrayList<Square> deepCopyBoard(ArrayList<Square> board)
    {
        ArrayList<Square> board2 = new ArrayList<>();
        for (Square copiedSquare  : board) {
            board2.add(new Square(copiedSquare.x, copiedSquare.y, copiedSquare.piece));
        }
        return board2;
    }
    private String getAsciiPiece(int piece)
    {
        // Unicode chess symbols (white: 1–6, black: 7–12)
        switch (piece)
        {
            case 1:  return "♖"; // White Rook
            case 2:  return "♘"; // White Knight
            case 3:  return "♗"; // White Bishop
            case 4:  return "♕"; // White Queen
            case 5:  return "♔"; // White King
            case 6:  return "♙"; // White Pawn
            case 7:  return "♜"; // Black Rook
            case 8:  return "♞"; // Black Knight
            case 9:  return "♝"; // Black Bishop
            case 10: return "♛"; // Black Queen
            case 11: return "♚"; // Black King
            case 12: return "♟"; // Black Pawn
            default: return "";
        }
    }

    public void drawBoard(ArrayList<Square> board, ShapeRenderer shapeRenderer, SpriteBatch batch)
    {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int startX = 200;
        int startY = 50;

        for (int i = 0; i < 64; i++)
        {
            int row = i / 8;
            int col = i % 8;

            if ((row + col) % 2 == 0)
                shapeRenderer.setColor(Color.GRAY);
            else
                shapeRenderer.setColor(new Color(0.25f, 0.25f, 0.3f, 1f));

            if (!board.get(i).isSelected)
                shapeRenderer.rect(board.get(i).x, board.get(i).y, Square.size, Square.size);
        }

        shapeRenderer.end();


        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/unicode.fnt"));
        font.getData().setScale(1.5f);

        batch.begin();

        for (int i = 0; i < 64; i++)
        {
            int piece = board.get(i).piece;
            if (piece != 0)
            {
                boolean isBlackPiece = (piece >= 7);
                String symbol = getAsciiPiece(piece);

                //Neat little trick
                Color outlineColor = isBlackPiece ? Color.WHITE : Color.BLACK;
                //I put a double outline here for more clarity
                font.setColor(outlineColor);
                font.draw(batch, symbol, board.get(i).x + Square.size / 2f - 9, board.get(i).y + Square.size / 2f + 9);
                font.draw(batch, symbol, board.get(i).x + Square.size / 2f - 11, board.get(i).y + Square.size / 2f + 11);

                font.setColor(isBlackPiece ? Color.BLACK : Color.WHITE);
                font.draw(batch, symbol, board.get(i).x + Square.size / 2f - 10, board.get(i).y + Square.size / 2f + 10);
            }
        }
        batch.end();
    }

    public void renderMouse(ShapeRenderer shapeRenderer)
    {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        {
            shapeRenderer.circle(Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY(), 50);
        }
        shapeRenderer.end();
    }

}
