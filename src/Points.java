import javax.swing.*;
import java.awt.*;

public class Points{
    int points,points_tile_left;
    Board board;
    Points(Board b)
    {
        board = b;
        points_tile_left = board.howManyTilesOfType(Board.POINT);
        System.out.println("Znaleziono punktow - "+points_tile_left);
        points = 0;
    }

    /**
     * Adds points for the current user
     * @param type type of point tile
     */
    void add(int type)
    {
        switch (type)
        {
            case Board.POINT -> points += 10;
            case Board.SNACK -> points += 200;
        }
        points_tile_left = board.howManyTilesOfType(Board.POINT) - 1;
        System.out.println("Punktow: "+points+" pozostalo pol: "+points_tile_left);
    }

    /**
     * Checks if there's any point cell still on the board
     * @return
     */
    boolean allPointsTaken(){if(points_tile_left != 0) return false; return true;}
    int getPoints(){return points;}
}
