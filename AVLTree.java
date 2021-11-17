


/**
 * AVLTree
 * <p>
 * An implementation of aמ AVL Tree with
 * distinct integer keys and info.
 */

public class AVLTree {
    private int size = 0;
    private IAVLNode VIRTUAL_NODE = new AVLNode();
    private IAVLNode root = new AVLNode(); //virtual for empty tree
    private IAVLNode min = new AVLNode();
    private IAVLNode max = new AVLNode();


    /**
     * public boolean empty()
     * <p>
     * Returns true if and only if the tree is empty.
     */
    public boolean empty() {
        return !this.root.isRealNode();
    }

    /**
     * public String search(int k)
     * <p>
     * Returns the info of an item with key k if it exists in the tree.
     * otherwise, returns null.
     */
    public String search(int k) {
        IAVLNode result = search_node(k);
        if (result.getKey() == k) {
            return result.getValue();
        }
        return null;
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

        IAVLNode node = search_node(k);
        if (node.getKey() == k) {
            return -1;
        }
        IAVLNode to_add = new AVLNode(k, i);
        to_add.setLeft(VIRTUAL_NODE);
        to_add.setRight(VIRTUAL_NODE);

        if (!node.isRealNode()) { //no need to rebalance
            this.size++;
            this.root = to_add;
            this.min = to_add;
            this.max = to_add;
            return 0;
        } else {

            if (k > node.getKey()) {
                node.setRight(to_add);

            } else {
                node.setLeft(to_add);
            }
            to_add.setParent(node);
            this.size++; //update three's size
            this.bottom_up_resize(to_add, 1);


            if (this.min.getKey() > k) {
                this.min = to_add;
            }
            if (this.max.getKey() < k) {
                this.max = to_add;
            }

        }
        //fix tree and return # of rebalance ops

        return this.rebalance(node);
    }
    //gets node that may violate avl property with it's children

    private int rebalance(IAVLNode node) {
        String balance = get_balance(node);
        switch (balance) {
            case "01":
                // similar to 10

            case "10":
                //System.out.println("hungry rotem");
                node.incHeight();
                if (node.getParent() != null) {
                    return 1 + rebalance(node.getParent());
                }
                return 1;
            case "02":
                String left_balance = get_balance(node.getLeft());
                //System.out.println(left_balance.equals("12"));
                if (left_balance.equals("12")) {
                    rotate_right(node.getLeft());   //do promotions that rotate performs count???
                    node.decHeight();
                    return 2;
                } else if (left_balance.equals("21")) {
                    node.decHeight();
                    node.getLeft().decHeight();
                    node.getLeft().getRight().incHeight();
                    rotate_left(node.getLeft().getRight()); //double rotate
                    rotate_right(node.getLeft());
                    return 5;
                } else {
                    System.out.println("wtf! rebalance in case 02 -- 21 shouldn't get here!");
                    return -1000;
                }
            case "20":
                String right_balance = get_balance(node.getRight());
                if (right_balance.equals("21")) {
                    rotate_left(node.getRight());   //do promotions that rotate performs count???
                    node.decHeight();
                    return 2;
                } else if (right_balance.equals("12")) {
                    node.decHeight();
                    node.getRight().decHeight();
                    node.getRight().getLeft().incHeight();
                    rotate_right(node.getRight().getLeft()); //double rotate
                    rotate_left(node.getRight());
                    return 5;
                } else {
                    System.out.println("wtf! rebalance in case 20 -- 12 shouldn't get here!");
                    return -1000;
                }


            default:
                return 0;
        }
    }

    private void rotate_right(IAVLNode x) {
        IAVLNode z = x.getParent();
        IAVLNode b = x.getRight();
        if(z.getParent() == null){
            this.root = x;
            x.setParent(null);
        }
        else{
            if(z.getParent().getLeft().getKey() == z.getKey()){
                z.getParent().setLeft(x);
            }
            else{
                z.getParent().setRight(x);
            }
        }
        x.setRight(z);
        z.setLeft(b);
        z.setParent(x);
        if(b.isRealNode()){
            b.setParent(z);
        }
        z.setSize(b.getSize()+z.getRight().getSize() + 1);
        x.setSize(z.getSize()+x.getLeft().getSize() + 1);
    }

    private void rotate_left(IAVLNode x) {
        IAVLNode z = x.getParent();
        IAVLNode b = x.getLeft();
        if(z.getParent() == null){
            this.root = x;
        }
        else{
            if(z.getParent().getLeft().getKey() == z.getKey()){
                z.getParent().setLeft(x);
            }
            else{
                z.getParent().setRight(x);
            }
        }
        x.setLeft(z);
        z.setRight(b);
        z.setParent(x);
        if(b.isRealNode()){
            b.setParent(z);
        }
        z.setSize(b.getSize()+z.getLeft().getSize() + 1);
        x.setSize(z.getSize()+x.getRight().getSize() + 1);

    }

    //@pre: node.is_realnode()==true
    private String get_balance(IAVLNode node) { //returns string of differences of heights between itself and children
        int my_height = node.getHeight();
        int left_height = node.getLeft().getHeight();
        int right_height = node.getRight().getHeight();
        return Integer.toString(my_height - left_height) + Integer.toString(my_height - right_height);
    }

    private void bottom_up_resize(IAVLNode node, int to_add) { //goes up the tree and adds to_add to each node's size
        if (node.getParent() == null) { //base case at root
            return;
        }
        node.getParent().setSize(node.getParent().getSize() + to_add);
        bottom_up_resize(node.getParent(), to_add);
    }


