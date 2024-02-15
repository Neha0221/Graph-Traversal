public class DFSPathfinding extends Strategy {

    public DFSPathfinding(int size) {
        super(size);
    }

    public DFSPathfinding(Frame frame, int size) {
        super(frame, size);
    }

    public DFSPathfinding(Frame frame, int size, Node start, Node end) {
        super(frame, size, start, end);
    }

    public Node getNextNode() {
        if (getOpenList().size() > 0) {
            return getOpenList().get(getOpenList().size() - 1);
        }

        return null;
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

                    // Disables ability to cut corners around borders
                    if (searchBorder(possibleX, parent.getY()) != -1 && searchBorder(parent.getX(), possibleY) != -1
                            && ((j == 0 | j == 2) && (i == 0 | i == 2))) {
                        System.out.println("i: " + i + " j: " + j);
                        System.out.println("possibleX: " + possibleX + " possibleY: " + possibleY);
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

        addOpen(openNode);
    }

    public void connectPath() {
        if (getPathList().size() == 0) {
            Node parentNode = getEndNode().getParent();

            while (!Node.isEqual(parentNode, getStartNode())) {
                addPath(parentNode);
                parentNode = parentNode.getParent();
            }
            reverse(getPathList());
        }

    }
}
