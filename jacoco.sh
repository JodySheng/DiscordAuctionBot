./gradlew jacocoReport
cd build/reports/jacoco/test/html
python3 -m http.server 8000