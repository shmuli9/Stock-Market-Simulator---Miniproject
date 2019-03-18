import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

/**************
 * GUI class provides a GUI for the Stock Market Simulation
 */
public class GUI extends JPanel {
    private JFrame windowFrame;
    private JSplitPane splitLogPane;
    private JTabbedPane tabbedPane;
    private JPanel stockMarketPanel;
    private JSplitPane stockSplitPane;
    private JScrollPane scrollPaneStock;
    private JTable stockTable;
    private JPanel stockRightPane;
    private JLabel labelName;
    private JTextField txtSimName;
    private JButton btnStartSim;
    private JButton btnRefresh;
    private JButton btnSave;
    private JPanel portfolioPanel;
    private JSplitPane stockSplitPane2;
    private JScrollPane scrollPaneStock2;
    private JTable portfolioTable;
    private JPanel portfolioRightPane;
    private JButton btnExec;
    private JLabel labelNumShares;
    private JTextField txtNumShares;
    private JPanel ledgerPanel;
    private JScrollPane scrollPaneLedger;
    private JTable ledgerTable;
    private JPanel logPanel;
    private JLabel labelLog;
    private JScrollPane scrollPaneLog;
    private JTextArea textLog;
    private ActionListener ALbtnStartSim;
    private ActionListener ALbtnRefresh;
    private ActionListener ALbtnSave;
    private ActionListener ALbtnExec;
    private ButtonGroup simRadioGroup;
    private ButtonGroup simTradeGroup;
    private JRadioButton radioFile;
    private JRadioButton radioGen;
    private JRadioButton radioBuy;
    private JRadioButton radioSell;
    private JLabel labelSelStock;
    private JTextField txtStockName;
    private SimData sim;
    private int selectedRow;

    public GUI() {
        initListeners();
        initComponents();
    }

