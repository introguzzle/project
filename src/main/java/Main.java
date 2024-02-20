public class Main {

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ru.grapher.Grapher g = new ru.grapher.Grapher();
                g.setVisible(true);
            }
        });
    }
}
