# LiFi-Based Data Transmission Using Visible Light Communication

## Overview

This project demonstrates a **Visible Light Communication (VLC)** system that leverages the visible light spectrum for secure and efficient data transmission. Using modulation techniques like **On-Off Keying (OOK)** and **Color Shift Keying (CSK)**, data is encoded into light signals emitted by a **TriLED**. The signals are captured by a **Google Pixel 7 Pro camera**, processed with **OpenCV**, and decoded back into binary data. 

The project explores challenges in VLC, such as ambient light interference, synchronization, and high data rates, and provides a robust solution using advanced image processing and clustering techniques.

## Features

- **Modulation Techniques**: On-Off Keying (OOK) and Color Shift Keying (CSK) for data encoding.
- **Receiver Processing**: Android application using OpenCV for decoding light signals into binary data.
- **Error Correction**: Reed-Solomon encoding ensures data reliability.
- **Throughput**:
  - 600-700 bps for 4-CSK.
  - 900-1000 bps for 8-CSK.
- **Low Error Rates**: Achieves error rates as low as 5% under optimal conditions.
- **Scalability**: Suitable for higher-order encoding schemes and secure applications.

## Applications

This system can be used in:

- **Smart Homes**: Secure communication for IoT devices.
- **Automotive Communication**: Vehicle-to-Vehicle (V2V) and Vehicle-to-Infrastructure (V2I).
- **Indoor Navigation**: Precise positioning systems in public spaces.
- **Secure Data Transmission**: Hospitals and other environments where RF interference is not suitable.

## Implementation

### Hardware
- **Transmitter**: Arduino Uno with a 4-pin TriLED for encoding and emitting light signals.
- **Receiver**: Google Pixel 7 Pro camera to capture signals.

### Software
- **Arduino IDE**: For implementing modulation techniques and light control.
- **Android Application**: Uses OpenCV for image processing and decoding.
- **K-Means Clustering**: Enhances color detection accuracy.

### System Workflow
1. **Transmitter**: Encodes binary data into light signals.
2. **Receiver**: Captures the emitted signals as RGB frames.
3. **Decoding**: Processes the frames using OpenCV and maps colors to binary data.
4. **Reconstruction**: Decoded binary data is reconstructed into the original transmitted message.

## Results

- **Throughput**: Up to 900 bps with 8-CSK.
- **Error Rates**:
  - 5% in dark environments.
  - Up to 30% in bright outdoor conditions.
- **Accuracy**:
  - 95% in optimal conditions.
  - 70% under challenging conditions.

## Future Work

- Improve robustness in dynamic environments (e.g., moving transmitter/receiver).
- Explore advanced modulation techniques for higher throughput.
- Enhance outdoor performance with noise filtering.
- Integrate machine learning for efficient decoding.
- Adapt for IoT device communication.

## References

For more details on VLC and its applications, refer to the project documentation and related research studies.

---

This project demonstrates the scalability and practicality of VLC as a reliable communication medium for diverse applications, highlighting its potential for secure, high-speed data transmission.
