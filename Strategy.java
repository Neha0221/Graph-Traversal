import java.util.ArrayList;

abstract public class Strategy {
    private int size, diagonalMoveCost;
    private long runTime;
    private double kValue;
    private Frame frame;
    private Node startNode, endNode, curNode;
    private boolean diagonal, running, noPath, complete;
    private ArrayList<Node> borders, open, closed, path;

    public Strategy(int size) {
        this.size = size;

        diagonalMoveCost = (int) (Math.sqrt(2 * (Math.pow(size, 2))));
        kValue = Math.PI / 2;
        diagonal = true;
        running = false;
        complete = false;

        borders = new ArrayList<Node>();
        open = new ArrayList<Node>();
        closed = new ArrayList<Node>();
        path = new ArrayList<Node>();
    }

    public Strategy(Frame frame, int size) {
        this.frame = frame;
        this.size = size;

        diagonalMoveCost = (int) (Math.sqrt(2 * (Math.pow(size, 2))));
        kValue = Math.PI / 2;
        diagonal = true;
        running = false;
        complete = false;

        borders = new ArrayList<Node>();
        open = new ArrayList<Node>();
        closed = new ArrayList<Node>();
        path = new ArrayList<Node>();
    }

    public Strategy(Frame frame, int size, Node start, Node end) {
        this.frame = frame;
        this.size = size;
        startNode = start;
        endNode = end;

        diagonalMoveCost = (int) (Math.sqrt(2 * (Math.pow(size, 2))));
        diagonal = true;
        running = false;
        complete = false;

        borders = new ArrayList<Node>();
        open = new ArrayList<Node>();
        closed = new ArrayList<Node>();
        path = new ArrayList<Node>();
    }

    public void start(Node s, Node e) {
        running = true;
        startNode = s;
        startNode.setG(0);
        endNode = e;

        // Adding the starting node to the closed list
        addClosed(startNode);

        long startTime = System.currentTimeMillis();

        findPath(startNode);

        complete = true;
        long endTime = System.currentTimeMillis();
        runTime = endTime - startTime;
        System.out.println("Completed: " + runTime + "ms");
    }

    public void setup(Node s, Node e) {
        running = true;
        startNode = s;
        startNode.setG(0);
        curNode = startNode;
        endNode = e;

        // Adding the starting node to the closed list
        addClosed(startNode);
    }

    public void setStartNode(Node s) {
        startNode = s;
        startNode.setG(0);
    }

    public void setCurNode(Node par) {
        this.curNode = par;
    }

