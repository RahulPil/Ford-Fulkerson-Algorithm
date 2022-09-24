import java.io.FileNotFoundException;
/*
Class tests Ford-Fulkerson algorithm which uses the shortest augmenting paths method for bipartite matching
This class is represented by the main method that creates an algorithm object which is used to call on the function
that implements said algorithm for testing
@author Rahul Pillalamarri
 */
public class main {
    /**
     * main
     * pre: none
     * post: bipartite matching of graph is outputted to the console
     * @param args
     * @throws FileNotFoundException as the algorithm takes in a file input
     */
    public static void main(String [] args) throws FileNotFoundException {
        algorithm main = new algorithm();
        main.bipartiteMatching();
    }
}
