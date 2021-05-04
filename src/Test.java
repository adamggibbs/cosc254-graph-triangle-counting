
public class Test {

    static private double pi_t(int t, int M){

        double product = (double)M / t;
        product *= (double)(M-1) / (t-1);
        product *= (double)(M-2) / (t-2);

        return product;
    }
    
    public static void main(String[] args){
        int t = 150_000;
        int M = 5000;

        // int a = t * (t-1) * (t-2);
        // int b = M * (M-1) * (M-2);

        // System.out.println((double)a / b);

        System.out.println(pi_t(t,M));

    }
}
