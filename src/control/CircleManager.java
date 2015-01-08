package control;

import gui.animations.AnimationHandler;
import gui.animations.StubAnimationHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import model.Circle;

public class CircleManager {

	private int rows, cols;
	private Circle circles[][];
	private Stack<Circle> selected = new Stack<Circle>();
	private boolean graph [][];
	private CircleFactory factory;
	private AnimationHandler animationHandler;
	private int circleDiameter;
	
	private boolean hasCycle = false;

	public CircleManager(int rows, int columns, int circleDiameter) {
		this.rows = rows;
		this.cols = columns;
		this.circleDiameter = circleDiameter;
		circles = new Circle[rows][columns];
		graph = new boolean [rows * columns][rows * columns];
		factory = CircleFactory.instance();
		animationHandler = new StubAnimationHandler();

		for (int row = 0; row < rows; row ++)
			for (int col = 0; col < cols; col++)
				circles[row][col] = factory.newCircle(row, col, circleDiameter);
		
		/*
		circles[0][0] = new Circle(0, 0, 0xff000000, circleDiameter);
		circles[1][0] = new Circle(1, 0, 0xff000000, circleDiameter);
		circles[0][1] = new Circle(0, 1, 0xff000000, circleDiameter);
		circles[1][1] = new Circle(1, 1, 0xff000000, circleDiameter);
		circles[2][1] = new Circle(2, 1, 0xff000000, circleDiameter);
		circles[1][2] = new Circle(1, 2, 0xff000000, circleDiameter);
		circles[2][2] = new Circle(2, 2, 0xff000000, circleDiameter);
		*/

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
			selected.push(c);
			return true;
		}
		
		Circle last = lastSelectedCircle();
		if (!areNeighbors(c, last) || !areSameColor(c, last))
			return false;
		
		if (!areConnected(c, last)) {
			selected.push(c);
			setConnected(c, last, true);
			checkCycleAndNotify();
			return true;
		} else if(selected.size() > 1) {
			/* Remove a circle */
			Circle lastMinusOne = selected.get(selected.size() - 2);
			if (lastMinusOne.equals(c)) {
				selected.pop();
				setConnected(last, lastMinusOne, false);
				hasCycle = hasCycle();
				return true;
			}
		}		
		return false;
	}
	
	private void checkCycleAndNotify() {
		boolean cycle = hasCycle();
		if (cycle && !hasCycle) {
			int color = selected.peek().getFill();
			for (int i = 0; i < rows * cols; i++) {
				Circle c = circles[i / rows][i % cols];
				if (c.getFill() == color)
					animationHandler.newFadeAnimation(c);
			}
		}
		hasCycle = cycle;
	}

	private boolean areConnected(Circle c, Circle d) {
		int cIndex = toGraphIndex(c);
		int dIndex = toGraphIndex(d);
		return graph[dIndex][cIndex];
	}
	
	private void setConnected(Circle c, Circle d, boolean value) {
		int cIndex = toGraphIndex(c);
		int dIndex = toGraphIndex(d);
		graph[dIndex][cIndex] = value;
		graph[cIndex][dIndex] = value;
	}

	private int toGraphIndex(Circle c) {
		return c.getRow() * cols + c.getColumn();
	}
	
	private boolean areSameColor(Circle c, Circle last) {
		return c.getFill() == last.getFill();
	}

	public int flush() {
		int flushedCircles = 0;
		
		/* Nothing to be flushed */
		if (selected.size() == 0)
			return flushedCircles;
		
		/* 
		 * Only one selected circle is not enough to be flushed, so unselect it
		 * and return.
		 */
		if (selected.size() == 1) {
			selected.pop();
			return flushedCircles;
		}
		
		int color = selected.peek().getFill();
			
		if (hasCycle) {
			/* removes all circles of the same color of the selected ones */
			for (int i = 0; i < rows * cols; i++) {
				int row = i / rows;
				int col = i % cols;
				if (circles[row][col].getFill() == color) {
					flushedCircles++;
					remove(row, col);
				}
			}
		} else {
			/* Clear those circles that have been selected */
			flushedCircles = selected.size();
			for (Circle b: selected) {
				int col = b.getColumn();
				int row = b.getRow();
				remove(row, col);
			}
		}

		/* Dropping circles */
		for (int col = 0; col < cols; col++) {
			/* Collect every non-null circle from the current column */
			List<Circle> column = new ArrayList<Circle>(rows);
			for (int row = rows - 1; row >= 0; row--) {
				if (circles[row][col] != null)
					column.add(circles[row][col]);
			}
			
			/* And stack them on the bottom of the column */
			int currentRow = rows - 1;
			for (Circle c: column) {
				setCircleNewRow(c, currentRow);
				currentRow--;
			}
			
			/* Create new circles to go above the ones that were stacked */
			fillVoidCells(col, currentRow, color);
		}
		
		/* Cleanup state */
		for (int row = 0; row < graph.length; row++)
			for (int col = 0; col < graph[0].length; col++)
				graph[row][col] = false;
		selected.clear();
		hasCycle = false;
		
		return flushedCircles;
	}
	
	private void remove(int row, int col) {
		animationHandler.newShrinkAnimation(circles[row][col]);
		circles[row][col] = null;
	}

	private void fillVoidCells(int col, int startingRow, int color) {
		
		int currentRow = startingRow;
		int fallFrom = -1;
		while (currentRow >= 0) {
			do {
				circles[currentRow][col] = factory.newCircle(currentRow, col, circleDiameter);
				/* If we just removed all circles of the same color from 
				 * the board, we will not create any new circle with that 
				 * color.
				 */
			} while (hasCycle && circles[currentRow][col].getFill() == color);
			Circle c = circles[currentRow][col];
			animationHandler.newFallingAnimation(c.getColumn(), fallFrom, currentRow, c);
			currentRow--;
			fallFrom--;
		}
	}

	private void setCircleNewRow(Circle c, int newRow) {
		animationHandler.newFallingAnimation(
				c.getColumn(), c.getRow(), newRow, c);
		
		circles[newRow][c.getColumn()] = c;
		c.setRow(newRow);
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
