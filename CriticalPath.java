package Project4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import Project4.Graph;


public class CriticalPath {

	public int s=0,t=101;
	public Graph graph;
	public ArrayList<Graph.Vertex> criticalPaths;
	public int[] TopologicalOrderIndex; 
	
	public CriticalPath(Graph g)
	{
		graph = g;
		criticalPaths = new ArrayList<Graph.Vertex>();
	}

	public static void main(String args[]) throws IOException
	{
		Scanner in = null;
		try{
			if (args.length > 0) {
			    File inputFile = new File(args[0]);
			    in = new Scanner(inputFile);
			} else {
			    in = new Scanner(System.in);
			}
		}
		catch (FileNotFoundException e) // Handle File not Found error
		{
			System.out.println("Please make sure the directory file is actually there.");
		} 
		Graph g = Graph.readGraph(in);
		g.initialize();
		g.printGraph();
		
		CriticalPath cp = new CriticalPath(g);
		cp.TopologicalOrder();
		cp.CalculateEC();
		cp.CalculateLC();
	}
	
	public void TopologicalOrder()
	{
		Stack<Graph.Vertex> stack = DFS_Top(graph);
		
		TopologicalOrderIndex = new int[graph.N+1];
		
		int i=0;
		stack.pop();
		stack.pop();
		while(!stack.isEmpty())
		{
			Graph.Vertex u = stack.pop();
			System.out.println("Vertex name"+u.name);
			TopologicalOrderIndex[i] = u.name;
			u.index = i;
			i++;
		}
	}
	
	public void CalculateEC()
	{
		graph.V[s].ec =0;
		for(int i=1;i<graph.N-2;i++)
		{
			Graph.Vertex u = graph.V[TopologicalOrderIndex[i]];
			for(Graph.Edge e:u.RevAdj)
			{
				Graph.Vertex v = e.otherEnd(u);
				if(v.ec+u.weight>u.ec)
					u.ec = v.ec+u.weight;			
			}
			System.out.println(" Order index "+TopologicalOrderIndex[i]+" EC "+u.ec);
		}
	
		for (Graph.Edge e : graph.V[t].RevAdj)
		{
			Graph.Vertex u = e.otherEnd(graph.V[t]);
			if (graph.V[t].ec < u.ec + graph.V[t].weight)
				graph.V[t].ec = u.ec + graph.V[t].weight;
		}

		
	}
	
	public void CalculateLC()
	{
		graph.V[t].lc = graph.V[t].ec;
		for(int i=graph.N-3;i>=0;i--)
		{
			Graph.Vertex u = graph.V[TopologicalOrderIndex[i]];
			for(Graph.Edge e:u.Adj)
			{
				Graph.Vertex v = e.otherEnd(u);
				if(v.lc-v.weight<u.lc)
					u.lc = v.lc - v.weight;
			
			}
			System.out.println(" Order index "+TopologicalOrderIndex[i]+" LC "+u.lc);

		}
	}
	
	public Stack<Graph.Vertex> DFS_Top(Graph g){		
		// Create Stack s
		Stack<Graph.Vertex> s = new Stack<Graph.Vertex>();
		// Call DFS_Visit
		for (Graph.Vertex u : g) {
			if (u.seen==false)
				DFS_Visit(u, s);
		}
		return s;
	}
	
	public static void DFS_Visit(Graph.Vertex u, Stack<Graph.Vertex> s) {
		// Set the vertex u with some value
		u.seen = true;
		u.active = true;

		for (Graph.Edge e : u.Adj) {
			Graph.Vertex v = e.otherEnd(u);
			if (v.seen==false) {
				DFS_Visit(v, s);
			} else if (v.active) { // To detect cycle
				System.out.print("Cycle is detected.Not a DAG");
				break;
			}
		}
		s.push(u);
		u.active = false;
	}
}
