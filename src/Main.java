import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import edu.princeton.cs.algs4.*;




public class Main {
    static EdgeWeightedDigraph graph;
    static DirectedEdge edge = new DirectedEdge(1,2,3);
    static ArrayList<Integer> busStops;
    static ArrayList<String> busStopInfo;
    static ArrayList<String> stopTimesInfo;
    static TST<String> tst;

    public static void main(String[] args) {
        readStops("stops.txt");
        readFileTransfers("transfers.txt");
        readFileStopTimes("stop_times.txt");

        userSearchArrivalTime();

        StdOut.println("keysWithPrefix(\"W 41 AVE NS COLUMBIA ST EB\"):");
        for (String s : tst.keysWithPrefix("W 41 AVE NS COLUMBIA ST EB"))
            StdOut.println(s);
        StdOut.println();

        StdOut.println();

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

    public static void checkArrivalTime(String time){
        for (String allInfo : stopTimesInfo) {
            String[] allInfoSplit = allInfo.split(",");
            if (allInfoSplit[1].trim().equals(time)) {
                System.out.println(allInfo);
            }
        }
    }

    public static void userSearchArrivalTime(){
        Scanner scanner = new Scanner(System.in);
        boolean end = false;
        while(!end){
            System.out.println("Enter \"quit\" to exit this program.");
            System.out.println("Enter arrival time in the format (hh:mm:ss): ");
            if(scanner.hasNextLine()){
                String userInput = scanner.nextLine().trim();
                if(userInput.equalsIgnoreCase("quit")){
                    end = true;
                }else{
                    String[] inputtedLineSplit = userInput.split(":");
                    try{
                        for (String s : inputtedLineSplit) {
                            if (Integer.parseInt(s) >= 0) {
                                Integer.parseInt(s);
                            }
                        }
                        checkArrivalTime(userInput);
                    }catch(Exception ignored){
                        System.out.println("Input arrival time in the correct format (hh:mm:ss).");
                    }
                }
            }else if(scanner.hasNext()){
                String input = scanner.nextLine();
                if(input.equalsIgnoreCase("quit")){
                    end = true;
                }else{
                    System.out.println("Please enter quit or an arrival time.");
                }
            } else{
                System.out.println("Incorrect format for arrival time please try again.");
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
                    graph.addEdge(edge);
                }else{
                    int cost = (Integer.parseInt(line[3]))/100;
                    int matrixValueOne = binarySearch(busStops,Integer.parseInt(line[0]));
                    int matrixValueTwo = binarySearch(busStops,Integer.parseInt(line[1]));
                    DirectedEdge edge = new DirectedEdge(matrixValueOne,matrixValueTwo, cost);
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
            stopTimesInfo = new ArrayList<>();
            myReader.nextLine(); // skip the first line
            String line = myReader.nextLine(); // read the line
            String[] lineOne = line.split(",");
            String[] time = lineOne[1].split(":"); // split the time into string array
            time[0] = time[0].trim();
            if(Integer.parseInt(time[0]) >= 0 && Integer.parseInt(time[0]) <= 23 ){
                stopTimesInfo.add(line);
                Collections.sort(stopTimesInfo);
            }
            while (myReader.hasNextLine()) {
                String nextLine = myReader.nextLine();
                String[] lineTwo = nextLine.split(",");
                String[] timeGiven = lineTwo[1].split(":"); // split the time into string array
                timeGiven[0] = timeGiven[0].trim();
                if(Integer.parseInt(timeGiven[0]) >= 0 && Integer.parseInt(timeGiven[0]) <= 23 ){
                    stopTimesInfo.add(nextLine);
                }
                if(Objects.equals(lineOne[0], lineTwo[0])) {
                    int matrixValueOne = binarySearch(busStops,Integer.parseInt(lineOne[3]));
                    int matrixValueTwo = binarySearch(busStops,Integer.parseInt(lineTwo[3]));
                    DirectedEdge edge = new DirectedEdge(matrixValueOne, matrixValueTwo,1);
                    graph.addEdge(edge);
                }
                lineOne = lineTwo;
            }
            stopTimesInfo.sort((o1, o2) -> {
                String[] lineSplit = o1.split(",");
                String[] secondLineSplit = o2.split(",");
                return lineSplit[0].compareTo(secondLineSplit[0]);
            });

            System.out.println("Number of Edges: " + graph.E());
            System.out.println("Number of Vertices: " + graph.V());
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static int binarySearch(ArrayList<Integer> arr, int x) {
        int left = 0, right = arr.size() - 1;

        while (left <= right) {
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
            busStopInfo = new ArrayList<>();
            tst = new TST<>();
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split(",");
                busStops.add(Integer.parseInt(line[0]));
                String name = changeName(line[2]);
                line[2] = name;
                shiftLeftByN(line,2);
                String newLine = formString(line, ",");

                tst.put(newLine,Integer.toString(count));
                count++;
            }
            Collections.sort(busStops);

            StdOut.println("keys(\"\"):");
            for (String key : tst.keys()) {
                StdOut.println("key is: " + key);
                StdOut.println(key + " " + tst.get(key));
            }
            StdOut.println();

            graph = new EdgeWeightedDigraph(busStops.size());
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String changeName(String name){
        String[] nameSplit = name.split(" ");
        if(nameSplit[0].equalsIgnoreCase("flagstop")||
                nameSplit[0].equalsIgnoreCase("wb") ||
                nameSplit[0].equalsIgnoreCase("nb") ||
                nameSplit[0].equalsIgnoreCase("sb") ||
                nameSplit[0].equalsIgnoreCase("eb")){
            if(nameSplit[1].equalsIgnoreCase("flagstop")||
                    nameSplit[1].equalsIgnoreCase("wb") ||
                    nameSplit[1].equalsIgnoreCase("nb") ||
                    nameSplit[1].equalsIgnoreCase("sb") ||
                    nameSplit[1].equalsIgnoreCase("eb")){
                if(nameSplit[2].equalsIgnoreCase("flagstop")||
                        nameSplit[2].equalsIgnoreCase("wb") ||
                        nameSplit[2].equalsIgnoreCase("nb") ||
                        nameSplit[2].equalsIgnoreCase("sb") ||
                        nameSplit[2].equalsIgnoreCase("eb")){
                    shiftLeftByN(nameSplit, 3);
                }else {
                    shiftLeftByN(nameSplit, 2);
                }
            }else {
                shiftLeftByN(nameSplit, 1);
            }
            name = formString(nameSplit, " ");
        }
        return name.trim();
    }

    public static String formString(String[] array, String delimeter) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s).append(delimeter);
        }
        return sb.toString();
    }


    public static String[] shiftLeftByN(String[] string, int n) {
        for (int i = 0; i < n; i++) {
            int j = 0;
            //Stores the first element of the array
            String first = string[0];
            for (; j < string.length - 1; j++) {
                //Shift element of array by one
                string[j] = string[j + 1];
            }
            //First element of array will be added to the end
            string[j] = first;
        }
        return string;
    }
}

