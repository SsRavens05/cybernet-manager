package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class QuanLyTaiChinhController {

    @FXML private BarChart<String, Number> barChartDoanhThu;
    @FXML private LineChart<String, Number> lineChartLoiNhuan;

    @FXML
    public void initialize() {
        // --- 1. ĐỔ DỮ LIỆU BIỂU ĐỒ CỘT (Doanh Thu vs Chi Phí) ---
        XYChart.Series<String, Number> seriesThu = new XYChart.Series<>();
        seriesThu.setName("Doanh Thu");
        seriesThu.getData().add(new XYChart.Data<>("T1", 4.5));
        seriesThu.getData().add(new XYChart.Data<>("T2", 5.2));
        seriesThu.getData().add(new XYChart.Data<>("T3", 4.8));
        seriesThu.getData().add(new XYChart.Data<>("T4", 6.1));
        seriesThu.getData().add(new XYChart.Data<>("T5", 5.8));
        seriesThu.getData().add(new XYChart.Data<>("T6", 7.2));

        XYChart.Series<String, Number> seriesChi = new XYChart.Series<>();
        seriesChi.setName("Chi Phí");
        seriesChi.getData().add(new XYChart.Data<>("T1", 1.2));
        seriesChi.getData().add(new XYChart.Data<>("T2", 1.0));
        seriesChi.getData().add(new XYChart.Data<>("T3", 1.5));
        seriesChi.getData().add(new XYChart.Data<>("T4", 1.1));
        seriesChi.getData().add(new XYChart.Data<>("T5", 1.3));
        seriesChi.getData().add(new XYChart.Data<>("T6", 1.6));

        barChartDoanhThu.getData().addAll(seriesThu, seriesChi);

        // --- 2. ĐỔ DỮ LIỆU BIỂU ĐỒ ĐƯỜNG (Lợi Nhuận) ---
        XYChart.Series<String, Number> seriesLoiNhuan = new XYChart.Series<>();
        seriesLoiNhuan.setName("Lợi Nhuận");
        seriesLoiNhuan.getData().add(new XYChart.Data<>("T1", 3.3));
        seriesLoiNhuan.getData().add(new XYChart.Data<>("T2", 4.2));
        seriesLoiNhuan.getData().add(new XYChart.Data<>("T3", 3.3));
        seriesLoiNhuan.getData().add(new XYChart.Data<>("T4", 5.0));
        seriesLoiNhuan.getData().add(new XYChart.Data<>("T5", 4.5));
        seriesLoiNhuan.getData().add(new XYChart.Data<>("T6", 5.6));

        lineChartLoiNhuan.getData().add(seriesLoiNhuan);
    }
}