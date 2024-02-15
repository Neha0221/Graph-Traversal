public class PQPathfinding extends Strategy {

    private Sort sort = new Sort();

    public PQPathfinding(int size) {
        super(size);
    }

    public PQPathfinding(Frame frame, int size) {
        super(frame, size);
    }

    public PQPathfinding(Frame frame, int size, Node start, Node end) {
        super(frame, size, start, end);
    }

    public void findPath(Node parent) {
        Node openNode = null;

        if (getDiagonal()) {
            // Detects and adds one step of nodes to open list
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i == 1 && j == 1) {
                        continue;
                    }
                    int possibleX = (parent.getX() - getSize()) + (getSize() * i);
                    int possibleY = (parent.getY() - getSize()) + (getSize() * j);
                    System.out.println("i: " + i + " j: " + j);
                    System.out.println("possibleX: " + possibleX + " possibleY: " + possibleY);
                    // Possible coordinates of borders
                    // Using (crossBorderX, parent.getY())
                    // and (parent.getX(), crossBorderY())
                    // To see if there are borders in the way
                    int crossBorderX = parent.getX() + (possibleX - parent.getX());
                    int crossBorderY = parent.getY() + (possibleY - parent.getY());

                    // Disables ability to cut corners around borders
                    if (searchBorder(crossBorderX, parent.getY()) != -1
                            | searchBorder(parent.getX(), crossBorderY) != -1 && ((j == 0 | j == 2) && i != 1)) {
                        continue;
                    }

                    calculateNodeValues(possibleX, possibleY, openNode, parent);
                }
            }
        } else {
            // Detects and adds one step of nodes to open list
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if ((i == 0 && j == 0) || (i == 0 && j == 2) ||
                            (i == 1 && j == 1) || (i == 2 && j == 0) ||
                            (i == 2 && j == 2)) {
                        continue;
                    }
                    int possibleX = (parent.getX() - getSize()) + (getSize() * i);
                    int possibleY = (parent.getY() - getSize()) + (getSize() * j);

                    calculateNodeValues(possibleX, possibleY, openNode, parent);
                }
            }
        }

        // Set the new parent node
        parent = getNextNode();

        if (parent == null) {
            System.out.println("END> NO PATH");
            setNoPath(true);
            setRunning(false);
            getFrame().repaint();
            return;
        }

        if (Node.isEqual(parent, getEndNode())) {
            getEndNode().setParent(parent.getParent());

            connectPath();
            setRunning(false);
            setComplete(true);
            getFrame().repaint();
            return;
        }

        // Remove parent node from open list
        removeOpen(parent);
        // Add parent node to closed list
        addClosed(parent);

        // Allows correction for shortest path during runtime
        // When new parent Node is selected.. Checks all adjacent open
        // Nodes.. Then checks if the (G Score of parent + open Node
        // distance from parent) is less than the current G score
        // of the open node.. If true.. Sets parent of open Node
        // as new parent.. and re-calculates G, and F values
        // if (diagonal) {
        // for (int i = 0; i < 3; i++) {
        // for (int j = 0; j < 3; j++) {
        // if (i == 1 && j == 1) {
        // continue;
        // }
        // int possibleX = (parent.getX() - size) + (size * i);
        // int possibleY = (parent.getY() - size) + (size * j);
        // Node openCheck = getOpenNode(possibleX, possibleY);

        // // If spot being looked at, is an open node
        // if (openCheck != null) {
        // int distanceX = parent.getX() - openCheck.getX();
        // int distanceY = parent.getY() - openCheck.getY();
        // int newG = parent.getG();

        // if (distanceX != 0 && distanceY != 0) {
        // newG += diagonalMoveCost;
        // } else {
        // newG += size;
        // }

        // if (newG < openCheck.getG()) {
        // int s = searchOpen(possibleX, possibleY);
        // open.get(s).setParent(parent);
        // open.get(s).setG(newG);
        // open.get(s).setF(open.get(s).getG() + open.get(s).getH());
        // }
        // }
        // }
        // }
        // }
        if (!getFrame().showSteps()) {
            findPath(parent);
        } else {
            setCurNode(parent);
        }
    }

    public void calculateNodeValues(int possibleX, int possibleY, Node openNode, Node parent) {
        // If the coordinates are outside of the borders
        if (possibleX < 0 | possibleY < 0 | possibleX >= getFrame().getWidth() | possibleY >= getFrame().getHeight()) {
            return;
        }

        // If the node is already a border node or a closed node or an
        // already open node, then don't make open node
        if (searchBorder(possibleX, possibleY) != -1 | searchClosed(possibleX, possibleY) != -1
                | searchOpen(possibleX, possibleY) != -1) {
            return;
        }
        // Create an open node with the available x and y
        // coordinates
        openNode = new Node(possibleX, possibleY);

        // Set the parent of the open node
        openNode.setParent(parent);

        // Calculating G cost
        // Cost to move from parent node to one open node (x
        // and
        // y
        // separately)
        // int GxMoveCost = openNode.getX() - parent.getX();
        // int GyMoveCost = openNode.getY() - parent.getY();
        // int gCost = parent.getG();

        // if (GxMoveCost != 0 && GyMoveCost != 0) {
        // gCost += getDiagonalMoveCost();
        // } else {
        // gCost += getSize();
        // }
        // openNode.setG(gCost);

        // // Calculating H Cost
        int HxDiff = Math.abs(getEndNode().getX() - openNode.getX());
        int HyDiff = Math.abs(getEndNode().getY() - openNode.getY());
        int hCost = HxDiff + HyDiff;
        openNode.setH(hCost);

        // Calculating F Cost
        int fCost = hCost;
        openNode.setF(fCost);

        addOpen(openNode);
    }

    public Node getNextNode() {
        return lowestFCost();
    }

    public Node lowestFCost() {
        if (getOpenList().size() > 0) {
            sort.bubbleSort(getOpenList());
            return getOpenList().get(0);
        }
        return null;
    }

    public void connectPath() {
        if (getPathList().size() == 0) {
            Node parentNode = getEndNode().getParent();

            while (!Node.isEqual(parentNode, getStartNode())) {
                addPath(parentNode);

                for (int i = 0; i < getClosedList().size(); i++) {
                    Node current = getClosedList().get(i);

                    if (Node.isEqual(current, parentNode)) {
                        parentNode = current.getParent();
                        break;
                    }
                }
            }
            reverse(getPathList());
        }

    }
}
