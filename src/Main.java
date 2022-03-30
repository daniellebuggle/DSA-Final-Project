import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;


public class Main {
    static Graph graph;
    static LinkedList<Edge> edges = new LinkedList<>();

    public static void main(String[] args){
        readFileTransfers("transfers.txt");
        readFileStopTimes("stop_times.txt");
    }

    public static void readFileTransfers(String filename){
        try {
            if (filename == null) {
                return;
            }
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            int count = 0;
            myReader.nextLine(); // skip the first line
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split(",");
                if(Objects.equals(line[2], "0")){
                    Edge edge = new Edge(Integer.parseInt(line[0]),Integer.parseInt(line[1]),2);
                    edges.add(edge);
                }else{
                    int cost = (Integer.parseInt(line[3]))/100;
                    Edge edge = new Edge(Integer.parseInt(line[0]),Integer.parseInt(line[1]), cost);
                    edges.add(edge);
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
                    Edge edge = new Edge(Integer.parseInt(lineOne[3]), Integer.parseInt(lineTwo[3]),1);
                    edges.add(edge);
                }
                lineOne = lineTwo;
            }
            Edge edge = edges.getLast();
            System.out.println("Vertice One: " + edge.getVerticeOne() + "\nVertice Two: " + edge.getVerticeTwo());
            System.out.println("Number of Edges: " + edges.size());
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
