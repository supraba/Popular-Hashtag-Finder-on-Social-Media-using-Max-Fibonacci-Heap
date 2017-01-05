/**
 *
 * @author Supraba Muruganantham
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

class Node {
	int deg = 0;
	boolean childCutVal = false;
	Node next;
	Node prev;
	Node parent;
	Node child;
	int data;
	String key;
	/**
	 * Constructs a new Node with the data/value specified by the elem
	 *
	 * @param elem
	 *            The data/hash tag count stored in this node.
	 * @param keyVal
	 *            The keyVal is the value of the string associated with this node.
	 */
	Node(int elem, String keyVal) {
		next = prev = this;
		data = elem;
		key = keyVal;
	}
}

class FiboHeap {
	Queue queueA = new LinkedList();
	/* Pointer to the maximum element in the heap. */
	Node max = null;
	HashMap<String, Node> hashMap;
	/* Heap size is maintained explicitly to avoid recomputation.*/
	int size = 0;

	/**
	 *Inserts a node into the Fibonacci heap with the specified value and key
	 *
	 * @param value
	 *            The value to insert.
	 * @param key
	 *            The associated hashtag/key
	 * @return An Node representing that element in the heap.
	 */
	public Node insert(int value, String key) {
		Node result = new Node(value, key);

		/* Merge this singleton list with the tree list. */
		max = combineLists(max, result);

		/* Increase the heap size */
		++size;

		return result;
	}
	/**
	 * Returns whether the heap is empty.
	 *
	 * @return Whether the heap is empty.
	 */
	public boolean isEmpty() {
		return max == null;
	}
	
	/**
	 * Returns the max value of the heap
	 * @return The largest element of the heap.
	 * @throws NoSuchElementException
	 *             If the heap is empty.
	 */
	public Node max() {
		if (isEmpty())
			throw new NoSuchElementException("Heap is empty.");
		return max;
	}

	/**
	 * Dequeues and returns the maximum element of the Fibonacci heap. If the
	 * heap is empty, this throws a NoSuchElementException.
	 *
	 * @return The largest element of the Fibonacci heap.
	 * @throws NoSuchElementException
	 *             If the heap is empty.
	 */
	public Node removeMax() {
		if (isEmpty()){
			throw new NoSuchElementException("Heap is empty.");
		}
		--size;
		/*Maximum element that is to be removed*/
		Node maxElem = max;

		/*Remove the max elem from the root list
		 * if max elem is the only elem in the root list, make max null
		 * else, arbitrarily reassign max to the node next to max
		 * */
		if (max.next == max) { 
			max = null;
		} else { 
			max.prev.next = max.next;
			max.next.prev = max.prev;
			max = max.next;
		}

		/*Reassign the parent field of the children of the removed max to null*/
		if (maxElem.child != null) {
			Node curr = maxElem.child;
			do {
				curr.parent = null;
				curr = curr.next;
			} while (curr != maxElem.child);
		}

		/*move the children of the old max to the root list*/
		max = combineLists(max, maxElem.child);

		if (max == null)// If the list becomes empty, return the max and we are done
			return maxElem;

		/*Treetable that keeps track of  the dgree of the subtrees during pairwise combine*/
		List<Node> treeTab = new ArrayList<Node>();

		/*List of nodes to visit during the traversal*/
		List<Node> nodeToVisit = new ArrayList<Node>();

		for (Node curr = max; nodeToVisit.isEmpty() || nodeToVisit.get(0) != curr; curr = curr.next)
			nodeToVisit.add(curr);

		for (Node curr : nodeToVisit) {
			while (true) {
				while (curr.deg >= treeTab.size()){
					treeTab.add(null);
				}

				/*Keep traversing until two trees of the same degree are found*/
				if (treeTab.get(curr.deg) == null) {
					treeTab.set(curr.deg, curr);
					break;
				}

				Node other = treeTab.get(curr.deg);
				treeTab.set(curr.deg, null); // Clear the old slot

				Node min = (other.data < curr.data) ? other : curr;
				Node max = (other.data < curr.data) ? curr : other;

				/*Remove the minimum element from the list*/
				min.next.prev = min.prev;
				min.prev.next = min.next;

				/*Make the min child of max by pairwise combine*/
				min.next = min.prev = min;
				max.child = combineLists(max.child, min);
				min.parent = max;

				min.childCutVal = false;
				++max.deg;
				curr = max;
			}
			//Update the max element
			if (curr.data >= max.data){
				max = curr;
			}
		}
		return maxElem;
	}

	/**
	 * Increases the key of a node by the specified value
	 *
	 * @param heapNode
	 *            The element whose data should be increased.
	 * @param addVal
	 *            The value by which the Node's value needs to be increased
	 */
	public void increaseKey(Node heapNode, int addVal) {
		/* Increase the node's value by addVal*/
		heapNode.data += addVal;

		/*If the node's new value is greater than that of it's parent then cut the node and insert it into the root list*/
		if (heapNode.parent != null && heapNode.data >= heapNode.parent.data){
			childCut(heapNode);
		}

		/*If the new value of the node is greater than the max node's value then the max node is pointed to the current node with the increased value*/
		if (heapNode.data >= max.data){
			max = heapNode;
		}
	}
	
	/**
	 * Merges two Fibonacci heaps into one with the updated maximum and size
	 * @param p
	 *            The first Fibonacci heap to merge.
	 * @param q
	 *            The second Fibonacci heap to merge.
	 * @return A new merged Fibonacci Heap.
	 */
	public static FiboHeap merge(FiboHeap p, FiboHeap q) {
		/* Holds the resulting merged FiboHeap with the new max and new size */
		FiboHeap result = new FiboHeap();
		result.max = combineLists(p.max, q.max);
		result.size = p.size + q.size;

		/* Clean up of the old heaps. */
		p.size = q.size = 0;
		p.max = null;
		q.max = null; 
		
		return result;
	}
	
