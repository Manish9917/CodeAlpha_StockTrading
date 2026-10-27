import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.TableRowSorter;

public class Main {

    private static final Color BACKGROUND = new Color(13, 25, 43);
    private static final Color PANEL_COLOR = new Color(20, 37, 60);
    private static final Color ACCENT = new Color(0, 200, 150);
    private static final Color TEXT_COLOR = new Color(235, 242, 255);
    private static final Color MUTED_TEXT = new Color(170, 190, 220);

    private static StockMarket stockMarket;
    private static DefaultTableModel tableModel;
    private static Portfolio portfolio;
    private static JLabel balanceLabel;
    private static JFrame mainFrame;
    private static JTable stockTable;
    private static JButton buyButton;
    private static JScrollPane rightScrollPane;
    private static String loggedInUsername = "";

    private static class UserIcon implements Icon {

        private final Color iconColor;

        public UserIcon(Color iconColor) {
            this.iconColor = iconColor;
        }

        @Override
        public int getIconWidth() {
            return 22;
        }

        @Override
        public int getIconHeight() {
            return 22;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics,
                int x, int y) {

            Graphics2D g2 = (Graphics2D) graphics.create();

            g2.setColor(iconColor);
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Person's head
            g2.fillOval(x + 8, y + 2, 7, 7);

            // Person's shoulders and body
            g2.fillRoundRect(x + 4, y + 11, 15, 9, 7, 7);

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }

    public static void openDashboard(String username) {
        SwingUtilities.invokeLater(() -> {
            loggedInUsername = username;

            stockMarket = new StockMarket();
            portfolio = Portfolio.loadFromFile(username);
            createGUI();

            mainFrame.setVisible(true);
        });
    }

    private static void createGUI() {

        mainFrame = new JFrame("CodeAlpha | Stock Trading Platform");
        JFrame frame = mainFrame;

        frame.setSize(1200, 750);
        frame.setMinimumSize(new Dimension(950, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // ================= TOP HEADER =================
        JPanel header = new JPanel(new BorderLayout(25, 0));
        header.setBackground(new Color(7, 24, 48));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(28, 67, 108)),
                BorderFactory.createEmptyBorder(12, 22, 12, 22)));

        // Left side: Brand
        JPanel brandPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        brandPanel.setOpaque(false);

