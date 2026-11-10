package utils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.io.*;
import java.nio.charset.StandardCharsets;

public final class ExportReportUtil {

    private ExportReportUtil() {}

    public static void exportJTableToCSV(JTable table, Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file CSV");
        chooser.setSelectedFile(new File("BaoCao.csv"));

        int result = chooser.showSaveDialog(parent);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {

            // Giúp Excel hiển thị đúng tiếng Việt
            writer.write('\ufeff');

            TableModel model = table.getModel();

            // Xuất tên các cột
            for (int col = 0; col < model.getColumnCount(); col++) {
                if (col > 0) writer.write(",");
                writer.write("\"" + model.getColumnName(col).replace("\"", "\"\"") + "\"");
            }
            writer.write("\r\n");

            // Xuất dữ liệu trong bảng
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    if (col > 0) writer.write(",");

                    Object value = table.getValueAt(row, col);
                    String text = value == null ? "" : value.toString();

                    writer.write("\"" + text.replace("\"", "\"\"")
                            .replace("\r", " ").replace("\n", " ") + "\"");
                }
                writer.write("\r\n");
            }

            JOptionPane.showMessageDialog(parent,
                    "Xuất CSV thành công!\n" + file.getAbsolutePath());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent,
                    "Lỗi khi xuất CSV: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}