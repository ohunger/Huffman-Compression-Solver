package main.compression;

import java.util.*;


import java.io.ByteArrayOutputStream; // Optional


/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    // TreeMap chosen here just to make debugging easier
    private TreeMap<Character, String> encodingMap = new TreeMap<Character, String>();
    // Character that represents the end of a compressed transmission
    private static final char ETB_CHAR = 23;
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * 
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    public static void main(String[] args) {
        Huffman a = new Huffman("ABBBCC");
        
    }
    public Huffman (String corpus) {
        // >> [KT] Make sure to remove commented TODOs! Leaving them in makes 
        // the submission look a bit incomplete  
        // TODO!    
            // Make distribution of characters in given corpus
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        for (int i = 0; i < corpus.length(); i++){
            char c = corpus.charAt(i);
            Integer temp = map.get(c);
            if(temp != null){
                map.put(c, temp + 1);
            }
            else{
                map.put(c,1);
            }
        }
            //Priority Queue with leaf nodes
        PriorityQueue<HuffNode> leaves = new PriorityQueue<HuffNode>();
        leaves.add(new HuffNode(ETB_CHAR, 1));
        for(Map.Entry<Character,Integer> entry : map.entrySet()){
            leaves.add(new HuffNode(entry.getKey(),entry.getValue()));
        }
            //Generating Huffman trie
        while(leaves.size() > 1){
            HuffNode zero = leaves.peek();
            for(HuffNode node : leaves){
                if(zero.compareTo(node) == 1){
                    zero = node;
                }
            }
            leaves.remove(zero);
            HuffNode one = leaves.peek();
            for(HuffNode node : leaves){
                if(zero.compareTo(node) == 1){
                    one = node;
                }
            }
            leaves.remove(one);
            
            HuffNode parent = new HuffNode(zero.character, zero.count + one.count);
            parent.zeroChild = zero;
            parent.oneChild = one;
            leaves.add(parent);
        }
        trieRoot = leaves.peek();
        


        paths(trieRoot, ""); 
    }
    
    // >> [KT] All methods, even tiny private helper methods, 
    // need proper Javadocs (-1) 
    public void paths(HuffNode node, String a){
        if(node.isLeaf()){
            encodingMap.put(node.character, a); 
            return;
        }else{ 
            paths(node.zeroChild, a + "0");
            paths(node.oneChild, a + "1"); 
        }
    }
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * 
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as:
     *         (1) the bitstring containing the message itself, (2) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
        // TODO!
        // >> [KT] Not sure that temp and temp2 are great variable names 
        // here. Looking over your code it's difficult to tell what they 
        // are supposed to represent! (-0.5)
        String temp = "";
        for (int i = 0; i < message.length(); i++) {
            temp = temp + encodingMap.get(message.charAt(i));
        }
        temp = temp + encodingMap.get(ETB_CHAR);
        while(temp.length() < ( (temp.length()>8)?16:8 )){
            temp = temp + "0";
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // >> [KT] Here's an alternate technique for padding if you're
        // interested: 
        // bitString += this.encodingMap.get(ETB_CHAR);
        // while (bitString.length()%8 > 0) {
        //     bitString += "0";
        // }
        String temp2 = "";
        if(temp.length() > 8){
            temp2 = temp.substring(8, 16);
            temp = temp.substring(0,8);
            output.write((byte) Integer.parseInt(temp, 2));
            output.write((byte) Integer.parseInt(temp2, 2));
        }
        else{
            output.write((byte) Integer.parseInt(temp, 2));
        }
        
        return output.toByteArray();
        
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * 
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as:
     *        (1) the bitstring containing the message itself, (2) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
        // TODO!
        String fullByte = "";
        for(byte temp : compressedMsg){
            String tempByte = Integer.toBinaryString(temp & 0xff);
            while(tempByte.length() < 8){
                tempByte = "0" + tempByte;
            }
            fullByte = fullByte + tempByte ;
        }
        System.out.println("Byte " + fullByte);
        //fullbyte is 8/16 string

        String result = "";
        String code = "";
        HuffNode node = trieRoot;
        for (char ch: fullByte.toCharArray()) {
            System.out.println("code: " + code);
            if(node.isLeaf()){
                for (Map.Entry<Character, String> entry : encodingMap.entrySet()){
                    if(entry.getValue().equals(code)){
                        
                        result = result + entry.getKey();
                        if(entry.getKey().equals(ETB_CHAR)){
                            // >> [KT] Need to make sure all print statements are 
                            // removed next time. It clogs up the terminal for us while
                            // grading your repos, and it just looks a bit sloppy to 
                            // leave into a final submission besides (-1)
                            System.out.println("final result: " + result);

                            return result.trim();
                        }
                    }
                }
                code = "";
                node = trieRoot;
            }
            if(ch == '0'){
                node = node.zeroChild;
                code = code + "0";
            }
            if(ch == '1'){
                node = node.oneChild;
                code = code + "1";
            }
        }
        return result.trim();
    }

    


    
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left (0) and right (1) child), contains
     * a character field that it represents, and a count field that holds the 
     * number of times the node's character (or those in its subtrees) appear 
     * in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode zeroChild, oneChild;
        char character;
        int count; 
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () { //if letter node aka like |B|3|
            return this.zeroChild == null && this.oneChild == null;
        }
        
        public int compareTo (HuffNode other) { // returns 0 if precedes other
            // TODO: Implemented incorrectly at the moment!
            if(this.count > other.count){
                return 1;
            }if(this.count < other.count){
                return 0;
            }
            if(this.character > other.character){
                return 1;
            }
            if(this.character < other.character){
                return 0;
            }
            return 0;
            
        }
        
    }

}

// ===================================================
// >>> [KT] Summary
// A great submission that shows strong command of
// programming fundamentals, generally good style,
// and a good grasp on the problem and supporting
// theory of compression algos. Indeed, there is definitely
// a lot to like in what you have above, but
// I think you could have tested it a little more just
// to round out the several edge cases that evaded your
// detection. Give yourself more time to test + debug
// future submissions and you'll be golden!
// ---------------------------------------------------
// >>> [KT] Style Checklist
// [X] = Good, [~] = Mixed bag, [ ] = Needs improvement
//
// [~] Variables and helper methods named and used well
// [X] Proper and consistent indentation and spacing
// [~] Proper JavaDocs provided for ALL methods
// [X] Logic is adequately simplified
// [X] Code repetition is kept to a minimum
// ---------------------------------------------------
// Correctness:          92.5 / 100 (-1.5 / missed unit test)
// Style Penalty:       -2.5
// Total:                90 / 100
// ===================================================
