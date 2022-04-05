import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import edu.princeton.cs.algs4.*;

public class Main {
    static EdgeWeightedDigraph graph; // empty graph used in Dijkstra
    static DirectedEdge edge; // edges to be added to graph for Dijkstra
    static ArrayList<Integer> busStops; // arraylist of integers used as corresponding matrix value for creation of
    // graph
    static ArrayList<String> stopTimesInfo; // arrayList of Strings containing all the information as a string from
    // stop_times.txt for each trip
    static TST<String> tst; // empty ternary search tree

    public static void main(String[] args) {
        // read in each file and initialise data
        readStops("stops.txt");
        readFileTransfers("transfers.txt");
        readFileStopTimes("stop_times.txt");

        boolean exitEntireProgram = false; // used for program loop
        while (!exitEntireProgram) {
            System.out.println("Please enter 1, 2 or 3 to use the following functions:\n");
            System.out.println("To search for the shortest trip between two Bus Stops, by inputting their bus Stop ID, "
                    + "please enter 1.\n");
            System.out.println("To search for a bus stop by name, please enter 2.\n");
            System.out.println("To search for a trip given its arrival time, please enter 3.\n");
            System.out.println("To exit the entire program please enter \"exit\".");
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                int valueInputted = scanner.nextInt(); // get integer value user inputted
                if (valueInputted == 1) {
                    searchBusStop(); // functionality to search for shortest trip between two valid bus stops
                } else if (valueInputted == 2) {
                    searchByName(); // functionality to search for bus stop names given valid prefix
                } else if (valueInputted == 3) {
                    userSearchArrivalTime(); // functionality to search for trip by its arrival time
                } else {
                    // error handling if user inputted invalid integer value
                    System.out.println("Please enter a valid integer value i.e. 1, 2, or 3.\n");
                }
            } else {
                // get user input as string
                String input = scanner.next();
                // exit the program if they type "exit"
                if (input.equalsIgnoreCase("exit")) {
                    exitEntireProgram = true;
                    System.out.println("Thank you, your session has ended.");
                }else{
                    // error handling if user inputted invalid string value
                    System.out.println("Please enter a valid integer value or \"quit\".");
                }
            }
        }


    }


    /**
     * Method used to implement functionality of searching for a bus stop by name using prefixes.
     */
    public static void searchByName() {
        Scanner scanner = new Scanner(System.in);
        boolean end = false; // boolean used for looping this part of the program.
        while (!end) {
            System.out.println("To exit this part of the program please enter \"quit\".");
            System.out.println("Please enter bus stop name to search for: ");
            String input = scanner.nextLine().trim(); // trim whitespace from inputted from user
            if (input.equalsIgnoreCase("quit")) {
                // exit this part of the program if user enters quit
                end = true;
            } else {
                StdOut.println("Bus Stops beginning with (\"" + input + "\"):");
                int count = 0;
                for (String s : tst.keysWithPrefix(input.toUpperCase())) {
                    count++; // count used to check if bus stops with this name were found
                    StdOut.println(s); // print out each bus stop name with inputted prefix
                }
                if (count == 0) {
                    StdOut.println("There were no Bus Stops with this prefix.");
                }
                StdOut.println();
            }
        }
    }

    /**
     * Method used to implement functionality of searching for shortest trip between two bus stops given their bus stop
     * ID.
     */
    public static void searchBusStop() {
        Scanner scanner = new Scanner(System.in);
        boolean end = false; // boolean used for looping this part of the program.
        while (!end) {
            System.out.println("To exit this part of the program please enter \"quit\".");
            System.out.println("Enter Bus Stop ID (from) or \"quit\": ");
            // check integer was inputted
            if (scanner.hasNextInt()) {
                int from = scanner.nextInt(); // get bus stop ID for start destination
                System.out.println("Enter Bus Stop ID (to) or \"quit\": ");
                // check integer was inputted
                if (scanner.hasNextInt()) {
                    int to = scanner.nextInt(); // get bus stop ID  for end destination
                    // get matrix value from list as to where these bus stop IDs are stored in the graph
                    int arrayValueFrom = binarySearch(busStops, from);
                    int arrayValueTo = binarySearch(busStops, to);
                    // if either are -1, bus stop does not exist and tells the user.
                    if (arrayValueFrom == -1) {
                        System.out.println("Bus Stop " + from + " does not exist.\nPlease Enter new stops.");
                        if(arrayValueTo == -1){
                            System.out.println("Bus Stop " + to + " does not exist.\nPlease Enter new stops.");
                        }
                        return;
                    } else if (arrayValueTo == -1) {
                        System.out.println("Bus Stop " + to + " does not exist.\nPlease Enter new stops.");
                    } else {
                        DijkstraSP dijkstraSP = new DijkstraSP(graph, arrayValueFrom); // create dijkstra to get SP
                        // beginning at users inputted start destination
                        // check if there is path between users start and end destinations
                        if (dijkstraSP.hasPathTo(arrayValueTo)) {
                            StdOut.printf("Bus stop: %d to Bus Stop: %d \n", from, to);
                            for (DirectedEdge e : dijkstraSP.pathTo(arrayValueTo)) {
                                // print out each step of the shortest path and its associated cost
                                StdOut.printf("Bus Stop: %d to Bus Stop: %d, cost: (%.2f)\n", busStops.get(e.from()),
                                        busStops.get(e.to()), e.weight());
                            }
                            StdOut.println();
                        }
                        // print out total cost of SP
                        System.out.println("Shortest Path costs a total of: " + dijkstraSP.distTo(arrayValueTo));
                    }
                } else {
                    if (scanner.next().equalsIgnoreCase("quit")) {
                        // exits this part of the program if user entered quit
                        end = true;
                    } else {
                        // error handling for if user enters invalid input
                        System.out.println("Please enter \"quit\" or a valid Bus Stop ID.");
                    }
                }
            } else {
                if (scanner.next().equalsIgnoreCase("quit")) {
                    // exits this part of the program if user entered quit
                    end = true;
                } else {
                    // error handling for if user enters invalid input
                    System.out.println("Please enter \"quit\" or a valid Bus Stop ID.");
                }
            }
        }
    }

    /**
     * Function used to check user inputted time is associated with any of the trips.
     * Prints out each trip associated with time parameter sorted by trip ID.
     * @param time
     */
    public static void checkArrivalTime(String time) {
        int count = 0; // used to count if any times match
        // loops through entire list checking if time matches
        for (String allInfo : stopTimesInfo) {
            String[] allInfoSplit = allInfo.split(",");
            if (allInfoSplit[1].trim().equals(time)) {
                count++;
                System.out.println(allInfo); // prints out trip information if time matches
            }
        }
        if (count == 0) {
            // if no times match informs the user
            System.out.println("There were no trips with this arrival time.");
        }
    }

    /**
     * Implements functionality for user to search for trips by inputting arrival time.
     */
    public static void userSearchArrivalTime() {
        Scanner scanner = new Scanner(System.in);
        boolean end = false; // boolean used for looping this part of the program.
        while (!end) {
            System.out.println("Enter \"quit\" to exit this program.");
            System.out.println("Enter arrival time in the format (hh:mm:ss): ");
            if (scanner.hasNextLine()) {
                String userInput = scanner.nextLine().trim(); // trim whitespace inputted by user
                if (userInput.equalsIgnoreCase("quit")) {
                    // if user enters "quit" exits this part of the program
                    end = true;
                } else {
                    String[] inputtedLineSplit = userInput.split(":"); // split user input by ":"
                    try {
                        // check if user entered valid time with three valid numbers
                        for (String s : inputtedLineSplit) {
                            if (Integer.parseInt(s) >= 0 && Integer.parseInt(s) <= 23 && s.length() < 3) {
                                Integer.parseInt(s);
                            }
                        }
                        System.out.println("trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign," +
                                "pickup_type,drop_off_type,shape_dist_traveled");
                        checkArrivalTime(userInput); // function to print out each trip with users inputted arrival time
                    } catch (Exception ignored) {
                        // error handling for if user did not input valid time in the correct format
                        System.out.println("Input arrival time in the correct format (hh:mm:ss).\n");
                    }
                }
            } else if (scanner.hasNext()) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("quit")) {
                    // exits this part of the program
                    end = true;
                } else {
                    // error handling for if user inputted invalid string
                    System.out.println("Please enter quit or an arrival time.\n");
                }
            } else {
                // error handling for when user inputs incorrect format for arrival time
                System.out.println("Incorrect format for arrival time please try again.\n");
            }
        }
    }

    /**
     * Method to read in the file transfers.txt and initialise all data that needs to be used.
     * @param filename
     */
    public static void readFileTransfers(String filename) {
        try {
            if (filename == null) {
                return;
            }
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            myReader.nextLine(); // skip the first line
            // loops while there is still data to read in the file
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split(","); // splits the line by the delimiter ","
                // if the transfer type is 0
                if (Objects.equals(line[2], "0")) {
                    // get corresponding matrix value for each of bus stop id
                    int matrixValueOne = binarySearch(busStops, Integer.parseInt(line[0]));
                    int matrixValueTwo = binarySearch(busStops, Integer.parseInt(line[1]));
                    edge = new DirectedEdge(matrixValueOne, matrixValueTwo, 2);// create new edge with the
                    // corresponding matrix values with a cost of 2
                    graph.addEdge(edge); // add edge to the graph
                } else {
                    int cost = (Integer.parseInt(line[3])) / 100; // get weight of edge
                    // get corresponding matrix value for each of bus stop id
                    int matrixValueOne = binarySearch(busStops, Integer.parseInt(line[0]));
                    int matrixValueTwo = binarySearch(busStops, Integer.parseInt(line[1]));
                    DirectedEdge edge = new DirectedEdge(matrixValueOne, matrixValueTwo, cost); // create new edge with
                    // the corresponding matrix values and with the cost calculated
                    graph.addEdge(edge);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Method to read in the file transfers.txt and initialise all data that needs to be used.
     * @param filename
     */
    public static void readFileStopTimes(String filename) {
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
            time[0] = time[0].trim(); // gets rid of the whitespace
            if (Integer.parseInt(time[0]) >= 0 && Integer.parseInt(time[0]) <= 23) {
                stopTimesInfo.add(line); // adds all stop info into the arrayList for use in printing later
            }
            while (myReader.hasNextLine()) {
                String nextLine = myReader.nextLine();
                String[] lineTwo = nextLine.split(",");
                String[] timeGiven = lineTwo[1].split(":"); // split the time into string array
                timeGiven[0] = timeGiven[0].trim(); // gets rid of whitespace
                if (Integer.parseInt(timeGiven[0]) >= 0 && Integer.parseInt(timeGiven[0]) <= 23) {
                    stopTimesInfo.add(nextLine); // adds all stop info into the arrayList for use in printing later
                }
                if (Objects.equals(lineOne[0], lineTwo[0])) {
                    // get corresponding matrix value for each of bus stop id
                    int matrixValueOne = binarySearch(busStops, Integer.parseInt(lineOne[3]));
                    int matrixValueTwo = binarySearch(busStops, Integer.parseInt(lineTwo[3]));
                    DirectedEdge edge = new DirectedEdge(matrixValueOne, matrixValueTwo, 1); // create edge with
                    // the corresponding matrix values and a cost of 1
                    graph.addEdge(edge); // add edge to the graph
                }
                lineOne = lineTwo; // set line one to line two to make sure not to miss any pairs
            }
            // sort the array in terms of trip ID
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

    /**
     * Credit: https://www.geeksforgeeks.org/binary-search/
     * Method to implement binary search on an ArrayList of type integer
     * @param arr
     * @param x
     * @return index of the array the value searched for is positioned
     */
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

    /**
     * Method to read in the stops.txt file and initialise all data that needs to be used.
     * @param filename
     */
    public static void readStops(String filename) {
        try {
            if (filename == null) {
                return;
            }
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            myReader.nextLine(); // skip the first line
            int count = 0; // used for positioning in TST
            busStops = new ArrayList<>(); // initialise busStops array
            tst = new TST<>(); // initialise tst
            // loops while there is still data to read in the file
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split(","); // split line using delimiter ","
                busStops.add(Integer.parseInt(line[0])); // add each bus stop ID to array and using its index as its
                // corresponding matrix value for creation of the graph
                String name = changeName(line[2]); // function edits bus stop name to remove unnecessary keywords at the
                // beginning of the name
                line[2] = name; // store new name back
                shiftLeftByN(line, 2); // shift left by 2 to put the name of bus stop at the beginning
                String newLine = formString(line, ","); // form new line with bus stop at the beginning
                // split by commas
                tst.put(newLine, Integer.toString(count)); // put new line in the tst
                count++;
            }
            Collections.sort(busStops); // sort Bus Stop array in ascending order
            graph = new EdgeWeightedDigraph(busStops.size()); // create graph with size of busStops because that is the
            // number of vertices
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Method to shift the name of the bus stop to move to the end of the name unnecessary keywords such as flagstop,
     * wb, nb, sb and eb.
     * @param name
     * @return Changed String with the new bus stop name.
     */
    public static String changeName(String name) {
        String[] nameSplit = name.split(" ");
        if (nameSplit[0].equalsIgnoreCase("flagstop") ||
                nameSplit[0].equalsIgnoreCase("wb") ||
                nameSplit[0].equalsIgnoreCase("nb") ||
                nameSplit[0].equalsIgnoreCase("sb") ||
                nameSplit[0].equalsIgnoreCase("eb")) {
            if (nameSplit[1].equalsIgnoreCase("flagstop") ||
                    nameSplit[1].equalsIgnoreCase("wb") ||
                    nameSplit[1].equalsIgnoreCase("nb") ||
                    nameSplit[1].equalsIgnoreCase("sb") ||
                    nameSplit[1].equalsIgnoreCase("eb")) {
                if (nameSplit[2].equalsIgnoreCase("flagstop") ||
                        nameSplit[2].equalsIgnoreCase("wb") ||
                        nameSplit[2].equalsIgnoreCase("nb") ||
                        nameSplit[2].equalsIgnoreCase("sb") ||
                        nameSplit[2].equalsIgnoreCase("eb")) {
                    shiftLeftByN(nameSplit, 3);
                } else {
                    shiftLeftByN(nameSplit, 2);
                }
            } else {
                shiftLeftByN(nameSplit, 1);
            }
            name = formString(nameSplit, " ");
        }
        return name.trim();
    }

    /**
     * Method to create new string with given a string array and a delimiter
     * @param array String array to be used to form new string
     * @param delimeter delimiter to be used to form string e.g. "," ":"
     * @return The formed string using the delimiter
     */
    public static String formString(String[] array, String delimeter) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s).append(delimeter);
        }
        return sb.toString();
    }


    /**
     * Method to shift a string array left by n.
     * @param string String array to be shifted
     * @param n Shift string array by n
     */
    public static void shiftLeftByN(String[] string, int n) {
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
    }
}

