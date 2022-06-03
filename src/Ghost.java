import javax.swing.*;
import java.awt.*;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Ghost {
    int inAction,speed,direction;
    int x,y,old_x,old_y,spawn_x,spawn_y;
    Image image;
    Pacman pacman;
    Board board;
    String name;

    Ghost(Pacman p, Board b, int sp_x, int sp_y)
    {
        spawn_x = sp_x; spawn_y = sp_y;
        pacman = p;
        board = b;
        respawn();
        speed = (int)(pacman.getSpeed() * 1.5); inAction = 0;

    }
    Ghost(Ghost g)
    {
        inAction = g.inAction; speed = g.speed; direction = g.direction;
        x = g.x; y = g.y; old_x = g.old_x; old_y = g.old_y;
        pacman = g.pacman; board = g.board; name = g.name;
        image = new ImageIcon("images/ghost_orange.png").getImage();
    }

    /**
     * Updates x,y of pacman
     */
    public void move()
    {
        if(inAction == 0) chooseDirection();
        inAction--;

        int x_factor = 0, y_factor = 0;
        switch (direction) {
            case Game.MOVE_UP -> y_factor = -Game.cell_height;
            case Game.MOVE_DOWN -> y_factor = Game.cell_height;
            case Game.MOVE_LEFT -> x_factor = -Game.cell_width;
            case Game.MOVE_RIGHT -> x_factor = Game.cell_width;
        }
        float shift = (float) (speed - inAction)/speed;

        x = (old_x + (int)(x_factor * shift) + Game.width)%Game.width;
        y = old_y + (int)(y_factor * shift);
    }
    void chooseDirection(){
        old_x = x; old_y = y;
        inAction = speed;
    }

    /**
     * Finds the shortest path between pacman and ghost
     * @param board board
     * @param row row in the board
     * @param column column in the board
     */
    void shortestPath(int[][] board, int row, int column)
    {
        column = (column + Board.cols) % Board.cols;
        if(board[row][column] == 999) return;
        boolean something_changed = false;
        int new_value = board[row][column] + 1;
        int tmp_val;
        for(int i = 0; i < 4; i++)
        {
            tmp_val = board[Board.getRowAround(row,i)][Board.getColumnAround(column,i)];
            if(tmp_val != 999 &&(tmp_val == 0 || tmp_val > new_value))
            {
                board[Board.getRowAround(row,i)][Board.getColumnAround(column,i)] = new_value;
                something_changed = true;
            }
        }
        if(!something_changed) return;

        for(int i = 0; i < 4; i++)
            shortestPath(board, Board.getRowAround(row, i), Board.getColumnAround(column, i));

    }
    void colision_check() {
        if(Math.abs(pacman.getX() - x) < Game.cell_width && Math.abs(pacman.getY() - y) < Game.cell_height)
            Game.signal = 10;
    }
    void respawn()
    {
        x = spawn_x; y = spawn_y;
        inAction = 0; direction = 0;
    }
    Image getImage(){return image;}
    int getX(){return x;}
    int getY(){return y;}
}

class Pinky extends Ghost
{
    int[] dir_left   = {2,3,1,0}; // 3 4 2 1
    int[] dir_right  = {3,2,0,1}; // 4 3 1 2
    int[] dir_behind = {1,0,3,2}; // 2 1 4 3
    ArrayList<Integer> moves_list;
    Pinky(Pacman p, Board b, int spawn_x, int spawn_y)
    {
        super(p,b,spawn_x,spawn_y);
        moves_list = new ArrayList<Integer>();
        image = new ImageIcon("images/ghost_pink.png").getImage();
        name = "Pinky";
    }
    Pinky(Ghost g)
    {
        super(g);
        moves_list = new ArrayList<Integer>();
    }
    /**
        <p>Fills moves_list array with new values</p>
        Amount of moves put into array depends on the distance between Pinky and pacman
     */
    void fillDirectionArray()
    {
        int[][] tmp_board = board.get_board();
        tmp_board[pacman.getRow()][pacman.getColumn()] = -1;
        placeWallNearPacman(tmp_board);
        shortestPath(tmp_board, pacman.getRow(), pacman.getColumn());

        int ghost_row = Board.coord_to_row(y);
        int ghost_col = Board.coord_to_column(x);
        int number_of_moves = Board.lowest_value_around(tmp_board,ghost_row,ghost_col) / 2 + 1; // + 1 tu?
        int tmp_direction;
        for(int i = 0; i < number_of_moves; i++)
        {
            tmp_direction = Board.best_direction_around(tmp_board,ghost_row,ghost_col);
            moves_list.add(tmp_direction);
            ghost_row = Board.getRowAround(ghost_row,tmp_direction-1);
            ghost_col = Board.getColumnAround(ghost_col,tmp_direction-1);
        }
    }

