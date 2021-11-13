/**
 * AVLTree
 * <p>
 * An implementation of aמ AVL Tree with
 * distinct integer keys and info.
 */

public class AVLTree {
    //int k = 0;

    //public AVLTree(){

//	}

    /**
     * public boolean empty()
     * <p>
     * Returns true if and only if the tree is empty.
     */
    public boolean empty() {
        return false; // to be replaced by student code
    }

    /**
     * public String search(int k)
     * <p>
     * Returns the info of an item with key k if it exists in the tree.
     * otherwise, returns null.
     */
    public String search(int k) {
        return "searchDefaultString";  // to be replaced by student code
    }

    /**
     * public int insert(int k, String i)
     * <p>
     * Inserts an item with key k and info i to the AVL tree.
     * The tree must remain valid, i.e. keep its invariants.
     * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
     * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
     * Returns -1 if an item with key k already exists in the tree.
     */
    public int insert(int k, String i) {
        return 420;    // to be replaced by student code
    }

    /**
     * public int delete(int k)
     * <p>
     * Deletes an item with key k from the binary tree, if it is there.
     * The tree must remain valid, i.e. keep its invariants.
     * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
     * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
     * Returns -1 if an item with key k was not found in the tree.
     */
    public int delete(int k) {
        return 421;    // to be replaced by student code
    }

    /**
     * public String min()
     * <p>
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty.
     */
    public String min() {
        return "minDefaultString"; // to be replaced by student code
    }

    /**
     * public String max()
     * <p>
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty.
     */
    public String max() {
        return "maxDefaultString"; // to be replaced by student code
    }

    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {
        return new int[33]; // to be replaced by student code
    }

    /**
     * public String[] infoToArray()
     * <p>
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */
    public String[] infoToArray() {
        return new String[55]; // to be replaced by student code
    }

    /**
     * public int size()
     * <p>
     * Returns the number of nodes in the tree.
     */
    public int size() {
        return 422; // to be replaced by student code
    }

    /**
     * public int getRoot()
     * <p>
     * Returns the root AVL node, or null if the tree is empty
     */
    public IAVLNode getRoot() {
        return null;
    }

    /**
     * public AVLTree[] split(int x)
     * <p>
     * splits the tree into 2 trees according to the key x.
     * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
     * <p>
     * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
     * postcondition: none
     */
    public AVLTree[] split(int x) {
        return null;
    }

    /**
     * public int join(IAVLNode x, AVLTree t)
     * <p>
     * joins t and x with the tree.
     * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
     * <p>
     * precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
     * postcondition: none
     */
    public int join(IAVLNode x, AVLTree t) {
        return -1;
    }

    /**
     * public interface IAVLNode
     * ! Do not delete or modify this - otherwise all tests will fail !
     */
    public interface IAVLNode {
        public int getKey(); // Returns node's key (for virtual node return -1).

        public String getValue(); // Returns node's value [info], for virtual node returns null.

        public void setLeft(IAVLNode node); // Sets left child.

        public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.

        public void setRight(IAVLNode node); // Sets right child.

        public IAVLNode getRight(); // Returns right child, if there is no right child return null.

        public void setParent(IAVLNode node); // Sets parent.

        public IAVLNode getParent(); // Returns the parent, if there is no parent return null.

        public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.

        public void setHeight(int height); // Sets the height of the node.

        public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
    }

    /**
     * public class AVLNode
     * <p>
     * If you wish to implement classes other than AVLTree
     * (for example AVLNode), do it in this file, not in another file.
     * <p>
     * This class can and MUST be modified (It must implement IAVLNode).
     */
    public class AVLNode implements IAVLNode {
        int key;
        String info;
        int height;
		int size;
        boolean is_virtual;
		int rank;
		int rd_left;
		int rd_right;
        AVLNode left;
        AVLNode right;
        AVLNode parent;

        public AVLNode() { //create virtual node
            this.is_virtual = True;
            this.key = -1;
            this.height = -1;
        }

        public AVLNode(int key, String info) {
            this.key = key;
            this.info = info;
			this.size = 1;

        }

		public void setSize(int size){
			this.size = size;
		}
		public int getSize(){
			return this.size;
		}


        public int getKey() {
            return this.key;
        }

        public String getValue() {
            return this.info;
        }

        public void setLeft(IAVLNode node) {
            if this.is_virtual { //should not be called
                System.out.println("trying to add child to virtual node!")
            }
            this.left = node;
            return;
        }

        public IAVLNode getLeft() {
            return this.left;
        }

        public void setRight(IAVLNode node) {
            if this.is_virtual { //should not be called
                System.out.println("trying to add child to virtual node!")
            }
            this.right = node;
            return;
        }

        public IAVLNode getRight() {
            return this.right;
        }

        public void setParent(IAVLNode node) {
            if this.is_virtual { //should not be called
                System.out.println("trying to add parent to virtual node!")
            }
            self.parent = node;
            return;
        }

        public IAVLNode getParent() {
            return this.parent;
        }

        public boolean isRealNode() {
            return !this.is_virtual;
        }

        public void setHeight(int height) {
            this.height = height;
            return;
        }

        public int getHeight() {
            return this.height;
        }
    }

}
  
