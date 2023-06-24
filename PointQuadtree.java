import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 *
 * @author Reed Levinson, Spring 2023
 * @partner Evan Lai
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 * @param p2 point to be inserted
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		// checks to see if p2 is above point (located in quadrant 1 or 2)
		if (p2.getY() < point.getY()) {
			// if p2 is right of point, quadrant 1
			if (p2.getX() > point.getX()) {
				// checks to see if already has point in quadrant, if yes, recurses on it if it does
				if (this.hasChild(1)) c1.insert(p2);
				// otherwise sets point in that quadrant to p2
				else this.c1 = new PointQuadtree<>(p2, pointX(), y1, x2, pointY());
			}
			// else, quadrant 2
			else {
				if (this.hasChild(2)) c2.insert(p2);
				else this.c2 = new PointQuadtree<>(p2, x1, y1, pointX(), pointY());
			}
		}
		// else, must be in quadrant 3 or 4
		else {
			// if p2 is left of point, quadrant 3
			if (p2.getX() < point.getX()) {
				if (this.hasChild(3)) c3.insert(p2);
				else this.c3 = new PointQuadtree<>(p2, x1, pointY(), pointX(), y2);
			}
			// else, quadrant 4
			else {
				if (this.hasChild(4)) c4.insert(p2);
				else this.c4 = new PointQuadtree<>(p2, pointX(), pointY(), x2, y2);
			}
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE
		int size = 1;
		// recurses size method for all children (same implementation as normal BTs)
		if (hasChild(1)) size += c1.size();
		if (hasChild(2)) size += c2.size();
		if (hasChild(3)) size += c3.size();
		if (hasChild(4)) size += c4.size();
		return size;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		ArrayList<E> result = new ArrayList<>();
		// uses allPointsHelper (see below)
		allPointsHelper(result);
		return result;
	}	

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		List<E> hits = new ArrayList<>();
		// checks to see if the search circle is contained within the bounds of the point's boundaries
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
			// adds point to hits if it's found within search circle
			if (Geometry.pointInCircle(pointX(), pointY(), cx, cy, cr)) hits.add(point);
			// recurses for all children
			for (int i = 1; i <= 4; i++) {
				if (this.hasChild(i)) {
					List<E> temp = getChild(i).findInCircle(cx, cy, cr);
					for (E e: temp) hits.add(e);
				}
			}
		}
		return hits;
	}

	// TODO: YOUR CODE HERE for any helper methods.

	/**
	 * Returns the x coordinate of the point at a particular node
	 * @return x coordinate
	 */
	public int pointX() {
		return (int) this.point.getX();
	}

	/**
	 * Returns the y coordinate of the point at a particular node
	 * @return y coordinate
	 */
	public int pointY() {
		return (int) this.point.getY();
	}

	/**
	 *
	 * @param points empty list of points to be filled
	 */
	public void allPointsHelper(ArrayList<E> points) {
		// adds point to list
		points.add(point);
		// recurses over all children and adds their points to list
		for (int i = 1; i <= 4; i++) {
			if (hasChild(i)) getChild(i).allPointsHelper(points);
		}
	}
}
