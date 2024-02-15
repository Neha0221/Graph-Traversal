public class APathfinding extends Strategy {

	private Sort sort = new Sort();

	public APathfinding(int size) {
		super(size);
	}

	public APathfinding(Frame frame, int size) {
		super(frame, size);
	}

	public APathfinding(Frame frame, int size, Node start, Node end) {
		super(frame, size, start, end);
	}

	public void findPath(Node curNode) {
		Node openNode = null;

		if (getDiagonal()) {
			// Detects and adds one step of nodes to open list
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (i == 1 && j == 1) {
						continue;
					}
					int possibleX = (curNode.getX() - getSize()) + (getSize() * i);
					int possibleY = (curNode.getY() - getSize()) + (getSize() * j);
					// System.out.println("i: " + i + " j: " + j);
					// System.out.println("possibleX: " + possibleX + " possibleY: " + possibleY);
					// Possible coordinates of borders
					// Using (crossBorderX, parent.getY())
					// and (parent.getX(), crossBorderY())
					// To see if there are borders in the way
					int crossBorderX = possibleX;
					int crossBorderY = possibleY;

					// Disables ability to cut corners around borders
					if (searchBorder(crossBorderX, curNode.getY()) != -1
							| searchBorder(curNode.getX(), crossBorderY) != -1 && ((j == 0 | j == 2) && i != 1)) {
						continue;
					}

					calculateNodeValues(possibleX, possibleY, openNode, curNode);
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
					int possibleX = (curNode.getX() - getSize()) + (getSize() * i);
					int possibleY = (curNode.getY() - getSize()) + (getSize() * j);

					calculateNodeValues(possibleX, possibleY, openNode, curNode);
				}
			}
		}

		// Set the new parent node
		curNode = getNextNode();

		if (curNode == null) {
			System.out.println("END> NO PATH");
			setNoPath(true);
			setRunning(false);
			getFrame().repaint();
			return;
		}

		if (Node.isEqual(curNode, getEndNode())) {
			getEndNode().setParent(curNode.getParent());

			connectPath();
			setRunning(false);
			setComplete(true);
			getFrame().repaint();
			return;
		}

		// Remove parent node from open list
		removeOpen(curNode);
		// Add parent node to closed list
		addClosed(curNode);

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
			findPath(curNode);
		} else {
			setCurNode(curNode);
		}
	}

	public void calculateNodeValues(int possibleX, int possibleY, Node nextNode, Node parent) {
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
		nextNode = new Node(possibleX, possibleY);

		// Set the parent of the open node
		nextNode.setParent(parent);

		// Calculating G cost
		// Cost to move from parent node to one open node (x
		// and
		// y
		// separately)
		int GxMoveCost = nextNode.getX() - parent.getX();
		int GyMoveCost = nextNode.getY() - parent.getY();
		int gCost = parent.getG();

		if (GxMoveCost != 0 && GyMoveCost != 0) {
			gCost += getDiagonalMoveCost();
		} else {
			gCost += getSize();
		}
		nextNode.setG(gCost);

		// Calculating H Cost
		int HxDiff = Math.abs(getEndNode().getX() - nextNode.getX());
		int HyDiff = Math.abs(getEndNode().getY() - nextNode.getY());
		int hCost = HxDiff + HyDiff;
		nextNode.setH(hCost);

		// Calculating F Cost
		int fCost = gCost + hCost;
		nextNode.setF(fCost);

		addOpen(nextNode);
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

	// We are calculating the Path Nodes from source to destination
	// Using the parent information of the node
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