    void chooseDirection()
    {
        super.chooseDirection();
        if(moves_list.size() == 0) fillDirectionArray();
        direction = moves_list.get(0);
        moves_list.remove(0);
    }

    /**
     * Places the wall behind pacman
     * @param board board
     */
    void placeWallNearPacman(int[][] board)
    {
        int wall_row = Board.getRowAround(pacman.getRow(),dir_behind[pacman.getDirection()]);
        int wall_column = Board.getColumnAround(pacman.getColumn(),dir_behind[pacman.getDirection()]);
        board[wall_row][wall_column] = 999;
    }
}

/**
 * Blinky follows pacman using the shortest way
 */
class Blinky extends Ghost
{
    Blinky(Pacman p, Board b, int spawn_x, int spawn_y)
    {
        super(p,b,spawn_x,spawn_y);
        image = new ImageIcon("images/ghost_red.png").getImage();
        name = "Blinky";
    }
    Blinky(Ghost g) {super(g);}
    void chooseDirection()
    {
        super.chooseDirection();
        int[][] tmp_board = board.get_board();
        tmp_board[pacman.getRow()][pacman.getColumn()] = -1;
        shortestPath(tmp_board,pacman.getRow(), pacman.getColumn());
        direction = Board.best_direction_around(tmp_board,Board.coord_to_row(y),Board.coord_to_column(x));
    }
}
class Clyde extends Ghost
{
    int moves_before_change, current_move;
    int direction_prio[] = {1,3,2,4};
    Random rand;
    Clyde(Pacman p, Board b, int spawn_x, int spawn_y)
    {
        super(p,b,spawn_x,spawn_y);
        direction_prio[0] = 1; direction_prio[1] = 3;
        moves_before_change = 5; current_move =5;
        rand = new Random();
        image = new ImageIcon("images/ghost_blue.png").getImage();
        name = "Clyde";
    }
    Clyde(Ghost g)
    {
        super(g);
        rand = new Random();
        moves_before_change = 5; current_move = 5;
    }
    void change_direction_prio()
    {
        int tmp;
        boolean unique;
        for(int i = 0; i < 4; i++)
        {
            unique = true;
            tmp = rand.nextInt(4) + 1;
            for(int j=i-1; j >= 0; j--)
                if(tmp == direction_prio[j]){ unique = false; break;}
            if(unique) direction_prio[i] = tmp;
            else i--;
        }
        //for(int i = 0; i < 4; i++) System.out.println(direction_prio[i]);
        current_move = moves_before_change;
    }
    void chooseDirection()
    {
        super.chooseDirection();
        int[][] tmp_board = board.get_board();
        if(current_move == 0) change_direction_prio();
        for(int i = 0; i < 4; i++)
        {
            if(Board.get_tile_value_from_board(tmp_board,Board.coord_to_row(y),Board.coord_to_column(x),direction_prio[i]) != 999)
            {
                direction = direction_prio[i];
                current_move --;
                break;
            }
        }
    }
}
class Inky extends Ghost
{
    Inky(Pacman p, Board b, int spawn_x, int spawn_y)
    {
        super(p,b,spawn_x,spawn_y);
        image = new ImageIcon("images/ghost_orange.png").getImage();
    }
}