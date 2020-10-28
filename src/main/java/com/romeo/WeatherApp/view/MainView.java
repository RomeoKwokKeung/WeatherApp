package com.romeo.WeatherApp.view;

import com.romeo.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.Service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SpringUI(path = "")
public class MainView extends UI {

    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLayout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private Button showWeatherButton;
    private Label currentLocationTitle;
    private Label currentTemp;
    private Label weatherDescription;
    private Label weatherMin;
    private Label weatherMax;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label windSpeedLabel;
    private Label sunRiseLabel;
    private Label sunSetLabel;
    private ExternalResource img;
    private Image iconImage;
    private HorizontalLayout dashBoardMain;
    private HorizontalLayout mainDescriptionLayout;
    private VerticalLayout descriptionLayout;
    private VerticalLayout pressureLayout;


    @Override
    protected void init(VaadinRequest request) {
        setUpLayout();
        setHeader();
        setLogo();
        setUpForm();
        dashBoardTitle();
        dashBoardDescription();

        showWeatherButton.addClickListener(event -> {
            if (!cityTextField.getValue().equals("")) {
                try {
                    updateUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Please enter a city");
            }
        });
    }

    public void setUpLayout() {

        iconImage = new Image();
        weatherDescription = new Label("Description: Clear Skies");
        weatherMin = new Label("Min: 56F");
        weatherMax = new Label("Max: 89F");
        pressureLabel = new Label("Pressure: 123pa");
        humidityLabel = new Label("Humidity: 34");
        windSpeedLabel = new Label("Wind Speed: 123/hr");
        sunRiseLabel = new Label("Sunrise:");
        sunSetLabel = new Label("Sunset: ");

        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        setContent(mainLayout);
    }

    private void setHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("Weather");
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);

        headerLayout.addComponent(title);
        mainLayout.addComponent(headerLayout);
    }

    private void setLogo() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Image icon = new Image(null, new ClassResource("/weather_icon.png"));
        icon.setWidth("125px");
        icon.setHeight("125px");

        logoLayout.addComponent(icon);
        mainLayout.addComponent(logoLayout);
    }

    private void setUpForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        // Create the selection component
        unitSelect = new NativeSelect<>();
        unitSelect.setWidth("55px");
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));
        formLayout.addComponent(unitSelect);

        // Add TextField
        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.addComponent(cityTextField);

        // Add Button
        showWeatherButton = new Button();
        showWeatherButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponent(showWeatherButton);

        mainLayout.addComponent(formLayout);
    }

    private void dashBoardTitle() {

        dashBoardMain = new HorizontalLayout();
        dashBoardMain.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        currentLocationTitle = new Label("Currently in Spokane");
        currentLocationTitle.addStyleName(ValoTheme.LABEL_LARGE);
        currentLocationTitle.addStyleName(ValoTheme.LABEL_LIGHT);

        // Current Temp Label
        currentTemp = new Label("19F");
        currentTemp.addStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.addStyleName(ValoTheme.LABEL_H1);
        currentTemp.addStyleName(ValoTheme.LABEL_LIGHT);
    }

    private void dashBoardDescription() {
        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        // Description Vertical Layout
        descriptionLayout = new VerticalLayout();
        descriptionLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        descriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        descriptionLayout.addComponent(weatherDescription);
        descriptionLayout.addComponent(weatherMin);
        descriptionLayout.addComponent(weatherMax);

        // Pressure, humidity, etc...
        pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        pressureLayout.addComponent(pressureLabel);
        pressureLayout.addComponent(humidityLabel);
        pressureLayout.addComponent(windSpeedLabel);
        pressureLayout.addComponent(sunRiseLabel);
        pressureLayout.addComponent(sunSetLabel);
    }

    private void updateUI() throws JSONException {
        String city = cityTextField.getValue();
        String defaultUnit;
        String unit;

        if (unitSelect.getValue().equals("F")) {
            defaultUnit = "imperial";
            unitSelect.setValue("F");
            // Degree Sign
            unit = "\u00b0" + "F";
        } else {
            defaultUnit = "metric";
            unitSelect.setValue("c");
            // Degree Sign
            unit = "\u00b0" + "C";
        }

        weatherService.setCityName(city);
        weatherService.setUnit(defaultUnit);

        currentLocationTitle.setValue("Currently in " + city);

        JSONObject myObject = weatherService.returnMainObject();
        double temp = myObject.getDouble("temp");

        currentTemp.setValue(temp + unit);

        // Get min, max, pressure, humidity
        JSONObject mainObject = weatherService.returnMainObject();
        double minTemp = mainObject.getDouble("temp_min");
        double maxTemp = mainObject.getDouble("temp_max");
        int pressure = mainObject.getInt("pressure");
        int humidity = mainObject.getInt("humidity");

        // Get Wind Speed
        JSONObject windObject = weatherService.returnWindObject();
        double wind = windObject.getDouble("speed");

        // Get SunRise and SunSet
        JSONObject systemObject = weatherService.returnSunSet();
        long sunRise = systemObject.getLong("sunrise") * 1000;
        long sunSet = systemObject.getLong("sunset") * 1000;

        // Setup icon image
        String iconCode = "";
        String description = "";
        JSONArray jsonArray = weatherService.returnWeatherArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObject = jsonArray.getJSONObject(i);
            description = weatherObject.getString("description");
            iconCode = weatherObject.getString("icon");
            System.out.println(iconCode);
        }
        iconImage.setSource(new ExternalResource("http://openweathermap.org/img/w/" + iconCode + ".png"));

        dashBoardMain.addComponents(currentLocationTitle, iconImage, currentTemp);
        mainLayout.addComponent(dashBoardMain);

        // Update description UI
        weatherDescription.setValue("Cloudiness: " + description);
        weatherDescription.addStyleName(ValoTheme.LABEL_SUCCESS);
        weatherMin.setValue("Min: " + String.valueOf(minTemp) + unit);
        weatherMax.setValue(("Max: " + String.valueOf(maxTemp) + unit));
        pressureLabel.setValue("Pressure: " + String.valueOf(pressure) + "hpa");
        humidityLabel.setValue("Humidity: " + String.valueOf(humidity) + "%");

        windSpeedLabel.setValue("Wind Speed: " + String.valueOf(wind) + "m/s");
        sunRiseLabel.setValue("Sunrise: " + covertTime(sunRise));
        sunSetLabel.setValue("Sunset: " + covertTime(sunSet));

        mainDescriptionLayout.addComponents(descriptionLayout, pressureLayout);
        mainLayout.addComponents(mainDescriptionLayout);
    }

    private String covertTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh.mm aa");
        return dateFormat.format(new Date(time));
    }
}

/*

  Label testLabel = new Label("Hello there");

        try {
            //System.out.println("Data: " + weatherService.getWeather("mumbai").getString("coord").toString());
            JSONArray jsonArray = weatherService.returnWeatherArray("mumbai");
            JSONObject myObject = weatherService.returnMain("mumbai");

            System.out.println("pressure: " + myObject.getLong("pressure"));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject weatherObject = jsonArray.getJSONObject(i);
                System.out.println("Id: " + weatherObject.getInt("id") +
                        ", main: " + weatherObject.getString("main") +
                        ", description: " + weatherObject.getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

 */
