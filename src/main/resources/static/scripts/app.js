$(document).ready(function () {
    var evtSource = new EventSource("/weather.stream");
    var weatherList = $("#weather-list");
    var store = [];

    evtSource.onmessage = function (e) {
        var weatherInfo = JSON.parse(e.data);
        store[weatherInfo.country+weatherInfo.city] = weatherInfo;
        setTimeout(render, 0);
    };

    function render() {
        weatherList.empty();
        Object.keys(store).map(function(key){return store[key];}).forEach(function (item) {
            weatherList.append($("<li>").append(
                $("<h2>").append(item.city).append(", ").append(item.country),
                $("<div>").append(
                    $("<i>").addClass("owf").addClass("owf-" + item.code).addClass("owf-4x").css("opacity", 0.4),
                    $("<div>").addClass("temp").append(
                        item.fahrenheitTemperature,
                        $("<sup>").append("°F"),
                        " / ",
                        item.celsiusTemperature,
                        $("<sup>").append("°C")
                    ),
                    $("<div>").css("clear", "both")
                )
            ));
        });
    }
});