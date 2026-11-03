import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Swing UI for DataSetApp.
 * Uses: Measurable, DataSet, BankAccount, Coin, Student (same package / folder).
 * Run: javac *.java  then  java LoginUI
 */
public class LoginUI extends JFrame {

    // ---------- colours ----------
    private static final Color BG = new Color(0xF6F8FB);
    private static final Color ACCENT = new Color(0x2F6FED);
    private static final Color HIGHLIGHT = new Color(0xFFF3C4);

    public LoginUI() {
        super("DataSet App - Measurable Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 14f));

        // ----- Bank accounts: only a balance is needed (account number is auto) -----
        List<Supplier<Measurable>> bankSample = new ArrayList<>();
        bankSample.add(() -> new BankAccount(0));
        bankSample.add(() -> new BankAccount(10000));
        bankSample.add(() -> new BankAccount(2000));
        tabs.addTab("Bank Accounts", new MeasurePanel(
                "Bank Account", null, "Balance (RM)", "Average Balance", "Highest Balance",
                new DecimalFormat("#,##0.00"),
                (name, v) -> new BankAccount(v), bankSample));

        // ----- Coins -----
        List<Supplier<Measurable>> coinSample = new ArrayList<>();
        coinSample.add(() -> new Coin(0.25, "Quarter"));
        coinSample.add(() -> new Coin(0.1, "Dime"));
        coinSample.add(() -> new Coin(0.05, "Nickel"));
        tabs.addTab("Coins", new MeasurePanel(
                "Coin", "Coin name", "Value", "Average Coin Value", "Highest Coin Value",
                new DecimalFormat("0.00"),
                (name, v) -> new Coin(v, name), coinSample));

        // ----- Students -----
        List<Supplier<Measurable>> studentSample = new ArrayList<>();
        studentSample.add(() -> new Student("Ali", 157.50));
        studentSample.add(() -> new Student("Alex", 165.00));
        studentSample.add(() -> new Student("Maria", 147.00));
        tabs.addTab("Students", new MeasurePanel(
                "Student", "Student name", "Height (cm)", "Average Height", "Tallest Height",
                new DecimalFormat("0.00"),
                (name, v) -> new Student(name, v), studentSample));

        setContentPane(tabs);
        setSize(640, 560);
        setMinimumSize(new Dimension(560, 480));
        setLocationRelativeTo(null);
    }

    // =====================================================================
    //  One reusable panel: works with ANY Measurable through DataSet
    // =====================================================================
    private static class MeasurePanel extends JPanel {

        interface Factory {
            Measurable create(String name, double value);
        }

        private final boolean needsName;
        private final DecimalFormat df;
        private final Factory factory;
        private final List<Supplier<Measurable>> sample;

        private DataSet data = new DataSet();
        private final List<Measurable> items = new ArrayList<>();

        private final JTextField nameField = new JTextField(12);
        private final JTextField valueField = new JTextField(8);
        private final DefaultTableModel model;
        private final JTable table;
        private final JLabel countLbl = value("0");
        private final JLabel avgLbl = value("-");
        private final JLabel maxLbl = value("-");
        private final JLabel maxNameLbl = value("-");
        private final JLabel status = new JLabel(" ");

        MeasurePanel(String kind, String nameLabel, String valueLabel,
                     String avgTitle, String maxTitle, DecimalFormat df,
                     Factory factory, List<Supplier<Measurable>> sample) {
            this.needsName = nameLabel != null;
            this.df = df;
            this.factory = factory;
            this.sample = sample;

            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(14, 14, 14, 14));
            setBackground(BG);

            // ---------- input row ----------
            JPanel input = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            input.setOpaque(false);
            input.setBorder(BorderFactory.createTitledBorder("Add " + kind));
            if (needsName) {
                input.add(new JLabel(nameLabel + ":"));
                input.add(nameField);
            } else {
                JLabel hint = new JLabel("(account number is auto-assigned)");
                hint.setForeground(Color.GRAY);
                input.add(hint);
            }
            input.add(new JLabel(valueLabel + ":"));
            input.add(valueField);

            JButton addBtn = new JButton("Add");
            addBtn.setBackground(ACCENT);
            addBtn.setForeground(Color.WHITE);
            addBtn.setOpaque(true);
            addBtn.setBorderPainted(false);
            addBtn.addActionListener(e -> addFromFields());
            input.add(addBtn);
            valueField.addActionListener(e -> addFromFields());
            nameField.addActionListener(e -> valueField.requestFocusInWindow());

            // ---------- table ----------
            model = new DefaultTableModel(new String[]{"#", "Name", valueLabel}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            table = new JTable(model);
            table.setRowHeight(24);
            table.getTableHeader().setReorderingAllowed(false);
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object v,
                        boolean sel, boolean foc, int row, int col) {
                    Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                    boolean isMax = !items.isEmpty() && items.get(row) == data.getMax();
                    if (!sel) {
                        c.setBackground(isMax ? HIGHLIGHT : Color.WHITE);
                    }
                    c.setFont(c.getFont().deriveFont(isMax ? Font.BOLD : Font.PLAIN));
                    return c;
                }
            });
            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(BorderFactory.createTitledBorder("Items in DataSet  (highlighted = max)"));

