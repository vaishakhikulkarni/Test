package Project4;

/*
 * @author : Vaishakhi Kulkarni
 * 		
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

import Project4.Graph;

public class CriticalPathFinal {

	public static LinkedList<Graph.Vertex> criticalNodes = new LinkedList<Graph.Vertex>();	// To stoe the critical path vertices
	public static int sum = 0;//to calculate the critical paths

	public static void main(String args[]) throws IOException {
		
		Scanner in = null;
		try {		//Take path address where file is passed from arguments
			if (args.length > 0) {
				File inputFile = new File(args[0]);
				in = new Scanner(inputFile);
			} else {
				in = new Scanner(System.in);
			}
		} catch (FileNotFoundException e) // Handle File not Found error
		{
			System.out
					.println("Please make sure the directory file is actually there.");
		}

		Graph g = Graph.readGraph(in);	//Read the file contents and form a graph
		
		g.initialize(); //Initialize the attributes

		//To store the topologicalorder of the given Graph

		int[] TopologicalOrderIndex = TopologicalOrder(g);

		CriticalPathFinal c = new CriticalPathFinal();	//Create object of class CriticalPathFinal

		System.out.println("<------Critical paths in projects (longest path in DAG)------>");
		
		// To calculate start running time complexity
		long startTime = System.currentTimeMillis();

		c.CalculateEC(g, TopologicalOrderIndex);	//Calculate earliest completion time for all vertices considering s and t vertex
		c.CalculateLC(g, TopologicalOrderIndex);	//Calculate latest completion time for all vertices considering s and t vertex 
		c.CalculateSlack(g);	//Calculate  Slack for all the vertices considering S and T vertex
		
//		c.printCriticalPath(g, TopologicalOrderIndex);
		c.printCriticalSequence(g);
		c.PrintTable(g);

		// To calculate end running time complexity
		long endTime = System.currentTimeMillis();
	
		// Total Time complexity
		long elapsedTime = endTime - startTime;
		System.out.println("\n Time is:" + elapsedTime);

	}

	/*
	 * Method used to perform Topological ordering of the specified graph
	 * Stack stack to store the topological order of the given graph
	 * Array TopologicalOrderIndex stores all the topological order of vertices
	 * This method returns the array TopologicalOrderIndex
	 */
	public static int[] TopologicalOrder(Graph g) {
		
		int[] TopologicalOrderIndex = new int[g.N + 1];	//To store the Topologicalorder of the given Graph

		Stack<Graph.Vertex> stack = DFS_Top(g);		//Call DFS and store the topological ordering of the graph in Stack stack

		int i = 1;

		while (!stack.isEmpty()) {

			Graph.Vertex u = stack.pop();

			TopologicalOrderIndex[i] = u.name;
			u.index = i;
			i++;
		}
		return TopologicalOrderIndex;
	}

	/*
	 * Method used to perform DFS 
	 * Returns stack which contains TopologicalOrder of the vertices from the graph
	 */
	public static Stack<Graph.Vertex> DFS_Top(Graph g) {

		Stack<Graph.Vertex> s = new Stack<Graph.Vertex>();

		for (Graph.Vertex u : g) {

			if (u.seen == false)
				DFS_Visit(u, s);
		}
		return s;
	}

	/*
	 * Method to perform DFS Visit on all vertices adjacent to Vertex u in the Graph
	 * Also detects if cycle is present in the graph and print the error message	
	 */
	public static void DFS_Visit(Graph.Vertex u, Stack<Graph.Vertex> s) {

		// Set the vertex u.seenas true and u.active as true
		u.seen = true;
		u.active = true;

		for (Graph.Edge e : u.Adj) {

			Graph.Vertex v = e.otherEnd(u);
			if (v.seen == false) {

				DFS_Visit(v, s);
			} else if (v.active) {

				System.out.print("Cycle is detected.Not a DAG");
				break;
			}
		}
		s.push(u);
		u.active = false;
	}

	/*
	 * Method to perform calculation and obtain earliest completion time 
	 * Parameters passed are the graph g and Topological ordr of the graph vertices
	 */
	public void CalculateEC(Graph graph, int[] TopologicalOrderIndex) {

		graph.s.ec = 0;	//Set the earliest completion time  for start vertex as 0

		for (int i = 1; i <= graph.N; i++) {	//Iterate for all the vertices in the graph except vertex s and t

			Graph.Vertex u = graph.V[TopologicalOrderIndex[i]];	//Take single node from the array TopologicalOrderIndex

			for (Graph.Edge e : u.RevAdj) {	//Search for the edges which are attached to vertex u from Reverse adj list 

				Graph.Vertex v = e.otherEnd(u);	// Other end of the edge that starts from vertex u

				if (v.ec + u.weight > u.ec)	//Find maximum earliest completion time for vertex u
					
					u.ec = v.ec + u.weight;
			}

		}

		for (Graph.Edge e : graph.t.RevAdj) {		//Calculate earliest completion time  for destination vertex using Reverse adj list 

			Graph.Vertex u = e.otherEnd(graph.t);

			if (graph.t.ec < u.ec + graph.t.weight)
				graph.t.ec = u.ec + graph.t.weight;
		}
	}

	/*
	 * Method to calculate latest completion time for all vertices in the graph
	 * Parameters passed are Graph g specified in input file and array which contains Topological Ordering of the graph
	 */
	public void CalculateLC(Graph graph, int[] TopologicalOrderIndex) {	

		graph.t.lc = graph.t.ec;	//Set destination node t latest completion time same as earliest completion time

		for (int i = TopologicalOrderIndex.length - 1; i > 0; i--) {	// Check for all the vertices from the graph

			Graph.Vertex u = graph.V[TopologicalOrderIndex[i]];	//Retrieve a vertex 
			
			for (Graph.Edge e : u.Adj) { //Find the edges which starts from vertex u

				Graph.Vertex v = e.otherEnd(u);	//Finds the other end of the vertex v

				if (v.lc - v.weight < u.lc) {	//find minimum latest completion time for vertex u

					u.lc = (v.lc - v.weight);

				}
			}
		}
	}
	
	/*
	 * Method to calculate Slack for each vertex except start S and destination T vertex
	 */
	public void CalculateSlack(Graph graph) {
		
		int i = 1;

		while (i != graph.V.length) {	//Check for the vertices in the graph

			Graph.Vertex v = graph.V[i];

			v.slack = Math.abs(v.ec - v.lc);	//Subtract ec - lc to get the slack time 

			if (v.slack == 0)		//If the slack is 0 that means it is a critical path
				criticalNodes.add(v);
			i++;
		}

	}

	/*
	 * method to print the table which has Task Number,EC ,LC and Slack for each vertex specified
	 * Parameters passed is the Graph g
	 */
	public void PrintTable(Graph g) {
		
		System.out.println("\n\nTask		" + "EC	" + "LC	" + "Slack \n");
		for (int i = 1; i <= g.N; i++) {

			System.out.println("" + i + " 		" + g.V[i].ec + "       "
					+ g.V[i].lc + "        " + g.V[i].slack);
		}


	}

	/*
	 * Method to print all the critical vertices from the graph
	 */
