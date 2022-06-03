import java.util.ArrayList;

/**
 * Stores the data of the player and his score
 */
public class Score {
    int score;
    String name;
    Score(String n,int s)
    {
        score = s;
        name = n;
    }
    String getName(){return name;}
    int getScore(){return score;}
    void changeName(String newName){name = newName;}

    /**
     * Adds and sort by score new score
     * @param scores array of scores
     * @param score new score to add
     * @return position where the new score was entered
     */
    static int addAndSort(ArrayList<Score> scores, Score score)
    {
        for(int i = 0; i < scores.size(); i++)
        {
            if(scores.get(i).getScore() < score.getScore())
            {
                scores.add(i,score);
                return i;
            }
        }
        scores.add(score);
        return Math.min(8,scores.size() - 1);
    }

    /**
     * Shows every player name and score in console
     * @param scores array with scores
     */
    static void show(ArrayList<Score> scores)
    {
        System.out.println("Znaleziono "+scores.size()+" wynikow");
        for(Score score: scores)
        {
            System.out.println("Name: "+score.getName()+" Points: "+score.getScore());
        }
    }
}
