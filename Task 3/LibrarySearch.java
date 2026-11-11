
import java.awt.*;
import java.awt.event.*;

public class LibrarySearch extends Frame {

    TextField searchField = new TextField(20);
    Choice category = new Choice();
    TextArea results = new TextArea();
    Label status = new Label("Enter a book name");

    LibrarySearch() {
        setTitle("Campus Library Search");
        setLayout(new BorderLayout(5, 5));

        Label title = new Label(
            "CAMPUS LIBRARY SEARCH", Label.CENTER
        );

        Panel searchPanel = new Panel(new FlowLayout());
        Button search = new Button("Search");

        category.add("All");
        category.add("Science");
        category.add("Technology");
        category.add("Literature");

        searchPanel.add(new Label("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(category);
        searchPanel.add(search);

        Panel bottomPanel = new Panel(new FlowLayout());
        Button prev = new Button("Prev");
        Button next = new Button("Next");
        Button reserve = new Button("Reserve");

        bottomPanel.add(prev);
        bottomPanel.add(next);
        bottomPanel.add(reserve);

        Panel southPanel = new Panel(new BorderLayout());
        southPanel.add(bottomPanel, BorderLayout.CENTER);
        southPanel.add(status, BorderLayout.SOUTH);

        add(title, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);
        add(results, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        search.addActionListener(e -> {
            String book = searchField.getText();

            if (book.trim().isEmpty()) {
                status.setText("Enter a book name");
            } else {
                results.setText(
                    "Book: " + book +
                    "\nCategory: " + category.getSelectedItem()
                );
                status.setText("Search completed");
            }
        });

        prev.addActionListener(e ->
            status.setText("Previous page selected")
        );

        next.addActionListener(e ->
            status.setText("Next page selected")
        );

        reserve.addActionListener(e ->
            status.setText("Book reservation requested")
        );

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(600, 400);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LibrarySearch();
    }
}
