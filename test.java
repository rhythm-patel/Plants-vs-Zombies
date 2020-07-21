import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class test {

    public static void main(String args[]){
        LinkedList<Integer> list = new LinkedList<Integer>();
        list.add(-8);
        list.add(10);
        list.add(-20);
        list.add(null);
        Comparator<Integer> r = Collections.reverseOrder();
        Collections.sort(list,r);
        for (int i: list)
            System.out.println(i + " ");
        System.out.println();
    }
}
