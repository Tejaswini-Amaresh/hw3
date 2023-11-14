# hw3- Implementation & Testing

The homework will be based on this project named "Expense Tracker",where users will be able to add/remove daily transaction. 

## Compile

To compile the code from terminal, use the following command:
```
cd src
javac ExpenseTrackerApp.java
java ExpenseTracker
```

You should be able to view the GUI of the project upon successful compilation. 

## Java Version
This code is compiled with ```openjdk 17.0.7 2023-04-18```. Please update your JDK accordingly if you face any incompatibility issue.

## Implementation
The expense tracker app containing add transaction and filter features have been updated to include an undo functionality where the user can delete a given transaction by selecting the row corresponding to the transaction to be deleted.
The following cases have been considered for undo :
1. Deleting an existing transaction by selection - updates the transactions table and total cost
2. Deleting a transaction without selecting - Throws a prompt stating that no transaction has been selected for undo.
3. Deleting transactions when no transactions exist - Throws a prompt stating that no transaction has been selected for undo.

## Test Suits
Additionally, test cases have been included to test all the functionalities of the code.

1. Add Transaction:
   Steps: Add a transaction with amount 50.00 and category ”food”
   Expected Output: Transaction is added to the table, Total Cost is updated

2. Invalid Input Handling:
   Steps: Attempt to add a transaction with an invalid amount or category
   Expected Output: Error messages are displayed, transactions and Total Cost remain unchanged

3. Filter by Amount:
   Steps: Add multiple transactions with different amounts, apply amount filter
   Expected Output: Only transactions matching the amount are returned (and will be highlighted)

4. Filter by Category:
   Steps: Add multiple transactions with different categories, apply category filter
   Expected Output: Only transactions matching the category are returned (and will be highlighted)

5. Undo Disallowed:
   Steps: Attempt to undo when the transactions list is empty
   Expected Output: Either UI widget is disabled or an error code (exception) is returned (thrown).

6. Undo Allowed:
   Steps: Add a transaction, undo the addition
   Expected Output: Transaction is removed from the table, Total Cost is updated

## Screeshots of the Application
<br>
1.Deleting an existing transaction by selection
<br>
Before: 
<br>
<img width="584" alt="image" src="https://github.com/Tejaswini-Amaresh/hw3/assets/45268882/2f056cd0-856c-47a2-be61-cc68ab812873">
<br>
After:<br> 
<img width="581" alt="image" src="https://github.com/Tejaswini-Amaresh/hw3/assets/45268882/40501e02-e733-4d22-af4f-677b4f7f6277">
<br>


2. Deleting a transaction without selecting<br>
<img width="592" alt="image" src="https://github.com/Tejaswini-Amaresh/hw3/assets/45268882/90f3dddb-7c6a-49ed-b5e2-43111d43ea25">
<br>

3. Deleting transactions when no transactions exist 
<img width="600" alt="image" src="https://github.com/Tejaswini-Amaresh/hw3/assets/45268882/bd2c0671-23f1-48b7-8eb5-595385c1541b">