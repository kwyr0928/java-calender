import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CalendarApp extends JFrame {
	ArrayList<Item> schedule = new ArrayList<Item>(); // 予定Itemを格納
	JComboBox<String> monthComboBox, yearComboBox,themeComboBox,jobComboBox1,jobComboBox2; // 年月、テーマ、時間の選択
	//右上
	JButton displayButton; // カレンダーの更新
	JButton addButton; // 予定の追加
	JButton jobButton; // シフトの追加
	//左中央
	JButton confirmButton; //予定の確認
	JButton updateButton; // 予定の更新
	JButton removeButton; // 予定の削除
	//下
	JList list; // 予定を表示
	JRadioButton radioButton1, radioButton2, radioButton3,radioButton4,radioButton5; // 予定の種類分け
	JTextField field; // 予定の詳細
	JTable calendarTable; // カレンダーのテーブル
	JLabel monthLabel; // 月のラベル
	Calendar calendar; // カレンダー
	SimpleDateFormat sdf; // 日付のフォーマット
	DefaultTableModel tableModel; // 日付を格納

	int theme; // テーマ番号

	public CalendarApp() {

		setTitle("自由課題");
		setSize(700, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		calendar = Calendar.getInstance(); // カレンダー情報を取得
		sdf = new SimpleDateFormat("MMMM yyyy"); // フォーマットを設定

		String[] hours = new String[24]; // 24時間
		for (int i = 0; i < 24; i++) {
			hours[i] = String.format("%02d:00", i); // 時間を設定
		}

		try {
			File file = new File("calendar.txt"); // 起動時にファイルを読み込む
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] data = line.split(",");
				for(int i=0; i<7; i++) {
					if(data[i].contains("null")) {
						data[i] = null;
					}
				}
				schedule.add(new Item(data[0],data[1],data[2],data[3],data[4],data[5],data[6],data[7])); // 予定Itemを作成しscheduleに格納
			}
			reader.close();
		} catch (Exception f) {
			f.printStackTrace();
		}

		monthLabel = new JLabel(sdf.format(calendar.getTime()));
		monthComboBox = new JComboBox<>(new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"}); // 月の選択
		yearComboBox = new JComboBox<>(new String[]{"2022年", "2023年", "2024年", "2025年"}); // 年の選択
		themeComboBox = new JComboBox<>(new String[]{"シンプル", "水色×ピンク", "紫×オレンジ", "水色×黄色"}); // テーマカラー選択
		jobComboBox1 = new JComboBox<>(hours); // 時間選択
		jobComboBox2 = new JComboBox<>(hours); // 時間選択

		JPanel button1 = new JPanel(new GridLayout(1, 4)); // 右上ボタン
		displayButton = new JButton( new DisplayAction() ); // カレンダーの更新
		JPanel sub = new JPanel(new GridLayout(1,2));
		button1.add(sub);
		button1.add( displayButton );
		addButton = new JButton( new AddAction() ); // 予定の追加
		button1.add( addButton );
		jobButton = new JButton( new JobAction() ); // シフトの追加
		button1.add( jobButton );
		sub.add(yearComboBox);
		sub.add(monthComboBox);

		JPanel button2 = new JPanel();
		button2.setLayout( new GridLayout(1, 3) ); // 左中央ボタン
		confirmButton = new JButton(new ConfirmAction() ); // 予定の確認
		button2.add( confirmButton );
		updateButton = new JButton( new DetailAction() ); // 予定の更新
		button2.add( updateButton );
		removeButton = new JButton( new RemoveAction() ); // 予定の削除
		button2.add( removeButton ); 

		String[] columnNames = {"日", "月", "火", "水", "木", "金", "土"}; // 曜日を設定
		String[][] data = new String[5][7]; // セルを設定

		tableModel = new DefaultTableModel(data, columnNames); // 格納場所を確保
		calendarTable = new JTable(tableModel); // テーブルの作成
		calendarTable.setDefaultRenderer(Object.class, new CalendarCellRenderer()); // 描画の設定
		calendarTable.setRowHeight(30);
		calendarTable.addMouseListener(new CellSelect()); // セル選択時の挙動を設定
		updateCalendar(); // カレンダーを更新

		JPanel radioPanel = new JPanel(); // ラジオボタン
		radioButton1 = new JRadioButton("大学"); // 予定の種類分け
		radioButton2 = new JRadioButton("遊び");
		radioButton3 = new JRadioButton("誕生日");
		radioButton4 = new JRadioButton("アルバイト");
		radioButton5 = new JRadioButton("その他");
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(radioButton1);
		buttonGroup.add(radioButton2);
		buttonGroup.add(radioButton3);
		buttonGroup.add(radioButton4);
		buttonGroup.add(radioButton5);
		radioPanel.add(radioButton1);
		radioPanel.add(radioButton2);
		radioPanel.add(radioButton3);
		radioPanel.add(radioButton4);
		radioPanel.add(radioButton5);

		field = new JTextField();
		field.setBorder( new TitledBorder( "予定の詳細" ) );

		DefaultListModel listModel = new DefaultListModel();
		list = new JList( listModel );
		JScrollPane sc = new JScrollPane( list );
		sc.setBorder( new TitledBorder( "予定一覧" )); // 日にち選択時に予定一覧を表示する

		JPanel topPanel = new JPanel(); // 上
		JPanel buttomPanel1 = new JPanel(); // 下1
		JPanel buttomPanel2 = new JPanel(); // 下2
		JPanel buttomPanel = new JPanel(new GridLayout()); 
		buttomPanel1.setLayout(new BoxLayout(buttomPanel1, BoxLayout.Y_AXIS));
		topPanel.add(button1); // ボタン
		buttomPanel1.add(button2);
		buttomPanel1.add(radioPanel);
		buttomPanel1.add(field);
		buttomPanel2.add(sc);
		buttomPanel.add(buttomPanel1);
		buttomPanel.add(buttomPanel2);

		add(topPanel, BorderLayout.NORTH)	; // 上
		add(new JScrollPane(calendarTable), BorderLayout.CENTER); // 中央 カレンダー
		add(buttomPanel, BorderLayout.SOUTH); // 下

		JMenuBar menuBar = new JMenuBar(); // メニューバー
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("ファイル");
		menuBar.add(fileMenu);
		JMenuItem item; // メニュー内容
		item = new JMenuItem( new ThemeAction() ); // テーマ変更
		fileMenu.add(item);
		item = new JMenuItem( new SaveAction()); // 保存
		fileMenu.add(item);
		fileMenu.addSeparator(); // 境界線
		item = new JMenuItem( new ExitAction(this)); // 終了
		fileMenu.add(item);
	}

	public void updateCalendar() { // カレンダーの更新
		monthLabel.setText(sdf.format(calendar.getTime())); // 現在の年月日を取得
		calendar.set(Calendar.DAY_OF_MONTH, 1); // その月の最初の日
		int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 月の最初の曜日
		int offset = (firstDayOfWeek - Calendar.SUNDAY + 7) % 7; // 曜日のずれを計算
		calendar.add(Calendar.DAY_OF_MONTH, -offset); // 曜日の表示を調整

		for (int row = 0; row < 5; row++) { // 行
			for (int col = 0; col < 7; col++) { // 列
				int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 現在の日
				calendar.add(Calendar.DAY_OF_MONTH, 1); // 1日進める
				tableModel.setValueAt(String.valueOf(dayOfMonth), row, col); // セルに数字を追加
			}
		}

	}

	class CellSelect implements MouseListener { // セル選択時の挙動
		public void mouseClicked(MouseEvent e) {
			DefaultListModel model = (DefaultListModel) list.getModel();
			model.clear();
			for(Item item : schedule) {
				int selectedRow = calendarTable.getSelectedRow(); // 選択した行
				int selectedCol = calendarTable.getSelectedColumn(); // 選択した列
				String selectedYear = yearComboBox.getSelectedItem().toString(); // 年
				String selectedMonth = monthComboBox.getSelectedItem().toString(); // 月
				String selectedDate = tableModel.getValueAt(selectedRow, selectedCol).toString(); // 日
				if(selectedYear.equals(item.getYear()) && selectedMonth.equals(item.getMonth()) && selectedDate.equals(item.getDay())) {
					model.addElement(item);
				}
			}
			list.setModel(model);
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
	}

	class DisplayAction extends AbstractAction { // カレンダーの更新
		DisplayAction() {
			putValue(Action.NAME, "カレンダーの更新");
			putValue(Action.SHORT_DESCRIPTION, "カレンダーの更新");
		}
		public void actionPerformed(ActionEvent e) {
			int selectedMonth = monthComboBox.getSelectedIndex(); // 選択した月を取得
			String year = (String)yearComboBox.getSelectedItem(); // 選択した年を取得
			int selectedYear = Integer.parseInt(year.replaceAll("年", ""));  // 数字にする
			calendar = new GregorianCalendar(selectedYear, selectedMonth, 1); // カレンダー作成
			updateCalendar(); // カレンダーの更新
		}
	}

	class AddAction extends AbstractAction { // 予定の追加
		AddAction() {
			putValue( Action.NAME, "予定の追加" );
			putValue( Action.SHORT_DESCRIPTION, "予定の追加" );
		}
		public void actionPerformed( ActionEvent e) {
			int selectedRow = calendarTable.getSelectedRow(); // 選択した行
			int selectedCol = calendarTable.getSelectedColumn(); // 選択した列
			if (selectedRow != -1 && selectedCol != -1 && tableModel.getValueAt(selectedRow, selectedCol) != null) {
				String selectedYear = yearComboBox.getSelectedItem().toString(); // 年
				String selectedMonth = monthComboBox.getSelectedItem().toString(); // 月
				String selectedDate = tableModel.getValueAt(selectedRow, selectedCol).toString(); // 日
				String dateStr = selectedYear + selectedMonth + selectedDate + "日"; // 年月日
				String eventName = JOptionPane.showInputDialog((Component) e.getSource(), dateStr + "に予定を追加する");
				if (eventName != null && !eventName.trim().isEmpty()) {
					DefaultListModel model = (DefaultListModel) list.getModel();
					schedule.add(new Item(selectedYear,selectedMonth,selectedDate,eventName,null,null,null,null)); // 配列に予定を保存
					model.addElement(new Item(selectedYear,selectedMonth,selectedDate,eventName,null,null,null,null));
					list.setModel(model);
				}

			} else {
				JOptionPane.showMessageDialog((Component) e.getSource(), "日付を選択してください");
			}
		} 
	}

	class JobAction extends AbstractAction { // シフトの追加
		JobAction() {
			putValue( Action.NAME, "シフトの追加" );
			putValue( Action.SHORT_DESCRIPTION, "シフトの追加" );
		}
		public void actionPerformed( ActionEvent e) {
			int selectedRow = calendarTable.getSelectedRow(); // 選択した行
			int selectedCol = calendarTable.getSelectedColumn(); // 選択した列

			if (selectedRow != -1 && selectedCol != -1 && tableModel.getValueAt(selectedRow, selectedCol) != null) {
				String selectedYear = yearComboBox.getSelectedItem().toString(); // 年
				String selectedMonth = monthComboBox.getSelectedItem().toString(); // 月
				String selectedDate = tableModel.getValueAt(selectedRow, selectedCol).toString(); // 日
				String dateStr = selectedYear + selectedMonth + selectedDate + "日"; // 年月日
				JPanel panel = new JPanel();
				JLabel startTimeLabel = new JLabel("開始時間:");
				panel.add(startTimeLabel);
				panel.add(jobComboBox1);
				JLabel finishTimeLabel = new JLabel("終了時間:");
				panel.add(finishTimeLabel);
				panel.add(jobComboBox2);
				int result = JOptionPane.showOptionDialog(
						null,
						panel,
						"時間を選択してください",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						null,
						null);
				if (result == JOptionPane.OK_OPTION) {
					DefaultListModel model = (DefaultListModel) list.getModel();
					schedule.add(new Item(selectedYear,selectedMonth,selectedDate,null,(String)jobComboBox1.getSelectedItem(),(String)jobComboBox2.getSelectedItem(),"アルバイト",null)); // 配列に予定を保存
					model.addElement(new Item(selectedYear,selectedMonth,selectedDate,null,(String)jobComboBox1.getSelectedItem(),(String)jobComboBox2.getSelectedItem(),"アルバイト",null));
					list.setModel(model);
				}}else {
					JOptionPane.showMessageDialog((Component) e.getSource(), "日付を選択してください");
				}}
	}

	class ThemeAction extends AbstractAction { // テーマ変更
		ThemeAction() {
			putValue( Action.NAME, "テーマ変更" );
			putValue( Action.SHORT_DESCRIPTION, "テーマ変更" );
		}
		public void actionPerformed( ActionEvent e) {
			JPanel panel = new JPanel();
			JLabel themeLabel = new JLabel("テーマ選択:");
			panel.add(themeLabel);
			panel.add(themeComboBox);
			int result = JOptionPane.showOptionDialog(
					null,
					panel,
					"テーマを選択してください",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					null,
					null);
			if (result == JOptionPane.OK_OPTION) {
				// 選択されたオプションを取得
				theme = themeComboBox.getSelectedIndex();
				calendarTable.setDefaultRenderer(Object.class, new CalendarCellRenderer()); // 描画の設定
			}
		}
	}

	class SaveAction extends AbstractAction { // 保存
		SaveAction() {
			putValue( Action.NAME, "保存" );
			putValue( Action.SHORT_DESCRIPTION, "保存" );
		}
		public void actionPerformed( ActionEvent e) {
			try {
				File file = new File("calendar.txt");
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				for(Item item : schedule) {
					writer.println(item.getYear() + "," + item.getMonth() + "," + item.getDay() + "," + item.getContext() + "," + item.getStart() + "," + item.getFinish() + "," + item.getSort() + "," + item.getDetail());
				}
				writer.close();
			}catch(Exception f) {
				f.printStackTrace();
			}
		}
	}

	class ExitAction extends AbstractAction { // 終了
		Component pane;
		ExitAction(Component pane) {
			this.pane = pane;
			putValue( Action.NAME, "終了" );
			putValue( Action.SHORT_DESCRIPTION, "終了" );
		}
		public void actionPerformed( ActionEvent e) {
			Object msg = "終了しますか？";
			int ans = JOptionPane.showConfirmDialog( pane, msg, "警告",
					JOptionPane.YES_NO_CANCEL_OPTION );
			if ( ans == 0 ) {
				System.exit(0);
			}
		}
	}

	class ConfirmAction extends AbstractAction { // 予定の確認
		ConfirmAction() {
			putValue( Action.NAME, "予定の確認" );
			putValue( Action.SHORT_DESCRIPTION, "予定の確認" );
		}
		public void actionPerformed( ActionEvent e) {
			int index =  list.getSelectedIndex();
			if( index < 0 ) { // 選択されていなかったら何もしない
				return;
			}
			DefaultListModel model = (DefaultListModel)list.getModel();
			JPanel panel = new JPanel();
			Item item = (Item) model.getElementAt(index);
			Object msg;
			if(item.getStart() == null || item.getFinish() == null) {
				msg = "予定\n"
						+ item.getContext() + "\n"
						+ "分類:" +item.getSort() + "  詳細:" + item.getDetail();
			}else {
				msg = "アルバイト\n"
						+ item.getStart() + "～" + item.getFinish() + "\n"
						+ "分類:" +item.getSort() + "  詳細:" + item.getDetail();
			}
			JOptionPane.showConfirmDialog( panel, msg, "予定の確認",
					JOptionPane.CLOSED_OPTION );
		}
	}

	class DetailAction extends AbstractAction{ // 予定の変更
		DetailAction(){
			putValue( Action.NAME, "予定の変更" );
			putValue( Action.SHORT_DESCRIPTION, "予定の変更" );
		}
		public void actionPerformed( ActionEvent e) {
			int index =  list.getSelectedIndex();
			if( index < 0 ) {
				return;
			}

			DefaultListModel model = (DefaultListModel)list.getModel();
			JPanel panel = new JPanel();
			Item item = (Item) model.getElementAt(index);
			Item item2 = schedule.get(index);
			if (radioButton1.isSelected()) {
				item.setSort("大学"); // 分類を大学に
				item2.setSort("大学"); // 分類を大学に
			} else if (radioButton2.isSelected()) {
				item.setSort("遊び"); // 分類を遊びに
				item2.setSort("遊び"); // 分類を遊びに
			} else if (radioButton3.isSelected()) {
				item.setSort("誕生日"); // 分類を誕生日に
				item2.setSort("誕生日"); // 分類を誕生日に
			} else if (radioButton4.isSelected()) {
				item.setSort("アルバイト"); // 分類をアルバイトに
				item2.setSort("アルバイト"); // 分類をアルバイトに
			} else if (radioButton5.isSelected()) {
				item.setSort("その他"); // 分類をその他に
				item2.setSort("その他"); // 分類をその他に
			}
			item.setDetail(field.getText());
			item2.setDetail(field.getText()); // 分類を大学に
			field.setText("");
		}
	}

	class RemoveAction extends AbstractAction { // 予定の削除
		RemoveAction() {
			putValue( Action.NAME, "予定の削除" );
			putValue( Action.SHORT_DESCRIPTION, "予定の削除" );
		}
		public void actionPerformed( ActionEvent e) {
			int index =  list.getSelectedIndex();
			if( index < 0 ) { // 選択されていなかったら何もしない
				return;
			}
			DefaultListModel model = (DefaultListModel)list.getModel();
			Object[] msg = { "予定を削除します" };
			int ans = JOptionPane.showConfirmDialog( (Component) e.getSource(),msg, "はい・いいえ・取消し",
					JOptionPane.YES_NO_CANCEL_OPTION );
			if( ans==0 ){ //削除
				model.remove( index ); // リストから削除
				schedule.remove(index); // 配列から削除
			}
			list.clearSelection(); // 選択を解除
		}
	}

	private class CalendarCellRenderer extends DefaultTableCellRenderer { // 描画の設定
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			switch(theme) {
			case 0:
				if (column == 6) {
					cell.setBackground(new Color(220, 220, 255)); //  淡い青 土曜
				} else if (column == 0) {
					cell.setBackground(new Color(255, 220, 220)); // 淡い赤 日曜
				} else {
					cell.setBackground(Color.white); // 白 平日
				}
				break;
			case 1:
				if (column == 6) {
					cell.setBackground(new Color(219, 253, 254)); // 水色 土曜
				} else if (column == 0) {
					cell.setBackground(new Color(255, 215, 245)); // ピンク 日曜
				} else {
					cell.setBackground(Color.white); // 白 平日
				}
				break;
			case 2:
				if (column == 6) {
					cell.setBackground(new Color(236, 222, 255)); //  紫 土曜
				} else if (column == 0) {
					cell.setBackground(new Color(255, 238, 222)); // オレンジ 日曜
				} else {
					cell.setBackground(Color.white); // 白 平日
				}
				break;
			case 3:
				if (column == 6) {
					cell.setBackground(new Color(219, 253, 254)); // 水色 土曜
				} else if (column == 0) {
					cell.setBackground(new Color(255, 254, 219)); // 黄色 日曜
				} else {
					cell.setBackground(Color.white); // 白 平日
				}
				break;
			default:
				if (column == 6) {
					cell.setBackground(new Color(220, 220, 255)); //  淡い青 土曜
				} else if (column == 0) {
					cell.setBackground(new Color(255, 220, 220)); // 淡い赤 日曜
				} else {
					cell.setBackground(Color.white); // 白 平日
				}

			}
			return cell;
		}
	}

	public static void main(String[] args) {
		CalendarApp calendarApp = new CalendarApp(); // 実行
		calendarApp.setVisible(true);
	}
}