        JLabel title = new JLabel("▥  CodeAlpha");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 23));

        JLabel brandSubtitle = new JLabel("     Stock Trading Platform");
        brandSubtitle.setForeground(new Color(165, 195, 235));
        brandSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        brandPanel.add(title);
        brandPanel.add(brandSubtitle);

        // Center: Search field
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setForeground(new Color(165, 195, 235));
        searchField.setBackground(new Color(10, 30, 56));
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(38, 77, 120)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        String placeholder = "Search by symbol or company name...";
        Color placeholderColor = new Color(165, 195, 235);
        Color normalTextColor = Color.WHITE;

        // Show placeholder initially
        searchField.setText(placeholder);
        searchField.setForeground(placeholderColor);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(normalTextColor);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(placeholderColor);
                }
            }
        });

        // Right side: Balance
        balanceLabel = new JLabel(
                "Cash Balance: $"
                        + String.format("%.2f", portfolio.getCashBalance()));
        balanceLabel.setForeground(ACCENT);
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        accountPanel.setOpaque(false);
        accountPanel.add(balanceLabel);

        // Logged-in username
        JLabel usernameLabel = new JLabel(
                loggedInUsername,
                new UserIcon(Color.WHITE),
                JLabel.LEFT);

        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setIconTextGap(8);

        accountPanel.add(usernameLabel);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        accountPanel.add(usernameLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(14, 39, 69));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createLineBorder(
                new Color(38, 77, 120)));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutButton.addActionListener(e -> {
            portfolio.saveToFile(loggedInUsername);
            frame.dispose();
            new Login().setVisible(true);
        });

        accountPanel.add(logoutButton);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(searchField, BorderLayout.CENTER);
        header.add(accountPanel, BorderLayout.EAST);

        // ================= LEFT SIDEBAR =================
        JPanel sidebar = new JPanel(new GridLayout(8, 1, 0, 8));
        sidebar.setBackground(new Color(8, 25, 47));
        sidebar.setPreferredSize(new Dimension(205, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));

        String[] menuItems = {
                "⌂   Market Overview",
                "🛒  Buy Stocks",
                "⇄   Sell Stocks",
                "◉   My Portfolio",
                "◷   Transaction History",
                "▥   Performance",
                "⚙   Settings",
                "⏻   Exit"
        };

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            button.setForeground(new Color(205, 220, 245));
            button.setBackground(new Color(10, 29, 53));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 5));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Hover effect
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(new Color(15, 82, 170));
                    button.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (item.contains("Market Overview")) {
                        button.setBackground(new Color(15, 82, 170));
                        button.setForeground(Color.WHITE);
                    } else {
                        button.setBackground(new Color(10, 29, 53));
                        button.setForeground(new Color(205, 220, 245));
                    }
                }
            });

            if (item.contains("Market Overview")) {
                button.setBackground(new Color(15, 82, 170));
                button.setForeground(Color.WHITE);
            }

            if (item.contains("My Portfolio")) {
                button.addActionListener(e -> showPortfolio());
            }

            if (item.contains("Sell Stocks")) {
                button.addActionListener(e -> sellSelectedStock());
            }

            if (item.contains("Transaction History")) {
                button.addActionListener(e -> showTransactionHistory());
            }

            if (item.contains("Exit")) {
                button.addActionListener(e -> {
                    portfolio.saveToFile(loggedInUsername);
                    frame.dispose();
                });
            }

            if (item.contains("Buy Stocks")) {
                button.addActionListener(e -> {
                    if (stockTable.getSelectedRow() == -1) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Please select a stock from the table first.");
                    } else {
                        // Reuse the existing Buy Selected Stock button.
                        JOptionPane.showMessageDialog(
                                frame,
                                "Select the stock and use Buy Selected Stock below the table.");
                    }
                });
            }

            sidebar.add(button);
        }

        // Dashboard content
        JPanel dashboard = new JPanel(new BorderLayout(12, 12));
        dashboard.setBackground(new Color(5, 22, 43));
        dashboard.setBorder(
                BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // Heading
        JLabel heading = new JLabel("Market Overview");
        heading.setForeground(TEXT_COLOR);
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel subtitle = new JLabel(
                "Explore stocks and their latest sample prices.");
        subtitle.setForeground(MUTED_TEXT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JPanel headingPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        headingPanel.add(subtitle);

        // ================= MARKET INDEX CARDS =================
        JPanel marketCards = new JPanel(new GridLayout(1, 3, 12, 0));
        marketCards.setOpaque(false);
        marketCards.setBorder(
                BorderFactory.createEmptyBorder(8, 0, 12, 0));

        String[][] marketIndices = {
                { "S&P 500", "5,783.22", "+24.56 (0.43%)" },
                { "NASDAQ", "18,271.86", "+82.14 (0.45%)" },
                { "DOW JONES", "42,313.00", "+120.32 (0.29%)" }
        };

        for (String[] index : marketIndices) {
            JPanel card = new JPanel(new BorderLayout(5, 8));
            card.setBackground(new Color(8, 30, 55));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            new Color(25, 83, 130)),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));

            JLabel nameLabel = new JLabel(index[0]);
            nameLabel.setForeground(new Color(185, 210, 245));
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

            JLabel valueLabel = new JLabel(index[1]);
            valueLabel.setForeground(Color.WHITE);
            valueLabel.setFont(new Font("SansSerif", Font.BOLD, 21));

            JLabel changeLabel = new JLabel("▲ " + index[2]);
            changeLabel.setForeground(new Color(0, 225, 170));
            changeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

            JPanel cardText = new JPanel(new GridLayout(3, 1, 0, 5));
            cardText.setOpaque(false);
            cardText.add(nameLabel);
            cardText.add(valueLabel);
            cardText.add(changeLabel);

            card.add(cardText, BorderLayout.CENTER);
            marketCards.add(card);
        }

        // Stock table columns
        String[] columns = {
                "Symbol",
                "Company Name",
                "Current Price ($)",
                "Change",
                "Change %",
                "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stockTable = new JTable(tableModel);
        stockTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        stockTable.setRowHeight(35);
        stockTable.setForeground(TEXT_COLOR);
        stockTable.setBackground(PANEL_COLOR);
        stockTable.setGridColor(new Color(45, 70, 100));
        stockTable.setSelectionBackground(new Color(38, 68, 95));
        stockTable.setSelectionForeground(Color.WHITE);
        stockTable.setFillsViewportHeight(true);

        // Custom colors for positive and negative price changes
        stockTable.setDefaultRenderer(Object.class, (table, value, isSelected,
                hasFocus, row, column) -> {

            JLabel label = new JLabel(
                    value == null ? "" : value.toString());

            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            if (isSelected) {
                label.setBackground(new Color(28, 64, 100));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(new Color(8, 30, 55));

                if (column == 3 || column == 4) {
                    String text = value == null ? "" : value.toString();

                    if (text.startsWith("+")) {
                        label.setForeground(new Color(0, 225, 160));
                    } else if (text.startsWith("-")) {
                        label.setForeground(new Color(255, 90, 105));
                    } else {
                        label.setForeground(Color.WHITE);
                    }
                } else {
                    label.setForeground(Color.WHITE);
                }
            }

            return label;
        });

        // Configure the Actions column with clickable Buy/Sell buttons
        stockTable.getColumnModel().getColumn(5)
                .setCellRenderer(new ActionButtonRenderer());

        stockTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        stockTable.setRowHeight(42);

        stockTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14));
        stockTable.getTableHeader().setForeground(TEXT_COLOR);
        stockTable.getTableHeader().setBackground(
                new Color(30, 50, 76));
        stockTable.getTableHeader().setReorderingAllowed(false);

        // Search functionality: filter by stock symbol or company name
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        stockTable.setRowSorter(sorter);

        // Search box placeholder
        searchField.setText("");
        searchField.setToolTipText("Search by stock symbol or company name");

        searchField.setForeground(placeholderColor);
        searchField.setText(placeholder);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(normalTextColor);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(placeholderColor);
                }
            }
        });

        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    private void filterStocks() {
                        String text = searchField.getText().trim();

                        if (text.isEmpty() || text.equals(placeholder)) {
                            sorter.setRowFilter(null);
                        } else {
                            sorter.setRowFilter(
                                    RowFilter.regexFilter(
                                            "(?i)" + java.util.regex.Pattern.quote(text),
                                            0, 1));
                        }
                    }

                    @Override
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        filterStocks();
                    }

                    @Override
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        filterStocks();
                    }

                    @Override
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        filterStocks();
                    }
                });

        // Load stocks from StockMarket into the GUI table
        loadStockData();

        stockTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                int viewRow = stockTable.rowAtPoint(e.getPoint());
                int viewColumn = stockTable.columnAtPoint(e.getPoint());

                // Sirf Actions column par click handle karo
                if (viewRow == -1 || viewColumn != 5) {
                    return;
                }

                // Button click ki position check karo
                java.awt.Rectangle cellRect = stockTable.getCellRect(viewRow, viewColumn, true);

                int relativeX = e.getX() - cellRect.x;

                // Selected row set karo
                stockTable.setRowSelectionInterval(viewRow, viewRow);

                // Left side = Buy, right side = Sell
                if (relativeX < cellRect.width / 2) {
                    buyButton.doClick();
                } else {
                    sellSelectedStock();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(45, 70, 100)));

        // Information label
        JLabel footer = new JLabel(
                "Showing " + stockMarket.getAllStocks().size()
                        + " sample stocks | Prices are not live market data.");
        footer.setForeground(MUTED_TEXT);
        footer.setBorder(
                BorderFactory.createEmptyBorder(12, 2, 2, 2));

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setOpaque(false);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(footer, BorderLayout.SOUTH);

        buyButton = new JButton("Buy Selected Stock");

        buyButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        buyButton.setForeground(Color.WHITE);
        buyButton.setBackground(new Color(0, 150, 110));
        buyButton.setFocusPainted(false);

        buyButton.addActionListener(e -> {

            int selectedRow = stockTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Please select a stock from the table first.",
                        "No Stock Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String symbol = stockTable.getValueAt(
                    selectedRow, 0).toString();

            Stock stock = stockMarket.findStock(symbol);

            if (stock == null) {
                return;
            }

            String input = JOptionPane.showInputDialog(
                    frame,
                    "Stock: " + stock.getCompanyName()
                            + "\nPrice per share: $"
                            + String.format("%.2f", stock.getPrice())
                            + "\nAvailable cash: $"
                            + String.format("%.2f", portfolio.getCashBalance())
                            + "\n\nEnter quantity to buy:",
                    "Buy Stock",
                    JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                return;
            }

            try {
                int quantity = Integer.parseInt(input.trim());

                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Quantity must be greater than zero.");
                    return;
                }

                double totalCost = stock.getPrice() * quantity;

                if (totalCost > portfolio.getCashBalance()) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Insufficient cash balance!\n"
                                    + "Required: $" + String.format("%.2f", totalCost)
                                    + "\nAvailable: $"
                                    + String.format(
                                            "%.2f", portfolio.getCashBalance()),
                            "Purchase Failed",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean success = portfolio.buyStock(stock, quantity);

                if (success) {
                    balanceLabel.setText(
                            "Cash Balance: $"
                                    + String.format(
                                            "%.2f", portfolio.getCashBalance()));

                    portfolio.saveToFile(loggedInUsername);

                    refreshRightPanel();

                    JOptionPane.showMessageDialog(
                            frame,
                            "Stock purchased successfully!\n\n"
                                    + "Company: " + stock.getCompanyName()
                                    + "\nQuantity: " + quantity
                                    + "\nTotal cost: $"
                                    + String.format("%.2f", totalCost)
                                    + "\nRemaining cash: $"
                                    + String.format(
                                            "%.2f", portfolio.getCashBalance()),
                            "Purchase Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a valid whole number for quantity.",
                        "Invalid Quantity",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(buyButton);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel topDashboard = new JPanel(new BorderLayout(0, 8));
        topDashboard.setOpaque(false);
        topDashboard.add(headingPanel, BorderLayout.NORTH);
        topDashboard.add(marketCards, BorderLayout.SOUTH);

        dashboard.add(topDashboard, BorderLayout.NORTH);
        // Main dashboard layout: stocks table + right portfolio panel
        JPanel mainBody = new JPanel(new BorderLayout(12, 0));
        mainBody.setOpaque(false);

        mainBody.add(centerPanel, BorderLayout.CENTER);

        // Right-side Portfolio Panel
        JPanel rightPanel = createRightPanel();

        rightScrollPane = new JScrollPane(rightPanel);

        rightScrollPane.setPreferredSize(new Dimension(300, 0));
        rightScrollPane.setMinimumSize(new Dimension(250, 0));

        rightScrollPane.setBorder(BorderFactory.createEmptyBorder());

        rightScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        rightScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        rightScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        rightScrollPane.getViewport().setBackground(
                new Color(8, 30, 55));

        rightScrollPane.setBackground(
                new Color(8, 30, 55));

        mainBody.add(rightScrollPane, BorderLayout.EAST);

        dashboard.add(mainBody, BorderLayout.CENTER);

        // Assemble the window
        frame.add(header, BorderLayout.NORTH);
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(dashboard, BorderLayout.CENTER);

        frame.getContentPane().setBackground(BACKGROUND);
        frame.setVisible(true);
    }

    private static JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(8, 30, 55));
        rightPanel.setPreferredSize(new Dimension(270, 1200));
        rightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.setBorder(
                BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Panel heading
        JLabel title = new JLabel("Portfolio Summary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(title);
        rightPanel.add(Box.createVerticalStrut(22));

        // Available cash
        addSummaryItem(
                rightPanel,
                "Available Cash",
                String.format("$%.2f", portfolio.getCashBalance()),
                new Color(0, 225, 160));

        rightPanel.add(Box.createVerticalStrut(20));

        // Invested amount
        double investedAmount = portfolio.getTotalInvestedAmount();

        addSummaryItem(
                rightPanel,
                "Invested Amount",
                String.format("$%.2f", investedAmount),
                Color.WHITE);

        rightPanel.add(Box.createVerticalStrut(20));

        // Total account value
        addSummaryItem(
                rightPanel,
                "Total Account Value",
                String.format("$%.2f", portfolio.getTotalAccountValue()),
                new Color(80, 170, 255));

        rightPanel.add(Box.createVerticalStrut(25));

        // Portfolio Performance heading
        JLabel performanceTitle = new JLabel("Portfolio Performance");
        performanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        performanceTitle.setForeground(Color.WHITE);
        performanceTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(performanceTitle);
        rightPanel.add(Box.createVerticalStrut(15));

        // Calculate Profit/Loss
        double profitLoss = portfolio.getProfitLoss();
        double profitLossPercentage = portfolio.getProfitLossPercentage();

        Color profitLossColor;

        if (profitLoss > 0) {
            profitLossColor = new Color(0, 225, 160);
        } else if (profitLoss < 0) {
            profitLossColor = new Color(255, 90, 105);
        } else {
            profitLossColor = Color.WHITE;
        }

        // Unrealized Profit/Loss
        addSummaryItem(
                rightPanel,
                "Unrealized Profit/Loss",
                String.format("%s$%.2f",
                        profitLoss > 0 ? "+" : profitLoss < 0 ? "-" : "",
                        Math.abs(profitLoss)),
                profitLossColor);

        rightPanel.add(Box.createVerticalStrut(15));

        // Profit/Loss Percentage
        addSummaryItem(
                rightPanel,
                "Return Percentage",
                String.format("%s%.2f%%",
                        profitLossPercentage > 0 ? "+" : profitLossPercentage < 0 ? "-" : "",
                        Math.abs(profitLossPercentage)),
                profitLossColor);

        rightPanel.add(Box.createVerticalStrut(28));

        // Holdings heading
        JLabel holdingsTitle = new JLabel("My Holdings");
        holdingsTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        holdingsTitle.setForeground(Color.WHITE);
        holdingsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(holdingsTitle);
        rightPanel.add(Box.createVerticalStrut(12));

        if (portfolio.getHoldings().isEmpty()) {
            JLabel emptyLabel = new JLabel("No holdings yet");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            emptyLabel.setForeground(new Color(170, 185, 205));
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightPanel.add(emptyLabel);
        } else {
            for (Holding holding : portfolio.getHoldings()) {
                JLabel holdingLabel = new JLabel(
                        holding.getStock().getSymbol()
                                + "  |  Qty: "
                                + holding.getQuantity());

                holdingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                holdingLabel.setForeground(Color.WHITE);
                holdingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                rightPanel.add(holdingLabel);
                rightPanel.add(Box.createVerticalStrut(10));
            }
        }

        // Recent Transactions Section
        rightPanel.add(Box.createVerticalStrut(25));

        JLabel transactionsTitle = new JLabel("Recent Transactions");
        transactionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        transactionsTitle.setForeground(Color.WHITE);
        transactionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(transactionsTitle);
        rightPanel.add(Box.createVerticalStrut(15));

        System.out.println("Recent Transactions heading added to panel.");

        // Get transaction history
        java.util.List<Transaction> transactions = portfolio.getTransactions();

        System.out.println("Recent panel transactions: " + transactions.size());

        if (transactions == null || transactions.isEmpty()) {

            JLabel noTransactions = new JLabel("No transactions yet");
            noTransactions.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            noTransactions.setForeground(new Color(170, 185, 205));
            noTransactions.setAlignmentX(Component.LEFT_ALIGNMENT);

            rightPanel.add(noTransactions);

        } else {

            // Show up to 5 most recent transactions
            int count = 0;

            for (int i = transactions.size() - 1; i >= 0 && count < 5; i--) {

                Transaction transaction = transactions.get(i);

                JPanel transactionItem = new JPanel();
                transactionItem.setLayout(
                        new BoxLayout(transactionItem, BoxLayout.Y_AXIS));
                transactionItem.setBackground(new Color(15, 42, 70));
                transactionItem.setBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10));
                transactionItem.setAlignmentX(Component.LEFT_ALIGNMENT);
                transactionItem.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, 75));

                // Stock symbol and action
                JLabel stockLabel = new JLabel(
                        transaction.getStockSymbol()
                                + "  |  "
                                + transaction.getAction());

                stockLabel.setFont(
                        new Font("Segoe UI", Font.BOLD, 13));

                String action = String.valueOf(transaction.getAction());

                if (action.equalsIgnoreCase("BUY")) {
                    stockLabel.setForeground(new Color(0, 225, 160));
                } else if (action.equalsIgnoreCase("SELL")) {
                    stockLabel.setForeground(new Color(255, 100, 115));
                } else {
                    stockLabel.setForeground(Color.WHITE);
                }

                // Quantity and price
                JLabel detailsLabel = new JLabel(
                        "Qty: " + transaction.getQuantity()
                                + "  |  Price: $"
                                + String.format("%.2f", transaction.getPrice()));

                detailsLabel.setFont(
                        new Font("Segoe UI", Font.PLAIN, 12));
                detailsLabel.setForeground(new Color(190, 205, 220));

                transactionItem.add(stockLabel);
                transactionItem.add(Box.createVerticalStrut(6));
                transactionItem.add(detailsLabel);

                rightPanel.add(transactionItem);
                rightPanel.add(Box.createVerticalStrut(8));

                count++;
            }
        }

        return rightPanel;
    }

    private static void refreshRightPanel() {
        if (rightScrollPane == null) {
            return;
        }

        rightScrollPane.setViewportView(createRightPanel());
        rightScrollPane.revalidate();
        rightScrollPane.repaint();
    }

    private static void addSummaryItem(
            JPanel panel,
            String titleText,
            String valueText,
            Color valueColor) {

        // Create a modern summary card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(24, 44, 68));

        // Rounded-style spacing and subtle border
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(48, 70, 96), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        // Card title
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(170, 190, 215));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card value
        JLabel valueLabel = new JLabel(valueText);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(valueColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components to the card
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(7));
        card.add(valueLabel);

        // Add card to the existing panel
        panel.add(card);
        panel.add(Box.createVerticalStrut(10));
    }

    private static void loadStockData() {

        tableModel.setRowCount(0);

        List<Stock> stocks = stockMarket.getAllStocks();

        for (Stock stock : stocks) {

            Object[] row = {
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    String.format("$%.2f", stock.getPrice()),
                    String.format("%+.2f", stock.getPriceChange()),
                    String.format("%+.2f%%",
                            stock.getPriceChangePercentage()),
                    "Actions"
            };
            tableModel.addRow(row);
        }
    }

    private static void showPortfolio() {

        String[] columns = {
                "Symbol",
                "Company",
                "Quantity",
                "Price per Share",
                "Current Value"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable portfolioTable = new JTable(model);
        portfolioTable.setRowHeight(30);

        for (Holding holding : portfolio.getHoldings()) {

            Stock stock = holding.getStock();

            model.addRow(new Object[] {
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    holding.getQuantity(),
                    String.format("$%.2f", stock.getPrice()),
                    String.format("$%.2f", holding.getCurrentValue())
            });
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("My Portfolio");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel totalValue = new JLabel(
                "Cash: $" + String.format("%.2f", portfolio.getCashBalance())
                        + "     |     Holdings: $"
                        + String.format("%.2f", portfolio.getPortfolioValue())
                        + "     |     Total Account: $"
                        + String.format("%.2f", portfolio.getTotalAccountValue()));

        JPanel topPanel = new JPanel(new BorderLayout(5, 10));
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(totalValue, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(portfolioTable), BorderLayout.CENTER);

        JFrame portfolioFrame = new JFrame("My Portfolio");
        portfolioFrame.setSize(800, 450);
        portfolioFrame.setLocationRelativeTo(mainFrame);
        portfolioFrame.add(panel);
        portfolioFrame.setVisible(true);

    }

    private static void showTransactionHistory() {

        String[] columns = {
                "Date & Time",
                "Stock Symbol",
                "Action",
                "Quantity",
                "Price per Share",
                "Total Amount"
        };

        DefaultTableModel historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Get all saved transactions
        List<Transaction> transactions = portfolio.getTransactions();

        // Add transactions to the table
        for (Transaction transaction : transactions) {

            Object[] row = {
                    transaction.getDateTime(),
                    transaction.getStockSymbol(),
                    transaction.getAction(),
                    transaction.getQuantity(),
                    String.format("$%.2f", transaction.getPrice()),
                    String.format("$%.2f", transaction.getTotalAmount())
            };

            historyModel.addRow(row);
        }

        JTable historyTable = new JTable(historyModel);
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        historyTable.setRowHeight(30);
        historyTable.setFillsViewportHeight(true);

        historyTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel summary = new JLabel(
                "Total transactions: " + transactions.size());

        JPanel topPanel = new JPanel(new BorderLayout(5, 10));
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(summary, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JFrame historyFrame = new JFrame("Transaction History");
        historyFrame.setSize(900, 450);
        historyFrame.setLocationRelativeTo(mainFrame);
        historyFrame.add(panel);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        historyFrame.setVisible(true);
    }

    private static void sellSelectedStock() {

        if (portfolio.getHoldings().isEmpty()) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Your portfolio is empty. Buy some stocks first.",
                    "No Holdings",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Holding> holdings = portfolio.getHoldings();

        String[] options = new String[holdings.size()];

        for (int i = 0; i < holdings.size(); i++) {
            Holding holding = holdings.get(i);

            options[i] = holding.getStock().getSymbol()
                    + " — " + holding.getStock().getCompanyName()
                    + " (Quantity: " + holding.getQuantity() + ")";
        }

        String selected = (String) JOptionPane.showInputDialog(
                mainFrame,
                "Select the stock you want to sell:",
                "Sell Stocks",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selected == null) {
            return;
        }

        int selectedIndex = -1;

        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(selected)) {
                selectedIndex = i;
                break;
            }
        }

        if (selectedIndex == -1) {
            return;
        }

        Holding holding = holdings.get(selectedIndex);
        Stock stock = holding.getStock();

        String input = JOptionPane.showInputDialog(
                mainFrame,
                "Stock: " + stock.getCompanyName()
                        + "\nOwned shares: " + holding.getQuantity()
                        + "\nCurrent price: $"
                        + String.format("%.2f", stock.getPrice())
                        + "\n\nEnter quantity to sell:");

        if (input == null) {
            return;
        }

        try {
            int quantity = Integer.parseInt(input.trim());

            if (quantity <= 0 || quantity > holding.getQuantity()) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Enter a quantity between 1 and "
                                + holding.getQuantity() + ".",
                        "Invalid Quantity",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double saleAmount = stock.getPrice() * quantity;

            boolean success = portfolio.sellStock(stock, quantity);

            if (success) {

                balanceLabel.setText(
                        "Cash Balance: $"
                                + String.format("%.2f", portfolio.getCashBalance()));

                portfolio.saveToFile(loggedInUsername);

                refreshRightPanel();

                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Stock sold successfully!\n\n"
                                + "Company: " + stock.getCompanyName()
                                + "\nQuantity sold: " + quantity
                                + "\nSale amount: $"
                                + String.format("%.2f", saleAmount)
                                + "\nNew cash balance: $"
                                + String.format("%.2f", portfolio.getCashBalance()),
                        "Sale Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Please enter a valid whole number.",
                    "Invalid Quantity",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method: select a stock row and reuse the existing Buy flow
    private static void buyStockFromRow(int modelRow) {
        int viewRow = stockTable.convertRowIndexToView(modelRow);
        stockTable.setRowSelectionInterval(viewRow, viewRow);

        // Reuse the existing Buy Selected Stock button action.
        buyButton.doClick();
    }

    private static class ActionButtonRenderer extends JPanel
            implements javax.swing.table.TableCellRenderer {

        private final JButton buy = new JButton("Buy");
        private final JButton sell = new JButton("Sell");

        ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setOpaque(true);

            buy.setBackground(new Color(0, 155, 110));
            buy.setForeground(Color.WHITE);
            buy.setFocusPainted(false);
            buy.setBorderPainted(false);

            sell.setBackground(new Color(190, 55, 70));
            sell.setForeground(Color.WHITE);
            sell.setFocusPainted(false);
            sell.setBorderPainted(false);

            add(buy);
            add(sell);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            setBackground(isSelected
                    ? new Color(28, 64, 100)
                    : new Color(8, 30, 55));

            return this;
        }
    }

    private static class ActionButtonEditor
            extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {

        private final JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 5, 2));

        private final JButton buy = new JButton("Buy");
        private final JButton sell = new JButton("Sell");

        private int modelRow;

        ActionButtonEditor() {
            buy.setBackground(new Color(0, 155, 110));
            buy.setForeground(Color.WHITE);
            buy.setFocusPainted(false);
            buy.setBorderPainted(false);

            sell.setBackground(new Color(190, 55, 70));
            sell.setForeground(Color.WHITE);
            sell.setFocusPainted(false);
            sell.setBorderPainted(false);

            panel.setOpaque(true);
            panel.add(buy);
            panel.add(sell);

            buy.addActionListener(e -> {
                fireEditingStopped();
                buyStockFromRow(modelRow);
            });

            sell.addActionListener(e -> {
                fireEditingStopped();
                sellSelectedStock();
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected,
                int row, int column) {

            modelRow = table.convertRowIndexToModel(row);
            panel.setBackground(new Color(28, 64, 100));

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}
