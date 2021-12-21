/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FibonacciHeap
{
    public int size;
    public int num_trees;
    public int num_marked;
    public static int TOTAL_LINKS;
    public static int TOTAL_CUTS;
    public HeapNode sentinel;
    public HeapNode min;

    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *
     */
    public FibonacciHeap(){
        HeapNode sentinel = new HeapNode(Integer.MAX_VALUE);
        sentinel.setPrev(sentinel);
        sentinel.setNext(sentinel);
        this.min = sentinel;
        this.sentinel = sentinel;
    }
    private int get_num_trees() {
        return num_trees;
    }

    private int get_num_marked() {
        return num_marked;
    }

    public void fill_width_heap(){
        HeapNode current = sentinel;
        while(current.getNext().getKey()!= sentinel.getKey()){
            current.fill_width();
        }
    }

    public HeapNode[] rootArr(){
        if(isEmpty()){
            return new HeapNode[]{};
        }
        HeapNode[] res = new HeapNode[count_roots()];
        HeapNode current = first_root();
        int i = 0;
        while(current.getKey() != sentinel.getKey()){
            res[i] = current;
            i++;
            current = current.getNext();
        }
        return res;
    }
    private int count_roots(){
        int res = 0;
        HeapNode current = first_root();
        while(current.getKey() != sentinel.getKey()){
            res++;
            current = current.getNext();
        }
        return res;
    }
    private void update_min_and_num_trees(){
        this.num_trees = count_roots();
        if(num_trees == 0){
            min = null;
            return;
        }
        int min_key = Integer.MAX_VALUE;
        for(HeapNode root : rootArr()){
            if(root.getKey() < min_key){
                min_key = root.getKey();
                min = root;
            }
        }
    }
    private int max_rank(){
        int max = -1;
        for(HeapNode root : rootArr()){
            if(root.getRank() > max){
                max = root.getRank();
            }
        }
        return max;
    }


    private HeapNode first_root(){
        if(isEmpty()){
            return null;
        }
        return sentinel.getNext();
    }
    public HeapNode getFirst(){
        return this.first_root();
    }
    private HeapNode last_root(){
        if(isEmpty()){
            return null;
        }
        return sentinel.getPrev();
    }


    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     */
    public HeapNode insert(int key) {
        size++;
        num_trees++;
        HeapNode new_node = new HeapNode(key);
        new_node.setNext(sentinel.getNext());
        new_node.setPrev(sentinel);
        sentinel.setNext(new_node);
        new_node.getNext().setPrev(new_node);
        if(key < this.findMin().getKey()){
            this.min = new_node;
        }
        return new_node;
    }

    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     *
     */
    public void deleteMin() {
        if(isEmpty()){
            return;
        }
        size--;
        HeapNode min = findMin();
        HeapNode [] children = min.getChildren();
        for(HeapNode child : children){
            child.setParent(null);
            if(child.isMark()){
                child.unMark();
                num_marked--;
            }
        }

        if(min.getRank()>0){
            min.getPrev().setNext(children[0]);
            children[0].setPrev(min.getPrev());
            min.getNext().setPrev(children[children.length-1]);
            children[children.length-1].setNext(min.getNext());
            successive_linking();
            update_min_and_num_trees();
            return;
        }
        min.getPrev().setNext(min.getNext());
        min.getNext().setPrev(min.getPrev());

        successive_linking();
        update_min_and_num_trees();
        return;
    }

    private void successive_linking(){ //updates total links and consolidates
        int num_buckets = 5 * (int) (Math.log(size)+1);
        HeapNode [] buckets = new HeapNode[num_buckets];
        HeapNode[] roots = rootArr();
        for(HeapNode root : roots){
            root.setPrev(null); root.setNext(null);
            throw_to_bucket(buckets, root);
        }
        HeapNode current = sentinel; //sentinel.setNext(sentinel); sentinel.setPrev(sentinel);

        for(HeapNode root : buckets){
            if(root == null){
                continue;
            }
            current.setNext(root); root.setPrev(current);
            current = root;
        }
        current.setNext(sentinel); sentinel.setPrev(current);
        return;
    }
    private void throw_to_bucket(HeapNode[] buckets, HeapNode root){
        int index = root.getRank();
        if(buckets[index] == null){
            buckets[index] = root;
            return;
        }
        HeapNode linked = link(root, buckets[index]);
        buckets[index] = null;
        throw_to_bucket(buckets, linked);
    }
    private HeapNode link(HeapNode root_a, HeapNode root_b){
        TOTAL_LINKS++;
        if(root_a.getKey() > root_b.getKey()){
            HeapNode temp = root_a;
            root_a = root_b;
            root_b = temp;
        }
        root_a.add_child(root_b);
        return root_a;
    }

    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     *
     */
    public HeapNode findMin()
    {
        if(isEmpty()){
            return null;
        }
        return min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
    public void meld (FibonacciHeap heap2)
    {
        if(heap2.isEmpty()){
            return;
        }
        this.last_root().setNext(heap2.first_root());
        heap2.first_root().setPrev(this.last_root());
        sentinel.setPrev(heap2.last_root());
        heap2.last_root().setNext(sentinel);
        if(heap2.findMin().getKey() < this.findMin().getKey()){
            this.min = heap2.findMin();
        }
        this.size += heap2.size();
        this.num_trees+= heap2.get_num_trees();
        this.num_marked+= heap2.get_num_marked();
        return;
    }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     */
    public int size()
    {
        return size;
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     *
     */
    public int[] countersRep() {
        if(isEmpty()) {
            return new int[]{};
        }
        int max_degree = 0;
        for(HeapNode root : rootArr()){
            if (root.getRank() > max_degree){
                max_degree = root.getRank();
            }
        }
        int n = max_degree + 1;
        int[] arr = new int[n];
        HeapNode current = first_root();
        while(current.getKey() != sentinel.getKey()){
            arr[current.getRank()]++;
        }
        return arr;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     *
     */
    public void delete(HeapNode x)
    {
        decreaseKey(x, x.getKey() - min.getKey() + 1);
        deleteMin();
        return;
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.setKey(x.getKey() - delta);
        if(!x.is_root() && x.getParent().getKey() > x.getKey()){
            num_marked -= x.unMark();
            cascadingCut(x);
        }
        if(x.getKey() < min.getKey()){
            min = x;
//            HeapNode[] roots = rootArr();
//            System.out.println("in end of decrease key, roots are:");
//            for(HeapNode root : roots){
//                System.out.println(root.getKey());
//            }
        }
        return;
    }

    private void cascadingCut(HeapNode node){
        HeapNode parent = node.getParent();
        cut(node);
        if(!parent.isMark() ){
            if(!parent.is_root()){
                num_marked += parent.mark();
            }

            return;
        }
        num_marked-= parent.unMark();
        cascadingCut(parent);
    }

    private void cut(HeapNode node){
        node.getParent().decRank();
        num_trees++;
        TOTAL_CUTS++;
        if(node.is_first_child()){
            if(node.getKey() == node.getNext().getKey()){
               // System.out.println("rotem");

                node.getParent().setFirst_child(null);

            }
            else{
                node.getParent().setFirst_child(node.getNext());
            }

        }
        node.setParent(null);
        node.getNext().setPrev(node.getPrev());
        node.getPrev().setNext(node.getNext());
        HeapNode current_first = first_root();
        sentinel.setNext(node); node.setPrev(sentinel); node.setNext(current_first); current_first.setPrev(node);


        return;
    }

    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     *
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential() {
        return num_trees + 2*num_marked;
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks()
    {
        return TOTAL_LINKS;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts()
    {
        return TOTAL_CUTS;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        int[] arr = new int[100];
        return arr; // should be replaced by student code
    }

    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     *
     */
    public static class HeapNode{

        public int key;
        public int rank;
        public boolean mark;
        public HeapNode first_child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public int width; // for printing

        public HeapNode(int key) {
            this.key = key;
        }

        public int fill_width(){
            if(this.getFirst_child() == null){
                this.width = 1;
                return 1;
            }
            HeapNode current = getFirst_child();
            for(int i = 0; i < rank; i++){
                this.width += current.fill_width();
                current = current.getNext();
            }
            return this.width;
        }
        private void add_child(HeapNode new_first){
            new_first.setParent(this);
            this.setRank(this.getRank() + 1);
            HeapNode former_first = this.first_child;
            setFirst_child(new_first);
            if(former_first != null){
                new_first.setPrev(former_first.getPrev());
                former_first.getPrev().setNext(new_first);
                former_first.setPrev(new_first);
                new_first.setNext(former_first);
                return;
            }
            new_first.setNext(new_first); new_first.setPrev(new_first);
            return;
        }

        public boolean is_root(){
            return (getParent() == null);
        }


        public int getKey() {
            return this.key;
        }
        public void setKey(int k) {
            this.key = k;
        }
        public int getRank() {
            return this.rank;
        }
        public void setRank(int k) {
            this.rank = k;
        }
        public void decRank(){
            setRank(this.rank -1);
        }

        public boolean isMark() {
            return this.mark;
        }
        public boolean getMarked(){
            return this.isMark();
        }

        public int mark() {
            int res = 0;
            if(!this.mark){
                res = 1;
            }
            this.mark = true;
            return res;
        }

        public int unMark() {
            int res = 0;
            if(this.mark){
                res = 1;
            }
            this.mark = false;
            return res;
        }

        public HeapNode getFirst_child() {
            return this.first_child;
        }
        private boolean is_first_child(){
            if(is_root()){
                System.out.println("wtf is first child on root!");
                return false;
            }
            return(getKey() == getParent().getFirst_child().getKey());
        }
        public HeapNode getChild(){
            return this.getFirst_child();
        }
        public HeapNode[] getChildren(){
            int n = this.getRank();
            HeapNode[] children = new HeapNode[n];
            HeapNode current = this.getFirst_child();
            for(int i = 0; i < n; i++){
                children[i] = current;
                current = current.getNext();
            }
            return children;
        }

        public void setFirst_child(HeapNode first_child) {
            this.first_child = first_child;
        }

        public HeapNode getNext() {
            return this.next;
        }

        public void setNext(HeapNode next) {
            this.next = next;
        }

        public HeapNode getPrev() {
            return this.prev;
        }

        public void setPrev(HeapNode prev) {
            this.prev = prev;
        }

        public HeapNode getParent() {
            return this.parent;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }
    }
}

class testing{
//    public static void main(String[] args) {
//        System.out.println("alo");
//    }

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
            }
            System.out.println();
        }
    }
    static void print_fib(FibonacciHeap fibi){
        int width = 0;
        int height = 10;
        FibonacciHeap.HeapNode current = fibi.sentinel;
        while(current.getNext().getKey()!= fibi.sentinel.getKey()){
            width+= current.width;
        }
        String[][] mat = new String[height][width];
    }
//    static void fill_mat(String [][] mat,int i, int j, FibonacciHeap.HeapNode root ){
//        FibonacciHeap.HeapNode current = root;
//        while(current.getNext().getKey()!= root.sentinel.getKey()){
//            width+= current.width;
//        }
//        if(root.getFirst_child() == null){
//            mat[i][j] = Integer.toString(root.getKey());
//            return;
//        }
//
//
//    }




}




class HeapPrinter {
    static final PrintStream stream = System.out;
    static void printIndentPrefix(ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        for (int i = 0; i < size - 1; ++i) {
            stream.format("%c   ", hasNexts.get(i).booleanValue() ? '│' : ' ');
        }
    }

    static void printIndent(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        printIndentPrefix(hasNexts);

        stream.format("%c── %s\n",
                hasNexts.get(size - 1) ? '├' : '╰',
                heapNode == null ? "(null)" : String.valueOf(heapNode.getKey())
        );
    }

    static String repeatString(String s,int count){
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < count; i++) {
            r.append(s);
        }
        return r.toString();
    }

    static void printIndentVerbose(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        if (heapNode == null) {
            printIndentPrefix(hasNexts);
            stream.format("%c── %s\n", hasNexts.get(size - 1) ? '├' : '╰', "(null)");
            return;
        }

        Function<Supplier<FibonacciHeap.HeapNode>, String> keyify = (f) -> {
            FibonacciHeap.HeapNode node = f.get();
            return node == null ? "(null)" : String.valueOf(node.getKey());
        };
        String title  = String.format(" Key: %d ", heapNode.getKey());
        List<String> content =  Arrays.asList(
                String.format(" Rank: %d ", heapNode.getRank()),
                String.format(" Marked: %b ", heapNode.getMarked()),
                String.format(" Parent: %s ", keyify.apply(heapNode::getParent)),
                String.format(" Next: %s ", keyify.apply(heapNode::getNext)),
                String.format(" Prev: %s ", keyify.apply(heapNode::getPrev)),
                String.format(" Child: %s", keyify.apply(heapNode::getChild))
        );

        /* Print details in box */
        int length = Math.max(
                title.length(),
                content.stream().map(String::length).max(Integer::compareTo).get()
        );
        String line = repeatString("─", length);
        String padded = String.format("%%-%ds", length);
        boolean hasNext = hasNexts.get(size - 1);

        //print header row
        printIndentPrefix(hasNexts);
        stream.format("%c── ╭%s╮%n", hasNext ? '├' : '╰', line);

        //print title row
        printIndentPrefix(hasNexts);
        stream.format("%c   │" + padded + "│%n", hasNext ? '│' : ' ', title);

        // print separator
        printIndentPrefix(hasNexts);
        stream.format("%c   ├%s┤%n", hasNext ? '│' : ' ', line);

        // print content
        for (String data : content) {
            printIndentPrefix(hasNexts);
            stream.format("%c   │" + padded + "│%n", hasNext ? '│' : ' ', data);
        }

        // print footer
        printIndentPrefix(hasNexts);
        stream.format("%c   ╰%s╯%n", hasNext ? '│' : ' ', line);
    }

    static void printHeapNode(FibonacciHeap.HeapNode heapNode, FibonacciHeap.HeapNode until, ArrayList<Boolean> hasNexts, boolean verbose) {
        if (heapNode == null || heapNode == until) {
            return;
        }
        hasNexts.set(
                hasNexts.size() - 1,
                heapNode.getNext() != null && heapNode.getNext() != heapNode && heapNode.getNext() != until
        );
        if (verbose) {
            printIndentVerbose(heapNode, hasNexts);
        } else {
            printIndent(heapNode, hasNexts);
        }

        hasNexts.add(false);
        printHeapNode(heapNode.getChild(), null, hasNexts, verbose);
        hasNexts.remove(hasNexts.size() - 1);

        until = until == null ? heapNode : until;
        printHeapNode(heapNode.getNext(), until, hasNexts, verbose);
    }

    public static void print(FibonacciHeap heap, boolean verbose) {
        if (heap == null) {
            stream.println("(null)");
            return;
        } else if (heap.isEmpty()) {
            stream.println("(empty)");
            return;
        }

        stream.println("╮");
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(false);
        printHeapNode(heap.getFirst(), null, list, verbose);
    }

    public static void demo() {
        /* Build an example */
        FibonacciHeap heap = new FibonacciHeap();
        heap.insert(3);
        heap.insert(100);
        heap.insert(200);
        heap.insert(300);
        heap.insert(54);
        heap.insert(26);
        FibonacciHeap.HeapNode node15 = heap.insert(15);
        FibonacciHeap.HeapNode node = heap.insert(18);
        heap.insert(1);
        HeapPrinter.print(heap, false);
        heap.deleteMin();
        HeapPrinter.print(heap, false);
        //heap.decreaseKey(node15, node15.getKey() - heap.min.getKey() + 1);
        HeapPrinter.print(heap, false);
        //heap.deleteMin();
        heap.delete(node15);

        //heap.decreaseKey(node, 20);

        //HeapPrinter.print(heap, false);
//        heap.insert(2);
//        heap.insert(7);


        /* Print */
//        stream.println("Printing in verbose mode:");
//        HeapPrinter.print(heap, true);

        stream.println("Printing in regular mode:");
        HeapPrinter.print(heap, false);
    }

    public static void main(String[] args) {
        demo();
    }
}