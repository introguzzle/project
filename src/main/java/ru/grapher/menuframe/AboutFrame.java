package ru.grapher.menuframe;

import ru.grapher.core.BrowserRedirector;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AboutFrame extends InformationFrame {

    private static final Font   FONT = new Font("Arial", Font.PLAIN, 20);
    private static final String TEXT =
            "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "</head>" +

                    "<body style=\"font-family: " + FONT.getFamily() + "font-size: 50px" + "\"" +

                    "   <font size=\"999\">" +
                    "   <h1 style=\"text-align: center;\"> Курсовая работа</h1>" +
                    "   </font>" +

                    "   <font size=\"6\">" +
                    "       <p style=\"text-align: center;\"> " +
                    "           Вариант 10" +
                    "           <br>" +
                    "           Выполнил Батожаргалов Балдан" +
                    "           <br>" +
                    "           группа з-422п-10-1" +
                    "           <br>" +
                    "           ТУСУР, 2024 " +
                    "           <br>" +
                    "       </p>" +
                    "   </font>" +


                    "   <font size=\"5\">" +
                    "   <p>" +

                    "       Эта курсовая работа представляет из себя простой аналог Desmos." +
                    "       <br><br>" +

                    "       Использованные библиотеки:" +
                    "       <br><br>" +

                    "       JFreeChart для отображения точек (x,y) и управления графиком" +
                    "       <br>" +

                    "       Java Swing для построения пользовательского интерфейса" +
                    "       <br><br>" +

                    "       Использованные источники:" +
                    "       <br><br>" +

                    "       документация <b>Java Swing</b>," +
                    "       <br>" +

                    "       документация <b>Java</b>," +
                    "       <br>" +

                    "       документация <b>JFreeChart</b>" +
                    "       <br>" +

                    "<a href=\"\"> <b>stackoverflow.com</b> </a>" +
                    "       <br>" +

                    "   </p>" +

                    "   </font>" +

                    "</body>" +
                    "</html>";


    public AboutFrame(JFrame owner) {
        super(owner, "About", TEXT, FONT);

        this.setIconImage(owner.getIconImage());

        this.remove(exitButton);
        this.remove(showButton);

        this.textPane.addHyperlinkListener(new BrowserRedirector(Map.of(
                "stackoverflow.com", "http://stackoverflow.com"
        )));
    }
}
