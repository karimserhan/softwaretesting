import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Line2D;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TraceFrame {
    private JPanel mainPanel;
    private JPanel tracePanel;
    private JComboBox computationComboBox;

    private class Coords {
        int x;
        int y;

        Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public class TracePanel extends JPanel {
        private final int lineGap = 100;
        private final int padding = 60;
        private final int windowWidth = 1500;
        private final int processStroke = 2;
        private final int eventStroke = 10;

        private Computation computation;
        private HashMap<Integer, Coords> eventCoords;

        public TracePanel(Computation computation) {
            this.computation = computation;
            this.eventCoords = new HashMap<>();
        }

        @Override
        public Dimension getPreferredSize() {
            int size = (computation.getNumberOfProcesses() + 1) * lineGap;
            return new Dimension(windowWidth, size);
        }

        @Override
        public void paintComponent(Graphics g) {
            drawTrace(g);
        }

        private void drawTrace(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int processWidth = getWidth() - 2*padding;

            //  Draw process lines
            int x = padding;
            int y = lineGap;

            g2.setFont(new Font(null, Font.BOLD, 30));
            g2.setStroke(new BasicStroke(processStroke));
            for (int i = 0; i < computation.getNumberOfProcesses(); i++) {
                g2.drawString("P" + i, x - 50, y + 10);
                g2.drawLine(x, y, x + processWidth, y);
                y += lineGap;
            }

            java.util.List<Set<Integer>> sortedEvents = computation.topologicalSort();
            int maxDepth = sortedEvents.size();
            int separation = processWidth / (maxDepth+1);
            x = padding + separation; // x-coords for first-level events

            g2.setStroke(new BasicStroke(eventStroke));
            for (Set<Integer> sameLevelEvents : sortedEvents) {
                for (int evtId : sameLevelEvents) {
                    int processId = computation.getEventById(evtId).getProcessId();
                    y = lineGap * (processId+1);
                    g2.setColor(Color.BLACK);
                    g2.drawLine(x, y, x, y);
                    eventCoords.put(evtId, new Coords(x, y));
                }
                x += separation;
            }

            Map<Integer, Set<Integer>> allMessages = computation.getMessages();
            Map<Integer, Set<Integer>> syncMessages = computation.getSyncMessages();

            for (Map.Entry<Integer, Set<Integer>> msg : allMessages.entrySet()) {
                int sendId = msg.getKey();
                for (int receiveId : msg.getValue()) {
                    if (syncMessages.get(sendId) == null || !syncMessages.get(sendId).contains(receiveId)) {
                        drawMessage(g, sendId, receiveId, Color.BLACK);
                    } else {
                        drawMessage(g, sendId, receiveId, Color.RED);
                    }
                }
            }
        }

        private void drawMessage(Graphics g, int e, int f, Color color) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(color);
            g2.setStroke(new BasicStroke(processStroke));
            Event sendEvt = computation.getEventById(e);
            Event recvEvt = computation.getEventById(f);
            if (sendEvt.getProcessId() == recvEvt.getProcessId()) {
                return;
            }

            Coords sendPos = eventCoords.get(e);
            Coords recvPos = eventCoords.get(f);
            int x1 = sendPos.x;
            int y1 = sendPos.y;
            int x2 = recvPos.x;
            int y2 = recvPos.y;

            // draw arrow
            double theta = Math.atan2(y2 - y1, x2 - x1);

            int barb = 20;
            double phi = Math.PI/6;
            double x = x2 - barb * Math.cos(theta + phi);
            double y = y2 - barb * Math.sin(theta + phi);
            g2.draw(new Line2D.Double(x2, y2, x, y));
            x = x2 - barb * Math.cos(theta - phi);
            y = y2 - barb * Math.sin(theta - phi);
            g2.draw(new Line2D.Double(x2, y2, x, y));
            g2.drawLine(x1, y1, x2, y2);
        }

    }

    Computation originalComputation;
    java.util.List<Computation> generatedComputations;

    TraceFrame(Computation originalComputation, java.util.List<Computation> generatedComputations) {
        this.originalComputation = originalComputation;
        this.generatedComputations = generatedComputations;
        this.tracePanel.add(new TracePanel(originalComputation), BorderLayout.NORTH);

        this.computationComboBox.addItem("Original Computation");
        this.computationComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        for (int i = 0; i < generatedComputations.size(); i++) {
            this.computationComboBox.addItem("Generated Computation #" + (i+1));
        }

        this.computationComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                computationComboBoxItemSelected(e);
            }
        });
    }

    private void computationComboBoxItemSelected(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            int selectedIndex = computationComboBox.getSelectedIndex();
            tracePanel.removeAll();

            if (selectedIndex == 0) {
                tracePanel.add(new TracePanel(originalComputation), BorderLayout.NORTH);
            } else {
                tracePanel.add(new TracePanel(generatedComputations.get(selectedIndex-1)), BorderLayout.NORTH);
            }

            tracePanel.revalidate();
            tracePanel.repaint();
        }
    }

    public static void showFrame(Computation originalComputation, java.util.List<Computation> generatedComputations) {
        JFrame frame = new JFrame("Traces");
        frame.setContentPane(new TraceFrame(originalComputation, generatedComputations).mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
