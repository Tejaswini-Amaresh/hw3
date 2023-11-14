// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


import java.util.Date;
import java.util.List;
import javax.swing.JTable;

import java.awt.Color;
import java.awt.Component;
import java.beans.Transient;
import java.text.NumberFormat;
import java.text.ParseException;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.util.EventListener;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import view.ExpenseTrackerView;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

    @Before
    public void setup() {
        model = new ExpenseTrackerModel();
        view = new ExpenseTrackerView();
        controller = new ExpenseTrackerController(model, view);
    }

    @After
    public void tearDown() {
        List<Transaction> allTransactions = model.getTransactions();
        for (Transaction transaction : allTransactions) {
            model.removeTransaction(transaction);
        }
        controller.refresh();
    }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }

    private void assertHighlightRow(JTable table, int rowIndex, Color expectedColor) {
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            Component cellRenderer = table.getCellRenderer(rowIndex, columnIndex)
                                          .getTableCellRendererComponent(table, null, false, false, rowIndex, columnIndex);

            assertEquals(expectedColor, cellRenderer.getBackground());
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }
    
    @Test
    public void testAddTransactionCtrlView() {
        double amount = 50.0;
        String category = "Food";

        // Pre-condition: List of transactions is empty
        int initialSize = model.getTransactions().size();
        assertEquals(0, initialSize);

        int initialRowCount = view.getTableModel().getRowCount();
        assertEquals(0, initialRowCount);
    
        //Perform the action
        // Add transaction through controller
        assertTrue(controller.addTransaction(amount, category));

        //Post-condition: 

        // Transaction list increased by one 
        assertEquals(initialSize + 1, model.getTransactions().size());
	
	    // Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);

        // Check if row is added in the UI by getting the row count (2 rows added)
        assertEquals(initialRowCount + 2, view.getTableModel().getRowCount());

        // Get the values of the last row (total cost)
        int lastTotalRowIndex = view.getTableModel().getRowCount() - 1;
        Object total = view.getTableModel().getValueAt(lastTotalRowIndex, 3);

        // Check if total cost in the ui is updated 
        assertEquals(amount, total);

        // Get the last row inserted 
        int lastRowIndex = view.getTableModel().getRowCount() - 2;

        // Verify the values in the last row
        assertEquals(amount, view.getTableModel().getValueAt(lastRowIndex, 1));
        assertEquals(category, view.getTableModel().getValueAt(lastRowIndex, 2));

    }

    @Test
    public void testAddNullTransaction() {
        double amount = 50.0;
        String category = "";

        // Pre-condition: List of transactions is empty
        int initialSize = model.getTransactions().size();
        assertEquals(0, initialSize);

        int initialRowCount = view.getTableModel().getRowCount();
        assertEquals(0, initialRowCount);
    
        //Perform the action

        // Add null transaction through model
        Transaction t = null;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The new transaction must be non-null.");
        model.addTransaction(t);
        
        
        //Post-condition: 

        // Transaction list not increased by one 
        assertEquals(initialSize , model.getTransactions().size());

        // Check if no row is added in the UI by getting the row count
        assertEquals(initialRowCount, view.getTableModel().getRowCount());

    } 

    @Test
    public void testAddInvalidTransaction() {
        double amount = 50.0;
        String category = "";

        // Pre-condition: List of transactions is empty
        int initialSize = model.getTransactions().size();
        assertEquals(0, initialSize);

        int initialRowCount = view.getTableModel().getRowCount();
        assertEquals(0, initialRowCount);
    
        //Perform the action

        // Add null transaction through model
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The category is not valid.");
        Transaction t = new Transaction(amount, category);
        
        
        //Post-condition: 

        // Transaction list not increased by one 
        assertEquals(initialSize , model.getTransactions().size());

        // Check if no row is added in the UI by getting the row count
        assertEquals(initialRowCount, view.getTableModel().getRowCount());

    } 

    @Test
    public void testAmountFilterTransaction() {

        // Pre-condition: Add 3 transactions 
        assertTrue(controller.addTransaction(150.0, "food"));
        assertTrue(controller.addTransaction(100.0, "food"));
        assertTrue(controller.addTransaction(150.0, "travel"));

        int initialSize = model.getTransactions().size();
        assertEquals(3, initialSize);

        int initialRowCount = view.getTableModel().getRowCount();
        assertEquals(4, initialRowCount);

        assertEquals(3, model.getTransactions().size());

        assertEquals(400.0, getTotalCost(), 0.01);

    
        //Perform the action

        // filter by amount==150
        AmountFilter amountFilter = new AmountFilter(150.0);
        controller.setFilter(amountFilter);
        controller.applyFilter();
        
        //Post-condition: 
        JTable table = view.getTransactionsTable();
        // Background color of row index 0 and 2 should be set to green 
        assertHighlightRow(table, 0, new Color(173, 255, 168));
        assertHighlightRow(table, 1, table.getBackground());
        assertHighlightRow(table, 2, new Color(173, 255, 168));

    } 

    @Test
    public void testInvalidAmountFilterTransaction() {

        // Pre-condition: Add 3 transactions 
        assertTrue(controller.addTransaction(150.0, "food"));
        assertTrue(controller.addTransaction(100.0, "food"));
        assertTrue(controller.addTransaction(150.0, "travel"));

        int initialSize = model.getTransactions().size();
        assertEquals(3, initialSize);

        int initialRowCount = view.getTableModel().getRowCount();
        assertEquals(4, initialRowCount);

        assertEquals(3, model.getTransactions().size());

        assertEquals(400.0, getTotalCost(), 0.01);

    
        //Perform the action

        // filter by amount==10000
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid amount filter");
        AmountFilter amountFilter = new AmountFilter(10000.0);
        controller.setFilter(amountFilter);
        controller.applyFilter();
        
        //Post-condition: 
        JTable table = view.getTransactionsTable();
        // Background color of row index 0 and 2 should be set to green 
        assertHighlightRow(table, 0, table.getBackground());
        assertHighlightRow(table, 1, table.getBackground());
        assertHighlightRow(table, 2, table.getBackground());

    } 

    @Test
    public void testCategoryFilterTransaction() {

        // Pre-condition: Add 3 transactions 
        assertTrue(controller.addTransaction(150.0, "food"));
        assertTrue(controller.addTransaction(100.0, "food"));
        assertTrue(controller.addTransaction(150.0, "travel"));

        int initialSize = model.getTransactions().size();
        assertEquals(3, initialSize);

        int initialRowCount = view.getTableModel().getRowCount();
        assertEquals(4, initialRowCount);

        assertEquals(3, model.getTransactions().size());

        assertEquals(400.0, getTotalCost(), 0.01);

    
        //Perform the action

        // filter by amount==150
        CategoryFilter categoryFilter = new CategoryFilter("food");
        controller.setFilter(categoryFilter);
        controller.applyFilter();
        
        //Post-condition: 
        JTable table = view.getTransactionsTable();

        // Background color of row index 0 and 2 should be set to green 
        assertHighlightRow(table, 0, new Color(173, 255, 168));
        assertHighlightRow(table, 1, new Color(173, 255, 168));
        assertHighlightRow(table, 2, table.getBackground());

    } 

    @Test
    public void testInvalidCategoryFilterTransaction() {

        // Pre-condition: Add 3 transactions 
        assertTrue(controller.addTransaction(150.0, "food"));
        assertTrue(controller.addTransaction(100.0, "food"));
        assertTrue(controller.addTransaction(150.0, "travel"));

        int initialSize = model.getTransactions().size();
        assertEquals(3, initialSize);

        int initialRowCount = view.getTableModel().getRowCount();
        assertEquals(4, initialRowCount);

        assertEquals(3, model.getTransactions().size());

        assertEquals(400.0, getTotalCost(), 0.01);

    
        //Perform the action

        // filter by amount==10000
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid category filter");
        CategoryFilter categoryFilter = new CategoryFilter("sports");
        controller.setFilter(categoryFilter);
        controller.applyFilter();

        //Post-condition: 
        JTable table = view.getTransactionsTable();
        // Background color of row index 0 and 2 should be set to green 
        assertHighlightRow(table, 0, table.getBackground());
        assertHighlightRow(table, 1, table.getBackground());
        assertHighlightRow(table, 2, table.getBackground());

    } 

    @Test
    public void testAllowedUndoTransaction() {

        // Pre-condition: Add 2 transactions 
        assertTrue(controller.addTransaction(150.0, "food"));
        assertTrue(controller.addTransaction(150.0, "travel"));

        int initialSize = model.getTransactions().size();
        assertEquals(2, initialSize);

        int initialRowCount = view.getTableModel().getRowCount();
        assertEquals(3, initialRowCount);

        assertEquals(300.0, getTotalCost(), 0.01);

    
        //Perform the action
        // undo the transaction with the category = food
        controller.deleteSelectedTransaction(0);
        

        //Post-condition: 
        // Transaction list decreased by one 
        double expectedTotalCost = 150.0;
        assertEquals(initialSize - 1, model.getTransactions().size());
	
	    // Check the total amount
        assertEquals(expectedTotalCost, getTotalCost(), 0.01);

        // Check if row is added in the UI by getting the row count (2 rows added)
        assertEquals(initialRowCount - 1, view.getTableModel().getRowCount());

        // Get the values of the last row (total cost)
        int lastTotalRowIndex = view.getTableModel().getRowCount() - 1;
        Object total = view.getTableModel().getValueAt(lastTotalRowIndex, 3);

        // Check if total cost in the ui is updated 
        assertEquals(expectedTotalCost, total);

    } 

    @Test
    public void testDisallowedUndoTransaction() {

        // Pre-condition: transaction is empty

        int initialSize = model.getTransactions().size();
        assertEquals(0, initialSize);

        int initialRowCount = view.getTableModel().getRowCount();
        assertEquals(0, initialRowCount);

        assertEquals(0.0, getTotalCost(), 0.01);

    
        //Perform the action
        // undo the transaction with the category = food
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No transaction selected for undo");
        controller.deleteSelectedTransaction(-1);
        

        //Post-condition: 
        // Transaction list remains same 
        assertEquals(initialSize, model.getTransactions().size());
	
	    // Check the total amount
        assertEquals(0.0, getTotalCost(), 0.01);
    } 

}
