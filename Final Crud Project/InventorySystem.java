package advancedJava;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class InventorySystem extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane, navPanel, headerPanel, mainPanel;
    private JButton activeNavButton = null;


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                InventorySystem frame = new InventorySystem();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    
    public InventorySystem() {
        setTitle("Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        initHeader();
        initNav();
        initMainPanel();
    }

    private void initHeader() {
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(100, 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));

        JLabel titleLabel = new JLabel("Inventory System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(titleLabel);

        contentPane.add(headerPanel, BorderLayout.NORTH);
    }

    private void initNav() {
        navPanel = new JPanel();
        navPanel.setBackground(new Color(52, 73, 94));
        navPanel.setPreferredSize(new Dimension(200, 0));
        navPanel.setLayout(new GridLayout(0, 1, 0, 0));

        String[] navItems = { "Dashboard", "Inventory", "Suppliers", "Purchase"};
        for (String item : navItems) {
            JButton btn = new JButton(item);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(52, 73, 94));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(200, 50));

            btn.addActionListener(e -> {
                switch (item) {
                    case "Dashboard":
                        showDashboardPanel();
                        break;
                    case "Inventory":
                        showInventoryPanel();
                        break;
                    case "Suppliers":
                        showSupplierPanel();
                        break;
                    case "Purchase":
                        showPurchasePanel();
                        break;
                    default:
                        showMessage(item + " clicked");
                        break;
                }
            });

           
            navPanel.add(btn);
        }

        contentPane.add(navPanel, BorderLayout.WEST);
    }

    private void initMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        JLabel welcome = new JLabel("Welcome to Inventory Dashboard");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        welcome.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcome, BorderLayout.CENTER);

        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        showDashboardPanel();
    }
    private void showDashboardPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Total Purchased Items Card
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(quantity) AS total_purchased FROM purchases")) {

            if (rs.next()) {
                int totalPurchased = rs.getInt("total_purchased");

                JPanel purchaseCard = new JPanel();
                purchaseCard.setPreferredSize(new Dimension(200, 120));
                purchaseCard.setBackground(new Color(39, 174, 96)); // green card
                purchaseCard.setLayout(new BorderLayout());
                purchaseCard.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 0), 2));

                JLabel title = new JLabel("Total Purchased", SwingConstants.CENTER);
                title.setForeground(Color.WHITE);
                title.setFont(new Font("Segoe UI", Font.BOLD, 18));
                title.setBorder(new EmptyBorder(10, 10, 0, 10));
                purchaseCard.add(title, BorderLayout.NORTH);

                JLabel countLabel = new JLabel(String.valueOf(totalPurchased), SwingConstants.CENTER);
                countLabel.setForeground(Color.WHITE);
                countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
                purchaseCard.add(countLabel, BorderLayout.CENTER);

                mainPanel.add(purchaseCard);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Failed to load purchase summary.");
        }

        // Category Cards
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT c.name, COUNT(p.id) AS product_count " +
                     "FROM categories c LEFT JOIN products p ON c.id = p.category_id " +
                     "GROUP BY c.id, c.name")) {

            while (rs.next()) {
                String categoryName = rs.getString("name");
                int count = rs.getInt("product_count");

                JPanel card = new JPanel();
                card.setPreferredSize(new Dimension(200, 120));
                card.setBackground(new Color(41, 128, 185));
                card.setLayout(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(new Color(0, 76, 153), 2));
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                JLabel title = new JLabel(categoryName, SwingConstants.CENTER);
                title.setForeground(Color.WHITE);
                title.setFont(new Font("Segoe UI", Font.BOLD, 18));
                title.setBorder(new EmptyBorder(10, 10, 0, 10));
                card.add(title, BorderLayout.NORTH);

                JLabel countLabel = new JLabel(String.valueOf(count), SwingConstants.CENTER);
                countLabel.setForeground(Color.WHITE);
                countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
                card.add(countLabel, BorderLayout.CENTER);

                mainPanel.add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Failed to load dashboard data.");
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    private void showInventoryPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Name", "Category", "Supplier", "Quantity", "Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Hide the ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        styleButton(addBtn, new Color(46, 204, 113));      
        styleButton(editBtn, new Color(52, 152, 219));     
        styleButton(deleteBtn, new Color(231, 76, 60));    
        styleButton(refreshBtn, new Color(149, 165, 166)); 

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(addBtn);
        controlPanel.add(editBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(refreshBtn);
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        ActionListener loadTable = e -> {
            model.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT p.id, p.name, c.name AS category, s.name AS supplier, p.quantity, p.price " +
                                 "FROM products p " +
                                 "LEFT JOIN categories c ON p.category_id = c.id " +
                                 "LEFT JOIN suppliers s ON p.supplier_id = s.id")) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("supplier"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    });
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showMessage("Failed to load data.");
            }
        };
        refreshBtn.addActionListener(loadTable);
        loadTable.actionPerformed(null);

        addBtn.addActionListener(e -> openProductForm(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                openProductForm(id);
            } else {
                JOptionPane.showMessageDialog(this, "Select a product to edit.");
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                        loadTable.actionPerformed(null);
                        JOptionPane.showMessageDialog(this, "Product deleted.");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Delete failed.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a product to delete.");
            }
        });

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    private void showPurchasePanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Name", "Category", "Quantity", "Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Hide the ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JButton purchaseBtn = new JButton("Purchase");
        JButton refreshBtn = new JButton("Refresh");
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(purchaseBtn);
        controlPanel.add(refreshBtn);
        styleButton(purchaseBtn, new Color(46, 204, 113));        
        styleButton(refreshBtn, new Color(149, 165, 166)); 
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Load products
        ActionListener loadProducts = e -> {
            model.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT p.id, p.name, c.name AS category, p.quantity, p.price " +
                                 "FROM products p LEFT JOIN categories c ON p.category_id = c.id")) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    });
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showMessage("Failed to load products.");
            }
        };
        refreshBtn.addActionListener(loadProducts);
        loadProducts.actionPerformed(null);

        // Handle purchase
        purchaseBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a product to purchase.");
                return;
            }

            int id = (int) model.getValueAt(row, 0);
            String name = (String) model.getValueAt(row, 1);
            int availableQty = (int) model.getValueAt(row, 3);

            String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to purchase for \"" + name + "\":");
            if (qtyStr != null && !qtyStr.isEmpty()) {
                try {
                    int qtyToBuy = Integer.parseInt(qtyStr);
                    if (qtyToBuy <= 0 || qtyToBuy > availableQty) {
                        JOptionPane.showMessageDialog(this, "Invalid quantity.");
                        return;
                    }

                    try (Connection conn = DBConnection.getConnection()) {
                        // Insert purchase record
                        PreparedStatement insertStmt = conn.prepareStatement(
                                "INSERT INTO purchases (product_id, quantity) VALUES (?, ?)");
                        insertStmt.setInt(1, id);
                        insertStmt.setInt(2, qtyToBuy);
                        insertStmt.executeUpdate();

                        // Update product quantity
                        PreparedStatement updateStmt = conn.prepareStatement(
                                "UPDATE products SET quantity = quantity - ? WHERE id = ?");
                        updateStmt.setInt(1, qtyToBuy);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Purchase successful.");
                        loadProducts.actionPerformed(null);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Enter a valid number.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Purchase failed.");
                }
            }
        });

        mainPanel.revalidate();
        mainPanel.repaint();
    }



    private void openSupplierForm(Integer supplierId) {
        JDialog dialog = new JDialog(this, supplierId == null ? "Add Supplier" : "Edit Supplier", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Contact:"));
        dialog.add(contactField);
        dialog.add(saveBtn);
        dialog.add(cancelBtn);

        // Load existing data for edit
        if (supplierId != null) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT name, contact FROM suppliers WHERE id = ?")) {
                ps.setInt(1, supplierId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    contactField.setText(rs.getString("contact"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Contact must be filled.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                if (supplierId == null) {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO suppliers (name, contact) VALUES (?, ?)");
                    ps.setString(1, name);
                    ps.setString(2, contact);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Supplier added.");
                } else {
                    PreparedStatement ps = conn.prepareStatement("UPDATE suppliers SET name=?, contact=? WHERE id=?");
                    ps.setString(1, name);
                    ps.setString(2, contact);
                    ps.setInt(3, supplierId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Supplier updated.");
                }
                dialog.dispose();
                showSupplierPanel(); // reload supplier list
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Operation failed.");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    private void showSupplierPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Name", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Hide the ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Buttons
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        // Style the buttons
        styleButton(addBtn, new Color(46, 204, 113));      
        styleButton(editBtn, new Color(52, 152, 219));     
        styleButton(deleteBtn, new Color(231, 76, 60));    
        styleButton(refreshBtn, new Color(149, 165, 166)); 

        // Top control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(addBtn);
        controlPanel.add(editBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(refreshBtn);
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Load data into the table
        ActionListener loadTable = e -> {
            model.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, name, contact FROM suppliers")) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("contact")
                    });
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showMessage("Failed to load supplier data.");
            }
        };
        refreshBtn.addActionListener(loadTable);
        loadTable.actionPerformed(null);

        // Add Supplier
        addBtn.addActionListener(e -> openSupplierForm(null));

        // Edit Supplier
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                openSupplierForm(id);
            } else {
                JOptionPane.showMessageDialog(this, "Select a supplier to edit.");
            }
        });

        // Delete Supplier
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Delete this supplier?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement("DELETE FROM suppliers WHERE id = ?")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                        loadTable.actionPerformed(null);
                        JOptionPane.showMessageDialog(this, "Supplier deleted.");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Delete failed.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a supplier to delete.");
            }
        });

        mainPanel.revalidate();
        mainPanel.repaint();
    }




    private void openProductForm(Integer productId) {
        JDialog dialog = new JDialog(this, productId == null ? "Add Product" : "Edit Product", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(7, 2, 10, 10)); // 7 rows now (added supplier)
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField();
        JComboBox<String> categoryBox = new JComboBox<>();
        JComboBox<String> supplierBox = new JComboBox<>();  // supplier dropdown
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();

        // Load categories
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM categories")) {
            while (rs.next()) {
                categoryBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load suppliers
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM suppliers")) {
            while (rs.next()) {
                supplierBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Category:"));
        dialog.add(categoryBox);
        dialog.add(new JLabel("Supplier:"));  // new label
        dialog.add(supplierBox);              // new supplier dropdown
        dialog.add(new JLabel("Quantity:"));
        dialog.add(quantityField);
        dialog.add(new JLabel("Price:"));
        dialog.add(priceField);
        dialog.add(saveBtn);
        dialog.add(cancelBtn);

        // If editing, load existing data including supplier
        if (productId != null) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "SELECT p.name, c.name AS category, p.quantity, p.price, p.supplier_id " +
                         "FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?")) {
                ps.setInt(1, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    categoryBox.setSelectedItem(rs.getString("category"));
                    quantityField.setText(String.valueOf(rs.getInt("quantity")));
                    priceField.setText(String.valueOf(rs.getDouble("price")));

                    int supplierId = rs.getInt("supplier_id");
                    // Select supplier in dropdown by matching ID prefix
                    for (int i = 0; i < supplierBox.getItemCount(); i++) {
                        String item = supplierBox.getItemAt(i);
                        if (item.startsWith(supplierId + " - ")) {
                            supplierBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        saveBtn.addActionListener(e -> {
            String name = nameField.getText();
            String category = (String) categoryBox.getSelectedItem();
            String supplierItem = (String) supplierBox.getSelectedItem();
            int quantity;
            double price;

            try {
                quantity = Integer.parseInt(quantityField.getText());
                price = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Quantity must be integer and Price must be number.");
                return;
            }

            if (supplierItem == null || supplierItem.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please select a supplier.");
                return;
            }

            // Extract supplier ID from the dropdown string "id - name"
            int supplierId = Integer.parseInt(supplierItem.split(" - ")[0]);

            try (Connection conn = DBConnection.getConnection()) {
                // Get category id
                PreparedStatement psCat = conn.prepareStatement("SELECT id FROM categories WHERE name = ?");
                psCat.setString(1, category);
                ResultSet rs = psCat.executeQuery();
                int categoryId = 0;
                if (rs.next()) categoryId = rs.getInt("id");

                if (productId == null) {
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO products (name, category_id, quantity, price, supplier_id) VALUES (?, ?, ?, ?, ?)");
                    ps.setString(1, name);
                    ps.setInt(2, categoryId);
                    ps.setInt(3, quantity);
                    ps.setDouble(4, price);
                    ps.setInt(5, supplierId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Product added.");
                } else {
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE products SET name=?, category_id=?, quantity=?, price=?, supplier_id=? WHERE id=?");
                    ps.setString(1, name);
                    ps.setInt(2, categoryId);
                    ps.setInt(3, quantity);
                    ps.setDouble(4, price);
                    ps.setInt(5, supplierId);
                    ps.setInt(6, productId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Product updated.");
                }
                dialog.dispose();
                showInventoryPanel(); // reload product list
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Operation failed.");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    

    private void showMessage(String message) {
        mainPanel.removeAll();
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        mainPanel.add(label, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
