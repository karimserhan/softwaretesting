import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Karim on 4/21/2016.
 */
public class TraceFrame {
    private JPanel mainPanel;
    private JPanel tracePanel;

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
        private final int defaultStroke = 2;

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
        public void paintComponent(Graphics g)
        {
            drawTrace(g);
        }

        private void drawTrace(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            int width = getWidth() - 2*padding;
            int height = getHeight();

            //  Draw lines starting from left to bottom
            int x = padding;
            int y = lineGap;

            g2.setFont(new Font(null, Font.BOLD, 30));
            g2.setStroke(new BasicStroke(defaultStroke));
            for (int i = 0; i < computation.getNumberOfProcesses(); i++)
            {
                g2.drawString("P" + i, x - 50, y + 10);
                g2.drawLine(x, y, x + width, y);
                java.util.List<Integer> localTrace = computation.getProcessEvents(i);
                int separation = width / (localTrace.size()+1);
                int point_x = x + separation;
                g2.setStroke(new BasicStroke(10));
                for (int j = 0; j < localTrace.size(); j++) {
                    Event evt = computation.getEventById(localTrace.get(j));
                    g2.setColor(Color.BLACK);
                    g2.drawLine(point_x, y, point_x, y);
                    eventCoords.put(localTrace.get(j), new Coords(point_x, y));
                    point_x += separation;
                }
                g2.setColor(Color.BLACK);

                y += lineGap;
                g2.setStroke(new BasicStroke(defaultStroke));
            }

            Map<Integer, Set<Integer>> allMessages = computation.getMessages();
            Map<Integer, Set<Integer>> syncMessages = computation.getSyncMessages();

            for (Map.Entry<Integer, Set<Integer>> msg : computation.getMessages().entrySet()) {
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
            g2.setStroke(new BasicStroke(defaultStroke));
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

    Computation computation;

    TraceFrame(Computation computation) {
        this.computation = computation;
        this.tracePanel.add(new TracePanel(computation), BorderLayout.NORTH);
    }

    public static void showFrame(Computation computation) {
        JFrame frame = new JFrame("TraceFrame");
        frame.setContentPane(new TraceFrame(computation).mainPanel);//new TracePanel(computation));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
