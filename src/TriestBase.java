import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class TriestBase implements DataStreamAlgo {

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
	public TriestBase(int samsize){

        // initialize inputted sample size
        this.samsize = samsize;
        
        // initialize other instance variables
        time = 0;
        triangles = 0;
        vertex_neighbors = new LinkedHashMap<Integer, LinkedHashSet<Integer>>();
        edges = new ArrayList<>();
	}

	public void handleEdge(Edge edge){

        // if less than samsize edges, just add edge
        // otherwise add edge with p = sample_size / time
        if(time <= samsize){

            // count how many new triangles are added with new edge
            // update triangles count 
            int new_triangles = countTriangles(edge);
            triangles += new_triangles;

            // add edge to sample
            addEdge(edge);

        } else if(flipCoin(time)){

            //randomly pick an edge
            Edge toRemove = getRandomEdge();
            // remove randomly picked edge
            removeEdge(toRemove);

            // count how many triangles were lost, 
            // update traingles count
            int lost_triangles = countTriangles(toRemove);
            triangles -= lost_triangles;

            // count how many new triangles are added with new edge
            // update triangles count 
            int new_triangles = countTriangles(edge);
            triangles += new_triangles;

            // add new edge to sample
            addEdge(edge);

        }

        time++;

    } // handleEdge()

	public int getEstimate(){ 

        // if fewer edges than sample size have been inserted,
        // return the number of triangles
        if(time <= samsize){
            return triangles;
        }

        // if full sample size has been reached,
        // return corrected triangle estimate
        return (int)(triangles / pi_t(time));

        
    } // getEstimate()

    /*
     * HELPER METHODS
     * flipcoin(): simulate coin flip, returns boolean
     * addEdge(): adds edge (and inverse) and its vertices to sample
     * removeEdge(): removes edge (and inverse) and its vertices from sample
     * getRandomEdge(): gets randomly chosen edge in sample, returns Edge
     * countTriangles(): counts how many triangles that edge is a part of, returns int
     * pi_t(): calculates the value of pi(t) to correct bias, returns double
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

    private double pi_t(int t){

        double product = (double)samsize / t;
        product *= (double)(samsize-1) / (t-1);
        product *= (double)(samsize-2) / (t-2);

        return product;
    } // pi_t()

} // TriestBase
