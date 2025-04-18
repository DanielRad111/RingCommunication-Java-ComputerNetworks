package org.example;

import java.io.*;
import java.net.*;

/**
 * RingNode - Implementare pentru un nod in topologia de comunicare în inel
 * Fiecare nod asculta pe un port si primeste un mesaj, apoi incrementează valoarea
 * trimite valoarea incrementata la nodul urmator din inel
 */
public class RingNode {
    private int nodeId;           // ID-ul nodului
    private int listenPort;       // Portul pe care asculta nodul
    private String nextNodeIP;    // IP-ul nodului urmator
    private int nextNodePort;     // Portul nodului urmator
    private boolean isInitiator;  // verifica daca este nod initiator
    private static final int FINAL_VALUE = 100; // Valoarea finala pentru oprire

    public RingNode(int nodeId, int listenPort, String nextNodeIP, int nextNodePort, boolean isInitiator) {
        this.nodeId = nodeId;
        this.listenPort = listenPort;
        this.nextNodeIP = nextNodeIP;
        this.nextNodePort = nextNodePort;
        this.isInitiator = isInitiator;
    }

    /**
     * Metoda principala de pornire a nodului
     */
    public void start() {
        System.out.println("Nod " + nodeId + ": Pornire...");
        System.out.println("Nod " + nodeId + ": Ascult pe portul " + listenPort);
        System.out.println("Nod " + nodeId + ": Conectez la " + nextNodeIP + ":" + nextNodePort);

        if (isInitiator) {
            try {
                Thread.sleep(2000);
                System.out.println("Nod " + nodeId + ": Initez prima conexiune catre Nod 2");
                sendMessage(1);
                System.out.println("Nod " + nodeId + ": Am initiat comunicarea cu valoarea 1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        new Thread(() -> listenForMessages()).start();
    }

    /**
     * Metoda de ascultare pentru mesaje primite de la nodul anterior
     */
    private void listenForMessages() {
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            System.out.println("Nod " + nodeId + ": Server pornit pe portul " + listenPort);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    System.out.println("\nNod " + nodeId + ": Am primit o noua conexiune:");
                    System.out.println("  - De la adresa: " + clientSocket.getInetAddress().getHostAddress());
                    System.out.println("  - De la portul: " + clientSocket.getPort());
                    System.out.println("  - Pe portul local: " + clientSocket.getLocalPort());

                    String messageStr = in.readLine();
                    System.out.println("Nod " + nodeId + ": Am primit mesajul: " + messageStr);

                    int receivedValue = extractValueFromMessage(messageStr);
                    System.out.println("Nod " + nodeId + ": Valoarea extrasa: " + receivedValue);

                    if (receivedValue >= FINAL_VALUE) {
                        System.out.println("Nod " + nodeId + ": Valoarea finala " + FINAL_VALUE + " a fost atinsa. Oprirea comunicarii.");
                        break;
                    }

                    int newValue = receivedValue + 1;
                    System.out.println("Nod " + nodeId + ": Incrementez valoarea la " + newValue +
                            " si o trimit catre Nod " + ((nodeId % 3) + 1));
                    sendMessage(newValue);

                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.err.println("Nod " + nodeId + ": Eroare la procesarea conexiunii: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Nod " + nodeId + ": Eroare la pornirea serverului: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int extractValueFromMessage(String message) {
        // Format mesaj: NOD_X_TO_NOD_Y_VALUE_Z
        // Extrage Z din mesaj
        String[] parts = message.split("_");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    /**
     * Trimite un mesaj cu o valoare spre nodul urmator din inel
     *
     * @param value Valoarea de trimis
     */
    private void sendMessage(int value) {
        try (Socket socket = new Socket(nextNodeIP, nextNodePort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("\nNod " + nodeId + ": Initez conexiune catre Nod " + ((nodeId % 3) + 1) + ":");
            System.out.println("  - La adresa: " + socket.getInetAddress().getHostAddress());
            System.out.println("  - La portul: " + socket.getPort());
            System.out.println("  - Din portul local: " + socket.getLocalPort());

            String message = "NOD_" + nodeId + "_TO_NOD_" + ((nodeId % 3) + 1) + "_VALUE_" + value;
            out.println(message);
            System.out.println("Nod " + nodeId + ": Am trimis mesajul: " + message);
        } catch (IOException e) {
            System.err.println("Nod " + nodeId + ": Eroare la trimiterea mesajului: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // Nod 1 : asculta pe portul 5001 și trimite catre Nod 2 pe portul 5002
        RingNode node1 = new RingNode(1, 5001, "127.0.0.1", 5002, true);

        // Nod 2: asculta pe portul 5002 și trimite catre Nod 3 pe portul 5003
        RingNode node2 = new RingNode(2, 5002, "127.0.0.1", 5003, false);

        // Nod 3: asculta pe portul 5003 si trimite inapoi catre Nod 1 pe portul 5001, se completeaza inelul astfel
        RingNode node3 = new RingNode(3, 5003, "127.0.0.1", 5001, false);

        // Pornim toate nodurile
        node1.start();
        node2.start();
        node3.start();

        System.out.println("Toate nodurile au fost pornite. Comunicarea în inel a început.");
    }
}