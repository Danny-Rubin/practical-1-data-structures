
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
    public IAVLNode min = new AVLNode();
    public IAVLNode max = new AVLNode();

    public AVLTree() {

    }

    private AVLTree(IAVLNode node) {
        if (!node.isRealNode()) {
            return;
        }
        node.setParent(null);
        this.root = node;
        this.size = node.getSize();
        this.max = node.get_max();
        this.min = node.get_min();

    }
    public int getHeight() {
        if (this.empty()){
            return -1;
        }
        return this.root.getHeight();
    }

    /**
     * public boolean empty()
     * <p>
     * Returns true if and only if the tree is empty.
     */
    public boolean empty() {
        return !this.root.isRealNode();
    }

    public int search_cost(int key){
        if(empty()){
            return 1;
        }
        IAVLNode Max = this.max;
        IAVLNode node_father = search_node(key);
        //find common ancestor:
        IAVLNode current = Max;
        while(current.getParent() != null && current.getParent().getKey() > node_father.getKey()){
            current = current.getParent();
        }
         return depth(node_father) + depth(Max) - 2 * depth(current) + 2;


    }
    public int depth(IAVLNode node){
        int depth = 0;
        while(node.getParent() != null){
            node = node.getParent();
            depth ++;
        }
        return depth;
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


    private int rebalance(IAVLNode node) {
        if (node == null) {
            return 0;
        }
        String balance = get_balance(node);
        switch (balance) {
            case "01":
                // similar to 10

            case "10":
                //System.out.println("case 10, " + node.getKey());
                node.incHeight();
                return 1 + rebalance(node.getParent());
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
                } else if (left_balance.equals("11")) {
                    IAVLNode parent = node.getParent();
                    node.getLeft().incHeight();
                    rotate_right(node.getLeft());
                    return 2 + rebalance(parent);
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

                }else if (right_balance.equals("11")) {
                    IAVLNode parent = node.getParent();
                    node.getRight().incHeight();
                    rotate_left(node.getRight());
                    return 2 + rebalance(parent);}
                else {
                    System.out.println("wtf! rebalance in case 20 -- 12 shouldn't get here!");
                    return -1000;
                }

            case "22":
                node.decHeight();
                if (node.getParent() != null) {
                    return 1 + rebalance(node.getParent());

                }
                return 1;

            case "31":

                String right_balance2 = get_balance(node.getRight());
                if (right_balance2.equals("11")) {
                    node.decHeight();
                    node.getRight().incHeight();
                    rotate_left(node.getRight());
                    return 3;
                }
                if (right_balance2.equals("21")) {
                    IAVLNode parent = node.getParent();
                    node.decHeight();
                    node.decHeight();
                    rotate_left(node.getRight());
                    return 3 + rebalance(parent);
                }
                if (right_balance2.equals("12")) {
                    IAVLNode parent = node.getParent();
                    node.decHeight();
                    node.decHeight();
                    node.getRight().decHeight();
                    node.getRight().getLeft().incHeight();
                    rotate_right(node.getRight().getLeft());
                    rotate_left(node.getRight());
                    return 6 + rebalance(parent);
                }

            case "13":

                String left_balance2 = get_balance(node.getLeft());
                if (left_balance2.equals("11")) {
                    node.decHeight();
                    node.getLeft().incHeight();
                    rotate_right(node.getLeft());
                    return 3;
                }
                if (left_balance2.equals("12")) {
                    IAVLNode parent = node.getParent();
                    node.decHeight();
                    node.decHeight();
                    rotate_right(node.getLeft());
                    return 3 + rebalance(parent);
                }
                if (left_balance2.equals("21")) {
                    IAVLNode parent = node.getParent();
                    node.decHeight();
                    node.decHeight();
                    node.getLeft().decHeight();
                    node.getLeft().getRight().incHeight();
                    rotate_left(node.getLeft().getRight());
                    rotate_right(node.getLeft());
                    return 6 + rebalance(parent);
                }

            default:
                return 0;
        }
    }

    private void rotate_right(IAVLNode x) {

        IAVLNode z = x.getParent();
        IAVLNode b = x.getRight();
        if (z.getParent() == null) {
            this.root = x;
            x.setParent(null);
        } else {
            x.setParent(z.getParent());
            if (z.getParent().getLeft().getKey() == z.getKey()) {
                z.getParent().setLeft(x);
            } else {
                z.getParent().setRight(x);
            }
        }
        x.setRight(z);
        z.setLeft(b);
        z.setParent(x);

        if (b.isRealNode()) {
            b.setParent(z);
        }
        z.setSize(b.getSize() + z.getRight().getSize() + 1);
        x.setSize(z.getSize() + x.getLeft().getSize() + 1);
    }

    private void rotate_left(IAVLNode x) {

        IAVLNode z = x.getParent();
        IAVLNode b = x.getLeft();

        if (z.getParent() == null) {

            this.root = x;
            x.setParent(null);
        } else {
            x.setParent(z.getParent());
            if (z.getParent().getLeft().getKey() == z.getKey()) {
                z.getParent().setLeft(x);
            } else {
                z.getParent().setRight(x);
            }
        }
        x.setLeft(z);
        z.setRight(b);
        z.setParent(x);
        if (b.isRealNode()) {
            b.setParent(z);
        }
        z.setSize(b.getSize() + z.getLeft().getSize() + 1);
        x.setSize(z.getSize() + x.getRight().getSize() + 1);

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
        IAVLNode node = search_node(k);
        if (node.getKey() != k || k == -1) {
            return -1;
        }
        this.size -= 1;
        if (node.getHeight() == 0) { //if a leaf
            if (k == getRoot().getKey()) {//if also a root
                this.root = new AVLNode(); //tree is empty;
                this.max = new AVLNode();
                this.min = new AVLNode();
                return 0;
            }
            IAVLNode parent = node.getParent();
            if (k == min.getKey()) {
                min = parent;
            }
            if (k == max.getKey()) {
                max = parent;
            }

            this.bottom_up_resize(node, -1);
            if (node.is_left_child()) {
                parent.setLeft(VIRTUAL_NODE);
            } else {
                parent.setRight(VIRTUAL_NODE);
            }
            return this.rebalance(parent);


        }

        //not a leaf:
        if (node.getRight().isRealNode() && !node.getLeft().isRealNode()) {
            IAVLNode right = node.getRight();
            if (k == getRoot().getKey()) {

                this.root = right;
                right.setParent(null);
                this.min = right.get_min();
                return 0;
            }
            IAVLNode parent = node.getParent();

            if (node.is_left_child()) {
                parent.setLeft(right);
            } else {
                parent.setRight(right);
            }
            right.setParent(parent);
            if (this.min.getKey() == k) {
                this.min = getRoot().get_min();
            }
            this.bottom_up_resize(node, -1);
            return this.rebalance(parent);
        }

        if (node.getLeft().isRealNode() && !node.getRight().isRealNode()) {
            IAVLNode left = node.getLeft();
            if (k == getRoot().getKey()) {

                this.root = left;
                left.setParent(null);
                this.max = left.get_max();
                return 0;
            }
            IAVLNode parent = node.getParent();

            if (!node.is_left_child()) {
                parent.setRight(left);
            } else {
                parent.setLeft(left);
            }
            left.setParent(parent);
            if (this.max.getKey() == k) {
                this.max = getRoot().get_max();
            }
            this.bottom_up_resize(node, -1);
            return this.rebalance(parent);
        } else { // node has 2 real children
            IAVLNode succ = node.getRight().get_min();
            switch_nodes(node, succ);
            IAVLNode parent = node.getParent();
            if (node.is_left_child()) {
                parent.setLeft(node.getRight());
            } else {
                parent.setRight(node.getRight());
            }
            if (node.getRight().isRealNode()) {
                node.getRight().setParent(parent);
            }
            this.bottom_up_resize(node, -1);
            return this.rebalance(parent);
        }
    }

    private void switch_nodes(IAVLNode node1, IAVLNode node2) {
        if (!(node1.isRealNode() && node2.isRealNode())) {
            System.out.println("trying to switch virtuals! ");
            return;
        }
        if (node2.getHeight() > node1.getHeight()) { //make sure node1 is higher up the tree
            IAVLNode temp = node1;
            node1 = node2;
            node2 = temp;
        }

        IAVLNode parent1 = node1.getParent();
        IAVLNode parent2 = node2.getParent();
        IAVLNode left1 = node1.getLeft();
        IAVLNode right1 = node1.getRight();
        IAVLNode left2 = node2.getLeft();
        IAVLNode right2 = node2.getRight();

        if (!(node1.getKey() == this.getRoot().getKey())) { // if not root
            if (node1.is_left_child()) {
                parent1.setLeft(node2);
            } else {
                parent1.setRight(node2);
            }

        } else {
            this.root = node2;
        }
        if (node2.getParent().getKey() == node1.getKey()) {//assume node2 is right child
            node2.setParent(parent1);
            node2.setLeft(left1);
            if (left1.isRealNode()) {
                left1.setParent(node2);
            }
            node2.setRight(node1);
            node1.setParent(node2);
            node1.setLeft(left2);
            if (left2.isRealNode()) {
                left2.setParent(node1);
            }
            node1.setRight(right2);
            if (right2.isRealNode()) {
                right2.setParent(node1);
            }
        } else {
            if (node2.is_left_child()) {
                parent2.setLeft(node1);
            } else {
                parent2.setRight(node1);
            }
            node2.setParent(parent1);

            node1.setParent(parent2);

            node1.setLeft(left2);
            if (left2.isRealNode()) {
                left2.setParent(node1);
            }

            node1.setRight(right2);
            if (right2.isRealNode()) {
                right2.setParent(node1);
            }
            node2.setLeft(left1);
            if (left1.isRealNode()) {
                left1.setParent(node2);
            }
            node2.setRight(right1);
            if (right1.isRealNode()) {
                right1.setParent(node2);
            }
        }
        //switch sizes and heights:
        int temp = node1.getSize();
        node1.setSize(node2.getSize());
        node2.setSize(temp);
        temp = node1.getHeight();
        node1.setHeight(node2.getHeight());
        node2.setHeight(temp);

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

    public IAVLNode[] treeToArray() {
        IAVLNode[] arr = new AVLNode[this.size];
        toArrRec(arr, this.getRoot(), 0);
        return arr;
    }

    private static void toArrRec(IAVLNode[] arr, IAVLNode root, int i) {
        if (!root.isRealNode()) {
            return;
        }
        int size_left = root.getLeft().getSize();
        toArrRec(arr, root.getLeft(), i);
        arr[i + size_left] = root;
        toArrRec(arr, root.getRight(), i + size_left + 1);
        return;
    }


    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {
        if (this.empty()) {
            int[] array = {};
            return array;
        }
        AVLTree.IAVLNode[] arr = this.treeToArray();
        int[] res = new int[this.size];
        for (int i = 0; i < this.size(); i++) {
            res[i] = arr[i].getKey();
        }
        return res;
    }

    /**
     * public String[] infoToArray()
     * <p>
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */
    public String[] infoToArray() {
        if (this.empty()) {
            String[] array = {};
            return array;
        }
        AVLTree.IAVLNode[] arr = this.treeToArray();
        String[] res = new String[this.size];
        for (int i = 0; i < this.size(); i++) {
            res[i] = arr[i].getValue();
        }
        return res;

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
        AVLTree small = new AVLTree();
        AVLTree big = new AVLTree();
        IAVLNode splitter = search_node(x);
        if (splitter.getLeft().isRealNode()) {
            small = new AVLTree(splitter.getLeft());
        }
        if (splitter.getRight().isRealNode()) {
            big = new AVLTree(splitter.getRight());
        }
        splitter = splitter.getParent();
        while (splitter != null) {
            IAVLNode parent = splitter.getParent();
            if (splitter.getKey() < x) {
                small.join(splitter, new AVLTree(splitter.getLeft()));
            } else {
                big.join(splitter, new AVLTree(splitter.getRight()));
            }
            splitter = parent;
        }

        return new AVLTree[]{small, big};
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
        if (t.empty()) {
            boolean this_empty = this.empty();
            int h = 0;
            if(!this_empty){
                h = this.getRoot().getHeight();
            }
            this.insert(x.getKey(), x.getValue());
            if (this_empty) {
                return 1;
            }
            return 2 + h;
        }
        if (this.empty()) {
            int ret = t.join(x, this);
            this.root = t.getRoot();
            this.min = t.min;
            this.max = t.max;
            this.size = t.size;
            return ret;
        }
        int res = absolute(this.getRoot().getHeight() - t.getRoot().getHeight()) + 1;
        if (x.getKey() > this.max.getKey()) {
            int rank_this = this.getRoot().getHeight();
            int rank_other = t.getRoot().getHeight();
            if (rank_other > rank_this) {
                IAVLNode current_other = t.getRoot();
                while (current_other.getHeight() > rank_this) {
                    current_other = current_other.getLeft();
                }
                x.setLeft(this.getRoot());
                this.getRoot().setParent(x);
                x.setRight(current_other);
                IAVLNode other_parent = current_other.getParent();
                current_other.setParent(x);
                other_parent.setLeft(x);
                x.setParent(other_parent);
                x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
                int num_growth = x.getSize() - current_other.getSize();
                bottom_up_resize(x, num_growth);
                this.root = t.getRoot();
                this.max = t.max;
                this.size += t.size() + 1;
                x.setHeight(maximum(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
                rebalance(x.getParent());
            } else if (rank_other == rank_this) {
                x.setLeft(this.getRoot());
                this.getRoot().setParent(x);
                x.setRight(t.getRoot());
                t.getRoot().setParent(x);
                x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
                x.setHeight(maximum(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
                this.size += t.size() + 1;
                this.max = t.max;
                this.root = x;
            } else {
                IAVLNode current_this = this.getRoot();
                while (current_this.getHeight() > rank_other) {
                    current_this = current_this.getRight();
                }
                x.setRight(t.getRoot());
                t.getRoot().setParent(x);
                x.setLeft(current_this);
                IAVLNode this_parent = current_this.getParent();
                current_this.setParent(x);
                this_parent.setRight(x);
                x.setParent(this_parent);
                x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
                int num_growth = x.getSize() - current_this.getSize();
                bottom_up_resize(x, num_growth);
                this.max = t.max;
                this.size += t.size() + 1;
                x.setHeight(maximum(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
                rebalance(x.getParent());
            }
        } else {
            t.join(x, this);
            this.root = t.getRoot();
            this.min = t.min;
            this.max = t.max;
            this.size = t.size;
        }
        return res;
    }

    public static int maximum(int a, int b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    public static int absolute(int a) {
        if (a > 0) {
            return a;
        }
        return (-1) * a;
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

        public boolean is_left_child(); //is left child, only for non root and real nodes

        public IAVLNode get_min(); //returns the deepest descendant on a path always going left that isn't virtual

        public IAVLNode get_max(); //returns the deepest descendant on a path always going right that isn't virtual

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

        public boolean is_left_child() {
            if (this.parent.getLeft().getKey() == this.getKey()) {
                return true;
            }
            return false;
        }

        public IAVLNode get_min() {
            if (!this.isRealNode()) {
                System.out.println("wtf getting min in virtual node!");
                return null;
            }
            if (this.getLeft().isRealNode()) {
                return this.getLeft().get_min();
            }
            return this;
        }

        public IAVLNode get_max() {
            if (!this.isRealNode()) {
                System.out.println("wtf getting max in virtual node!");
                return null;
            }
            if (this.getRight().isRealNode()) {
                return this.getRight().get_max();
            }
            return this;
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
                    System.out.print(mat[i][j]);
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
        int width = 6 * (int) Math.pow(2, height);
        mat[i][j + width / 2] = Integer.toString(root.getKey());
        fill_mat(root.getLeft(), mat, i + 2, j);
        fill_mat(root.getRight(), mat, i + 2, j - 1 + width / 2);
    }

    static void print_tree(AVLTree tree) {
        if (tree.empty()) {
            System.out.println("empty tree lol");
            return;
        }
        AVLTree.IAVLNode root = tree.getRoot();
        int height = root.getHeight() + 1;
        int width = 6 * (int) Math.pow(2, height);
        String[][] mat = new String[2 * height][width];
        fill_mat(root, mat, 0, 0);
        print_mat(mat);

    }

    static boolean good_heights(AVLTree t){
        if(t.empty()){
            return true;
        }
        return good_heights_rec(t.getRoot());
    }
    static boolean good_heights_rec(AVLTree.IAVLNode root){
        if(!root.isRealNode()){
            return true;
        }
        AVLTree.IAVLNode left = root.getLeft();
        AVLTree.IAVLNode right = root.getRight();
        return (root.getHeight()== 1+ AVLTree.maximum(left.getHeight(), right.getHeight())) && good_heights_rec(left) && good_heights_rec(right);
    }

    static AVLTree rand_ops(int n){
        AVLTree t = new AVLTree();
        int minimum = 0;
        int maximum = 7;
        for(int i = 0; i < n; i++){
            //print_tree(t);
            int randomOp = minimum + (int)(Math.random() * maximum);
            System.out.println("performing op " + randomOp );
            switch (randomOp){
                case 0: // insert
                    int key_to_insert = 1 + (int)(Math.random() * 30);
                    System.out.println("inserting " + key_to_insert);
                    t.insert(key_to_insert, Integer.toString(key_to_insert));
                    break;
                case 1: // delete
                    int key_to_delete = 1 + (int)(Math.random() * 30);
                    System.out.println("deleting " + key_to_delete);
                    t.delete(key_to_delete);
                    break;
                case 2: // print key array
                    System.out.println("printing all keys :");
                    for(int key :t.keysToArray()){
                        System.out.print(key + " ");
                    }
                    System.out.println();
                    break;
                case 3: // print str array
                    System.out.println("printing all info :");
                    for(String key :t.infoToArray()){
                        System.out.print(key + " ");
                    }
                    System.out.println();
                    break;
                case 4: // searching
                    int key_to_search = 1 + (int)(Math.random() * 30);
                    System.out.println("searching " + key_to_search);
                    t.search(key_to_search);
                    break;
                case 5: //empty
                    System.out.println("calling empty()");
                    System.out.println(t.empty());
                    break;
                case 6: //min, max
                    System.out.println("calling min, max");
                    System.out.println(t.min() +" " + t.max());
                    break;
                case 7: //get root
                    System.out.println("getting root");
                    System.out.println(t.getRoot().getKey());
                    break;
                default:
                    System.out.println("wtf no case like this");
            }
            if(!good_heights(t)){
                System.out.println("bad heights!!!");
                AVLTree.IAVLNode [] nodes = t.treeToArray();
                for(AVLTree.IAVLNode node : nodes){
                    System.out.println("key:  " + node.getKey() + " height: " + node.getHeight());
                }
                System.exit(0);
                break;
            }
        }
        //System.out.println("final tree: ");
        return t;
    }

    static AVLTree rand_tree(int n){
        AVLTree t = new AVLTree();
        int minimum = 0;
        int maximum = 2;
        int num_iterations = 1 + (int)(Math.random() * n);
        for(int i = 0; i < num_iterations; i++){
            int key_to_insert = 1 + (int)(Math.random() * 30);
            t.insert(key_to_insert, Integer.toString(key_to_insert));
        }
        return t;
    }

    static int inversions(int [] array){
        int n = array.length;
        if(n == 0){
            return 0;
        }
        int sum = 0;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < i; j++){
                sum += (array[j] > array[i] ?  1 : 0);
            }
        }
        return sum;
    }

    static int insert_count(int [] array){
        AVLTree tree = new AVLTree();
        int total_cost = 0;
        for(int key : array){
            total_cost += tree.search_cost(key);
            tree.insert(key, "");
        }
        return total_cost;

    }

    static int [] reverse_array(int i){
        int n = 1000*(int) Math.pow(2, i);
        int[] array = new int[n];
        int k = 1;
        for(int j = n-1; j > -1; j--){
            array[j] = k;
            k++;
        }
        return array;
    }
    static boolean in_array(int [] array, int num){
        for(int i = 0; i < array.length; i++){
            if(array[i] == num){
                return true;
            }
        }
        return false;
    }
    static int choose(int [] array){
        if(array.length == 0){
            System.out.println("empty array cant choose");
            return -1;
        }
        int choice_index = 0 + (int)(Math.random() * array.length);
        return array[choice_index];
    }

    static int [] rand_permute(int n){
        int [] array = new int[n];
        int minimum = 0; int maximum = 20*(n-1);
        int randomNum = minimum + (int)(Math.random() * maximum);
        for(int i = 0; i < n; i ++){
            randomNum = minimum + (int)(Math.random() * maximum);
            while(in_array(array, randomNum)){
                randomNum = minimum + (int)(Math.random() * maximum);
            }
            array[i] = randomNum;
        }
        return array;

    }
    static void split_test(int num_tests){
        for(int i = 0; i < num_tests; i++){
            AVLTree tree = rand_tree(10);
            System.out.println("random tree: ");
            print_tree(tree);
            int key = choose(tree.keysToArray());
            System.out.println("spliting by node with key = " + key );
            AVLTree[] result = tree.split(key);
            System.out.println("smol result:");
            print_tree(result[0]);
            System.out.println("big result:");
            print_tree(result[1]);
        }
    }





    public static void main(String[] args) {

        split_test(100);

//        for(int i : rand_permute(1000)){
//            System.out.print(i + " ");
//        }

//        for(int i = 1; i <= 5 ; i++){
//            System.out.println("for i = " + i );
//            System.out.println("num inversions: " + inversions(reverse_array(i)));
//            System.out.println(insert_count(reverse_array(i)));
//        }

//        for(int i = 1; i <= 5 ; i++){
//            int [] array = rand_permute(1000*(int)Math.pow(2,i));
//            System.out.println("for i = " + i );
//            System.out.println("num inversions: " + inversions(array));
//            System.out.println("search cost: " + insert_count(array));
//        }




//        for(int i = 0; i < 100; i++){
//            AVLTree t = testing.rand_ops(500);
//            print_tree(t);
//        }
//        System.out.println("finished testing");
//        int [] arr = {50, 30, 70, 20, 35, 60, 80, 15, 25, 40, 55, 10};
//        AVLTree tree = new AVLTree();
//        for(int i : arr){
//            tree.insert(i, Integer.toString(i));
//        }
//        tree.delete(80);
//        int [] arr2 = {15, 12, 54, 8, 13, 18, 60, 5, 9, 14, 16, 56, 70};
//        for(int i : arr2){
//            tree.insert(i, Integer.toString(i));
//        }
//
//
//        tree.delete(8);
//
//        tree.delete(12);
//        print_tree(tree);
//        System.out.println("############");
//        System.out.println("############");
//        tree.delete(14);
//
//        print_tree(tree);
        /*
        AVLTree huge = new AVLTree();
        int[] arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        for (int i : arr) {
            huge.insert(i, "");
        }
            AVLTree[] splat = huge.split(0);
            print_tree(splat[0]);
            System.out.println("########################################################################");
            System.out.println("########################################################################");
            print_tree(splat[1]);  */



    }
}
