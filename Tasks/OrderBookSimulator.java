import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;


class OrderBookEntry {
    int price;
    int size;

    public OrderBookEntry(int price, int size) {
        this.price = price;
        this.size = size;
    }
}

public class OrderBookSimulator {
  
    private static PriorityQueue<OrderBookEntry> bids = new PriorityQueue<>(Comparator.comparingInt(a -> -a.price));
    private static PriorityQueue<OrderBookEntry> asks = new PriorityQueue<>(Comparator.comparingInt(a -> a.price));

  
    private static void processOrderBookUpdate(String[] parts) {
        int price = Integer.parseInt(parts[1]);
        int size = Integer.parseInt(parts[2]);
        String type = parts[3];

        if (type.equals("bid")) {
            bids.add(new OrderBookEntry(price, size));
        } else if (type.equals("ask")) {
            asks.add(new OrderBookEntry(price, size));
        }
    }

    
    private static String processQueryOrMarketOrder(String[] parts) {
        String type = parts[0];

        if (type.equals("q")) {
            String queryType = parts[1];

            if (queryType.equals("best_bid")) {
                OrderBookEntry bestBid = bids.peek();
                return bestBid != null ? bestBid.price + "," + bestBid.size : "";
            } else if (queryType.equals("best_ask")) {
                OrderBookEntry bestAsk = asks.peek();
                return bestAsk != null ? bestAsk.price + "," + bestAsk.size : "";
            } else if (queryType.equals("size")) {
                int price = Integer.parseInt(parts[2]);
                // Search in bids, asks, and spreads
                for (OrderBookEntry entry : bids) {
                    if (entry.price == price) {
                        return entry.size + "";
                    }
                }
                for (OrderBookEntry entry : asks) {
                    if (entry.price == price) {
                        return entry.size + "";
                    }
                }
                return "0";
            }
        } else if (type.equals("o")) {
            String marketOrderType = parts[1];
            int size = Integer.parseInt(parts[2]);

            if (marketOrderType.equals("buy")) {
                return executeBuyMarketOrder(size);
            } else if (marketOrderType.equals("sell")) {
                return executeSellMarketOrder(size);
            }
        }

        return "";
    }

   
    private static String executeBuyMarketOrder(int size) {
        StringBuilder result = new StringBuilder();
        while (size > 0 && !asks.isEmpty()) {
            OrderBookEntry ask = asks.peek();
            if (ask.size <= size) {
                size -= ask.size;
                asks.poll();
            } else {
                ask.size -= size;
                size = 0;
            }
        }
        return result.toString();
    }

  
    private static String executeSellMarketOrder(int size) {
        StringBuilder result = new StringBuilder();
        while (size > 0 && !bids.isEmpty()) {
            OrderBookEntry bid = bids.peek();
            if (bid.size <= size) {
                size -= bid.size;
                bids.poll();
            } else {
                bid.size -= size;
                size = 0;
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts[0].equals("u")) {
                    processOrderBookUpdate(parts);
                } else {
                    String output = processQueryOrMarketOrder(parts);
                    if (!output.isEmpty()) {
                        writer.write(output);
                        writer.newLine();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
