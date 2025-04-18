package org.example;

/**
 * Clasa RingLauncher permite pornirea individuala a fiecarui nod
 * Se utilizeaza aceasta clasa daca se doreste o pornire individuala a nodurilor, pentru a vedea mai clar comunicarea dintre noduri
 */
public class RingLauncher {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Utilizare: java RingLauncher <nodeNumber>");
            System.out.println("nodeNumber: 1, 2 sau 3 pentru a porni nodul corespunzator");
            return;
        }

        int nodeNumber = Integer.parseInt(args[0]);

        switch (nodeNumber) {
            case 1:
                // Pornire Nod 1
                RingNode node1 = new RingNode(1, 5001, "127.0.0.1", 5002, true);
                node1.start();
                System.out.println("Node 1 started as initiator.");
                break;
            case 2:
                // Pornire Nod 2
                RingNode node2 = new RingNode(2, 5002, "127.0.0.1", 5003, false);
                node2.start();
                System.out.println("Node 2 started.");
                break;
            case 3:
                // Pornire Nod 3
                RingNode node3 = new RingNode(3, 5003, "127.0.0.1", 5001, false);
                node3.start();
                System.out.println("Node 3 started.");
                break;
            default:
                System.out.println("Numar de nod invalid. Folositi 1, 2 sau 3.");
        }
    }
}