    public void setEndNode(Node e) {
        endNode = e;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isComplete() {
        return complete;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public Node getCurNode() {
        return curNode;
    }

    public boolean isNoPath() {
        return noPath;
    }

    public boolean isDiagonal() {
        return diagonal;
    }

    public int getDiagonalMoveCost() {
        return diagonalMoveCost;
    }

    public void setDiagonal(boolean d) {
        diagonal = d;
    }

    public void setSize(int s) {
        size = s;
        diagonalMoveCost = (int) (Math.sqrt(2 * (Math.pow(size, 2))));
    }

    public abstract void findPath(Node parent);

    public abstract void connectPath();

    public void addBorder(Node node) {
        if (borders.size() == 0) {
            borders.add(node);
        } else if (!checkBorderDuplicate(node)) {
            borders.add(node);
        }
    }

    public void addOpen(Node node) {
        if (open.size() == 0) {
            open.add(node);
        } else if (!checkOpenDuplicate(node)) {
            open.add(node);
        }
    }

    public void addClosed(Node node) {
        if (closed.size() == 0) {
            closed.add(node);
        } else if (!checkClosedDuplicate(node)) {
            closed.add(node);
        }
    }

    public void addPath(Node node) {
        path.add(node);
    }

    public void removePath(int location) {
        path.remove(location);
    }

    public void removeBorder(int location) {
        borders.remove(location);
    }

    public void removeOpen(int location) {
        open.remove(location);
    }

    public void removeOpen(Node node) {
        for (int i = 0; i < open.size(); i++) {
            if (node.getX() == open.get(i).getX() && node.getY() == open.get(i).getY()) {
                open.remove(i);
            }
        }
    }

    public void removeClosed(int location) {
        closed.remove(location);
    }

    public boolean checkBorderDuplicate(Node node) {
        for (int i = 0; i < borders.size(); i++) {
            if (node.getX() == borders.get(i).getX() && node.getY() == borders.get(i).getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkOpenDuplicate(Node node) {
        for (int i = 0; i < open.size(); i++) {
            if (node.getX() == open.get(i).getX() && node.getY() == open.get(i).getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkClosedDuplicate(Node node) {
        for (int i = 0; i < closed.size(); i++) {
            if (node.getX() == closed.get(i).getX() && node.getY() == closed.get(i).getY()) {
                return true;
            }
        }
        return false;
    }

    public int searchBorder(int xSearch, int ySearch) {
        int Location = -1;

        for (int i = 0; i < borders.size(); i++) {
            if (borders.get(i).getX() == xSearch && borders.get(i).getY() == ySearch) {
                Location = i;
                break;
            }
        }
        return Location;
    }

    public int searchClosed(int xSearch, int ySearch) {
        int Location = -1;

        for (int i = 0; i < closed.size(); i++) {
            if (closed.get(i).getX() == xSearch && closed.get(i).getY() == ySearch) {
                Location = i;
                break;
            }
        }
        return Location;
    }

    public int searchOpen(int xSearch, int ySearch) {
        int Location = -1;

        for (int i = 0; i < open.size(); i++) {
            if (open.get(i).getX() == xSearch && open.get(i).getY() == ySearch) {
                Location = i;
                break;
            }
        }
        return Location;
    }

    public void reverse(ArrayList list) {
        int j = list.size() - 1;

        for (int i = 0; i < j; i++) {
            Object temp = list.get(i);
            list.remove(i);
            list.add(i, list.get(j - 1));
            list.remove(j);
            list.add(j, temp);
            j--;
        }
    }

    public ArrayList<Node> getBorderList() {
        return borders;
    }

    public ArrayList<Node> getOpenList() {
        return open;
    }

    public Node getOpen(int location) {
        return open.get(location);
    }

    public ArrayList<Node> getClosedList() {
        return closed;
    }

    public ArrayList<Node> getPathList() {
        return path;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    public boolean getRunning() {
        return running;
    }

    public boolean getDiagonal() {
        return diagonal;
    }

    public boolean getNoPath() {
        return noPath;
    }

    public boolean getComplete() {
        return complete;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setNoPath(boolean noPath) {
        this.noPath = noPath;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getSize() {
        return size;
    }

    public double getKValue() {
        return kValue;
    }

    public void setKValue(double kValue) {
        this.kValue = kValue;
    }

    public Frame getFrame() {
        return this.frame;
    }

    public void reset() {
        while (open.size() > 0) {
            open.remove(0);
        }

        while (closed.size() > 0) {
            closed.remove(0);
        }

        while (path.size() > 0) {
            path.remove(0);
        }
        noPath = false;
        running = false;
        complete = false;
    }

    public Node getOpenNode(int x, int y) {
        for (int i = 0; i < open.size(); i++) {
            if (open.get(i).getX() == x && open.get(i).getY() == y) {
                return open.get(i);
            }
        }
        return null;
    }

    public void printBorderList() {
        for (int i = 0; i < borders.size(); i++) {
            System.out.print(borders.get(i).getX() + ", " + borders.get(i).getY());
            System.out.println();
        }
        System.out.println("===============");
    }

    public void printOpenList() {
        for (int i = 0; i < open.size(); i++) {
            System.out.print(open.get(i).getX() + ", " + open.get(i).getY());
            System.out.println();
        }
        System.out.println("===============");
    }

    public void printPathList() {
        for (int i = 0; i < path.size(); i++) {
            System.out.print(i + ": " + path.get(i).getX() + ", " + path.get(i).getY() + ": " + path.get(i).getF());
            System.out.println();
        }
        System.out.println("===============");
    }

    public abstract Node getNextNode();

    public void copyNode(Strategy s) {
        s.borders = this.borders;
        s.closed = this.closed;
        s.complete = this.complete;
        s.curNode = this.curNode;
        s.diagonal = this.diagonal;
        s.diagonalMoveCost = this.diagonalMoveCost;
        s.endNode = this.endNode;
        s.frame = this.frame;
        s.kValue = this.kValue;
        s.noPath = this.noPath;
        s.open = this.open;
        s.path = this.path;
    }
}
