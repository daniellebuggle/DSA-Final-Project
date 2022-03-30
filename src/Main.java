import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import edu.princeton.cs.algs4.*;




public class Main {
    static EdgeWeightedDigraph graph;
    static DirectedEdge edge = new DirectedEdge(1,2,3);
    static ArrayList<Integer> busStops;

    public static void main(String[] args) {
        readStops("stops.txt");
        readFileTransfers("transfers.txt");
        readFileStopTimes("stop_times.txt");

        Scanner scanner = new Scanner(System.in);
        boolean end = false;
        while(!end) {
            System.out.println("Enter Bus Stop ID (from): ");
            int from = scanner.nextInt();
            System.out.println("Enter Bus Stop ID (to): ");
            int to = scanner.nextInt();
            int arrayValueFrom = binarySearch(busStops, from);
            int arrayValueTo = binarySearch(busStops, to);
            if(arrayValueFrom == -1){
                System.out.println("Bus Stop " + from + " does not exist.\nPlease Enter new stops");
            }else if(arrayValueTo == -1){
                System.out.println("Bus Stop " + to + " does not exist.\nPlease Enter new stops");
            }else {
                DijkstraSP dijkstraSP = new DijkstraSP(graph, arrayValueFrom);
                if (dijkstraSP.hasPathTo(arrayValueTo)) {
                    StdOut.printf("%d to %d \n", from, to);
                    for (DirectedEdge e : dijkstraSP.pathTo(arrayValueTo)) {
                        StdOut.printf("%d to %d, cost: (%.2f)\n", busStops.get(e.from()), busStops.get(e.to()), e.weight());
                    }
                    StdOut.println();
                }
                System.out.println("Shortest Path costs: " + dijkstraSP.distTo(arrayValueTo));
                end = true;
            }
        }



    }

    public static void readFileTransfers(String filename){
        try {
            if (filename == null) {
                return;
            }
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            myReader.nextLine(); // skip the first line
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split(",");

                if(Objects.equals(line[2], "0")){
                    int matrixValueOne = binarySearch(busStops,Integer.parseInt(line[0]));
                    int matrixValueTwo = binarySearch(busStops,Integer.parseInt(line[1]));
                    edge = new DirectedEdge(matrixValueOne,matrixValueTwo,2);
                    //System.out.println(edge);
                    graph.addEdge(edge);
                }else{
                    int cost = (Integer.parseInt(line[3]))/100;
                    int matrixValueOne = binarySearch(busStops,Integer.parseInt(line[0]));
                    int matrixValueTwo = binarySearch(busStops,Integer.parseInt(line[1]));
                    DirectedEdge edge = new DirectedEdge(matrixValueOne,matrixValueTwo, cost);
                    //System.out.println(edge);
                    graph.addEdge(edge);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void readFileStopTimes(String filename){
        try {
            if (filename == null) {
                return;
            }
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            int count = 0;
            myReader.nextLine(); // skip the first line
            String[] lineOne = myReader.nextLine().split(",");
            while (myReader.hasNextLine()) {
                String[] lineTwo = myReader.nextLine().split(",");
                if(Objects.equals(lineOne[0], lineTwo[0])) {
                    int matrixValueOne = binarySearch(busStops,Integer.parseInt(lineOne[3]));
                    int matrixValueTwo = binarySearch(busStops,Integer.parseInt(lineTwo[3]));
                    DirectedEdge edge = new DirectedEdge(matrixValueOne, matrixValueTwo,1);
                    graph.addEdge(edge);
                    //System.out.println(edge);
                }
                lineOne = lineTwo;
            }
            System.out.println("Number of Edges: " + graph.E());
            System.out.println("Number of Vertices: " + graph.V());
            //System.out.println(graph);
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static int binarySearch(ArrayList<Integer> arr, int x)
    {
        int left = 0, right = arr.size() - 1;

        while (left <= right)
        {
            int mid = left + (right - left) / 2;

            // Check if x is present at mid
            if (arr.get(mid) == x)
                return mid;

            // If x greater, ignore left half
            if (arr.get(mid) < x)
                left = mid + 1;

                // If x is smaller, ignore right half
            else
                right = mid - 1;
        }

        // if we reach here, then element was
        // not present
        return -1;
    }

    public static void readStops(String filename){
        try {
            if (filename == null) {
                return;
            }
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            myReader.nextLine(); // skip the first line
            int count = 0;
            busStops = new ArrayList<>();
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split(",");
                busStops.add(Integer.parseInt(line[0]));
            }
            Collections.sort(busStops);
            //System.out.println(busStops);
            graph = new EdgeWeightedDigraph(busStops.size());
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
