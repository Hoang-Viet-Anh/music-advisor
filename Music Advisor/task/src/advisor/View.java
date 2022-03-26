package advisor;

import java.util.*;

public class View {

    private static final View INSTANCE = new View();
    private int limit = 5;

    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }

    private View() { }

    public static View getInstance() {
        return INSTANCE;
    }

    public static <T> void printPage(ListIterator<T> listIterator, LinkedList<T> linkedList, Scanner scanner) {
        int limit = View.getInstance().getLimit();
        int count = 0;
        int pages = 0;
        int maxPages = (int) Math.ceil( (double) (linkedList.size() / limit) );
        List<T> list = new ArrayList<>();
        String type = "default";

        while (true) {
            switch (type) {
                case "next":
                    if (listIterator.hasNext()) {
                        count = 0;
                        for (int i = 0; i < limit; i++) {
                            if (listIterator.hasNext()) {
                                System.out.println(listIterator.next());
                                count++;
                            }
                        }
                        System.out.printf("---PAGE %d OF %d---%n", ++pages, maxPages);
                    } else {
                        System.out.println("No more pages.");
                    }
                    break;
                case "prev":
                    for (int i = 0; i < count; i++) {
                        if (listIterator.hasPrevious()) {
                            listIterator.previous();
                        }
                    }
                    if (listIterator.hasPrevious()) {
                        count = 0;
                        for (int i = 0; i < limit; i++) {
                            if (listIterator.hasPrevious()) {
                                list.add(listIterator.previous());
                            }
                        }
                        if (!list.isEmpty()) {
                            Collections.reverse(list);
                            list.forEach(System.out::println);
                            System.out.printf("---PAGE %d OF %d---%n", --pages, maxPages);
                            list.clear();
                        }
                        for (int i = 0; i < limit; i++) {
                            if (listIterator.hasNext()) {
                                listIterator.next();
                                count++;
                            }
                        }
                    } else {
                        System.out.println("No more pages.");
                        for (int i = 0; i < limit; i++) {
                            if (listIterator.hasNext()) {
                                listIterator.next();
                            }
                        }
                    }
                    break;
                case "exit":
                    System.out.println("Returned.");
                    return;
                case "default":
                    count = 0;
                    pages = 1;
                    listIterator = linkedList.listIterator();
                    for (int i = 0; i < limit; i++) {
                        if (listIterator.hasNext()) {
                            System.out.println(listIterator.next());
                            count++;
                        }
                    }
                    System.out.printf("---PAGE %d OF %d---%n", pages, maxPages);
                    break;
                default:
                    System.out.println("Unknown command.");
                    break;
            }
            type = scanner.nextLine();
        }
    }

}
