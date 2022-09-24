
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
/*
Class carries code required for implementing Ford-Fulkerson algorithm using the shortest augmenting paths method for
bipartite matching.
This algorithm is represented by a node object, number of nodes, array of names, and two graphs implemented using an adjacency list
Functionality includes reading and initializing data structures through file input, breadth first search, and ford fulkerson algorithm
@author Rahul Pillalamarri
 */
public class algorithm {
    LinkedList<Node>[] residualGraph;
    LinkedList<Node>[] capacityGraph;
    int numOfNodes;
    String [] nodeNames;
    /*
    Class to represent a Node
    Each node is represented by an integer flow, capacity per edge, and vertex number which is the index of the node in the order given
    Functionality includes initializing all variables through constructor and augment method that augments the flow with the path
    @author Rahul Pillalamarri
     */
    class Node{
        int flow;
        int capacity;
        int vertex;

        /**
         * constructor
         * pre: none
         * post: instance variables are initialized
         * @param vertex is the index of the node with respect to the representation within the file input
         * @param flow is the number of units that are currently flowing through the edge of the node
         * @param capacity is the total number of units that can flow through the edge of the node
         */
        public Node(int vertex, int flow, int capacity){
            this.vertex = vertex;
            this.flow = flow;
            this.capacity = capacity;
        }

        /**
         * augment
         * pre: bipartite graph is created and initial path is found as well
         * post: path is updated and improved
         * @param startingVertex is the vertex of which the algorithm is augmenting the flow with respect to the overall path
         */
        public void augment(int startingVertex){
            if(flow == capacity){
                Iterator<Node> checker = residualGraph[startingVertex].listIterator();
                while(checker.hasNext()){
                    Node current = checker.next();
                    if(current.vertex == vertex){
                        // reversing edge in residual graph to reflect all updates made
                        checker.remove();
                        residualGraph[vertex].add(new Node(startingVertex, 0, 1));
                        break;
                    }
                }
            }
        }
    }

    /**
     * constructor:
     * pre: none
     * post: object of class is created to enable testing in main class
     */
    algorithm(){ }

    /**
     * bipartiteMatching
     * pre: residual graph has been constructed
     * post: max cardinality matchings are found
     * @throws FileNotFoundException as initializeData is called within this function to create a bipartiteMatching using the file input
     */
    public void bipartiteMatching() throws FileNotFoundException {
        initializeData();
        String [] matches = new String [residualGraph.length];
        boolean run = true;
        int traversalNode;
        Stack<Integer> path = new Stack<>();

        // while level graph can be constructed using the residualGraph
        while(run == true){
            // ensures that path from source to sink exists
            if(bfs() != true){
                run = false;
            }else{
                traversalNode = 0;
                path.add(0);
                // while traverseNode is not stuck at the source node
                while((capacityGraph[traversalNode].isEmpty() != true && traversalNode == 0) || traversalNode !=0){
                    // if a path has been found to the sink
                    if(traversalNode == numOfNodes+1){
                        int startingVertex;
                        int endingVertex;
                        while(path.size() > 1){
                            endingVertex=path.pop();
                            startingVertex = path.peek();
                            Iterator<Node> checkerResidual = residualGraph[startingVertex].listIterator();
                            Iterator<Node> checkerCapacity = capacityGraph[startingVertex].listIterator();
                            // augmenting flow of path and residual graph
                            while(checkerResidual.hasNext() == true) {
                                Node currentResidual = checkerResidual.next();
                                if (currentResidual.vertex == endingVertex) {
                                    currentResidual.flow = 1;
                                    currentResidual.augment(startingVertex);
                                    break;
                                }
                            }
                            // removing path from level graph after augmentation
                            while(checkerCapacity.hasNext() == true){
                                Node currentCapacity = checkerCapacity.next();
                                if(currentCapacity.vertex == endingVertex){
                                    checkerCapacity.remove();
                                }
                            }
                            // recording bipartite match from path
                            matches[startingVertex] = nodeNames[startingVertex] + " / " + nodeNames[endingVertex];
                            startingVertex = endingVertex;

                        }
                        // resetting position of traverse node to source
                        traversalNode = 0;
                        path.pop();
                        path.push(0);
                    }else if(capacityGraph[traversalNode].isEmpty() == true){
                        // if the traversal node is stuck in the path then it retreats and deletes the current node and incoming edges to that node
                        for(int i = 0; i < capacityGraph.length; i++){
                            Iterator<Node> checker = capacityGraph[i].listIterator();
                            while(checker.hasNext()){
                                Node cur = checker.next();
                                if(cur.vertex == traversalNode){
                                    checker.remove();
                                    break;
                                }
                            }
                        }
                        path.pop();
                        traversalNode = path.peek();
                    }else{
                        // advancing the traverse node along some edge in level graph towards the sink and update the current path
                        traversalNode = capacityGraph[traversalNode].peekFirst().vertex;
                        path.add(traversalNode);
                    }
                }
            }
        }
        // prints out the matches
        int counter = 0;
        for(int i = 1; i <= numOfNodes/2; i++){
            if(matches[i] != null){
                System.out.println(matches[i]);
                counter++;
            }
        }
        System.out.println(counter + " total matches");
    }

