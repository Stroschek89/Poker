import java.util.*;

public class CardGame {

    // Creates a standard 52-card deck with ranks and suits
    public static List<String> createDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        List<String> deck = new ArrayList<>();

        // Combine each rank with each suit to form a full deck
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(rank + " of " + suit);
            }
        }

        return deck;
    }

    // Draws a specified number of cards from the deck
    public static List<String> drawCards(List<String> deck, int numCards) {
        List<String> drawnCards = new ArrayList<>();

        // Remove cards from the end of the deck and add to the drawn cards list
        for (int i = 0; i < numCards; i++) {
            if (!deck.isEmpty()) {
                drawnCards.add(deck.remove(deck.size() - 1));
            }
        }

        return drawnCards;
    }

    // Deals a specified number of hands, each containing 2 cards
    public static List<List<String>> dealHands(List<String> deck, int numHands) {
        List<List<String>> hands = new ArrayList<>();

        for (int i = 0; i < numHands; i++) {
            List<String> hand = drawCards(deck, 2); // Each hand gets 2 cards
            hands.add(hand);
        }

        return hands;
    }

    // Evaluates the type of hand based on player's hand and table cards
    public static String evaluateHand(List<String> hand, List<String> tableCards) {
        // Combine player's hand and table cards for evaluation
        List<String> combined = new ArrayList<>(hand);
        combined.addAll(tableCards);

        // Separate the ranks and suits of the cards
        List<String> ranks = new ArrayList<>();
        List<String> suits = new ArrayList<>();

        for (String card : combined) {
            String[] parts = card.split(" of ");
            ranks.add(parts[0]);
            suits.add(parts[1]);
        }

        // Map card ranks to numeric values for easier comparisons
        Map<String, Integer> rankValues = new HashMap<>();
        String[] rankOrder = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        for (int i = 0; i < rankOrder.length; i++) {
            rankValues.put(rankOrder[i], i + 2);
        }

        // Count occurrences of each rank and suit
        Map<String, Integer> rankCounts = new HashMap<>();
        Map<String, Integer> suitCounts = new HashMap<>();
        for (String rank : ranks) {
            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
        }
        for (String suit : suits) {
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
        }

        // Identify pairs, three-of-a-kinds, four-of-a-kinds, and two-pairs
        boolean hasPair = false;
        boolean hasTwoPair = false;
        boolean hasThreeOfAKind = false;
        boolean hasFourOfAKind = false;

        int pairCount = 0;
        for (int count : rankCounts.values()) {
            if (count == 2) {
                pairCount++;
            } else if (count == 3) {
                hasThreeOfAKind = true;
            } else if (count == 4) {
                hasFourOfAKind = true;
            }
        }
        if (pairCount == 1) {
            hasPair = true;
        } else if (pairCount == 2) {
            hasTwoPair = true;
        }

        // Check for flush (five or more cards of the same suit)
        boolean hasFlush = suitCounts.values().stream().anyMatch(count -> count >= 5);

        // Check for straight (five consecutive ranks)
        Set<Integer> uniqueRanks = new HashSet<>();
        for (String rank : ranks) {
            uniqueRanks.add(rankValues.get(rank));
        }
        List<Integer> sortedRanks = new ArrayList<>(uniqueRanks);
        Collections.sort(sortedRanks);
        boolean hasStraight = false;
        for (int i = 0; i <= sortedRanks.size() - 5; i++) {
            if (sortedRanks.get(i + 4) - sortedRanks.get(i) == 4) {
                hasStraight = true;
                break;
            }
        }
        // Check for Ace-low straight (A, 2, 3, 4, 5)
        if (uniqueRanks.containsAll(Arrays.asList(2, 3, 4, 5, 14))) {
            hasStraight = true;
        }

        // Check for straight flush and royal flush
        boolean hasStraightFlush = false;
        boolean hasRoyalFlush = false;
        if (hasStraight && hasFlush) {
            Map<String, List<Integer>> suitToRanks = new HashMap<>();
            for (int i = 0; i < ranks.size(); i++) {
                suitToRanks.computeIfAbsent(suits.get(i), k -> new ArrayList<>()).add(rankValues.get(ranks.get(i)));
            }
            for (List<Integer> suitRanks : suitToRanks.values()) {
                Collections.sort(suitRanks);
                for (int i = 0; i <= suitRanks.size() - 5; i++) {
                    if (suitRanks.get(i + 4) - suitRanks.get(i) == 4) {
                        hasStraightFlush = true;
                        if (suitRanks.subList(i, i + 5).containsAll(Arrays.asList(10, 11, 12, 13, 14))) {
                            hasRoyalFlush = true;
                        }
                        break;
                    }
                }
            }
        }

        // Return the hand type based on the evaluation
        if (hasRoyalFlush) {
            return "Royal Flush";
        } else if (hasStraightFlush) {
            return "Straight Flush";
        } else if (hasFourOfAKind) {
            return "Four of a Kind";
        } else if (hasThreeOfAKind && hasPair) {
            return "Full House";
        } else if (hasFlush) {
            return "Flush";
        } else if (hasStraight) {
            return "Straight";
        } else if (hasThreeOfAKind) {
            return "Three of a Kind";
        } else if (hasTwoPair) {
            return "Two Pair";
        } else if (hasPair) {
            return "Pair";
        } else {
            return "High Card";
        }
    }
    // Strength of every possible hand
    public static final Map<String, Integer> handRankings = Map.of(
            "High Card", 1,
            "Pair", 2,
            "Two Pair", 3,
            "Three of a Kind", 4,
            "Straight", 5,
            "Flush", 6,
            "Full House", 7,
            "Four of a Kind", 8,
            "Straight Flush", 9,
            "Royal Flush", 10
    );


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean playAgain;

        do {
            // Creating and shuffling deck
            List<String> deck = createDeck();
            Collections.shuffle(deck);

            System.out.print("Enter the number of hands to deal: ");

            try {
                int numHands = Integer.parseInt(scanner.nextLine());

                // Checking if there is a proper amount of cards
                if (numHands < 1) {
                    System.out.println("The number of hands must be at least 1.");
                } else if (numHands * 2 + 5 > deck.size()) {
                    System.out.println("Not enough cards to deal " + numHands + " hands. Please enter a smaller number.");
                } else {
                    // Drawing the cards
                    List<String> tableCards = drawCards(deck, 5);
                    System.out.println("Cards on the table: " + String.join(", ", tableCards));

                    List<List<String>> hands = dealHands(deck, numHands);

                    // Showing player hand
                    List<String> firstHand = hands.get(0);
                    String firstHandType = evaluateHand(firstHand, tableCards);
                    System.out.println("Your Hand: " + String.join(", ", firstHand) + " | Hand Type: " + firstHandType);

                    // Asking player about the prediction
                    System.out.print("Do you predict you will win or lose? (win/lose): ");
                    String prediction = scanner.nextLine().trim().toLowerCase();

                    String bestHandType = "";
                    int bestHandRank = 0;
                    int winner = -1;

                    // Evaluating all hands
                    for (int i = 0; i < hands.size(); i++) {
                        List<String> hand = hands.get(i);
                        String handType = evaluateHand(hand, tableCards);
                        int handRank = handRankings.getOrDefault(handType, 0);

                        if (i == 0) {
                            System.out.println("Your Hand: " + String.join(", ", hand) + " | Hand Type: " + handType);
                        } else {
                            System.out.println("Hand " + (i + 1) + ": " + String.join(", ", hand) + " | Hand Type: " + handType);
                        }

                        // Choosing the winner
                        if (handRank > bestHandRank) {
                            bestHandRank = handRank;
                            bestHandType = handType;
                            winner = i + 1;
                        }
                    }

                    System.out.println("\nThe winner is Hand " + winner + " with a " + bestHandType + "!");

                    // Checking the player predicion
                    boolean playerWon = winner == 1;
                    if ((playerWon && prediction.equals("win")) || (!playerWon && prediction.equals("lose"))) {
                        System.out.println("Your prediction was correct!");
                    } else {
                        System.out.println("Your prediction was incorrect.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number for the hands.");
            }

            // Asking the player if he wants to play again
            System.out.print("\nDo you want to play again? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            playAgain = response.equals("yes");

        } while (playAgain);



        System.out.println("Thank you for playing!");
        scanner.close();
    }
}
