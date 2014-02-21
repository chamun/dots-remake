package control;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import model.AnimationHandler;
import model.Circle;

public class CircleManager {

	private int rows, cols;
	private Circle circles[][];
	private Stack<Circle> selected = new Stack<Circle>();
	private boolean graph [][];
	private CircleFactory factory;
	private AnimationHandler animationHandler;

	public CircleManager(int rows, int columns) {
		this.rows = rows;
		this.cols = columns;
		circles = new Circle[rows][columns];
		graph = new boolean [rows * columns][rows * columns];
		factory = CircleFactory.instance();

		for (int row = 0; row < rows; row ++)
			for (int col = 0; col < cols; col++)
				circles[row][col] = factory.newCircle(row, col);
		
		circles[0][0] = new Circle(0, 0, 0xff000000, 0xff222222, 40);
		circles[1][0] = new Circle(1, 0, 0xff000000, 0xff222222, 40);
		circles[0][1] = new Circle(0, 1, 0xff000000, 0xff222222, 40);
		circles[1][1] = new Circle(1, 1, 0xff000000, 0xff222222, 40);
		circles[2][1] = new Circle(2, 1, 0xff000000, 0xff222222, 40);
		circles[1][2] = new Circle(1, 2, 0xff000000, 0xff222222, 40);
		circles[2][2] = new Circle(2, 2, 0xff000000, 0xff222222, 40);

	}

	/** 
	 * Acts on (row, col) by either adding the target circle or removing it.
	 * Returns whether the action did something.
	 */
	public boolean actionAt(int row, int col) {
		if(inValidRowAndCol(row, col))
			return false;
		
		Circle c = circles[row][col];
		if(selected.isEmpty()) {
			c.select();
			selected.push(c);
			return true;
		}
		
		Circle last = lastSelectedCircle();
		if (!areNeighbors(c, last) || !areSameColor(c, last))
			return false;
		
		if (!areConnected(c, last)) {
			c.select();
			selected.push(c);
			turnOn(c, last);
			checkCycleAndNotify();
			return true;
		} else if(selected.size() > 1) {
			/* Remove a circle */
			Circle lastMinusOne = selected.get(selected.size() - 2);
			if (lastMinusOne.equals(c)) {
				selected.pop();
				/* last may still be in the stack */
				if(!selected.contains(last))
					last.unselect();
				turnOff(last, lastMinusOne);
				return true;
			}
		}		
		return false;
	}
	
	private void checkCycleAndNotify() {
		if (animationHandler == null)
			return;
		if(!hasCycle())
			return;
		int color = selected.peek().getFill();
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				Circle c = circles[row][col];
				if (c.getFill() == color) {
					animationHandler.newFadeAnimation(c);
				}
			}
	}

	private boolean areConnected(Circle c, Circle d) {
		int cIndex = toGraphIndex(c);
		int dIndex = toGraphIndex(d);
		return graph[dIndex][cIndex];
	}
	
	private void turnOn(Circle c, Circle d) {
		int dIndex = toGraphIndex(d);
		int cIndex = toGraphIndex(c);
		setGraph(cIndex, dIndex, true);
	}
	
	private void turnOff(Circle c, Circle d) {
		int dIndex = toGraphIndex(d);
		int cIndex = toGraphIndex(c);
		setGraph(cIndex, dIndex, false);
	}
	
	
	private void setGraph(int cIndex, int dIndex, boolean value) {
		graph[dIndex][cIndex] = value;
		graph[cIndex][dIndex] = value;
	}

	private int toGraphIndex(Circle c) {
		return c.getRow() * cols + c.getColumn();
	}
	
	private boolean areSameColor(Circle c, Circle last) {
		return c.getFill() == last.getFill();
	}

	public void flush() {
		
		/* Nothing to be flushed */
		if (selected.size() == 0)
			return;
		
		/* 
		 * Only one selected circle is not enough to be flushed, so unselect it
		 * and return.
		 */
		if (selected.size() == 1) {
			selected.pop().unselect();
			return;
		}
		
		int color = selected.peek().getFill();
		
		/* check the presence of a cycle */
		boolean cycle = hasCycle();
		
		if (cycle) {
			/* removes all circles of the same color of the selected ones */
			for (int i = 0; i < rows * cols; i++) {
				int row = i / rows;
				int col = i % cols;
				if (circles[row][col].getFill() == color)
					circles[row][col] = null;
			}
		} else {
			/* Clear those circles that have been selected */
			for (Circle b: selected) {
				int col = b.getColumn();
				int row = b.getRow();
				circles[row][col] = null;
			}
		}

		/* Dropping circles */
		for (int col = 0; col < cols; col++) {
			/* Collect every non-null circle from the current column */
			List<Circle> column = new ArrayList<Circle>(rows);
			for (int j = rows - 1; j >= 0; j--) {
				if (circles[j][col] != null)
					column.add(circles[j][col]);
			}
			
			/* And stack them on the bottom of the column */
			int currentRow = rows - 1;
			for (Circle c: column) {
				animationHandler.newFallingAnimation(c.getColumn(), 
						c.getRow(), currentRow, c);
				circles[currentRow][col] = c;
				circles[currentRow][col].setRow(currentRow);
				currentRow--;
			}
			
			/* Create new circles to go above the ones that were stacked */
			while (currentRow >= 0) {
				do {
					circles[currentRow][col] = factory.newCircle(currentRow, col);
					/* If we just removed all circles of the same color from 
					 * the board, we will not create any new circle with that 
					 * color.
					 */
				} while (cycle && circles[currentRow][col].getFill() == color);
				Circle c = circles[currentRow][col];
				animationHandler.newFallingAnimation(c.getColumn(), 
						currentRow - rows, currentRow, c);
				currentRow--;
			}
		}
		
		for (int row = 0; row < graph.length; row++)
			for (int col = 0; col < graph[0].length; col++)
				graph[row][col] = false;
		selected.clear();
	}
	
	private boolean hasCycle() {
		int count [] = new int[rows * cols];
		for (Circle c: selected) {
			int index = toGraphIndex(c);
			count[index]++;
			if(count[index] > 1)
				return true;
		}
		return false;
	}

	public void setAnimationHandler(AnimationHandler animationHandler) {
		this.animationHandler = animationHandler;
	}

	public boolean hasSelection() { return !selected.isEmpty(); }
	
	public Circle getCircle(int row, int col) {
		if (inValidRowAndCol(row, col))
			return null;
		return circles[row][col];
	}
	
	public List<Circle> getSelected() { return selected; }
	

	public Circle lastSelectedCircle() {
		if (selected.isEmpty())
			return null;
		return selected.peek();
	}
	
	private boolean inValidRowAndCol(int row, int col) {
		return (col >= cols || row >= rows || row < 0 || col < 0);
	}
	
	private boolean areNeighbors(Circle a, Circle b) {
		int myCol = a.getColumn();
		int myRow = a.getRow();
		int bCol = b.getColumn();
		int bRow = b.getRow();

		return xor(myCol == bCol, myRow == bRow) &&
				xor(Math.abs(myCol - bCol) == 1, Math.abs(myRow - bRow) == 1);
	}
	
	private boolean xor(boolean a, boolean b) {
		return (a && !b) || (!a && b);
	}

}
