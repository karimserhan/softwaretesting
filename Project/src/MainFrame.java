import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainFrame {
    class IntFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, string);

            if (test(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            }
        }

        private boolean test(String text) {
            if (text.equals("")) { return true; }
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            if (test(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.delete(offset, offset + length);

            if (test(sb.toString())) {
                super.remove(fb, offset, length);
            }
        }
    }

    class GenerationTask extends SwingWorker<Void, Void> {
        TraceGenerator generator;
        App app;
        Computation computation;
        java.util.List<Computation> generatedComputations;

        public GenerationTask(TraceGenerator generator, App app) {
            this.generator = generator;
            this.app = app;
        }

        @Override
        public Void doInBackground() {
            computation = generator.generateTrace();
            generatedComputations = app.generateComputations(computation);
            return null;
        }

        @Override
        public void done() {
            // re-enable GUI
            enableAll();
            // open trace dialog
            JDialog traceDialog = new TraceDialog(computation, generatedComputations);
            traceDialog.setLocationRelativeTo(mainFrame);
            traceDialog.setVisible(true);
        }
    }

    private static JFrame mainFrame;

    private JButton resetParamsBtn;
    private JButton generateParamsBtn;
    private JPanel mainPanel;
    private JTextField maxNbrProcessesTB;
    private JTextField minNbrEventsTB;
    private JTextField maxNbrEventsTB;
    private JTextField minNbrMessagesTB;
    private JTextField maxNbrMessagesTB;
    private JTextField minNbrVariablesTB;
    private JTextField maxNbrVariablesTB;
    private JTextField minRepeatsTB;
    private JTextField maxRepeatsTB;
    private JTextField minNbrProcessesTB;
    private JCheckBox flowDependenciesDependenciesCheckBox;
    private JCheckBox warCheckBox;
    private JCheckBox wawCheckBox;
    private JCheckBox rawCheckBox;

    public MainFrame() {
        ((PlainDocument)minNbrProcessesTB.getDocument()).setDocumentFilter(new IntFilter());
        ((PlainDocument)maxNbrProcessesTB.getDocument()).setDocumentFilter(new IntFilter());
        ((PlainDocument)minNbrEventsTB.getDocument()).setDocumentFilter(new IntFilter());
        ((PlainDocument)maxNbrEventsTB.getDocument()).setDocumentFilter(new IntFilter());
        ((PlainDocument)minNbrMessagesTB.getDocument()).setDocumentFilter(new IntFilter());
        ((PlainDocument)maxNbrMessagesTB.getDocument()).setDocumentFilter(new IntFilter());
        ((PlainDocument)minNbrVariablesTB.getDocument()).setDocumentFilter(new IntFilter());
        ((PlainDocument)maxNbrVariablesTB.getDocument()).setDocumentFilter(new IntFilter());
        ((PlainDocument)minRepeatsTB.getDocument()).setDocumentFilter(new IntFilter());
        ((PlainDocument)maxRepeatsTB.getDocument()).setDocumentFilter(new IntFilter());

        resetParamsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetParamsBtnActionPerformed(e);
            }
        });

        generateParamsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateParamsBtnActionPerformed(e);
            }
        });
    }

    private void generateParamsBtnActionPerformed(ActionEvent e) {
        if (maxNbrProcessesTB.getText().equals("") || minNbrProcessesTB.getText().equals("")
                || maxNbrEventsTB.getText().equals("") || minNbrEventsTB.getText().equals("")
                || maxNbrMessagesTB.getText().equals("") || minNbrMessagesTB.getText().equals("")
                || maxNbrVariablesTB.getText().equals("") || minNbrVariablesTB.getText().equals("")
                || maxRepeatsTB.getText().equals("") || minRepeatsTB.getText().equals("")) {
            JOptionPane.showMessageDialog(this.mainPanel, "Some fields have not been set", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int maxNbrProcesses = Integer.parseInt(maxNbrProcessesTB.getText());
        int minNbrProcesses  = Integer.parseInt(minNbrProcessesTB.getText());
        int maxNbrEvents = Integer.parseInt(maxNbrEventsTB.getText());
        int minNbrEvents = Integer.parseInt(minNbrEventsTB.getText());
        int maxNbrMsgs = Integer.parseInt(maxNbrMessagesTB.getText());
        int minNbrMsgs = Integer.parseInt(minNbrMessagesTB.getText());
        int maxNbrVariables = Integer.parseInt(maxNbrVariablesTB.getText());
        int minNbrVariables = Integer.parseInt(minNbrVariablesTB.getText());
        int maxRepeats = Integer.parseInt(maxRepeatsTB.getText());
        int minRepeats = Integer.parseInt(minRepeatsTB.getText());

        if (maxNbrProcesses < minNbrProcesses || maxNbrEvents < minNbrEvents || maxNbrMsgs < minNbrMsgs
                || maxNbrVariables < minNbrVariables || maxRepeats < minRepeats) {
            JOptionPane.showMessageDialog(this.mainPanel, "Invalid range for some of the fields: "
                    + "max should be greater than or equal to min", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        disableAll();
        TraceGenerator generator = new TraceGenerator(minNbrProcesses, maxNbrProcesses, minNbrEvents, maxNbrEvents,
                minNbrMsgs, maxNbrMsgs, minNbrVariables, maxNbrVariables, minRepeats, maxRepeats);
        App app = new App(false, rawCheckBox.isSelected(), warCheckBox.isSelected(), wawCheckBox.isSelected());
        // run the task to generate computations
        GenerationTask task = new GenerationTask(generator, app);
        task.execute();
    }

    private void disableAll() {
        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setEnabled(mainFrame.getContentPane(), false);
    }
    private void enableAll() {
        mainFrame.setCursor(Cursor.getDefaultCursor());
        setEnabled(mainFrame.getContentPane(), true);
    }

    private void setEnabled(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container) {
            Container container = (Container)component;
            for (Component child : container.getComponents()) {
                setEnabled(child, enabled);
            }
        }
    }

    private void resetParamsBtnActionPerformed(ActionEvent e) {
        maxNbrProcessesTB.setText("");
        minNbrEventsTB.setText("");
        maxNbrEventsTB.setText("");
        minNbrMessagesTB.setText("");
        maxNbrMessagesTB.setText("");
        minNbrVariablesTB.setText("");
        maxNbrVariablesTB.setText("");
        minRepeatsTB.setText("");
        maxRepeatsTB.setText("");
        minNbrProcessesTB.setText("");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        mainFrame = new JFrame("Computation Parameters");
        mainFrame.setResizable(false);
        mainFrame.setContentPane(new MainFrame().mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();

        // center
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setLocation(dim.width / 2 - mainFrame.getSize().width / 2, dim.height / 2 - mainFrame.getSize().height / 2);

        mainFrame.setVisible(true);
    }
}
