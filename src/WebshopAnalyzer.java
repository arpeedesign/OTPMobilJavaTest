import java.io.*;
import java.util.*;
import java.util.logging.*;

public class WebshopAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(WebshopAnalyzer.class.getName());
    private static final String CUSTOMERS_FILE_PATH = "customer.csv";
    private static final String PAYMENTS_FILE_PATH = "payments.csv";
    private static final String REPORT01_FILE_PATH = "report01.csv";
    private static final String REPORT02_FILE_PATH = "report02.csv";
    private static final String TOP_FILE_PATH = "top.csv";
    private static final String DELIMITER = ";";

    private Map<String, Customer> customers;
    private Map<String, Webshop> webshops;

    public WebshopAnalyzer() {
        customers = new HashMap<>();
        webshops = new HashMap<>();
        try {
            Handler fileHandler = new FileHandler("application.log");
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create log file", e);
        }
    }

    public class Customer {
        private String webshopId;
        private String customerId;
        private String name;
        private String address;
        private int totalAmount;

        public Customer(String webshopId, String customerId, String name, String address) {
            this.webshopId = webshopId;
            this.customerId = customerId;
            this.name = name;
            this.address = address;
            this.totalAmount = 0;
        }

        public String getWebshopId() {
            return webshopId;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public void addAmount(int amount) {
            totalAmount += amount;
        }

        public void addPayment(Payment payment) {
            int amount = payment.getAmount();
            addAmount(amount);
        }
    }

    public class Payment {
        private String webshopId;
        private String customerId;
        private String paymentMethod;
        private int amount;
        private String bankAccount;
        private String cardNumber;
        private String paymentDate;

        public Payment(String webshopId, String customerId, String paymentMethod, int amount,
                       String bankAccount, String cardNumber, String paymentDate) {
            this.webshopId = webshopId;
            this.customerId = customerId;
            this.paymentMethod = paymentMethod;
            this.amount = amount;
            this.bankAccount = bankAccount;
            this.cardNumber = cardNumber;
            this.paymentDate = paymentDate;
        }

        public String getWebshopId() {
            return webshopId;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public int getAmount() {
            return amount;
        }

        public String getBankAccount() {
            return bankAccount;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public String getPaymentDate() {
            return paymentDate;
        }
    }

    public class Webshop {
        private String id;
        private int cardTotal;
        private int transferTotal;

        public Webshop(String id) {
            this.id = id;
            this.cardTotal = 0;
            this.transferTotal = 0;
        }

        public String getId() {
            return id;
        }

        public int getCardTotal() {
            return cardTotal;
        }

        public int getTransferTotal() {
            return transferTotal;
        }

        public void addCardAmount(int amount) {
            cardTotal += amount;
        }

        public void addTransferAmount(int amount) {
            transferTotal += amount;
        }
    }

    public void loadDataFromCSV() {
        readCustomersFromCSV();
        readPaymentsFromCSV();
    }

/*    private void readCustomersFromCSV2() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(DELIMITER);
                if (fields.length == 4) {
                    String webshopId = fields[0];
                    String customerId = fields[1];
                    String name = fields[2];
                    String address = fields[3];
                    Customer customer = new Customer(webshopId, customerId, name, address);
                    customers.put(customerId, customer);
                    webshops.computeIfAbsent(webshopId, Webshop::new);
                } else {
                    LOGGER.log(Level.SEVERE, "Invalid customer data: " + line);
                }
            }
        }
    }*/
    private void readCustomersFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] fields = line.split(DELIMITER);
                    if (fields.length == 4) {
                        String webshopId = fields[0];
                        String customerId = fields[1];
                        String name = fields[2];
                        String address = fields[3];
                        Customer customer = new Customer(webshopId, customerId, name, address);
                        customers.put(customerId, customer);
                        webshops.computeIfAbsent(webshopId, Webshop::new);
                    } else {
                        LOGGER.log(Level.SEVERE, "Invalid customer data at line " + lineNumber + ": " + line);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error processing customer data at line " + lineNumber + ": " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading data from CSV files", e);
        }
    }
    private void readPaymentsFromCSV2() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(PAYMENTS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(DELIMITER);
                if (fields.length == 7) {
                    String webshopId = fields[0];
                    String customerId = fields[1];
                    String paymentMethod = fields[2];
                    int amount = Integer.parseInt(fields[3]);
                    String bankAccount = fields[4];
                    String cardNumber = fields[5];
                    String paymentDate = fields[6];
                    Payment payment = new Payment(webshopId, customerId, paymentMethod, amount, bankAccount, cardNumber, paymentDate);
                    Customer customer = customers.get(customerId);
                    if (customer != null) {
                        customer.addPayment(payment);
                        Webshop webshop = webshops.get(webshopId);
                        if (webshop != null) {
                            if (paymentMethod.equals("card")) {
                                webshop.addCardAmount(amount);
                            } else if (paymentMethod.equals("transfer")) {
                                webshop.addTransferAmount(amount);
                            }
                        } else {
                            LOGGER.log(Level.SEVERE, "Webshop not found for payment: " + line);
                        }
                    } else {
                        LOGGER.log(Level.SEVERE, "Customer not found for payment: " + line);
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Invalid payment data: " + line);
                }
            }
        }
    }
    private void readPaymentsFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PAYMENTS_FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] fields = line.split(DELIMITER);
                    if (fields.length == 7) {
                        String webshopId = fields[0];
                        String customerId = fields[1];
                        String paymentMethod = fields[2];
                        int amount = Integer.parseInt(fields[3]);
                        String bankAccount = fields[4];
                        String cardNumber = fields[5];
                        String paymentDate = fields[6];

                        if (paymentMethod.equals("card")) {
                            if (cardNumber.isEmpty() && !bankAccount.isEmpty()) {
                                LOGGER.log(Level.SEVERE, "Invalid payment data at line " + lineNumber + ": " + line);
                            } else {
                                // Process valid payment data for card payment
                                Payment payment = new Payment(webshopId, customerId, paymentMethod, amount, bankAccount, cardNumber, paymentDate);
                                Customer customer = customers.get(customerId);
                                if (customer != null) {
                                    customer.addPayment(payment);
                                    Webshop webshop = webshops.get(webshopId);
                                    if (webshop != null) {
                                        webshop.addCardAmount(amount);
                                    } else {
                                        LOGGER.log(Level.SEVERE, "Webshop not found for payment: " + line);
                                    }
                                } else {
                                    LOGGER.log(Level.SEVERE, "Customer not found for payment: " + line);
                                }
                            }
                        } else if (paymentMethod.equals("transfer")) {
                            if (bankAccount.isEmpty() && !cardNumber.isEmpty()) {
                                LOGGER.log(Level.SEVERE, "Invalid payment data at line " + lineNumber + ": " + line);
                            } else {
                                // Process valid payment data for transfer payment
                                Payment payment = new Payment(webshopId, customerId, paymentMethod, amount, bankAccount, cardNumber, paymentDate);
                                Customer customer = customers.get(customerId);
                                if (customer != null) {
                                    customer.addPayment(payment);
                                    Webshop webshop = webshops.get(webshopId);
                                    if (webshop != null) {
                                        webshop.addTransferAmount(amount);
                                    } else {
                                        LOGGER.log(Level.SEVERE, "Webshop not found for payment: " + line);
                                    }
                                } else {
                                    LOGGER.log(Level.SEVERE, "Customer not found for payment: " + line);
                                }
                            }
                        } else {
                            LOGGER.log(Level.SEVERE, "Invalid payment method at line " + lineNumber + ": " + line);
                        }
                    } else {
                        LOGGER.log(Level.SEVERE, "Invalid payment data at line " + lineNumber + ": " + line);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error processing payment data at line " + lineNumber + ": " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading data from CSV files", e);
        }
    }

    public void generateReport01() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT01_FILE_PATH))) {
            for (Customer customer : customers.values()) {
                int totalAmount = customer.getTotalAmount();
                writer.println(customer.getName() + DELIMITER + customer.getAddress() + DELIMITER + totalAmount);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error generating Report01", e);
        }
    }

    public void generateReport02() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT02_FILE_PATH))) {
            for (Webshop webshop : webshops.values()) {
                int cardTotal = webshop.getCardTotal();
                int transferTotal = webshop.getTransferTotal();
                writer.println(webshop.getId() + DELIMITER + cardTotal + DELIMITER + transferTotal);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error generating Report02", e);
        }
    }

    public void generateTopCustomers() {
        List<Customer> sortedCustomers = new ArrayList<>(customers.values());
        sortedCustomers.sort(Comparator.comparingInt(Customer::getTotalAmount).reversed());

        try (PrintWriter writer = new PrintWriter(new FileWriter(TOP_FILE_PATH))) {
            int count = Math.min(sortedCustomers.size(), 2);
            for (int i = 0; i < count; i++) {
                Customer customer = sortedCustomers.get(i);
                writer.println(customer.getName() + DELIMITER + customer.getAddress() + DELIMITER + customer.getTotalAmount());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error generating Top Customers", e);
        }
    }

}