    private void initComponents() {
        {
            //Window and tabs
            windowFrame = new JFrame();
            tabbedPane = new JTabbedPane();

            //Log section
            splitLogPane = new JSplitPane();
            logPanel = new JPanel();
            scrollPaneLog = new JScrollPane();
            labelLog = new JLabel();
            textLog = new JTextArea();

            //Stock market page
            /*Containers*/
            stockSplitPane = new JSplitPane();
            stockMarketPanel = new JPanel();
            stockRightPane = new JPanel();
            scrollPaneStock = new JScrollPane();
            stockTable = new JTable();
            /*Buttons*/
            btnStartSim = new JButton();
            btnRefresh = new JButton();
            btnSave = new JButton();
            btnExec = new JButton();
            simRadioGroup = new ButtonGroup();
            radioFile = new JRadioButton();
            radioGen = new JRadioButton();
            simTradeGroup = new ButtonGroup();
            radioBuy = new JRadioButton();
            radioSell = new JRadioButton();
            /*TextAreas*/
            labelName = new JLabel();
            labelNumShares = new JLabel();
            txtSimName = new JTextField();
            labelSelStock = new JLabel();
            txtStockName = new JTextField();
            txtNumShares = new JTextField();

            //Portfolio page
            portfolioPanel = new JPanel();
            portfolioRightPane = new JPanel();
            stockSplitPane2 = new JSplitPane();
            scrollPaneStock2 = new JScrollPane();
            portfolioTable = new JTable();

            //Ledger page
            ledgerPanel = new JPanel();
            scrollPaneLedger = new JScrollPane();
            ledgerTable = new JTable();
        }

        //windowFrame
        windowFrame.setTitle("Stock Market Simulator");
        Container windowFrameContentPane = windowFrame.getContentPane();
        windowFrameContentPane.setLayout(new GridLayout());

        //splitLogPane
        splitLogPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitLogPane.setDividerLocation(400);

        //tabbedPane
        tabbedPane.setPreferredSize(null);

        //stockMarketPanel
        stockMarketPanel.setLayout(new GridLayout());

        //stockSplitPane
        stockSplitPane.setDividerLocation(650);

        //stockTable
        stockTable.setColumnSelectionAllowed(false);
        stockTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null, null, null, null}}, new String[]{"Name", "Symbol", "Price", "Open", "High", "Low", "Market Cap"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        scrollPaneStock.setViewportView(stockTable);
        stockSplitPane.setLeftComponent(scrollPaneStock);

        //stockRightPane
        stockRightPane.setLayout(new FlowLayout());

        //labelName
        labelName.setText("Name");
        stockRightPane.add(labelName);

        //txtSimName
        txtSimName.setMinimumSize(new Dimension(75, 23));
        txtSimName.setPreferredSize(new Dimension(75, 23));
        stockRightPane.add(txtSimName);

        //radioGen
        radioGen.setLabel("Generate new sim");
        radioGen.setActionCommand("new");
        simRadioGroup.add(radioGen);
        stockRightPane.add(radioGen);

        //radioFile
        radioFile.setLabel("Load sim from file");
        radioFile.setActionCommand("file");
        simRadioGroup.add(radioFile);
        stockRightPane.add(radioFile);
        radioGen.setSelected(true);

        //btnStartSim
        btnStartSim.setText("Start Simulation");
        btnStartSim.addActionListener(ALbtnStartSim);
        stockRightPane.add(btnStartSim);

        //btnRefresh
        btnRefresh.setText("Refresh Stock Prices");
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(ALbtnRefresh);
        stockRightPane.add(btnRefresh);

        //labelSelStock
        labelSelStock.setText("Selected Stock");
        stockRightPane.add(labelSelStock);

        //txtStockName
        txtStockName.setMinimumSize(new Dimension(75, 23));
        txtStockName.setPreferredSize(new Dimension(75, 23));
        stockRightPane.add(txtStockName);

        //labelNumShares
        labelNumShares.setText("Number of shares:");
        stockRightPane.add(labelNumShares);

        //txtNumShares
        txtNumShares.setPreferredSize(new Dimension(75, 23));
        stockRightPane.add(txtNumShares);

        //radioBuy
        radioBuy.setLabel("Buy");
        radioBuy.setActionCommand("buy");
        radioBuy.setEnabled(false);
        simTradeGroup.add(radioBuy);
        stockRightPane.add(radioBuy);

        //radioSell
        radioSell.setLabel("Sell");
        radioSell.setActionCommand("sell");
        radioSell.setEnabled(false);
        simTradeGroup.add(radioSell);
        stockRightPane.add(radioSell);

        //btnExec
        btnExec.setText("Execute");
        btnExec.setEnabled(false);
        btnExec.addActionListener(ALbtnExec);
        stockRightPane.add(btnExec);

        //btnSave
        btnSave.setText("Save Simulation to File");
        btnSave.setEnabled(false);
        btnSave.addActionListener(ALbtnSave);
        stockRightPane.add(btnSave);
        stockSplitPane.setRightComponent(stockRightPane);
        stockMarketPanel.add(stockSplitPane);
        tabbedPane.addTab("Stock Market", stockMarketPanel);

        //portfolioPanel
        portfolioPanel.setLayout(new GridLayout());

        //stockSplitPane2
        stockSplitPane2.setDividerLocation(650);

        //portfolioTable
        portfolioTable.setColumnSelectionAllowed(true);
        portfolioTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}}, new String[]{"Name", "Symbol", "Price", "Open"}));
        scrollPaneStock2.setViewportView(portfolioTable);
        stockSplitPane2.setLeftComponent(scrollPaneStock2);

        //portfolioRightPane
        portfolioRightPane.setLayout(new FlowLayout());
        stockSplitPane2.setRightComponent(portfolioRightPane);
        portfolioPanel.add(stockSplitPane2);
        tabbedPane.addTab("Portfolio", portfolioPanel);

        //ledgerPanel
        ledgerPanel.setLayout(new GridLayout());

        //ledgerTable
        ledgerTable.setColumnSelectionAllowed(true);
        ledgerTable.setModel(new DefaultTableModel(
                new Object[][]{
                        {null, null, null, null}
                },
                new String[]{
                        "Name", "Symbol", "Price", "Open"
                }
        ) {
            Class<?>[] columnTypes = new Class<?>[]{
                    String.class, String.class, Double.class, Double.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        scrollPaneLedger.setViewportView(ledgerTable);
        ledgerPanel.add(scrollPaneLedger);
        tabbedPane.addTab("Ledger", ledgerPanel);
        splitLogPane.setTopComponent(tabbedPane);

        //logPanel
        logPanel.setLayout(new BorderLayout());

        //labelLog
        labelLog.setText("Log:");
        logPanel.add(labelLog, BorderLayout.NORTH);

        //textLog
        textLog.setEditable(false);
        scrollPaneLog.setViewportView(textLog);
        textLog.setText("Log Output:");
        logPanel.add(scrollPaneLog, BorderLayout.CENTER);
        splitLogPane.setBottomComponent(logPanel);

        windowFrameContentPane.add(splitLogPane);
        windowFrame.setSize(844, 587);
        windowFrame.setLocationRelativeTo(windowFrame.getOwner());
        windowFrame.setVisible(true);
    }

    private void initListeners() {
        ALbtnStartSim = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean loadFromFile = simRadioGroup.getSelection().getActionCommand().equals("file");
                String playerName = txtSimName.getText();
                try {
                    sim = new SimData(playerName, loadFromFile);
                    simStarted(loadFromFile ? "Details loaded from file" : "New stocks generated");
                    refreshStockTable();
                    refreshPortfTable();
                    refreshLedgerTable();
                } catch (Error er) {
                    printLog("Error occurred");
                }
            }
        };

        ALbtnExec = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedStock = stockTable.getValueAt(stockTable.getSelectedRow(), 1).toString();
                int qty = Integer.parseInt(txtNumShares.getText());
                double price = Double.parseDouble(stockTable.getValueAt(stockTable.getSelectedRow(), 2).toString());
                boolean buy = simTradeGroup.getSelection().getActionCommand().equals("buy");

                if (buy) {
                    if (sim.getPortfolio().buyStock(selectedStock, qty, price)) {
                        printLog("Purchased " + qty + " of " + selectedStock + " at " + price + " per share\nSee full details in Ledger");
                        sim.updRandomStocks();
                    } else {
                        printLog("Stock was not purchased");
                    }
                } else {
                    if (sim.getPortfolio().sellStock(selectedStock, qty, price)) {
                        printLog("Sold " + qty + " of " + selectedStock + " at " + price + " per share\nSee full details in Ledger");
                        sim.updRandomStocks();
                    } else {
                        printLog("Stock could not be sold. Please check that you have enough stock to execute this sale.");
                    }
                }
                refreshStockTable();
                refreshPortfTable();
                refreshLedgerTable();
            }
        };

        ALbtnSave = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sim.saveToFile();
                printLog("Simulation saved");
            }
        };

        ALbtnRefresh = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sim.updRandomStocks();
                refreshStockTable();
                printLog("Stock prices updated");
            }
        };
    }

    //Convenient methods
    private void refreshLedgerTable() {
        ledgerTable.setModel(new DefaultTableModel(sim.getPortfolio().getLedger().rowsToArray(), sim.getPortfolio().getLedger().columnsNamesToArray()) {
            Class<?>[] columnTypes = new Class<?>[]{
                    String.class, String.class, String.class, String.class, String.class, String.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    private void refreshPortfTable() {
        portfolioTable.setModel(new DefaultTableModel(sim.getPortfolio().rowsToArray(), sim.getPortfolio().columnsNamesToArray()) {
            Class<?>[] columnTypes = new Class<?>[]{
                    String.class, String.class, String.class, String.class, String.class, String.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    private void refreshStockTable() {
        sim.getMarket().sortTable("Name");
        stockTable.setModel(new DefaultTableModel(sim.getMarket().rowsToArray(), sim.getMarket().columnsNamesToArray()) {
            Class<?>[] columnTypes = new Class<?>[]{
                    String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        stockTable.
                getSelectionModel().
                addListSelectionListener(new ListSelectionListener() {
                                             public void valueChanged(ListSelectionEvent event) {
                                                 if (stockTable.getSelectedRow() != -1) {
                                                     selectedRow = stockTable.getSelectedRow();
                                                     rowSelected();
                                                 } else {
                                                     nothingSelected();
                                                 }
                                             }
                                         }
                );
    }

    private void nothingSelected() {
        btnExec.setEnabled(false);
        radioSell.setEnabled(false);
        radioBuy.setEnabled(false);
        txtNumShares.setEnabled(false);
        txtNumShares.setText("");
        txtStockName.setText("");
    }

    private void rowSelected() {
        txtStockName.setText(stockTable.getValueAt(stockTable.getSelectedRow(), 1).toString());
        btnExec.setEnabled(true);
        radioSell.setEnabled(true);
        radioBuy.setEnabled(true);
        txtNumShares.setEnabled(true);
    }

    private void simStarted(String msg) {
        printLog("Simulation started - " + msg);
        btnStartSim.setEnabled(false);
        radioFile.setEnabled(false);
        radioGen.setEnabled(false);
        txtSimName.setEnabled(false);
        btnRefresh.setEnabled(true);
        btnSave.setEnabled(true);
        btnExec.setEnabled(true);
    }

    private void printLog(String message) {
        textLog.setText(textLog.getText() + "\n" + message);
    }
}