/*	public void printCriticalPath(Graph g, int[] TopologicalOrderIndex) {

		System.out.println("\n Critical Path");
		int i = 1;
		while (i != g.N) {
			Graph.Vertex u = g.V[TopologicalOrderIndex[i]];
			if (u.slack == 0)
				System.out.print(" " + u.name);
			i++;
		}
	}	*/

	/*
	 * Method to print the Critical Path
	 * Parameter passed is the graph g
	 * This method initialize all the parameters vertex.seen to person operations to find critical path
	 */
	public void printCriticalSequence(Graph g) {

		//	LinkedList to store the criticalPath
		// LinkedList is of Stack of graph vertices
		LinkedList<Stack<Graph.Vertex>> criticalPaths = new LinkedList<Stack<Graph.Vertex>>();
			
		//Set the destination node t attribute seen as false and slack as 0.
		g.t.seen = false;
		g.t.slack = 0;
		
		//Set all the variables seen as false
		int i = 1;
		while (i != (g.N + 1)) {
			Graph.Vertex u = g.V[i];
			u.seen = false;
			i++;
		}

		//Method to find different critical paths from the graph 
		Criticalpath(g,g.s,criticalPaths,new Stack<Graph.Vertex>());

		System.out.print("\n <-----> Critical Path is:" + g.t.ec
				+ " <----->Size is:" + criticalNodes.size());
		System.out.print("\n <-----> Number of Paths: "+criticalPaths.size());

		int index=1;
		for (Stack<Graph.Vertex> stack : criticalPaths) {	//Prints all vertices from the stack
	
			System.out.print("\n" + (index) + ": ");
			index++;
			
			int x = 0;
			
			for(Graph.Vertex u:stack){	//Print all the vertex from the graph
				
				if(x++>0)
					System.out.print(u.name + " ");
			}
		}

	}

	/*
	 * Method to find all the Criticalpaths in the graph
	 * Parameters passed are Graph g
	 * Vertex u from where we need to find all the critical path
	 * LinkedList which contains the path
	 * Stack which contains vertex of a single path
	 */
	private void Criticalpath(Graph g,Graph.Vertex u,LinkedList<Stack<Graph.Vertex>> differentPaths,Stack<Graph.Vertex> stackTemp) {

		Stack<Graph.Vertex> stack = new Stack<Graph.Vertex>();
		
		if (u.name==g.t.name) {	//check whether it reach the destination node

			if (sum == g.t.lc) {	//Check if the sum is same as destination t attribute lc time
				//Add to the stack 
				stack.addAll(stackTemp);
				//Add to the linkedList
				differentPaths.add(stack);
			}
		} 
		else {
			//If the node has not reached the destination node then check all the node on that specified path
			u.seen = true;
			stackTemp.push(u);
			
			if(u.name==0)	//If it is a start node s then use adj list this is s.Adj
				u.Adj = g.s.Adj;
			
			for (Graph.Edge e : u.Adj) {	//check all the edges adjacent to vertex u
				
				Graph.Vertex v = e.otherEnd(u);	//Fin ethe order end of the vertex
				
				if (v.ec == u.ec + v.weight && !v.seen && v.slack == 0) {	//checks if the other vertex is not already seen and has slack as 0 of the other noe v
					//increment the sum and then check for the path
					sum = sum + v.weight;
					//recursively call the function to find the 
					Criticalpath(g,v, differentPaths,stackTemp);
					//decrement the sum 
					sum = sum - v.weight;
				}
			}
			u.seen = false;
			stackTemp.pop();
		}

	}

}
