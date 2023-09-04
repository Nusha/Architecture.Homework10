package com.example.cafeapp.view;

import com.example.cafeapp.model.*;

import javax.swing.*;
import java.awt.FlowLayout;
import java.util.List;

import org.instancio.Instancio;

public class OrderApp extends JFrame {
    private final OrderRepositoryImpl orderRepo = new OrderRepositoryImpl();


    OrderItem orderItem = Instancio.of(OrderItem.class).create();

    public OrderApp() {
        setTitle("Order Management");

        JLabel idLabel = new JLabel("Order ID:");
        JTextField idField = new JTextField(10);
        JLabel resultLabel = new JLabel();
        JButton findButton = new JButton("Find Order");
        JButton deleteButton = new JButton("Delete Order");
        JButton findAllButton = new JButton("Find All Orders");

        findButton.addActionListener(e -> {
            String id = idField.getText();
            Order order = orderRepo.findById(id);
            if (order != null) {
                resultLabel.setText("Order found: " + order);
            } else {
                resultLabel.setText("Order not found");
            }
        });
        deleteButton.addActionListener(e -> {
            String id = idField.getText();
            Order order = orderRepo.findById(id);
            if (order != null) {
                orderRepo.deleteById(id);
                resultLabel.setText("Order deleted: " + order);

            } else {
                resultLabel.setText("Order not found");
            }
        });
        findAllButton.addActionListener(e -> {
            //           List<Order> orders = orderRepo.findAll(1, 10);
            List<Order> orders = Instancio.ofList(Order.class).size(10).create(); // Для UI теста
            if (!orders.isEmpty()) {
                String[] columnNames = {"Order ID", "Items", "Total Amount"};
                Object[][] data = new Object[orders.size()][3];
                for (int i = 0; i < orders.size(); i++) {
                    Order order = orders.get(i);
                    data[i][0] = order.getId();
                    data[i][1] = order.getItems();
                    data[i][2] = order.calculateTotalPrice();
                }
                JTable orderTable = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(orderTable);
                JOptionPane.showMessageDialog(null, scrollPane, "All Orders", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No orders found", "All Orders", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.add(idLabel);
        panel.add(idField);
        panel.add(findButton);
        panel.add(deleteButton);
        panel.add(findAllButton);
        panel.add(resultLabel);

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new OrderApp();
    }
}
