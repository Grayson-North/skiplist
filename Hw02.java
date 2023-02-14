/*==============================================================================
| Assignment: HW 02 - Building and managing a Skiplist
|
| Author: Grayson North
| Language: Java
|
| To Compile: javac Hw02.java
|
| To Execute: java Hw02 filename
| where filename is in the current directory and contains
| commands to insert, delete, print and quit.
|
| Class: COP3503 - CS II Summer 2020
| Instructor: McAlpin
| Due Date: 07/19/20
|
+=============================================================================*/

// ------------------------- LIBRARIES AND DEFINITIONS -------------------------
import java.util.*;
import java.io.*;
import java.lang.Math;
// -----------------------------------------------------------------------------



// ----------------------------------- TO DO -----------------------------------
// TO DO: bug testing : printAll()
//------------------------------------------------------------------------------

public class Hw02 {
  private int maxLevel;
  Node head, tail;
  double PI = Double.POSITIVE_INFINITY;
  double NI = Double.NEGATIVE_INFINITY;
  public Random random;

  // ------------------------------ NODE CLASS ---------------------------------
  static class Node {
    int data, level;
    Node previous, next, up, down;

    // node constructor
    public Node(int dataValue, int level) {
      this.data = dataValue;
      this.level = level;
      this.previous = null;
      this.next = null;
      this.up = null;
      this.down = null;
    }
  }
  // ---------------------------------------------------------------------------

  // ----------------------- SKIPLIST HELPER FUNCTIONS -------------------------
  // skiplist constructor
  public Hw02(long seed) {
    this.head = new Node((int)NI, 1);
    this.tail = new Node((int)PI, 1);
    this.update_maxLevel(1);
    head.next = tail;
    tail.previous = head;
    head.previous = null;
    tail.next = null;
    head.up = null;
    head.down = null;
    tail.up = null;
    tail.down = null;
    this.random = new Random(seed);
  }

  public void update_maxLevel(int level) {
    maxLevel += level;
  }

  public int get_maxLevel() {
    return maxLevel;
  }

  public void coinFlip(Node node) {
    int x;
    x = Math.abs(random.nextInt() % 2);

    if (x == 0)
      return;

    while (x == 1) {
      node = promote(node);
      x = Math.abs(random.nextInt() % 2);
    }
  }

  // this is a helper function that returns the node next to the one to be
  // inserted/deleted
  public Node getNode(Node node) {
    Node tempHead = head;

    while (true) {
      while (tempHead.next != null && tempHead.data <= node.data)
        tempHead = tempHead.next;

      if (tempHead.data == node.data)
        break;
      while (tempHead.down != null) {
        tempHead = tempHead.down;
        while (tempHead.previous != null && tempHead.previous.data > node.data)
          tempHead = tempHead.previous;
      }
      break;
    }
    return tempHead;
  }

  // ---------------------------------------------------------------------------

  // -------------------------- REQUIRED FUNCTIONS -----------------------------
  public Node promote(Node node) {
    if (node == null)
      return null;

    // here the program makes a new node and links it to its lower level
    // counterpart
    Node newNode = new Node(node.data, node.level + 1);
    node.up = newNode;
    newNode.down = node;
    node = newNode;

    // if the new level being made is greater than the max level, the program
    // will first make a new head node ("newHead") and link its "down" pointer
    // to the topmost head's "up" pointer. (I know Java doesn't have pointers,
    // but I don't know what else to call them). Then the function will insert
    // the new node into this new, higher level.
    if (node.level > get_maxLevel()) {
      update_maxLevel(1);
      Node newHead = new Node(head.data, head.level + 1);
      newHead.down = head;
      head.up = newHead;
      head = newHead;
      head.next = node;
      node.previous = head;
      Node newTail = new Node(tail.data, tail.level + 1);
      newTail.down = tail;
      tail.up = newTail;
      tail = newTail;
      node.next = tail;
      tail.previous = node;
    }
    // here, the level already exists so the program inserts into the current
    // level
    else {
      int currentLevel = node.level;
      int i;
      Node walker = head;

      for (i = get_maxLevel(); i > currentLevel; i--) {
        while (walker.next != null && walker.data < node.data)
          walker = walker.next;
        walker = walker.down;
      }


      if (walker.data == head.data) {
        walker.next.previous = node;
        node.next = walker.next;
        node.previous = walker;
        walker.next = node;
        return node;
      }

      else {
        while (walker.previous.data > node.data)
          walker = walker.previous;

        walker.previous.next = node;
        node.previous = walker.previous;
        node.next = walker;
        walker.previous = node;
      }
    }
    return node;
  }

