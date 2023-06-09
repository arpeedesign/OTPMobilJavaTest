public class Main {
    public static void main(String[] args) {
        WebshopAnalyzer analyzer = new WebshopAnalyzer();
        analyzer.loadDataFromCSV();
        analyzer.generateReport01();
        analyzer.generateReport02();
        analyzer.generateTopCustomers();
    }
}