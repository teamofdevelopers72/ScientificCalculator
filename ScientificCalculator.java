import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class ScientificCalculator extends JFrame {
    private JTextField display;
    private double result = 0;
    private String lastOperation = "";
    private double memory = 0;
    private boolean newInput = true;
    private String currentInput = "0";
    private StringBuilder expression = new StringBuilder();
    private DecimalFormat df = new DecimalFormat("#.##########");
    private boolean isDarkTheme = true;
    private JPanel buttonPanel;

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setSize(300, 450); // Initial size
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        display = new JTextField();
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBackground(new Color(40, 40, 40)); // Darker gray like Apple
        display.setForeground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(display, BorderLayout.NORTH);

        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(28, 28, 28)); // Very dark gray like Apple background
        add(buttonPanel, BorderLayout.CENTER);

        String[] buttons = {
            "MC", "MR", "M+", "C",
            "C", "√", "x³", "%",
            "sin", "cos", "tan", "log",
            "ln", "e^x", "10^x", "nPr",
            "mod", "|x|", "nCr", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "", "="
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        int row = 0, col = 0;
        for (int i = 0; i < buttons.length; i++) {
            String buttonText = buttons[i];
            JButton btn = new JButton(buttonText);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setBackground(new Color(60, 60, 60)); // Light gray for buttons, Apple-inspired
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            btn.setUI(new RoundedButtonUI());
            btn.addActionListener(e -> handleInput(buttonText));

            gbc.gridx = col;
            gbc.gridy = row;
            gbc.gridwidth = (buttonText.equals("=") && col == 3) ? 1 : 1;
            buttonPanel.add(btn, gbc);

            col++;
            if (col > 3) {
                col = 0;
                row++;
            }
            if (buttonText.equals("=")) {
                gbc.gridx = 2;
                gbc.gridwidth = 2;
                buttonPanel.add(btn, gbc);
                break;
            }
        }

        // Menu Bar for Theme
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(28, 28, 28)); // Match background
        JMenu themeMenu = new JMenu("Theme");
        themeMenu.setForeground(Color.WHITE);
        JMenuItem light = new JMenuItem("Light Mode");
        light.setForeground(Color.WHITE);
        JMenuItem dark = new JMenuItem("Dark Mode");
        dark.setForeground(Color.WHITE);
        light.addActionListener(e -> {
            isDarkTheme = false;
            updateTheme();
        });
        dark.addActionListener(e -> {
            isDarkTheme = true;
            updateTheme();
        });
        themeMenu.add(light);
        themeMenu.add(dark);
        menuBar.add(themeMenu);
        setJMenuBar(menuBar);

        updateTheme();
        setupKeyBindings();

        // Resize listener for scaling
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeFontsAndButtons();
            }
        });

        setVisible(true);
    }

    private void resizeFontsAndButtons() {
        int width = getWidth();
        int height = getHeight();

        // Ensure minimum window size for usability
        if (width < 200 || height < 300) {
            setSize(Math.max(200, width), Math.max(300, height));
            return;
        }

        // Display size and font
        int displayHeight = Math.max(40, Math.min(80, (int) (height * 0.1)));
        display.setPreferredSize(new Dimension(width, displayHeight));
        int displayFontSize = Math.max(14, Math.min(30, (int) (height * 0.03)));
        display.setFont(new Font("Arial", Font.BOLD, displayFontSize));

        // Button size and font
        int buttonWidth = Math.max(40, Math.min(80, (int) (width * 0.05)));
        int buttonHeight = Math.max(25, Math.min(50, (int) (height * 0.05)));
        int buttonFontSize = Math.max(10, Math.min(18, (int) (height * 0.02)));

        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setFont(new Font("Arial", Font.BOLD, buttonFontSize));
                if (btn.getText().equals("=")) {
                    btn.setPreferredSize(new Dimension(buttonWidth * 2, buttonHeight));
                } else {
                    btn.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                }
            }
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
        revalidate();
        repaint();
    }

    private void setupKeyBindings() {
        InputMap im = display.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = display.getActionMap();

        // Digit bindings
        for (int i = 0; i <= 9; i++) {
            int digit = i;
            im.put(KeyStroke.getKeyStroke((char) ('0' + digit)), "digit" + digit);
            am.put("digit" + digit, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    handleInput(String.valueOf(digit));
                }
            });
        }

        // Operation bindings
        im.put(KeyStroke.getKeyStroke('+'), "add");
        am.put("add", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput("+");
            }
        });

        im.put(KeyStroke.getKeyStroke('-'), "subtract");
        am.put("subtract", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput("-");
            }
        });

        im.put(KeyStroke.getKeyStroke('*'), "multiply");
        am.put("multiply", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput("*");
            }
        });

        im.put(KeyStroke.getKeyStroke('/'), "divide");
        am.put("divide", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput("/");
            }
        });

        im.put(KeyStroke.getKeyStroke('!'), "factorial");
        am.put("factorial", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput("!");
            }
        });

        im.put(KeyStroke.getKeyStroke('^'), "power");
        am.put("power", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput("^");
            }
        });

        // Equal and Enter bindings
        im.put(KeyStroke.getKeyStroke('='), "equals");
        am.put("equals", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput("=");
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "equals"); // Bind Enter to "="
        am.put("equals", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput("=");
            }
        });

        // Existing bindings
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace");
        am.put("backspace", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    display.setText(expression.toString() + currentInput);
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clear");
        am.put("clear", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput("C");
            }
        });

        im.put(KeyStroke.getKeyStroke('.'), "dot");
        am.put("dot", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleInput(".");
            }
        });
    }

    private void updateTheme() {
        Color bgColor = isDarkTheme ? new Color(28, 28, 28) : new Color(245, 245, 245); // Apple dark gray
        Color fgColor = isDarkTheme ? Color.WHITE : Color.BLACK;
        Color btnBg = isDarkTheme ? new Color(60, 60, 60) : new Color(220, 220, 220); // Apple button gray
        Color displayBg = isDarkTheme ? new Color(40, 40, 40) : Color.WHITE; // Darker display
        Color displayFg = isDarkTheme ? Color.WHITE : Color.BLACK;

        getContentPane().setBackground(bgColor);
        buttonPanel.setBackground(bgColor);
        display.setBackground(displayBg);
        display.setForeground(displayFg);

        for (Component c : buttonPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setBackground(btnBg);
                c.setForeground(fgColor);
            }
        }

        // Update menu bar and menu items
        if (isDarkTheme) {
            getJMenuBar().setBackground(bgColor);
            for (Component comp : getJMenuBar().getComponents()) {
                if (comp instanceof JMenu) {
                    ((JMenu) comp).setForeground(fgColor);
                    for (Component item : ((JMenu) comp).getMenuComponents()) {
                        if (item instanceof JMenuItem) {
                            ((JMenuItem) item).setForeground(fgColor);
                        }
                    }
                }
            }
        }

        repaint();
    }

    private void handleInput(String input) {
        if (input.equals("C")) {
            currentInput = "";
            expression.setLength(0);
            display.setText("");
        } else if (input.equals("=") || "+-*/%mod".contains(input) || input.equals("^") || input.equals("!")) {
            handleOperation(input);
        } else if (input.equals("MC") || input.equals("MR") || input.equals("M+") || input.equals("M-")) {
            handleMemory(input);
        } else if (input.equals("sin") || input.equals("cos") || input.equals("tan") || input.equals("√") ||
                   input.equals("log") || input.equals("ln") || input.equals("x³") || input.equals("e^x") || input.equals("10^x") ||
                   input.equals("|x|") || input.equals("±")) {
            handleFunction(input);
        } else {
            handleDigit(input);
        }
    }

    private void handleDigit(String digit) {
        if (newInput) {
            currentInput = digit.equals(".") ? "0." : digit;
            newInput = false;
        } else {
            currentInput += digit;
        }
        display.setText(expression.toString() + currentInput);
    }

    private void handleOperation(String op) {
        double num = parseDouble(currentInput);

        if (!lastOperation.isEmpty() && !op.equals("=")) {
            result = performCalculation(result, num, lastOperation);
            expression.append(" ").append(currentInput).append(" ").append(op);
        } else if (!lastOperation.isEmpty() && op.equals("=")) {
            result = performCalculation(result, num, lastOperation);
            expression.append(" ").append(currentInput).append(" = ");
            display.setText(expression.toString() + df.format(result));
            expression.setLength(0);
            lastOperation = "";
            currentInput = df.format(result);
            newInput = true;
            return;
        } else {
            result = num;
            expression.append(currentInput).append(" ").append(op);
        }

        lastOperation = op;
        display.setText(expression.toString());
        currentInput = "";
        newInput = true;
    }

    private void handleFunction(String func) {
        double val = parseDouble(currentInput);
        double res = 0;

        switch (func) {
            case "sin": res = Math.sin(Math.toRadians(val)); break;
            case "cos": res = Math.cos(Math.toRadians(val)); break;
            case "tan": res = Math.tan(Math.toRadians(val)); break;
            case "√": res = Math.sqrt(val); break;
            case "log": res = Math.log10(val); break;
            case "ln": res = Math.log(val); break;
            case "x³": res = val * val * val; break;
            case "e^x": res = Math.exp(val); break;
            case "10^x": res = Math.pow(10, val); break;
            case "|x|": res = Math.abs(val); break;
            case "±": res = -val; break;
        }

        currentInput = df.format(res);
        display.setText(expression.toString() + currentInput);
        newInput = true;
    }

    private void handleMemory(String action) {
        double current = parseDouble(currentInput);
        switch (action) {
            case "MC": memory = 0; break;
            case "MR": currentInput = df.format(memory); display.setText(currentInput); break;
            case "M+": memory += current; break;
            case "M-": memory -= current; break;
        }
    }

    private double performCalculation(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return b != 0 ? a / b : Double.NaN;
            case "%": return a * b / 100;
            case "mod": return a % b;
            case "!": return factorial(a);
            case "^": return Math.pow(a, b);
            default: return b;
        }
    }

    private double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0;
        }
    }

    private double factorial(double n) {
        if (n < 0 || n != (int) n) return Double.NaN;
        if (n > 20) return Double.POSITIVE_INFINITY;
        double result = 1;
        for (int i = 2; i <= (int) n; i++) result *= i;
        return result;
    }

    static class RoundedButtonUI extends BasicButtonUI {
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
    }

    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        int yOffset = b.getModel().isPressed() ? 2 : 0;
        paintBackground(g, b, yOffset); // Pass b to paintBackground
        super.paint(g, c);
    }

    private void paintBackground(Graphics g, AbstractButton b, int yOffset) { // Updated parameter
        Dimension size = b.getSize();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(b.getBackground());
        g.fillRoundRect(0, yOffset, size.width, size.height - yOffset, 10, 10);
        if (b.getModel().isPressed()) {
            g.setColor(new Color(50, 50, 50)); // Darker shade when pressed
            g.fillRoundRect(0, yOffset, size.width, size.height - yOffset, 10, 10);
        }
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ScientificCalculator());
    }
}