  public void insert(int data) {
    Node node = new Node(data, 1);
    Node temp = getNode(node);

    // returns if the data already exists in the skiplist
    if (temp.data == data)
      return;

    // otherwise getNode() returned the node at the position one "element" after
    // where the node should be inserted
    node.previous = temp.previous;
    node.previous.next = node;
    temp.previous = node;
    node.next = temp;

    coinFlip(node);
    return;
  }

  public void search(int data) {
    Node walker = head;

    while (true) {
      while (walker.data < data)
        walker = walker.next;

      if (walker.data == data) {
        System.out.println(data + " found");
        break;
      }
      else if (walker.down == null && walker.data != data) {
        System.out.println(data + " NOT FOUND");
        break;
      }
      else {
        walker = walker.down;
      }
    }
    return;
  }

  public void delete(int data) {
    Node node = new Node(data, 1);
    Node toBeDeleted = getNode(node);
    node = toBeDeleted.previous;

    if (node.data != data) {
      System.out.println(data + " integer not found - delete not successful");
      return;
    }

    else {
      // the delete function deletes from the bottom level up
      while (node.up != null) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
        node.next = null;
        node.previous = null;
        node = node.up;
        node.down = null;
      }
      System.out.println(data + " deleted");
    }
    return;
  }

  public void printAll() {
    Node pw = head;

    while (pw.down != null)
      pw = pw.down;

    pw = pw.next;

    System.out.println("the current Skip List is shown below:");
    System.out.println("---infinity");

    while (pw.next != null) {
      System.out.printf("\t%d;", pw.data);
      while (pw.up != null) {
        pw = pw.up;
        System.out.printf("\t%d;", pw.data);
      }
      System.out.println();
      while (pw.down != null)
        pw = pw.down;

      pw = pw.next;
    }

    System.out.println("+++infinity");
    System.out.println("---End of Skip List---");
  }
  // ---------------------------------------------------------------------------

  // --------------------------------- MAIN ------------------------------------
  public static void main(String[] args) {
    long default_seed = 42;
    Hw02 skiplist;
    File file;
    BufferedReader br;
    String s;
    Object stringPlaceholder;
    int i;
    Queue<String> q = new LinkedList<String>();

    file = new File(args[0]);
    try {
      br = new BufferedReader(new FileReader(file));
      s = br.readLine();

      // while loop to read and print out each line of the file, then adds each
      // line to a Queue named q, where each line can be broken up into a single
      // character and an integer.
      while (s != null) {
        q.add(s);
        s = br.readLine();
      }

      br.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    if (args.length > 1) {
      long seed = Long.parseLong(args[1]);
      skiplist = new Hw02(seed);
      System.out.println("For the input file named " + file);
      System.out.println("With the RNG seeded,");
    }
    else {
      skiplist = new Hw02(default_seed);
      System.out.println("For the input file named " + file);
      System.out.println("With the RNG unseeded,");
    }

    while (q.size() > 0) {
      stringPlaceholder = q.remove();
      s = stringPlaceholder.toString();

      if (s.charAt(0) == 'p')
        skiplist.printAll();

      else if (s.charAt(0) == 'q') {
        System.err.println("gr967419;3.5;20.0");
        System.exit(0);
      }

      else {
        // this line gets rid of the character and the space in front of the
        // integer value.
        i = Integer.parseInt(s.replaceAll("[\\D]", ""));

        if (s.charAt(0) == 'i')
          skiplist.insert(i);

        else if (s.charAt(0) == 's')
          skiplist.search(i);

        else if (s.charAt(0) == 'd')
          skiplist.delete(i);
      }
    }
  }
}
// -----------------------------------------------------------------------------

/*==============================================================================
| I Grayson North (gr967419) affirm that this program is
| entirely my own work and that I have neither developed my code together with
| any another person, nor copied any code from any other person, nor permitted
| my code to be copied or otherwise used by any other person, nor have I
| copied, modified, or otherwise used programs created by others. I acknowledge
| that any violation of the above terms will be treated as academic dishonesty.
+=============================================================================*/
