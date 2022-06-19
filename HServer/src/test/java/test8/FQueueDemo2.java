package test8;


import top.hserver.core.queue.fqueue.FQueue;


public class FQueueDemo2 {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    FQueue queue = new FQueue("/tmp/test-fqueue");

    queue.offer("abc".getBytes());
    System.out.println("label1: " + queue.size());

    String data2 = new String(queue.peek());
    System.out.println("label2: " + queue.size());
    System.out.println("label2: data2 -> " + data2);

    String data3 = new String(queue.remove());
    System.out.println("label3: " + queue.size());
    System.out.println("label3: data3 -> " + data3);
  }

}