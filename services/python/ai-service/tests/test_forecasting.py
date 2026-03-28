"""Tests for financial forecasting."""
from app.services.forecasting_service import ForecastingService


def test_forecast_with_linear_data():
    svc = ForecastingService()
    data = [{"period": i, "value": 100 + i * 10} for i in range(1, 7)]
    result = svc.forecast(data, periods=3)
    assert len(result["forecasts"]) == 3
    assert result["r_squared"] > 0.9
    # Values should continue the trend
    assert result["forecasts"][0]["predicted_value"] > 160


def test_forecast_with_insufficient_data():
    svc = ForecastingService()
    result = svc.forecast([{"period": 1, "value": 100}], periods=3)
    assert "error" in result


def test_forecast_includes_bounds():
    svc = ForecastingService()
    data = [{"period": i, "value": 100 + i * 5 + (i % 2) * 10} for i in range(1, 13)]
    result = svc.forecast(data, periods=3)
    for f in result["forecasts"]:
        assert f["lower_bound"] <= f["predicted_value"] <= f["upper_bound"]
