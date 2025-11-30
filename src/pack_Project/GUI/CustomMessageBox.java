
package pack_Project.GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class CustomMessageBox extends JDialog {

    public CustomMessageBox(Window parent, String title, String message, int messageType) {
        super(getFrameParent(parent), title, true);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);


        JLabel iconLabel = new JLabel();
        Icon icon;
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            icon = UIManager.getIcon("OptionPane.errorIcon");
        } else if (messageType == JOptionPane.INFORMATION_MESSAGE) {
            icon = UIManager.getIcon("OptionPane.informationIcon");
        } else if (messageType == JOptionPane.WARNING_MESSAGE) {
            icon = UIManager.getIcon("OptionPane.warningIcon");
        } else {
            icon = UIManager.getIcon("OptionPane.plainIcon");
        }
        iconLabel.setIcon(icon);
        panel.add(iconLabel, BorderLayout.WEST);


        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(Color.WHITE);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setColumns(25);
        messageArea.setRows(Math.min(5, (message.length() / 25) + 1));

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);


        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBackground(new Color(63, 150, 219));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setOpaque(true);
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }


    public static void showInfoMessage(Window parent, String message, String title) {
        new CustomMessageBox(parent, title, message, JOptionPane.INFORMATION_MESSAGE).setVisible(true);
    }


    public static void showErrorMessage(Window parent, String message, String title) {
        new CustomMessageBox(parent, title, message, JOptionPane.ERROR_MESSAGE).setVisible(true);
    }


    public static void showWarningMessage(Window parent, String message, String title) {
        new CustomMessageBox(parent, title, message, JOptionPane.WARNING_MESSAGE).setVisible(true);
    }
    

    private static Frame getFrameParent(Window parent) {
        if (parent instanceof Frame) {
            return (Frame) parent;
        } else if (parent instanceof Dialog) {

            Window dialogParent = ((Dialog) parent).getOwner();
            if (dialogParent instanceof Frame) {
                return (Frame) dialogParent;
            }
        }
        return null;
    }
}
