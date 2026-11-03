public class MobileApp {
    public static void main(String[] args) {
        MobileDevice m1 = new SmartPhone("Samsung", "S20", "Galaxy");
        MobileDevice m2 = new BasicPhone("Sonny Ericson", "Walkman");

        m1.setStation("Hot FM");
        m1.setFrequency(97.60);
        m2.setStation("Fly FM");
        m2.setFrequency(109.8);

        m1.printDetails();
        System.out.println("Station:" + m1.station);
        System.out.println("Frqeuency:" + m1.frequency);

        m2.printDetails();
        System.out.println("Station:" + m2.station);
        System.out.println("Frequency:" + m2.frequency);
    }
}