// file: MaxPQC.java
// author: Kevin Irace
// date: March 16, 2014
//
// This is an implementation of the MaxPQ ADT. The API is specified in
// in the interface found in the accompanying file MaxPQ.java.  This 
// implementation of MaxPQs is non-standard: it uses linked data structures
// rather than sequential ones.
//
import java.util.*;

public class MaxPQC<Key extends Comparable<Key>> implements MaxPQ<Key> {
    
    private static final boolean DEBUG = true;
    
    private Node root;
    private int N;
    
    // An internal node structure. The structure supports threaded
    // trees --- nodes have explicit links to their parents.
    //
    class Node {
        Key info;
        Node parent;
        Node left;
        Node right;
        
        public String toString(){  
            if (info == null){ return "-";}  // "-" represents null
            else if (left == null && right == null){
                return info.toString();
            } else if (left == null){
                return info.toString() +" " + right.toString();
            } else if (right == null){
                return left.toString() + " " + info.toString();
            } else{
                return left.toString() + " " + info.toString() + " " + right.toString();
            }
        }
        
            
        Node(Key info, Node parent, Node left, Node right) {
            this.info = info;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }
        
        Node(Key info, Node parent) {
            this(info, parent, null, null);
        }
        
        // Getters
        //
        Key getInfo()    { return this.info; }
        Node getParent() { return this.parent; }
        Node getLeft()   { return this.left; }
        Node getRight()  { return this.right; }
        
        // Setters. This is a MUTABLE data structure.
        //
        void setInfo(Key info)   { this.info  = info; }
        void setLeft(Node node)  { this.left  = node; }
        void setRight(Node node) { this.right = node; }
        void setParent(Node node){ this.parent = node; }
    }
    
    // A single no-argument constructor.
    //
    public MaxPQC() { 
        this.N = 0;
        this.root = null;
    }
    
    // Beginning of implementation of API entry points.
    //
    public void insert(Key key) {
        if (DEBUG)
           System.out.println("inserting key: " + key + " into pq: " + this.toString());
        
        if (root == null){
            root = new Node(key, null, null, null);
            N++;
        }
        else{
                if(root.getLeft() == null){
                   root.setLeft(new Node(key, root, null, null));
                   N++; 
                   swim();
                }
                else if(root.getRight() == null){
                    root.setRight(new Node(key, root, null, null));
                    N++;
                    swim();
                }
                else{
                    for(int x = 1; x <= N; x++){
                        if(findNode(x).getLeft() == null){
                            findNode(x).setLeft(new Node(key, findNode(x), null, null));
                            N++;
                            swim();
                            break;
                        }
                        else if(findNode(x).getRight() == null){
                            findNode(x).setRight(new Node(key, findNode(x), null, null));
                            N++;
                            swim();
                            break;
                        }
                    } 
                }                                        
        }
        if(DEBUG) {
            System.out.println("pq: " + this.toString());
            System.out.println();
        }
    }
    
    
    private void swim(){
        
        Node current = findNode(N);
        
        while ((current.getParent() != null) && (current.getInfo().compareTo(current.getParent().getInfo()) > 0)){
            Key tempCurrent = current.getInfo();
            Key tempParent = current.getParent().getInfo();
            current.getParent().setInfo(tempCurrent);
            current.setInfo(tempParent);
            current = current.getParent();
        }
    }
    
    private void sink(){
        
        Node current = root;
       
        while((current.getInfo() != null) && ((current.getLeft() != null) && (current.getRight() != null))){ 
            if(current.getLeft().getInfo() == null){break;}
            if(current.getRight().getInfo() == null){break;}

            Key tempCurrent = current.getInfo();
            Key tempLeft = current.getLeft().getInfo();
            Key tempRight = current.getRight().getInfo();
            if((current.getInfo().compareTo(current.getLeft().getInfo()) < 0) && ((current.getLeft().getInfo().compareTo(current.getRight().getInfo()) > 0) || (current.getRight().getInfo().compareTo(current.getLeft().getInfo()) == 0))){
                current.setInfo(tempLeft);
                current.getLeft().setInfo(tempCurrent); 
                current = current.getLeft();
            }
            else if((current.getInfo().compareTo(current.getRight().getInfo()) < 0) && ((current.getRight().getInfo().compareTo(current.getLeft().getInfo()) > 0))){
                current.setInfo(tempRight);
                current.getRight().setInfo(tempCurrent); 
                current = current.getRight();
            }
            else{ break; }
        }
    }

    public Key delMax() {
        
       Node rootLeft = root.getLeft();
       Node rootRight = root.getRight();
       Key max = root.getInfo(); 
       Node currentTemp = findNode(N);
       root.setInfo(currentTemp.getInfo());
       findNode(N).setInfo(null);
       findNode(N).setParent(null);
       N--;
       sink();

       return max;
    }
        
    public int size()        { return N; }
    
    public boolean isEmpty() { return N == 0; }
    
    public String toString() { 
        if (root != null){
        return root.toString();
        } else{
        return "";
        }
    }

    /////// End of API Entry Points. Beginning of helper code. ////////
    
    // Three little helper functions for finding the kth Node in the tree
    // where k is the number of a node using the usual level-order numbering.
    
    private Node findNode(int k) {
        String path = this.pathTo(k);
        return this.followPath(this.root, path);
    }
    
    // This function uses a string of 0's and 1's to represent a path
    // to the kth node.
    //
    private String pathTo(int k) {
        if (k <= 1)
            return "";
        else
            return pathTo(k / 2) + (k % 2);  // yields a String!
    }
    
    private Node followPath(Node node, String path) {
        if (path.equals(""))
            return node;
        else {
            char c = path.charAt(0);
            Node next = (c == '0') ? node.getLeft() : node.getRight();
            return followPath(next, path.substring(1));
        }
    }    
    
    public static void main(String[] args) {
     
        MaxPQ<Integer> pq = new MaxPQC<Integer>();
        
        pq.insert(5);
        pq.insert(1);
        pq.insert(3);
        pq.insert(7);
        pq.insert(9);
        pq.insert(4);
        pq.insert(2);
        
        System.out.println("MAX: " + pq.delMax());
        System.out.println(pq);
        System.out.println();
        System.out.println("MAX: " + pq.delMax());
        System.out.println(pq);
        System.out.println();
        System.out.println("MAX: " + pq.delMax());
        System.out.println(pq);
        System.out.println();
        System.out.println("MAX: " + pq.delMax());
        System.out.println(pq);
        System.out.println();
        System.out.println("MAX: " + pq.delMax());
        System.out.println(pq);
        System.out.println();
        System.out.println("MAX: " + pq.delMax());
        System.out.println(pq);
        System.out.println();
        System.out.println("MAX: " + pq.delMax());
        System.out.println(pq);
        System.out.println();
      
        //toString "1 7 5 9 3 4 2" is a poor representation of:
        /*
         *                            9
         *                       7         4   
         *                    1    5     3    2
         *
         */
    }
}
