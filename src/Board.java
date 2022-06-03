
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Board extends JPanel {
    final static int EMPTY = 0;
    final static int POINT = 1;
    final static int WALL = 2;
    final static int GHOST_EATER = 3;
    final static int SNACK = 4;
    final static int THIS = 0;
    final static int UP = 1;
    final static int DOWN = 2;
    final static int LEFT = 3;
    final static int RIGHT = 4;
    static int direct[][] = {{-1,0},{1,0},{0,-1},{0,1}};
    public int board[][] = {
            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
            {2,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,2},
            {2,0,2,2,2,2,2,0,2,0,2,2,2,2,2,0,2},
            {2,0,0,2,0,0,0,0,0,0,0,0,0,2,0,0,2},
            {2,2,0,2,0,2,2,1,2,0,2,2,0,2,0,2,2},
            {2,0,0,0,0,0,1,1,2,0,0,0,0,0,0,0,2},
            {2,0,2,2,2,0,2,2,2,2,2,0,2,2,2,0,2},
            {2,0,2,0,0,0,0,0,0,0,0,0,0,0,2,0,2},
            {2,0,2,0,2,0,2,2,0,2,2,0,2,0,2,0,2},
            {2,0,0,0,2,0,0,0,0,0,0,0,2,0,0,0,2},
            {2,2,0,2,2,2,0,0,2,0,0,2,2,2,0,2,2},
            {2,0,0,0,2,0,0,2,2,2,0,0,2,0,0,0,2},
            {2,0,2,0,0,0,0,0,0,0,0,0,0,0,2,0,2},
            {2,0,2,0,0,0,2,2,0,2,2,0,0,0,2,0,2},
            {2,0,2,0,2,0,2,0,0,0,2,0,2,0,2,0,2},
            {0,0,0,0,2,0,2,0,0,0,2,0,2,0,0,0,0},
            {2,0,2,0,2,0,2,2,2,2,2,0,2,0,2,0,2},
            {2,0,2,0,0,0,0,0,2,0,0,0,0,0,2,0,2},
            {2,0,2,0,2,2,2,0,0,0,2,2,2,0,2,0,2},
            {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
            {2,0,2,2,2,0,2,2,2,2,2,0,2,2,2,0,2},
            {2,0,2,0,0,0,2,2,2,2,2,0,0,0,2,0,2},
            {2,0,2,0,2,0,0,0,2,0,0,0,2,0,2,0,2},
            {2,0,0,0,2,2,2,0,0,0,2,2,2,0,0,0,2},
            {2,0,2,2,2,0,0,0,0,0,0,0,2,2,2,0,2},
            {2,0,0,0,0,0,2,2,0,2,2,0,0,0,0,0,2},
            {2,0,2,2,2,0,2,2,0,2,2,0,2,2,2,0,2},
            {2,0,2,2,2,0,0,0,0,0,0,0,2,2,2,0,2},
            {2,0,0,0,0,0,2,2,2,2,2,0,0,0,0,0,2},
            {2,2,2,2,2,2,2,0,0,0,2,2,2,2,2,2,2}};
    int pom;
    public static final int  rows = 30,cols = 17;
    Board(int r, int c)
    {
        pom = 0;
    }
    public void changeTile(int row, int column,int type)
    {
        board[row][column] = type;
    }
    public int getNumberof(String type)
    {
        if(Objects.equals(type, "rows")) return rows;
        else if(Objects.equals(type, "cols")) return cols;
        else return 0;
    }

    public int[][] get_board()
    {
        int tmp_board[][];
        tmp_board = new int[rows][cols];
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                if(board[i][j] == 2)tmp_board[i][j] = 999;
                else tmp_board[i][j] = 0;
            }
        }
        return tmp_board;
    }
    public int getTile(int row,int column){return board[row][column];}
    public static int coord_to_row(int y_value) {return (y_value / Game.cell_height);}
    public static int coord_to_column(int x_value) {return (x_value / Game.cell_width + cols) % cols;}
    //safe board getter
    public static int get_tile_value_from_board(int[][] board,int row, int column)
    {
        row = row % rows;
        column = (column + cols) % cols;
        return board[row][column];
    }
    public static int get_tile_value_from_board(int[][] board,int row, int column, int tile)
    {
        if(tile == THIS) return get_tile_value_from_board(board,row,column);
        int tmp_row = (row + direct[tile - 1][0] + rows )% rows;
        int tmp_col = (column + direct[tile - 1][1] + cols)% cols;
        return board[tmp_row][tmp_col];
    }
    public static int lowest_value_around(int board[][], int row, int column)
    {
        int min_val = 999, value_of_checked_tile;
        for(int i = 0  ; i< 4 ; i++)
        {
            value_of_checked_tile = Board.get_tile_value_from_board(board,row + direct[i][0],column+direct[i][1]);
            if (value_of_checked_tile < min_val)
                min_val = value_of_checked_tile;
        }
        return min_val;
    }
    /**
     * <p>Returns direction of lowest value around given point(row,column)</p>
     * @param board 2D array in int[][] format
     * @param row row of the point
     * @param column column of the point
     * @return Returns direction in 1-4 format (1 - UP, 2 - DOWN, 3 - LEFT, 4 - RIGHT)
     */
    public static int best_direction_around(int[][] board, int row, int column)
    {
        int min_val = 999, direction = 0,checked_value;
        for(int i = 0; i < 4; i++)
        {
            checked_value = get_tile_value_from_board(board,row,column,i+1);
            if(checked_value < min_val)
            {
                min_val = checked_value;
                direction = i + 1;
            }
        }
        if(direction == 0 ) System.out.println("Invalid Direction - best_direction_around");
        return direction;
    }

    /**
     * @param row
     * @param direction direction in 0-3 Format (0-UP, 1-DOWN, 2-LEFT, 3-RIGHT)
     * @return row in given direction (given row +- 1)
     */
    public static int getRowAround(int row, int direction)
    {
        int new_row = row;
        if(direction == 2 || direction == 3) new_row = new_row;
        else if(direction == 0) new_row -= 1;
        else if(direction == 1) new_row += 1;
        else System.out.println("Invalid direction - must be 0 - 3, was "+ direction);
        return new_row;
    }

    /**
     *
     * @param column
     * @param direction direction in 0-3 Format (0-UP, 1-DOWN, 2-LEFT, 3-RIGHT)
     * @return column in given direction (given column +- 1). if column is out of range returns modulo.
     */
    public static int getColumnAround(int column, int direction)
    {
        int new_column = column;
        if(direction == 0 || direction == 1) new_column = new_column;
        else if(direction == 2) new_column -= 1;
        else if(direction == 3) new_column += 1;
        else System.out.println("Invalid direction - must be 0 - 3, was "+ direction);
        new_column = (new_column + cols) % cols;
        return new_column;
    }

    /**
     * Counts tile of chosen type in array
     * @param type type of cells
     * @return number of cells of chosen type
     */
    public int howManyTilesOfType(int type)
    {
        int amount = 0;
        for(int i = 0; i < cols; i++){
            for(int j = 0; j < rows; j++)
                if(board[j][i] == type) amount++;}
        return amount;
    }

    /**
     * Changes every empty cell on the board to the point cell
     */
    public void repaintBoard()
    {
        for(int i = 0; i < cols; i++)
        {
            for(int j = 0; j < rows; j++)
                if(board[j][i] == Board.EMPTY) board[j][i] = Board.POINT;
        }
    }
}
