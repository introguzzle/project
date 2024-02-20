package ru.grapher.menu;

import ru.grapher.GrapherGUI;

import javax.swing.*;
import java.awt.*;

public class HelpFrame extends InformationFrame {

    private static final Font   FONT = new Font("Arial", Font.PLAIN, 20);

    private static final String TEXT =
            "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "</head>" +

                    "<body style=\"font-family: " + FONT.getFamily() + "font-size: 30px" + "\"" +

                    "   <h1 style=\"text-align: center;\"> Справка </h1>" +
                    "   <font size=\"5\">" +
                    "   <p>" +

                    "       Кнопка <b>Add</b> вызывает окно, где вводится функция." +
                    "       Функция обязательно начинается с <b>f(x)</b>," +
                    "       иначе парсер не распознает её." +
                    "       Если функция неявная, можно попробовать" +
                    "       её ввести ( работа не гарантирована )." +
                    "       <br>" +
                    "       <br>" +
                    "       <b>Пример:</b> y + 1 = x - 1 + sqrt(y)" +

                    "   </p>" +

                    "   <p>" +

                    "       Вообще говоря, можно понять, готова ли" +
                    "       программа принять ввод, по цвету надписи" +
                    "       на кнопке <b>Confirm</b>. Если она зелёная, то " +
                    "       можно запросить рисование, если красная," +
                    "       то с функцией что-то не так." +
                    "       <br>" +
                    "       <br>" +

                    "       Она светится красным и в том случае, если" +
                    "       введены не все значения коэффициентов." +
                    "       <br>" +
                    "       <br>" +

                    "       Нажмите кнопку <b>Generate</b>, чтобы сгенерировать" +
                    "       гарантированно-правильную функцию." +
                    "       Также внутри есть кнопка <b>Parametric</b>, где можно" +
                    "       ввести функцию в параметрическом виде." +
                    "       <br>" +
                    "       <br>" +


                    "       Логика такая же, за исключением того, " +
                    "       что нужно ввести 2 функции для <b>x</b> и <b>y</b>" +
                    "       <br>" +
                    "       <br>" +

                    "       Кнопка <b>Clear</b> удаляет все графики." +
                    "       <br>" +

                    "       Кнопка <b>Reset</b> сбрасывает зум." +
                    "       <br>" +

                    "       Кнопка <b>Range</b> позволяет установить границы" +
                    "       изменения коэффициентов. Можно ли" +
                    "       поставить значения, которые вы указали," +
                    "       опять же можно узнать по цвету кнопки <b>Confirm</b>." +
                    "       <br>" +
                    "       <br>" +

                    "       Также на панели представлены три элемента:" +
                    "       выпадающий бокс содержит коэффициенты" +
                    "       всех функций," +
                    "       левый слайдер управляет значением выбранного" +
                    "       коэффициента," +
                    "       а правый, соответственно, зумом" +
                    "       <br>" +
                    "       <br>" +

                    "       Удачи в использовании." +
                    "       <br>" +
                    "       <br>" +
                    "       <br>" +
                    "       <br>" +


                    "   </p>" +

                    "   </font>" +

                    "</body>" +
                    "</html>";

    public HelpFrame(JFrame owner) {
        super(owner, TEXT, FONT);

        this.setIconImage(GrapherGUI.__IMAGE);
        this.setTitle("Help");
    }

    public static void main(String... a) {
        String style = "";

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (style.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                 InstantiationException ignored) {

        }

        EventQueue.invokeLater(() -> {
            HelpFrame instance = new HelpFrame(null);
            instance.setVisible(true);
        });
    }
}