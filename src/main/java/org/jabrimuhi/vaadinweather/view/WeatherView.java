package org.jabrimuhi.vaadinweather.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import org.jabrimuhi.vaadinweather.entity.WeatherHistory;
import org.jabrimuhi.vaadinweather.model.WeatherData;
import org.jabrimuhi.vaadinweather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route("")
public class WeatherView extends VerticalLayout {
    private final WeatherService weatherService;
    private final Grid<WeatherHistory> historyGrid = new Grid<>(WeatherHistory.class);
    private final Div weatherResult = new Div();

    @Autowired
    public WeatherView(WeatherService weatherService) {
        this.weatherService = weatherService;

        NumberField latitudeField = new NumberField("Широта");
        NumberField longitudeField = new NumberField("Долгота");

        latitudeField.setPlaceholder("Например: 55.75");
        longitudeField.setPlaceholder("Например: 37.62");

        latitudeField.setMin(-90);
        latitudeField.setMax(90);
        longitudeField.setMin(-180);
        longitudeField.setMax(180);


        Button getWeatherButton = new Button("Показать", event -> {
            if (latitudeField.getValue() != null && longitudeField.getValue() != null) {
                if (latitudeField.getValue() < -90 || latitudeField.getValue() > 90 ||
                        longitudeField.getValue() < -180 || longitudeField.getValue() > 180) {

                    showErrorAlert("Неверные координаты! Широта: -90 до 90, Долгота: -180 до 180");
                    return;
                }
                WeatherData data = weatherService.getWeather(latitudeField.getValue(), longitudeField.getValue());
                if (data != null) {
                    showWeather(data);
                    updateHistory();
                }
            }
        });


        weatherResult.getStyle()
                .set("border", "1px solid #ccc")
                .set("padding", "10px")
                .set("width", "50%")
                .set("font-size", "24px")
                .set("min-height", "50px");

        HorizontalLayout inputLayout = new HorizontalLayout(latitudeField, longitudeField, getWeatherButton);
        inputLayout.setSpacing(true);
        inputLayout.setAlignItems(Alignment.END);

        historyGrid.setColumns("latitude", "longitude", "description", "timestamp");
        historyGrid.addColumn(history -> formatDate(history.getTimestamp()))
                .setHeader("Date")
                .setSortable(true);

        historyGrid.addColumn(history -> formatTime(history.getTimestamp()))
                .setHeader("Time")
                .setSortable(true);
        historyGrid.removeColumnByKey("timestamp"); // Убираем старый timestamp

        historyGrid.setWidth("100%");


        Div separator = new Div();
        separator.getStyle().set("border-top", "2px solid #ccc").set("margin", "20px 0");

        // Основная разметка
        HorizontalLayout mainLayout = new HorizontalLayout(weatherResult, historyGrid);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(2, weatherResult);
        mainLayout.setFlexGrow(1, historyGrid);

        add(new H2("Погодный сервис"), inputLayout, separator, mainLayout);
        updateHistory();
    }

    private void showWeather(WeatherData data) {
        weatherResult.removeAll();

        Image weatherPic = new Image();
        Image weatherIcon = new Image(data.getIconUrl(), "Weather Icon");
        weatherIcon.getStyle()
                .setWidth("100px")
                .setHeight("100px");

        if (data.getTemperature() >= 20) {
            weatherPic.setSrc("sunny.png");
        } else if (data.getTemperature() >= 10) {
            weatherPic.setSrc("chilly.png");
        } else {
            weatherPic.setSrc("cold.png");
        }

        weatherPic.getStyle()
                .set("float", "right")
                .set("border-radius", "5px")
                .set("margin-right", "5px")
                .setWidth("250px")
                .setHeight("250px");

        weatherResult.add(
                new Div(new H2(data.getDescription())),
                new Div("Температура: " + Math.round(data.getTemperature()) + "°C"),
                new Div("Ощущается как: " + Math.round(data.getFeelsLikeTemperature()) + "°C"),
                weatherIcon,
                weatherPic
        );
    }

    private void updateHistory() {
        List<WeatherHistory> historyList = weatherService.getWeatherHistory()
                .stream()
                .sorted(Comparator.comparing(WeatherHistory::getTimestamp).reversed())
                .limit(20)
                .collect(Collectors.toList());

        historyGrid.setItems(historyList);
    }

    private String formatDate(LocalDateTime timestamp) {
        return timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private String formatTime(LocalDateTime timestamp) {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void showErrorAlert(String message) {
        UI.getCurrent().getPage().executeJs("alert($0)", message);
    }

}