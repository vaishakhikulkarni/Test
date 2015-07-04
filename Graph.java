package Project4;

/*
 * @author : Balaji Raghavachari
 * @author : Vaishakhi Kulkarni
 * 			// made changes as per requirements 
 * */


import java.io.*;
import java.util.*;

public class Graph implements Iterable<Graph.Vertex> {
	static final int INFINITY = Integer.MAX_VALUE;
	public Vertex[] V; // array of vertices
	public Vertex s;	//source vertex
	public Vertex t;	//destination vertex
	public int N; // number of vertices in the graph

	/**
	 * Constructor for Graph
	 * 
	 * @param size
	 *            : int - number of vertices
	 */
	Graph(int size) {

		N = size;
		V = new Vertex[size + 1];

		s = new Vertex(0);		//assign 0 as the name for source vertex
		t = new Vertex(N + 1);	// assign N(Size)+1 as the name for the destination vertex

		// create an array of Vertex objects
		for (int i = 1; i <= N; i++)
			V[i] = new Vertex(i);
	}

	/**
	 * Class that represents an arc in a Graph
	 *
	 */
	public static class Edge {

		public Vertex From; // head vertex
		public Vertex To; // tail vertex

		/**
		 * Constructor for Edge
		 * 
		 * @param u
		 *            : Vertex - The head of the arc
		 * @param v
		 *            : Vertex - The tail of the arc
		 * @param w
		 *            : int - The weight associated with the arc
		 */
		Edge(Vertex u, Vertex v) {
			From = u;
			To = v;
		}

		/**
		 * Method to find the other end end of the arc given a vertex reference
		 * 
		 * @param u
		 *            : Vertex
		 * @return
		 */
		public Vertex otherEnd(Vertex u) {
			// if the vertex u is the head of the arc, then return the tail else
			// return the head
			if (From == u) {
				return To;
			} else {
				return From;
			}
		}

		/**
		 * Method to represent the edge in the form (x,y) where x is the head of
		 * the arc and y is the tail of the arc
		 */
		public String toString() {
			return "(" + From + "," + To + ")";
		}
	}

	/**
	 * Class to represent a vertex of a graph
	 * 
	 *
	 */
	public static class Vertex implements Comparable<Vertex> {
		public int name; // name of the vertex
		public int ec; // earliest completion time
		public int lc; // latest completion time
		public int slack; // slack time
		public boolean seen; // flag to check if the vertex has already been
		public boolean active; // visited
		public int index; // index for the vertex
		public int weight; // field for storing int attribute of vertex
		public LinkedList<Edge> Adj; // adjacency list
		public LinkedList<Edge> RevAdj; // Reverse adj list

		/**
		 * Constructor for the vertex
		 * 
		 * @param n
		 *            : int - name of the vertex
		 */
		Vertex(int n) {
			name = n;
			seen = false;
			Adj = new LinkedList<Edge>();	//Adjacency list
			RevAdj = new LinkedList<Edge>();	//Reverse adjacency list
		}

		/**
		 * Method used to order vertices, based on algorithm
		 */
		public int compareTo(Vertex v) {
			return this.weight - v.weight;
		}

		/**
		 * Method to represent a vertex by its name
		 */
		public String toString() {
			return Integer.toString(name);
		}
	}

	/**
	 * Method to add an arc to the graph
	 * 
	 * @param a
	 *            : int - the head of the arc
	 * @param b
	 *            : int - the tail of the arc
	 * @param weight
	 *            : int - the weight of the arc
	 */
	void addEdge(int a, int b) {
		Edge e = new Edge(V[a], V[b]);
		V[a].Adj.add(e);		//Add edge in the adjacency list
		V[b].RevAdj.add(e);		//Add reverse of the edge in the reverse adjacency list

	}

	/**
	 * Method to add an duration to the graph vertex
	 */
	void addWeights(int weights[]) {

		for (int i = 1; i <= N; i++)
			V[i].weight = weights[i - 1];

		s.weight = 0; // Initialize weight of s as 0
		t.weight = 0; // Initialize weight of t as 0
	}

	/**
	 * Method to create an instance of VertexIterator
	 */
	public Iterator<Vertex> iterator() {
		return new VertexIterator<Vertex>(V, N);
	}

	/**
	 * A Custom Iterator Class for iterating through the vertices in a graph
	 * 
	 *
	 * @param <Vertex>
	 */
	public class VertexIterator<Vertex> implements Iterator<Vertex> {
		private int nodeIndex = 0;
		private Vertex[] iterV;// array of vertices to iterate through
		private int iterN; // size of the array

