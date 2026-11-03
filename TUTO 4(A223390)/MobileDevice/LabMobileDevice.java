import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Swing UI for MobileDevice.
 * Uses: RadioTuner, MobileDevice, SmartPhone, BasicPhone (same folder).
 * Run: javac *.java  then  java LabMobileDevice
 */
public class LabMobileDevice extends JFrame {

    private static final Color BG = new Color(0xF6F8FB);
    private static final Color ACCENT = new Color(0x2F6FED);

    private final JComboBox<String> kindBox = new JComboBox<>(new String[]{"SmartPhone", "BasicPhone"});
    private final JTextField manufacturerField = new JTextField(12);
    private final JTextField modelField = new JTextField(12);
    private final JTextField typeField = new JTextField(12);
    private final JLabel typeLabel = new JLabel("Type:");
    private final JTextField stationField = new JTextField(12);
    private final JTextField frequencyField = new JTextField(8);
    private final JTextArea log = new JTextArea();
    private final JLabel status = new JLabel(" ");

    public LabMobileDevice() {
        super("Lab MobileDevice");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(BG);

        // ---------- input form ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createTitledBorder("Create a mobile device"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        addRow(form, g, 0, new JLabel("Device:"), kindBox, new JLabel("Manufacturer:"), manufacturerField);
        addRow(form, g, 1, new JLabel("Model:"), modelField, typeLabel, typeField);
        addRow(form, g, 2, new JLabel("Station:"), stationField, new JLabel("Frequency (MHz):"), frequencyField);

        kindBox.addActionListener(e -> updateTypeVisibility());
        updateTypeVisibility();

        JButton createBtn = new JButton("Create & Show Details");
        createBtn.setBackground(ACCENT);
        createBtn.setForeground(Color.WHITE);
        createBtn.setOpaque(true);
        createBtn.setBorderPainted(false);
        createBtn.addActionListener(e -> createFromInput());
        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 4;
        form.add(createBtn, g);

        // ---------- output ----------
        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        log.setMargin(new Insets(8, 8, 8, 8));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(BorderFactory.createTitledBorder("Output"));

        // ---------- buttons ----------
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        JButton demoBtn = new JButton("Run Demo (same as MobileApp main)");
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
        setSize(700, 560);
        setMinimumSize(new Dimension(660, 480));
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

    /** The extra "Type" field is only used by SmartPhone. */
    private void updateTypeVisibility() {
        boolean smart = "SmartPhone".equals(kindBox.getSelectedItem());
        typeLabel.setVisible(smart);
        typeField.setVisible(smart);
    }

    /** Runs code that prints with System.out (printDetails) and returns what it printed. */
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

    private void show(MobileDevice m) {
        log.append(capture(m::printDetails));
        log.append("Station: " + m.station + "\n");
        log.append("Frequency: " + m.frequency + "\n\n");
    }

    private void createFromInput() {
        String manufacturer = manufacturerField.getText().trim();
        String model = modelField.getText().trim();
        String type = typeField.getText().trim();
        String station = stationField.getText().trim();
        boolean smart = "SmartPhone".equals(kindBox.getSelectedItem());

        if (manufacturer.isEmpty() || model.isEmpty() || station.isEmpty()
                || (smart && type.isEmpty())) {
            status.setText("Please fill in all fields.");
            return;
        }
        double frequency;
        try {
            frequency = Double.parseDouble(frequencyField.getText().trim());
        } catch (NumberFormatException ex) {
            status.setText("Frequency must be a number.");
            frequencyField.requestFocusInWindow();
            return;
        }
        if (frequency <= 0) {
            status.setText("Frequency must be greater than 0.");
            return;
        }
        status.setText(" ");

        MobileDevice device = smart
                ? new SmartPhone(manufacturer, model, type)
                : new BasicPhone(manufacturer, model);
        device.setStation(station);
        device.setFrequency(frequency);
        show(device);
    }

    /** Same data as MobileApp.main. */
    private void runDemo() {
        status.setText(" ");
        MobileDevice m1 = new SmartPhone("Samsung", "S20", "Galaxy");
        MobileDevice m2 = new BasicPhone("Sonny Ericson", "Walkman");

        m1.setStation("Hot FM");
        m1.setFrequency(97.60);
        m2.setStation("Fly FM");
        m2.setFrequency(109.8);

        show(m1);
        show(m2);
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
        SwingUtilities.invokeLater(() -> new LabMobileDevice().setVisible(true));
    }
}