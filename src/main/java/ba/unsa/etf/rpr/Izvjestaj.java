package ba.unsa.etf.rpr;



import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class Izvjestaj extends JFrame {
    public void showReport(Connection conn) throws JRException {
        InputStream reportSrcFile = getClass().getResourceAsStream("/reports/grad.jrxml");
        InputStream reportsDir =  getClass().getResourceAsStream("/reports/cherry.jpg)");
        JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
        // Fields for resources path
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("reportsDirPath", reportsDir);
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        list.add(parameters);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);
        JRViewer viewer = new JRViewer(print);
        viewer.setOpaque(true);
        viewer.setVisible(true);
        this.add(viewer);
        this.setSize(800, 500);
        this.setVisible(true);
    }
}
