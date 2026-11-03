import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Swing UI for PriceApp.
 * Uses: Price, Product, SmartPhone, Laptop, Car, Customer (same folder).
 * Run: javac *.java  then  java LabPrice
 */
public class LabPrice extends JFrame {

    private static final Color BG = new Color(0xF6F8FB);
    private static final Color ACCENT = new Color(0x2F6FED);

    private final JComboBox<String> kindBox = new JComboBox<>(new String[]{"SmartPhone", "Laptop", "Car"});
    private final JLabel itemLabel = new JLabel("Product name:");
    private final JTextField itemField = new JTextField(12);
    private final JTextField priceField = new JTextField(10);
    private final JTextField customerField = new JTextField(12);
    private final JTextField salaryField = new JTextField(10);
    private final JTextArea log = new JTextArea();
    private final JLabel status = new JLabel(" ");

    public LabPrice() {
        super("Lab Price");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(BG);

        // ---------- input form ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createTitledBorder("Can the customer buy it?"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        addRow(form, g, 0, new JLabel("Item:"), kindBox, itemLabel, itemField);
        addRow(form, g, 1, new JLabel("Price (RM):"), priceField, new JLabel(" "), new JLabel(" "));
        addRow(form, g, 2, new JLabel("Customer name:"), customerField,
                new JLabel("Salary (RM):"), salaryField);

        kindBox.addActionListener(e -> updateLabels());

        JButton checkBtn = new JButton("Check Price");
        checkBtn.setBackground(ACCENT);
        checkBtn.setForeground(Color.WHITE);
        checkBtn.setOpaque(true);
        checkBtn.setBorderPainted(false);
        checkBtn.addActionListener(e -> checkFromInput());
        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 4;
        form.add(checkBtn, g);

        // ---------- output ----------
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        log.setMargin(new Insets(8, 8, 8, 8));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(BorderFactory.createTitledBorder("Output"));

        // ---------- buttons ----------
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        JButton demoBtn = new JButton("Run Demo (same as PriceApp main)");
        demoBtn.addActionListener(e -> runDemo());
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            log.setText("");
            status.setText(" ");
        });
        status.setForeground(new Color(0xB00020));
        buttons.add(demoBtn);
        buttons.add(clearBtn);
        buttons.add(status);

        root.add(form, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        setSize(720, 560);
        setMinimumSize(new Dimension(680, 480));
        setLocationRelativeTo(null);
    }

    private static void addRow(JPanel p, GridBagConstraints g, int row,
                               JComponent l1, JComponent f1, JComponent l2, JComponent f2) {
        g.gridy = row;
        g.gridwidth = 1;
        g.gridx = 0;
        p.add(l1, g);
        g.gridx = 1;
        p.add(f1, g);
        g.gridx = 2;
        p.add(l2, g);
        g.gridx = 3;
        p.add(f2, g);
    }

    private void updateLabels() {
        boolean car = "Car".equals(kindBox.getSelectedItem());
        itemLabel.setText(car ? "Car brand:" : "Product name:");
    }

    /** Runs code that prints with System.out (getPrice) and returns what it printed. */
    private static String capture(Runnable action) {
        PrintStream old = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true));
        try {
            action.run();
        } finally {
            System.setOut(old);
        }
        return buffer.toString();
    }

    /** Works with any Price (Product subclasses and Car) - polymorphism. */
    private void show(Price item, Customer customer) {
        log.append(capture(() -> item.getPrice(customer)));
        log.append("\n");
    }

    private void checkFromInput() {
        String kind = (String) kindBox.getSelectedItem();
        String itemName = itemField.getText().trim();
        String customerName = customerField.getText().trim();

        if (itemName.isEmpty() || customerName.isEmpty()) {
            status.setText("Please fill in all names.");
            return;
        }
        double price;
        double salary;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            salary = Double.parseDouble(salaryField.getText().trim());
        } catch (NumberFormatException ex) {
            status.setText("Price and salary must be numbers.");
            return;
        }
        if (price < 0 || salary < 0) {
            status.setText("Price and salary cannot be negative.");
            return;
        }
        status.setText(" ");

        Price item;
        if ("SmartPhone".equals(kind)) {
            item = new SmartPhone("SmartPhone", itemName, price);
        } else if ("Laptop".equals(kind)) {
            item = new Laptop("Laptop", itemName, price);
        } else {
            item = new Car(itemName, price);
        }
        show(item, new Customer(customerName, salary));
    }

    /** Same data as PriceApp.main. */
    private void runDemo() {
        status.setText(" ");
        SmartPhone sp = new SmartPhone("SmartPhone", "Iphone 14 ProMax", 5500.00);
        Laptop lp = new Laptop("Laptop", "Lenovo Yoga", 6000.00);
        Car c = new Car("BMW", 200000);

        Customer c1 = new Customer("Sally", 3000.00);
        Customer c2 = new Customer("Aaron", 8000.00);
        Customer c3 = new Customer("Simon", 10000.00);

        show(sp, c1);
        show(lp, c2);
        show(c, c3);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // fall back to default look and feel
        }
        SwingUtilities.invokeLater(() -> new LabPrice().setVisible(true));
    }
}