import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PearlJam {

    private static List<Resident> loadResidents() {
        List<Resident> residents = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("combinedRS.csv"))) {
            int linesToSkip = 2;
            String line;
            while ((line = br.readLine()) != null) {
                if (linesToSkip > 0) {
                    linesToSkip--;
                    continue; // Skip the specified number of lines
                }
                String[] data = line.split(",");
                String name = data[0].trim();
                String age = data[1].trim();
                String gender = data[2].trim();

                residents.add(new Resident(name, age, gender));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return residents;
    }

    static class MenuItem {
        private String name;
        private double price;

        public MenuItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    private static Map<String, List<Resident>> assignFoodAndRestaurant(List<Resident> residents) {
        Map<String, List<Resident>> waitingLists = new HashMap<>();

        for (Resident resident : residents) {
            String restaurant = getRandomRestaurant();
            resident.setRestaurant(restaurant);

            List<MenuItem> menu = getRestaurantMenu(restaurant);
            MenuItem randomMenu = getRandomMenu(menu);
            resident.setMenu(randomMenu);

            List<Resident> waitingList = waitingLists.getOrDefault(restaurant, new ArrayList<>());
            waitingList.add(resident);
            waitingLists.put(restaurant, waitingList);
        }

        return waitingLists;
    }

    private static MenuItem getRandomMenu(List<MenuItem> menu) {
        if (menu.isEmpty()) {
            return null;
        }

        int randomIndex = (int) (Math.random() * menu.size());
        return menu.get(randomIndex);
    }

    private static List<MenuItem> getRestaurantMenu(String restaurant) {
        List<MenuItem> menu = new ArrayList<>();

        switch (restaurant) {
            case "Jade Garden":
                // Add menu items for Jade Garden
                menu.add(new MenuItem("Braised Chicken in Black Bean Sauce", 15.00));
                menu.add(new MenuItem("Braised Goose Web with Vermicelli", 21.00));
                menu.add(new MenuItem("Deep-fried Hiroshima Oysters", 17.00));
                menu.add(new MenuItem("Poached Tofu with Dried Shrimps", 12.00));
                menu.add(new MenuItem("Scrambled Egg White with Milk", 10.00));
                break;
            case "Cafe Deux Magots":
                // Add menu items for Cafe Deux Magots
                menu.add(new MenuItem("Sampling Matured Cheese Platter", 23.00));
                menu.add(new MenuItem("Spring Lobster Salad", 35.00));
                menu.add(new MenuItem("Spring Organic Omelette", 23.00));
                menu.add(new MenuItem("Truffle-flavoured Poultry Supreme", 34.00));
                menu.add(new MenuItem("White Asparagus", 26.00));
                break;
            case "Trattoria Trussardi":
                menu.add(new MenuItem("Caprese Salad", 10.00));
                menu.add(new MenuItem("Creme caramel", 6.50));
                menu.add(new MenuItem("Lamb Chops with Apple Sauce", 25.00));
                menu.add(new MenuItem("Spaghetti alla Puttanesca", 15.00));
                break;
            case "libeccio":
                menu.add(new MenuItem("Formaggio", 12.50));
                menu.add(new MenuItem("Ghiaccio", 1.01));
                menu.add(new MenuItem("Melone", 5.20));
                menu.add(new MenuItem("Prosciutto and Pesci", 20.23));
                menu.add(new MenuItem("Risotto", 13.14));
                menu.add(new MenuItem("Zucchero and Sale", 0.60));
                break;
            case "Savage Garden":
                menu.add(new MenuItem("Abbacchio’s Tea", 1.00));
                menu.add(new MenuItem("DIO’s Bread", 36.14));
                menu.add(new MenuItem("Giorno’s Donuts", 6.66));
                menu.add(new MenuItem("Joseph’s Tequila", 35.00));
                menu.add(new MenuItem("Kakyoin’s Cherry", 3.50));
                menu.add(new MenuItem("Kakyoin’s Porridge", 4.44));
                break;
        }
        return menu;
    }

    private static String getRandomRestaurant() {
        String[] restaurants = {
                "Jade Garden",
                "Cafe Deux Magots",
                "Trattoria Trussardi",
                "Libeccio",
                "Savage Garden"
        };

        int randomIndex = (int) (Math.random() * restaurants.length);
        return restaurants[randomIndex];
    }

    private static List<Resident> getWaitingList(Map<String, List<Resident>> waitingLists, String restaurant) {
        return waitingLists.getOrDefault(restaurant, new ArrayList<>());
    }

    private static List<Resident> generateOrderProcessingList(List<Resident> waitingList, String restaurant) {

        List<Resident> orderProcessingList = new ArrayList<>();

        switch (restaurant) {
            case "Jade Garden":
                for (int i = 0; i < waitingList.size() / 2; i++) {
                    orderProcessingList.add(waitingList.get(i));
                    orderProcessingList.add(waitingList.get(waitingList.size() - 1 - i));
                }
                if (waitingList.size() % 2 != 0) {
                    orderProcessingList.add(waitingList.get(waitingList.size() / 2));
                }
                break;
            case "Cafe Deux Magots":
                List<Resident> sortedList = new ArrayList<>(waitingList);
                sortedList.removeIf(resident -> resident.getAge().equals("N/A"));
                sortedList.sort((c1, c2) -> {
                    int age1 = Integer.parseInt(c1.getAge());
                    int age2 = Integer.parseInt(c2.getAge());
                    return Integer.compare(age1, age2);
                });

                List<Resident> processingList = new ArrayList<>();

                int startIndex = 0;
                int endIndex = sortedList.size() - 1;

                while (startIndex <= endIndex) {
                    Resident oldest = sortedList.get(endIndex);
                    Resident youngest = sortedList.get(startIndex);

                    processingList.add(oldest);
                    if (startIndex != endIndex) {
                        processingList.add(youngest);
                    }

                    startIndex++;
                    endIndex--;
                }

                // Add residents with unknown ages ("N/A") to the end of the processing list
                for (Resident resident : waitingList) {
                    if (resident.getAge().equals("N/A")) {
                        processingList.add(resident);
                    }
                }

                orderProcessingList.addAll(processingList);
                break;

            case "Trattoria Trussardi":
                List<Resident> males = new ArrayList<>();
                List<Resident> females = new ArrayList<>();
                List<Resident> naAges = new ArrayList<>();

                for (Resident resident : waitingList) {
                    if (resident.getGender().equals("Male")) {
                        males.add(resident);
                    } else {
                        females.add(resident);
                    }
                }
                males.sort(Comparator.comparingInt((Resident r) -> {
                    if (r.getAge().equals("N/A")) {
                        return Integer.MAX_VALUE; // Assign a high value for "N/A" ages
                    } else {
                        return Integer.parseInt(r.getAge());
                    }
                }));
                females.sort(Comparator.comparingInt((Resident r) -> {
                    if (r.getAge().equals("N/A")) {
                        return Integer.MAX_VALUE; // Assign a high value for "N/A" ages
                    } else {
                        return Integer.parseInt(r.getAge());
                    }
                }).reversed());

                // Add residents with "N/A" age to the naAges list for each gender
                for (Resident resident : males) {
                    if (resident.getAge().equals("N/A")) {
                        naAges.add(resident);
                    }
                }
                males.removeAll(naAges);

                for (Resident resident : females) {
                    if (resident.getAge().equals("N/A")) {
                        naAges.add(resident);
                    }
                }
                females.removeAll(naAges);

                while (!males.isEmpty() || !females.isEmpty()) {
                    if (!males.isEmpty()) {
                        orderProcessingList.add(males.remove(0));
                    }
                    if (!females.isEmpty()) {
                        orderProcessingList.add(females.remove(0));
                    }
                    if (!males.isEmpty()) {
                        orderProcessingList.add(males.remove(males.size()-1));
                    }
                    if (!females.isEmpty()) {
                        orderProcessingList.add(females.remove(females.size()-1));
                    }
                }
                // Add the naAges list to the end of the orderProcessingList
                orderProcessingList.addAll(naAges);
                break;
            case "Libeccio":
                List<Resident> waitingListCopy = new ArrayList<>(waitingList);
                int dayNumber = getCurrentDay();
                System.out.println(dayNumber);
                int index = 0;
                while (!waitingListCopy.isEmpty()) {
                    int multiple = dayNumber * index;
                    multiple %= waitingListCopy.size();
                    orderProcessingList.add(waitingListCopy.remove(multiple));
                    index++;
                }
                break;
            case "Savage Garden":
                List<Resident> waitingListCopy2 = new ArrayList<>(waitingList);
                dayNumber = getCurrentDay();
                index = dayNumber - 1;
                while (!waitingListCopy2.isEmpty()) {
                    index %= waitingListCopy2.size();
                    orderProcessingList.add(waitingListCopy2.remove(index));
                    index++;
                }
                break;
            default:
                // Handle unknown restaurant
                break;
        }
        return orderProcessingList;
    }

    private static int getCurrentDay() {
        Random random = new Random();
        return random.nextInt(10)+1;
    }

    public static void main(String[] args) {

        // Load residents' information from the CSV file
        List<Resident> residents = loadResidents();

        // Randomly assign a food and restaurant to each resident
        Map<String, List<Resident>> waitingLists = assignFoodAndRestaurant(residents);

        // Prompt for restaurant selection
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of the restaurant (1-5):");
        int restaurantNumber = scanner.nextInt();

        String restaurant = getRestaurantByNumber(restaurantNumber);
        List<Resident> waitingList = getWaitingList(waitingLists, restaurant);
        List<Resident> orderProcessingList = generateOrderProcessingList(waitingList, restaurant);

        printWaitingList(waitingList, restaurant);
        printOrderProcessingList(orderProcessingList, restaurant);
    }

    private static String getRestaurantByNumber(int restaurantNumber) {
        switch (restaurantNumber) {
            case 1:
                return "Jade Garden";
            case 2:
                return "Cafe Deux Magots";
            case 3:
                return "Trattoria Trussardi";
            case 4:
                return "Libeccio";
            case 5:
                return "Savage Garden";
            default:
                return "";
        }
    }

    private static void printWaitingList(List<Resident> waitingList, String restaurant) {
        System.out.println("Restaurant: " + restaurant +"\n");
        System.out.println("Waiting List");
        System.out.println("+----+-------------------------+-----+--------+------------------------------------------+");
        System.out.println("| No | Name                    | Age | Gender | Order                                    |");
        System.out.println("+----+-------------------------+-----+--------+------------------------------------------+");
        int count = 0;
        for (Resident resident : waitingList) {
            if (resident.getRestaurant().equals(restaurant)) {
                count++;
                System.out.printf("| %-2d | %-24s| %-3s | %-6s | %-40s |\n", count, resident.getName(), resident.getAge(), resident.getGender(), resident.getMenu().getName());
            }
        }

        if (count == 0) {
            System.out.println("| No residents in the waiting list for " + restaurant);
            System.out.println("+----+-------------------------+-----+--------+------------------------------------------+");
        } else {
            System.out.println("+----+-------------------------+-----+--------+------------------------------------------+");
        }
    }


    private static void printOrderProcessingList(List<Resident> orderProcessingList, String restaurant) {
        System.out.println("\nOrder Processing List");
        System.out.println("+----+-------------------------+-----+--------+------------------------------------------+");
        System.out.println("| No | Name                    | Age | Gender | Order                                    |");
        System.out.println("+----+-------------------------+-----+--------+------------------------------------------+");
        int count = 0;
        for (Resident resident : orderProcessingList) {
            if (resident != null && restaurant.equals(resident.getRestaurant())) {
                count++;
                MenuItem menu = resident.getMenu();
                String menuName = (menu != null) ? menu.getName() : "Unknown";
                System.out.printf("| %-2d | %-24s| %-3s | %-6s | %-40s |\n", count, resident.getName(), resident.getAge(), resident.getGender(), menuName);
            }
        }
        if (count == 0) {
            System.out.println("| No residents in the order processing list for " + restaurant);
            System.out.println("+----+-------------------------+-----+--------+------------------------------------------+");
        } else {
            System.out.println("+----+-------------------------+-----+--------+------------------------------------------+");
        }
    }

    static class Resident {
        private String name;
        private String age;
        private String gender;
        private String restaurant;
        private MenuItem menu;

        public Resident(String name, String age, String gender) {
            this.name = name;
            this.age = age;
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public String getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public String getRestaurant() {
            return restaurant;
        }

        public void setRestaurant(String restaurant) {
            this.restaurant = restaurant;
        }

        public MenuItem getMenu() {
            return menu;
        }

        public void setMenu(MenuItem menuItem) {
            this.menu = menuItem;
        }
    }
}