		/**
		 * Constructor for VertexIterator
		 * 
		 * @param v
		 *            : Array of vertices
		 * @param n
		 *            : int - Size of the graph
		 */
		private VertexIterator(Vertex[] v, int n) {
			nodeIndex = 0;
			iterV = v;
			iterN = n;
		}

		/**
		 * Method to check if there is any vertex left in the iteration
		 * Overrides the default hasNext() method of Iterator Class
		 */
		public boolean hasNext() {
			return nodeIndex != iterN;
		}

		/**
		 * Method to return the next Vertex object in the iteration Overrides
		 * the default next() method of Iterator Class
		 */
		public Vertex next() {
			nodeIndex++;
			return iterV[nodeIndex];
			// nodeIndex++;
		}

		/**
		 * Throws an error if a vertex is attempted to be removed
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in;

		if (args.length > 0) {
			File inputFile = new File(args[0]);
			in = new Scanner(inputFile);

		} else {
			in = new Scanner(System.in);
		}
		Graph g = readGraph(in);
		g.printGraph();
	}

	/**
	 * Method to read the Graph from the text file specified
	 * 
	 * @param n
	 *            : Total number of vertices
	 * @param m
	 *            : Total number of edges
	 * 
	 */
	static public Graph readGraph(Scanner in) {

		// read the graph related parameters
		int n = in.nextInt(); // number of vertices in the graph
		int m = in.nextInt(); // number of edges in the graph

		// create a graph instance
		Graph g = new Graph(n);

		// Assign true for vertex s and t as they are starting and termination
		// points
		g.s.seen = true;
		g.t.seen = true;

		int i = 0;
		int[] weights = new int[n];
		while (i != n) {		//store the precedence constraints of every edge
			weights[i] = in.nextInt();
			i++;
		}

		for (i = 0; i < m; i++) {		//add the edges into the adjacency list

			int u = in.nextInt();
			int v = in.nextInt();
			g.addEdge(u, v);
		}

		for (i = 1; i <= n; i++) {

			Edge e1 = new Edge(g.s, g.V[i]);	// add edges from source vertex s to all the vertices in the graph
			g.V[i].Adj.add(e1);
			Edge e2 = new Edge(g.V[i], g.t);
			g.V[i].RevAdj.add(e2);			// add edges from all vertices to destination node t in the graph and update in reverse 
											//adj list and adj list
			g.s.Adj.add(e2);

			Edge e3 = new Edge(g.V[i], g.s); // add edges from all vertices to source node s in the graph in reverse adj list
			g.V[i].RevAdj.add(e3);
			Edge e4 = new Edge(g.t, g.V[i]);	//add edges from destination t to all the vertices in the graph in reverse adj list	
												//as well in adj list
			g.V[i].Adj.add(e4);
			g.t.RevAdj.add(e4);

		}

		g.addWeights(weights);

		in.close();
		return g;
	}

	/**
	 * Method to initialize a graph 1) Set active attribute as false
	 * 2) Sets the seen attribute of every vertex as false 3) Set index attribute as 0
	 * 4)Set ec attribute as 0. 5)Set lc attribute as INFINITY 6)Set slack attribute as 0
	 * 
	 * @param g
	 *            : Graph - The reference to the graph
	 */
	void initialize() {

		for (Vertex u : this) {

			u.seen = false;
			u.active = false;
			u.index = 0;
			u.ec = 0;
			u.slack = 0;
			u.lc = INFINITY;
		}

		s.lc = t.lc = INFINITY;		//Setting values of lc and ec for source and destination
		s.ec = t.ec = 0;
	}

	/**
	 * Method to print the graph and reverse graph
	 * 
	 * @param g
	 *            : Graph - The reference to the graph
	 */
	void printGraph() {

		System.out.println("Adjacency List");

		for (Vertex u : this) {

			System.out.print(u + ": ");
			for (Edge e : u.Adj) {

				System.out.print(e);
			}
			System.out.println("Weight is" + u.weight);

			System.out.println();
		}

		System.out.println("Reversed Adjacency List");
		for (Vertex u : this) {

			System.out.print(u + ": ");

			for (Edge e : u.RevAdj) {

				System.out.print(e);
			}
			System.out.println("Weight is" + u.weight);

			System.out.println();
		}
	}
}
