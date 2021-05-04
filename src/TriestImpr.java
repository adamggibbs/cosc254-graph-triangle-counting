import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class TriestImpr implements DataStreamAlgo {
    int samsize;
    int time;
    int triangles;
    LinkedHashMap<Integer, LinkedHashSet<Integer>> vertex_neighbors;
    ArrayList<Edge> edges;

    /*
     * Constructor.
     * The parameter samsize denotes the size of the sample, i.e., the number of
     * edges that the algorithm can store.
     */
	public TriestImpr(int samsize){

        // initialize inputted sample size
        this.samsize = samsize;
        
        // initialize other instance variables
        time = 0;
        triangles = 0;
        vertex_neighbors = new LinkedHashMap<Integer, LinkedHashSet<Integer>>();
        edges = new ArrayList<>();
	}

	public void handleEdge(Edge edge){

        

        if(time <= samsize){
            int new_triangles = countTriangles(edge);
            triangles += new_triangles;
            addEdge(edge);
        } else{

            int new_triangles = countTriangles(edge);

            double nt = nt_t(time);

            triangles += (int)(nt*new_triangles);

            if(flipCoin(time)){
                Edge randEdge = getRandomEdge();
                removeEdge(randEdge);
                addEdge(edge);
            }
        }

        time++;

    } // handleEdge()

	public int getEstimate(){ 

        return triangles;
        
    } // getEstimate()

    /*
     * HELPER METHODS
     * flipcoin(): simulate coin flip, returns boolean
     * addEdge(): adds edge (and inverse) and its vertices to sample
     * removeEdge(): removes edge (and inverse) and its vertices from sample
     * getRandomEdge(): gets randomly chosen edge in sample, returns Edge
     * countTriangles(): counts how many triangles that edge is a part of, returns int
     * nt_t(): calculates the value of nt(t) to update triangles, returns double
     */
    private boolean flipCoin(int t){

        double bias = (double)samsize / t;
        return Math.random() < bias;

    } //flipCoin()

    private void addEdge(Edge edge){

        edges.add(edge);

        if(vertex_neighbors.containsKey(edge.u)){
            vertex_neighbors.get(edge.u).add(edge.v);
        } else {
            vertex_neighbors.put(edge.u, new LinkedHashSet<Integer>());
            vertex_neighbors.get(edge.u).add(edge.v);
        }

        if(vertex_neighbors.containsKey(edge.v)){
            vertex_neighbors.get(edge.v).add(edge.u);
        } else {
            vertex_neighbors.put(edge.v, new LinkedHashSet<Integer>());
            vertex_neighbors.get(edge.v).add(edge.u);
        }

    } // addEdge()

    private void removeEdge(Edge edge){

        vertex_neighbors.get(edge.u).remove(edge.v);
        if(vertex_neighbors.get(edge.u).isEmpty()){
            vertex_neighbors.remove(edge.u);
        }

        if(edge.u != edge.v){
            vertex_neighbors.get(edge.v).remove(edge.u);
            if(vertex_neighbors.get(edge.v).isEmpty()){
                vertex_neighbors.remove(edge.v);
            }
        }
        

    } // removeEdge()

    private Edge getRandomEdge(){

        int index = (int)(Math.random()*edges.size());
        Edge randEdge = edges.get(index);
        edges.remove(index);

        return randEdge;
    
    } // getRandomEdge()

    private int countTriangles(Edge edge){

        if(edge.u == edge.v){
            return 0;
        }

        int triangles = 0;

        LinkedHashSet<Integer> u_neighbors = vertex_neighbors.get(edge.u);
        LinkedHashSet<Integer> v_neighbors = vertex_neighbors.get(edge.v);

        if(u_neighbors != null && v_neighbors != null){
            for (int neighbor : u_neighbors) {

                if(v_neighbors.contains(neighbor)){
                    triangles++;
                }
                
            }
        }
        

        return triangles;

    } // countTriangles()

    private double nt_t(int t){

        double nt = (double)(t-1) / samsize;
        nt *= (double)(t-2) / (samsize-1);

        return nt;
    
    } // nt_t()
}