    //returns node s.t node.key == k or last node in search otherwise
    public IAVLNode search_node(int k) {
        IAVLNode node = this.root;
        IAVLNode prev = this.root;
        while (node.isRealNode()) {
            prev = node;
            if (node.getKey() == k) {
                return node;
            } else if (node.getKey() > k) {
                node = node.getLeft();
            } else {
                node = node.getRight();
            }
        }
        return prev;
    }
/*
    private void rotate_right(AVLNode x){
        AVLNode y = x.parent;
        AVLNode z = x.getRight();
        z.setParent(y);

        AVLNode temp = x;
        y.setLeft(x.getRight());
        x.setRight(y);
    }
*/

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
        return this.min.getValue();
    }

    /**
     * public String max()
     * <p>
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty.
     */
    public String max() {
        return this.max.getValue();
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
        return this.size;
    }

    /**
     * public int getRoot()
     * <p>
     * Returns the root AVL node, or null if the tree is empty
     */
    public IAVLNode getRoot() {
        if (!this.root.isRealNode()) {
            return null;
        }
        return this.root;
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

        public void incHeight(); //add 1 to height

        public void decHeight(); //add 1 to height

        public int getHeight(); // Returns the height of the node (-1 for virtual nodes).

        public int getSize(); //returns size of subtree including given node, if virtual then 0.

        public void setSize(int size); //sets the size of subtree including given node, if virtual then does nothing.
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
        //int rank;
        //int rd_left;
        //int rd_right;
        AVLNode left;
        AVLNode right;
        AVLNode parent;

        public AVLNode() { //create virtual node
            this.is_virtual = true;
            this.key = -1;
            this.height = -1;
        }

        public AVLNode(int key, String info) {
            this.key = key;
            this.info = info;
            this.size = 1;

        }

        public int getKey() {
            return this.key;
        }

        public String getValue() {
            return this.info;
        }

        public void setLeft(IAVLNode node) {

            if (!this.isRealNode()) { //should not be called
                System.out.println("trying to add child to virtual node!");
            }
            this.left = (AVLNode) node;
            return;
        }

        public IAVLNode getLeft() {
            return this.left;
        }

        public void setRight(IAVLNode node) {
            if (!this.isRealNode()) { //should not be called
                System.out.println("trying to add child to virtual node!");
            }
            this.right = (AVLNode) node;
            return;
        }

        public IAVLNode getRight() {
            return this.right;
        }

        public void setParent(IAVLNode node) {
            if (!this.isRealNode()) { //should not be called
                System.out.println(node.getKey());
                System.out.println("trying to add parent to virtual node!");
            }
            this.parent = (AVLNode) node;
            return;
        }

        public IAVLNode getParent() {
            return this.parent;
        }

        public boolean isRealNode() {
            return !this.is_virtual;
        }

        public void setHeight(int height) {
            if (!this.isRealNode()) {
                System.out.println("trying to set height to virtual node!!!");
            }
            this.height = height;
            return;
        }

        public int getHeight() {
            return this.height;
        }

        public int getSize() {
            return this.size;
        }

        public void setSize(int size) {
            if (this.isRealNode()) {
                this.size = size;
            }
        }

        public void incHeight() {
            this.setHeight(this.getHeight() + 1);
        }

        public void decHeight() {
            this.setHeight(this.getHeight() - 1);
        }
    }


}

class testing {
    static void print_mat(String[][] mat) {
        int height = mat.length;
        int width = mat[0].length;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (mat[i][j] != null) {
                    System.out.print(mat[i][j] + " ");
                } else {
                    System.out.print(" ");
                }
                //System.out.print(mat[i][j] + " ");
            }
            System.out.println();

        }
    }

    static void fill_mat(AVLTree.IAVLNode root, String[][] mat, int i, int j) {
        if (!root.isRealNode()) {
            return;
        }
        int height = root.getHeight() + 1;
        int width = 2 * (int) Math.pow(2, height);
        mat[i][j + width / 2] = Integer.toString(root.getKey());
        fill_mat(root.getLeft(), mat, i + 1, j);
        fill_mat(root.getRight(), mat, i + 1, j  + width / 2);
    }

    static void print_tree(AVLTree tree) {
        AVLTree.IAVLNode root = tree.getRoot();
        int height = root.getHeight() + 1;
        int width = 2 * (int) Math.pow(2, height);
        String[][] mat = new String[height][width];
        fill_mat(root, mat, 0, 0);
        print_mat(mat);

    }

    public static void main(String[] args) {
        AVLTree t = new AVLTree();
        t.insert(50, "");
        //print_tree(t);
        //System.out.println();
        t.insert(30, "");
        //print_tree(t);
        //System.out.println();

       // t.insert(60, "");
        //print_tree(t);
        //System.out.println();
        t.insert(10, " ");
        //print_tree(t);
        //System.out.println();

        //System.out.println(t.getRoot().getLeft().getParent().getKey());
        //System.out.println(t.getRoot().getRight().getParent().getKey());
        /*
        System.out.println(t.getRoot().getHeight());
        System.out.println(t.getRoot().getLeft().getHeight());
        System.out.println(t.getRoot().getLeft().getLeft().getHeight());
*/
        //print_tree(t);

        System.out.println();
        t.insert(32, " ");
        print_tree(t);
        System.out.println();
        t.insert(70, " ");
        print_tree(t);
        System.out.println();
        t.insert(31, " ");
        System.out.println(t.search_node(31).getHeight());
        //print_tree(t);


    }


}