	/**
	 * Merges two doubly linked lists in O(1)time
	 * 
	 * @param p
	 *            A pointer into p of the q linked lists.
	 * @param q
	 *            A pointer into the other of the q linked lists.
	 * @return A pointer to the smallest element of the resulting list.
	 */
	private static Node combineLists(Node p, Node q) {
		if (p == null && q == null) { // Both null, resulting list is null.
			return null;
		} else if (p != null && q == null) { // q is null, result is p.
			return p;
		} else if (p == null && q != null) { // p is null, result is q.
			return q;
		} else { // combine the lists if both p and q is non null
			Node pNext = p.next; 
			p.next = q.next;
			p.next.prev = p;
			q.next = pNext;
			q.next.prev = q;

			/* A pointer to larger node is returned */
			return p.data > q.data ? p : q;
		}
	}
	
	/**
	 * Removes the first N max elements from the Fibonacci heap.
	 * @param n
	 *            The number of maxes to remove
	 */
	public void removeNMaxes(int n) throws Exception {
		/*The output is written to the output_file.txt*/
		FileWriter writer = new FileWriter(new File("output_file.txt"), true);
		PrintWriter pw = new PrintWriter(writer);
		for (int i = 0; i < n; i++) {
			/* Call removeMax n times to remove n maxes */
			Node currMax = removeMax();
			pw.write(currMax.key);
			if (i != n - 1) {
				pw.write(",");
			}
			// print(max,1);
			queueA.add(currMax);
		}
		pw.println();
		pw.flush();
		pw.close();
		while (!queueA.isEmpty()) {
			Node ins = (Node) queueA.remove();
			Node res = insert(ins.data, ins.key);
			hashMap.get(ins.key).next = res;
		}
	}
	
	/**
	 * Method to help debug by printing the FiboHeap
	 * @param elem
	 *            The elem to begin printing from
	 * @param newR
	 			If true, prints special characters. Set to false during recursive calls
	 */
	public void print(Node elem, int newR) throws Exception {
		if (newR == 1) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		Node start = elem;
		System.out.print(elem.key + " " + elem.data);
		if (elem.child != null) {
			System.out.print("<===");
			print(elem.child, 0);
			System.out.print("===//");
		}
		elem = elem.next;
		while (!elem.equals(start)) {
			System.out.print(",");
			System.out.print(elem.key + " " + elem.data);
			if (elem.child != null) {
				System.out.print("<===");
				print(elem.child, 0);
				System.out.print("===//");
			}
			elem = elem.next;
		}
	}

	/**
	 * Recursively cuts the marked parents of a node
	 *
	 * @param heapNode
	 *            The node to cut from its parent.
	 */
	private void childCut(Node heapNode) {
		heapNode.childCutVal = false;

		if (heapNode.parent == null)
			return;

		/*remove the node from it's siblings list*/
		if (heapNode.next != heapNode) { 
			heapNode.next.prev = heapNode.prev;
			heapNode.prev.next = heapNode.next;
		}

		/*Change  the child pointer of the parent of the cut node, if necessary*/
		if (heapNode.parent.child == heapNode) {
			if (heapNode.next != heapNode) {
				heapNode.parent.child = heapNode.next;
			}else {
				heapNode.parent.child = null;
			}
		}

		--heapNode.parent.deg;

		/*Add the cut node to the root list*/
		heapNode.prev = heapNode.next = heapNode;
		max = combineLists(max, heapNode);

		/*Recursively cut the parents if marked already else mark their childCut to true*/
		if (heapNode.parent.childCutVal)
			childCut(heapNode.parent);
		else
			heapNode.parent.childCutVal = true;

		heapNode.parent = null;
	}
}
/**
 * Main class containing the main method the reads the input from the file specified in the argument and writes the output in the desired format to the output_file.txt
 *
 */
public class hashtagcounter {
	public static void main(String[] args) throws Exception {

		/*Clear the output file getting the maxes*/
		FileWriter writer = new FileWriter(new File("output_file.txt"));
		writer.write("");
		writer.flush();
		writer.close();
		
		FiboHeap f = new FiboHeap();
		f.hashMap = new HashMap<String, Node>();
		String str;
		int i = 1;
		
		/* Read from the file got from the argument*/
		File filename = new File(args[0]);
		FileInputStream fstream = new FileInputStream(filename);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fstream));
		FileReader fileReader = new FileReader(filename);

		while ((str = bufferedReader.readLine()) != null && !str.equals("STOP")) {
			String[] strArr = str.split(" ");
			if (str.indexOf('#') != -1) {
				strArr[0] = strArr[0].substring(1);
				int val = Integer.parseInt(strArr[1]);
				if (f.hashMap.containsKey(strArr[0])) {
					/*increase the value of the key if the node is already present*/
					f.increaseKey(f.hashMap.get(strArr[0]).next, val);
				} else {
					/*insert the new node if the key is not present already*/
					Node ins = f.insert(val, strArr[0]);
					Node pointerNode = new Node(-1, null);
					pointerNode.next = ins;
					f.hashMap.put(strArr[0], pointerNode);
				}
			} else {
				// call remove max method with the remNum times
				Integer remNum = Integer.parseInt(strArr[0]);
				f.removeNMaxes(remNum);
			}
		}
		bufferedReader.close();
	}

}