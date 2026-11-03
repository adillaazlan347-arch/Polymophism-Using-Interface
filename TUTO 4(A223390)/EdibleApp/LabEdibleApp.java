import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Swing UI for EdibleApp.
 * Uses: Edible, Fruit, Apple, Orange, Animal, Person (same folder).
 * Run: javac *.java  then  java LabEdibleApp
 */
public class LabEdibleApp extends JFrame {

    private static final Color BG = new Color(0xF6F8FB);
    private static final Color ACCENT = new Color(0x2F6FED);

    private final JComboBox<String> personBox = new JComboBox<>(new String[]{"Ali", "Sarah"});
    private final JComboBox<String> typeBox =
            new JComboBox<>(new String[]{"Animal", "Apple", "Orange", "Fruit"});
    private final JTextField itemField = new JTextField(14);
    private final JTextArea log = new JTextArea();
    private final JLabel status = new JLabel(" ");

    public LabEdibleApp() {
        super("Lab EdibleApp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(BG);

        // ---------- input ----------
        personBox.setEditable(true); // can type any person's name
        JPanel input = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        input.setOpaque(false);
        input.setBorder(BorderFactory.createTitledBorder("Who eats what?"));
        input.add(new JLabel("Person:"));
        input.add(personBox);
        input.add(new JLabel("Type:"));
        input.add(typeBox);
        input.add(new JLabel("Name:"));
        input.add(itemField);

        JButton eatBtn = new JButton("Eat");
        eatBtn.setBackground(ACCENT);
        eatBtn.setForeground(Color.WHITE);
        eatBtn.setOpaque(true);
        eatBtn.setBorderPainted(false);
        eatBtn.addActionListener(e -> eatFromInput());
        itemField.addActionListener(e -> eatFromInput());
        input.add(eatBtn);

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
        JButton demoBtn = new JButton("Run Demo (same as EdibleApp main)");
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

        root.add(input, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        setSize(680, 460);
        setMinimumSize(new Dimension(600, 380));
        setLocationRelativeTo(null);
    }

    /** Builds the chosen Edible (Animal, Apple, Orange or Fruit). */
    private Edible createEdible(String type, String name) {
        switch (type) {
            case "Animal":
                return new Animal(name);
            case "Apple":
                return new Apple(name);
            case "Orange":
                return new Orange(name);
            default:
                return new Fruit(name);
        }
    }

    private void eatFromInput() {
        String personName = String.valueOf(personBox.getEditor().getItem()).trim();
        String itemName = itemField.getText().trim();
        if (personName.isEmpty()) {
            status.setText("Please enter a person's name.");
            return;
        }
        if (itemName.isEmpty()) {
            status.setText("Please enter a name for the food.");
            itemField.requestFocusInWindow();
            return;
        }
        status.setText(" ");
        Person person = new Person(personName);
        Edible food = createEdible((String) typeBox.getSelectedItem(), itemName);
        log.append(person.eating(food) + "\n");
        itemField.setText("");
        itemField.requestFocusInWindow();
    }

    /** Same logic as EdibleApp.main: Ali eats animals, Sarah eats everything else. */
    private void runDemo() {
        status.setText(" ");
        Edible[] objects = {new Animal("Chicken"), new Apple("Red Apple"), new Orange("Navel Orange")};
        Person p1 = new Person("Ali");
        Person p2 = new Person("Sarah");
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof Animal) {
                log.append(p1.eating(objects[i]) + "\n");
            } else {
                log.append(p2.eating(objects[i]) + "\n");
            }
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
        SwingUtilities.invokeLater(() -> new LabEdibleApp().setVisible(true));
    }
}