    /**
     * bfs
     * pre: residual graph has been constructed
     * post: capacity graph has been constructed and level ordering of all nodes is clearly updated in the levelOrdering array
     * @return true if bfs finds a path from source to sink, false if no path from source to sink exists
     */
    public boolean bfs () {
        // graph implemented using an adjacency list implementation
        capacityGraph = new LinkedList[numOfNodes + 2];

        for(int i = 0; i < capacityGraph.length; i++){
            capacityGraph[i] = new LinkedList<Node>();
        }
        // array that holds the level orderings of all the nodes with respect to the order of which they were presented in the input file
        int[] levelOrdering = new int[numOfNodes + 2];

        // queue used to implement bfs and hold nodes that keep track of nodes needed to visit
        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(0);
        levelOrdering[0] = 1;

        // searches through graph until all nodes have been visited
        while (queue.size() != 0) {
            int index = queue.poll();
            // loops through edges of node index and adds the edges into level graph/capacity graph given that they make progress towards the sink node
            Iterator<Node> checker = residualGraph[index].listIterator();
            while(checker.hasNext()){
                Node currentNode = checker.next();
                // if the level order of the current node is not initialized to a level then initialize to the correct level and add to queue for bfs
                // as well as add that node to the level order graph
                if (levelOrdering[currentNode.vertex] == 0) {
                    levelOrdering[currentNode.vertex] = levelOrdering[index] + 1;
                    queue.add(currentNode.vertex);
                    capacityGraph[index].add(new Node(currentNode.vertex, currentNode.flow, currentNode.capacity));
                } else if (levelOrdering[currentNode.vertex] > levelOrdering[index]) {
                    capacityGraph[index].add( new Node(currentNode.vertex, currentNode.flow, currentNode.capacity));
                }
            }
        }
        // checks to see if the level orderings of all nodes are updated in the levelOrdering array
        // if they aren't then that means that we haven't found a path to the sink node
        if(levelOrdering[numOfNodes+1] != 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * intializeData
     * pre: none
     * post: residual graph is constructed as well as many important variables such as numOfNodes and numOfEdges are initalized
     * @throws FileNotFoundException as function reads through a file input to construct residualGraph
     */
    public void initializeData() throws FileNotFoundException {
        // reads through file data using Scanner
        File file = new File("program3data.txt");
        Scanner input = new Scanner(file);
        numOfNodes = Integer.parseInt(input.nextLine());

        // creates adjacency list for residual graph for total number of nodes+2 for source and sink nodes
        residualGraph = new LinkedList[numOfNodes+2];

        // goes through and creates adjacency lists for each node including the source and sink node
        for(int i = 0; i < residualGraph.length; i++){
            residualGraph[i] = new LinkedList<Node>();
        }

        // populates array of people's names given in input file that is used later for printing out final bipartite matchings
        nodeNames = new String[numOfNodes+2];
        nodeNames[0] = "source";
        nodeNames[numOfNodes+1] = "sink";
        for(int i = 1; i <= numOfNodes; i++){
            nodeNames[i] = input.nextLine();
        }
        // maybe skip a line here dont know yet
        int numOfEdges = Integer.parseInt(input.nextLine());

        // reads through file and creates relationships between various nodes
        for(int i = 0; i < numOfEdges; i++){
            int startingVertex = input.nextInt();
            int endingVertex = input.nextInt();
            residualGraph[startingVertex].add(new Node(endingVertex, 0, 1));
        }

        //connecting the first 5 nodes to the source node and the sink node to the last five nodes
        for(int i = 1; i <= numOfNodes; i++){
            if(i <= numOfNodes/2){
                residualGraph[0].add(new Node(i, 0, 1));
            }else if(i > numOfNodes/2){
                residualGraph[i].add(new Node(numOfNodes+1, 0, 1));
            }
        }
    }
}
