#include <iostream>

class SensorData {
public:
    int temperature;

    SensorData(int temp) : temperature(temp) {
        std::cout << "SensorData created with temperature: " << temperature << std::endl;
    }

    void printTemperature() {
        std::cout << "Current temperature: " << temperature << std::endl;
    }

    ~SensorData() {
        std::cout << "SensorData object destroyed!" << std::endl;
    }
};

void processData(SensorData* data) {
    if (data == nullptr) { // Explicit null check
        std::cout << "Invalid SensorData pointer. Cannot process." << std::endl;
        return;  // Exit early
    }

    data->printTemperature();
}

int main() {
    SensorData* sensor1 = new SensorData(25);
    processData(sensor1);

    delete sensor1;
    sensor1 = nullptr;

    SensorData* sensor2 = new SensorData(15);
    processData(sensor2);

    delete sensor2;
    sensor2 = nullptr;

    processData(sensor1);  // Prints "Invalid SensorData pointer."
    processData(sensor2);  // Prints "Invalid SensorData pointer."

    return 0;
}