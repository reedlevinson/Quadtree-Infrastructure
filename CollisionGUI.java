import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 *
 * @author Reed Levinson, Spring 2023
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		// Ask all the blobs to draw themselves.
		g.setColor(Color.BLACK);
		for (Blob b: blobs) {
			b.draw(g);
		}
		// Ask the colliders to draw themselves in red.
		g.setColor(Color.RED);
		if (colliders == null) return;
		for (Blob c: colliders) {
			c.draw(g);
		}
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// TODO: YOUR CODE HERE
		// Create the tree
		PointQuadtree<Blob> blobTree = null;
		for (Blob b: blobs) {
			if (blobTree == null) blobTree = new PointQuadtree<>(b, 0, 0, width, height);
			else blobTree.insert(b);
		}
		// For each blob, see if anybody else collided with it
		colliders = new ArrayList<>();
		List<Blob> blobList = blobTree.allPoints();
		for (Blob c: blobList) {
			List<Blob> temp = blobTree.findInCircle(c.getX(), c.getY(), 2 * c.getR());
			for (Blob d: temp) {
				if (c.equals(d)) continue;
				colliders.add(d);
			}
		}
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
