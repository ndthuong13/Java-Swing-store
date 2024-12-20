package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import controller.BillController;
import controller.UserController;
import dao.BillDAO;
import dao.UserDAO;
import model.Bill;
import model.User;
import util.ButtonHover;

public class BillManagementView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private JTextField searchField;
	private JComboBox<String> searchOptionComboBox;
	private JTable table;
	private BillController billController;
	private BillDAO billDAO;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BillManagementView(User user) {
		billDAO = new BillDAO();
		billController = new BillController(billDAO, this);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				new Home(user);
			}
		});

		setResizable(false);
		setTitle("Quản lý tiệm tạp hoá Xanh");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 840, 720);
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(mainPanel);
		mainPanel.setLayout(new BorderLayout(0, 0));

		JLabel titleLabel = new JLabel("Quản lý hoá đơn", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 28));
		mainPanel.add(titleLabel, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		mainPanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel headerPanel = new JPanel();
		panel.add(headerPanel, BorderLayout.NORTH);
		FlowLayout fl_headerPanel = new FlowLayout(FlowLayout.LEFT, 5, 5);
		headerPanel.setLayout(fl_headerPanel);

		JButton detailBtn = new JButton("XEM CHI TIẾT");
		detailBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Bill bill = getSelectedBill();
				if (bill == null) {
					JOptionPane.showMessageDialog(BillManagementView.this, "Vui lòng chọn hoá đơn", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				new BillDetailView(bill);
			}
		});
		
		detailBtn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		headerPanel.add(detailBtn);
		detailBtn.setOpaque(false);
		detailBtn.setContentAreaFilled(false);
		detailBtn.setBorderPainted(false);
	
		ButtonHover.addButtonHover(detailBtn);

		JLabel lblNewLabel = new JLabel("                                                             ");
		headerPanel.add(lblNewLabel);

		table = new JTable();
		table.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Mã hoá đơn", "Tên khách hàng",
						"Người tạo", "Ngày tạo", "Tổng tiền" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(0).setMinWidth(100);
		table.getColumnModel().getColumn(0).setMaxWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setMinWidth(200);
		table.getColumnModel().getColumn(1).setMaxWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setMinWidth(200);
		table.getColumnModel().getColumn(2).setMaxWidth(200);
		table.setRowHeight(30);
		table.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollPane, BorderLayout.CENTER);

		try {
			updateBillTable(billController.getAllBills());
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private List<Bill> getDataFromTable() {
		List<Bill> bills = new ArrayList<>();
		DefaultTableModel dtm = (DefaultTableModel) this.table.getModel();
		int rowCount = dtm.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			String id = String.valueOf(dtm.getValueAt(i, 0));
			try {
				Bill bill = billController.getBillById(id);
				bills.add(bill);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		return bills;
	}

	public void updateBillTable(List<Bill> bills) {
		DefaultTableModel dtm = (DefaultTableModel) this.table.getModel();
		dtm.setRowCount(0);
		UserDAO userDao = new UserDAO();
		UserController userController = new UserController(userDao);
		bills.forEach(bill -> {
			try {
				User user = userController.getUserById(bill.getAdminId());
				dtm.addRow(new Object[] { bill.getId(), bill.getName(), user.getUsername(), bill.getDate(),
						bill.getTotal() });
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		});
	}

	public Bill getSelectedBill() {
		DefaultTableModel dtm = (DefaultTableModel) this.table.getModel();
		int row = table.getSelectedRow();
		if (row == -1)
			return null;
		String id = String.valueOf(dtm.getValueAt(row, 0));
		try {
			Bill bill = billController.getBillById(id);
			return bill;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
