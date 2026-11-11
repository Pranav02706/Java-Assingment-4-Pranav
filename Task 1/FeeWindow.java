
import java.awt.*;
import java.awt.event.*;

class FeeService {

    public String pay(String regNo, String amount) {
        if (regNo.trim().isEmpty() || amount.trim().isEmpty()) {
            return "All fields are required";
        }

        try {
            double value = Double.parseDouble(amount);

            if (value <= 0) {
                return "Enter a valid amount";
            }

            return "Payment successful for " + regNo
                    + "\nAmount: Rs. " + value;

        } catch (NumberFormatException e) {
            return "Invalid amount";
        }
    }
}

public class FeeWindow extends Frame {

    TextField regNo = new TextField(20);
    TextField amount = new TextField(20);

    Button pay = new Button("Pay");
    Button clear = new Button("Clear");
    Button receipt = new Button("Receipt");

    Label status = new Label("Enter payment details");

    FeeService service = new FeeService();
    String receiptText = "";

    FeeWindow() {
        setTitle("Student Fee Payment");
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new Label("Register Number:"));
        add(regNo);

        add(new Label("Amount:"));
        add(amount);

        add(pay);
        add(clear);
        add(receipt);
        add(status);

        pay.addActionListener(e -> {
            receiptText = service.pay(
                    regNo.getText(), amount.getText()
            );

            status.setText(receiptText.replace("\n", " "));
            showMessage(receiptText);
        });

        clear.addActionListener(e -> {
            regNo.setText("");
            amount.setText("");
            status.setText("Fields cleared");
        });

        receipt.addActionListener(e -> {
            if (receiptText.isEmpty()) {
                showMessage("No receipt available");
            } else {
                showMessage(receiptText);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(450, 250);
        setVisible(true);
    }

    void showMessage(String message) {
        Dialog d = new Dialog(this, "Payment Information", true);
        d.setLayout(new FlowLayout());
        d.add(new Label(message));

        Button ok = new Button("OK");
        ok.addActionListener(e -> d.dispose());

        d.add(ok);
        d.setSize(350, 130);
        d.setVisible(true);
    }

    public static void main(String[] args) {
        new FeeWindow();
    }
}
