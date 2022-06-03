public class GhostController extends Thread {
    Ghost controlled_ghost;
    int task;

    /**
     * Multithread starter
     * @param new_ghost ghost controlled by this class
     * @param t task to make by thead (0- move, 1 - collision check)
     */
    GhostController(Ghost new_ghost, int t)
    {
        task = t;
        controlled_ghost = new_ghost;
    }
    public void run()
    {
        switch (task)
        {
            case 0 -> controlled_ghost.move();
            case 1 -> controlled_ghost.colision_check();
            default -> System.out.println("Invalid task");
        }
        task++;
    }
}
