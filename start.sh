#!/bin/bash

# In den Ordner "frontend" navigieren und einen Screen für das Frontend starten
cd frontend
screen -dmS frontend bash -c "npm install && npm run dev -- --host --port 8000"

# Zurück ins Hauptverzeichnis und in den Ordner "backend" navigieren
cd ../backend
screen -dmS backend bash -c "mvn spring-boot:run"