            // ---------- results ----------
            JPanel results = new JPanel(new GridLayout(4, 2, 8, 6));
            results.setBorder(BorderFactory.createTitledBorder("DataSet Results"));
            results.setBackground(Color.WHITE);
            results.add(new JLabel("Number of items:"));
            results.add(countLbl);
            results.add(new JLabel(avgTitle + ":"));
            results.add(avgLbl);
            results.add(new JLabel(maxTitle + ":"));
            results.add(maxLbl);
            String nameTitle = kind.equals("Student") ? "Tallest Student's Name"
                    : kind.equals("Coin") ? "Highest Coin's Name"
                    : "Highest Account";
            results.add(new JLabel(nameTitle + ":"));
            results.add(maxNameLbl);

            // ---------- buttons ----------
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            buttons.setOpaque(false);
            JButton sampleBtn = new JButton("Load Sample Data");
            sampleBtn.addActionListener(e -> loadSample());
            JButton clearBtn = new JButton("Clear");
            clearBtn.addActionListener(e -> clearAll());
            buttons.add(sampleBtn);
            buttons.add(clearBtn);
            status.setForeground(new Color(0xB00020));
            buttons.add(status);

            JPanel south = new JPanel(new BorderLayout(0, 8));
            south.setOpaque(false);
            south.add(results, BorderLayout.CENTER);
            south.add(buttons, BorderLayout.SOUTH);

            add(input, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
            add(south, BorderLayout.SOUTH);
        }

        private static JLabel value(String text) {
            JLabel l = new JLabel(text);
            l.setFont(l.getFont().deriveFont(Font.BOLD, 14f));
            l.setForeground(new Color(0x1B3A8A));
            return l;
        }

        // ---------- actions ----------
        private void addFromFields() {
            String name = nameField.getText().trim();
            if (needsName && name.isEmpty()) {
                status.setText("Please enter a name.");
                nameField.requestFocusInWindow();
                return;
            }
            double v;
            try {
                v = Double.parseDouble(valueField.getText().trim());
            } catch (NumberFormatException ex) {
                status.setText("Value must be a number.");
                valueField.requestFocusInWindow();
                return;
            }
            if (v < 0) {
                status.setText("Value cannot be negative.");
                return;
            }
            status.setText(" ");
            addItem(factory.create(name, v));
            nameField.setText("");
            valueField.setText("");
            (needsName ? nameField : valueField).requestFocusInWindow();
        }

        private void addItem(Measurable m) {
            data.add(m);
            items.add(m);
            model.addRow(new Object[]{items.size(), m.getName(), df.format(m.getMeasure())});
            refresh();
        }

        private void loadSample() {
            clearAll();
            for (Supplier<Measurable> s : sample) {
                addItem(s.get());
            }
        }

        private void clearAll() {
            data = new DataSet();
            items.clear();
            model.setRowCount(0);
            status.setText(" ");
            refresh();
        }

        private void refresh() {
            countLbl.setText(String.valueOf(items.size()));
            if (items.isEmpty()) {
                avgLbl.setText("-");
                maxLbl.setText("-");
                maxNameLbl.setText("-");
            } else {
                Measurable max = data.getMax();
                avgLbl.setText(df.format(data.getAverage()));
                maxLbl.setText(df.format(max.getMeasure()));
                maxNameLbl.setText(max.getName());
            }
            table.repaint();
        }
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
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}