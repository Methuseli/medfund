"""ML models for fraud detection and risk scoring."""
import logging
import numpy as np
from sklearn.ensemble import IsolationForest
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

logger = logging.getLogger(__name__)


class FraudMLModel:
    """Fraud detection using IsolationForest trained on synthetic data."""

    def __init__(self, seed: int = 42):
        self.model_version = "1.0.0"
        self._isolation_forest = None
        self._scaler = None
        self._trained = False
        self._train(seed)

    def _train(self, seed: int):
        """Train on synthetic data for development."""
        rng = np.random.RandomState(seed)

        # Generate 1000 normal claims
        n_normal = 950
        n_fraud = 50
        n_total = n_normal + n_fraud

        # Features: amount, procedure_count, diagnosis_count, days_since_enrollment
        normal_data = np.column_stack([
            rng.lognormal(6, 1, n_normal),      # amount (centered ~400)
            rng.poisson(2, n_normal),             # procedure_count
            rng.poisson(1.5, n_normal),           # diagnosis_count
            rng.uniform(90, 3650, n_normal),      # days_since_enrollment
        ])

        # Fraudulent: higher amounts, more procedures, newer members
        fraud_data = np.column_stack([
            rng.lognormal(9, 0.5, n_fraud),       # amount (~8000+)
            rng.poisson(6, n_fraud),               # many procedures
            rng.poisson(1, n_fraud),               # fewer diagnoses
            rng.uniform(10, 180, n_fraud),          # newer members
        ])

        X = np.vstack([normal_data, fraud_data])

        # Train Isolation Forest (unsupervised anomaly detection)
        self._scaler = StandardScaler()
        X_scaled = self._scaler.fit_transform(X)
        self._isolation_forest = IsolationForest(
            contamination=0.05, random_state=seed, n_estimators=100
        )
        self._isolation_forest.fit(X_scaled)
        self._trained = True
        logger.info("FraudMLModel trained on %d samples", n_total)

    def predict(self, features: dict) -> dict:
        """Predict fraud risk for a claim."""
        if not self._trained:
            return {"risk_score": 0.1, "risk_level": "LOW", "indicators": [], "model": "untrained"}

        X = np.array([[
            features.get("amount", 0),
            features.get("procedure_count", 0),
            features.get("diagnosis_count", 0),
            features.get("days_since_enrollment", 365),
        ]])

        X_scaled = self._scaler.transform(X)

        # IsolationForest: anomaly_score in [-1, 1], more negative = more anomalous
        anomaly_score = self._isolation_forest.decision_function(X_scaled)[0]
        # Convert to risk score [0, 1]: lower decision_function = higher risk
        risk_score = max(0, min(1, 0.5 - anomaly_score * 0.5))

        indicators = []
        if features.get("amount", 0) > 5000:
            indicators.append("high_value")
        if features.get("procedure_count", 0) > 5:
            indicators.append("many_procedures")
        if features.get("days_since_enrollment", 365) < 90:
            indicators.append("new_member")
        if features.get("diagnosis_count", 0) == 0:
            indicators.append("no_diagnosis")

        risk_level = "HIGH" if risk_score > 0.6 else "MEDIUM" if risk_score > 0.3 else "LOW"

        return {
            "risk_score": round(float(risk_score), 4),
            "risk_level": risk_level,
            "indicators": indicators,
            "model": "isolation_forest",
            "model_version": self.model_version,
        }
