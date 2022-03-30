public class Edge {
    int verticeOne;
    int verticeTwo;
    double weight;


    public Edge(int verticeOne, int verticeTwo, double weight) {
        this.verticeOne = verticeOne;
        this.verticeTwo = verticeTwo;
        this.weight = weight;
    }

    public int getVerticeOne() {
        return verticeOne;
    }

    public int getVerticeTwo() {
        return verticeTwo;
    }

    public double getWeight() {
        return weight;
    }
}
