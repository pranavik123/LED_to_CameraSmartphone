//Authors : Pranavi Kamavarapu, Venkata Naga Sai Tejaswini Samala

const int redPin = 9;
const int greenPin = 10;
const int bluePin = 11;
const int dataRate = 1000; //1000bps


int binaryData[] = {
    1,0,0, // Magenta: The binary representation for the color magenta.
    0,1,0, // Blue: The binary representation for the color blue.
    0,0,1, // Green: The binary representation for the color green.
    1,1,1,  // Black: The binary representation for the color black
    0,1,1, // Cyan: The binary representation for the color cyan.
    1,1,0, // white: The binary representation for the color white
    1,0,1, // Yellow: The binary representation for the color yellow.
    0,0,0 // Red: The binary representation for the color red.
};



void setup() {
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
}

void sendColor(int r, int g, int b) {
  analogWrite(redPin, r);
  analogWrite(greenPin, g);
  analogWrite(bluePin, b);
  delayMicroseconds(dataRate);  // Transmit the color for 3.33 ms
}

// Function to convert 3-bit binary to a color
void transmitSymbol(int b1, int b2, int b3) {
  if (b1 == 0 && b2 == 0 && b3 == 0) sendColor(255, 0, 0);       // Red
  else if (b1 == 0 && b2 == 0 && b3 == 1) sendColor(0, 255, 0);   // Green
  else if (b1 == 0 && b2 == 1 && b3 == 0) sendColor(0, 0, 255);   // Blue
  else if (b1 == 0 && b2 == 1 && b3 == 1) sendColor(0, 255, 255); // Cyan
  else if (b1 == 1 && b2 == 0 && b3 == 0) sendColor(255, 0, 255); // Magenta
  else if (b1 == 1 && b2 == 0 && b3 == 1) sendColor(255, 255, 0); // Yellow
  else if (b1 == 1 && b2 == 1 && b3 == 0) sendColor(255, 255, 255); // whote
  else if (b1 == 1 && b2 == 1 && b3 == 1) sendColor(0, 0, 0); // Black
}



void loop() {
  // Transmit each 3-bit group as a color
  // sendColor(255, 0, 0);
  // sendColor(255, 255, 255);
  // delay(1000);
  for (int i = 0; i < sizeof(binaryData) / sizeof(binaryData[0]); i += 3) {
    int b1 = binaryData[i];
    int b2 = binaryData[i + 1];
    int b3 = binaryData[i + 2];
    transmitSymbol(b1, b2, b3); // Ensure transmitSymbol is correctly defined
}
// sendColor(255, 255, 255);
//   delay(1000);


// }

//4-CSK
// const int redPin = 9;
// const int greenPin = 10;
// const int bluePin = 11;
// const int dataRate = 1000; 

// // Replace this with your actual binary data.
// int binaryData[] = {
//     0, 0, // Red: Binary representation for the color red.
//     0, 1, // green: Binary representation for the color green.
//     1, 0, // blue: Binary representation for the color blue.
//     1, 1, // yellow: Binary representation for the color yellow.
// };




// void setup() {
//   pinMode(redPin, OUTPUT);
//   pinMode(greenPin, OUTPUT);
//   pinMode(bluePin, OUTPUT);
// }

// void sendColor(int r, int g, int b) {
//   analogWrite(redPin, r);
//   analogWrite(greenPin, g);
//   analogWrite(bluePin, b);
//   delayMicroseconds(dataRate);  // Transmit the color for 3.33 ms
// }

// // Function to convert 3-bit binary to a color
// void transmitSymbol(int b1, int b2) {
//   if (b1 == 0 && b2 == 0) sendColor(255, 0, 0);       // Red
//   else if (b1 == 0 && b2 == 1) sendColor(0, 255, 0);  // Green
//   else if (b1 == 1 && b2 == 0) sendColor(0, 0, 255);  // Blue
//   else if (b1 == 1 && b2 == 1) sendColor(255, 255, 0); // Yellow
// }


// void loop() {
//   // Transmit each 3-bit group as a color
//   // sendColor(255, 0, 0);
  
//   for (int i = 0; i < sizeof(binaryData) / sizeof(binaryData[0]); i += 2) {
//     int b1 = binaryData[i];
//     int b2 = binaryData[i + 1];
//     transmitSymbol(b1, b2); // Ensure transmitSymbol is correctly defined
// }

//   // sendColor(255,255,255);
// }




//16-CSK
// const int redPin = 9;
// const int greenPin = 10;
// const int bluePin = 11;
//  // Symbol duration in microseconds (1 kHz)

// // Sample binary data array of 16-CSK (4-bit per symbol).
// bool binaryData[] = {
//     0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//     0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//     0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
//         0, 0, 0, 0, // Color 1: Red
//     0, 0, 0, 1, // Color 2: Green
//     0, 0, 1, 0, // Color 3: Blue
//     0, 0, 1, 1, // Color 4: Cyan
//     0, 1, 0, 0, // Color 5: Magenta
//     0, 1, 0, 1, // Color 6: Yellow
//     0, 1, 1, 0, // Color 7: White
//     0, 1, 1, 1, // Color 8: Black
//     1, 0, 0, 0, // Color 9: Orange
//     1, 0, 0, 1, // Color 10: Purple
//     1, 0, 1, 0, // Color 11: Pink
//     1, 0, 1, 1, // Color 12: Teal
//     1, 1, 0, 0, // Color 13: Brown
//     1, 1, 0, 1, // Color 14: Light Gray
//     1, 1, 1, 0, // Color 15: Dark Gray
//     1, 1, 1, 1,  // Color 16: Bright White
// };

// void setup() {
//   pinMode(redPin, OUTPUT);
//   pinMode(greenPin, OUTPUT);
//   pinMode(bluePin, OUTPUT);
// }

// // Function to set RGB values for colors
// void sendColor(int r, int g, int b) {
//   analogWrite(redPin, r);
//   analogWrite(greenPin, g);
//   analogWrite(bluePin, b);
//   delayMicroseconds(100); // Transmit the color for the defined symbol duration
// }

// // Function to convert 4-bit binary to a color
// void transmitSymbol(bool b1, bool b2, bool b3, bool b4) {
//   if (b1 == 0 && b2 == 0 && b3 == 0 && b4 == 0) sendColor(255, 0, 0);       // Red
//   else if (b1 == 0 && b2 == 0 && b3 == 0 && b4 == 1) sendColor(0, 255, 0);   // Green
//   else if (b1 == 0 && b2 == 0 && b3 == 1 && b4 == 0) sendColor(0, 0, 255);   // Blue
//   else if (b1 == 0 && b2 == 0 && b3 == 1 && b4 == 1) sendColor(0, 255, 255); // Cyan
//   else if (b1 == 0 && b2 == 1 && b3 == 0 && b4 == 0) sendColor(255, 0, 255); // Magenta
//   else if (b1 == 0 && b2 == 1 && b3 == 0 && b4 == 1) sendColor(255, 255, 0); // Yellow
//   else if (b1 == 0 && b2 == 1 && b3 == 1 && b4 == 0) sendColor(255, 255, 255); // White
//   else if (b1 == 0 && b2 == 1 && b3 == 1 && b4 == 1) sendColor(0, 0, 0);     // Black
//   else if (b1 == 1 && b2 == 0 && b3 == 0 && b4 == 0) sendColor(255, 165, 0); // Orange
//   else if (b1 == 1 && b2 == 0 && b3 == 0 && b4 == 1) sendColor(128, 0, 128); // Purple
//   else if (b1 == 1 && b2 == 0 && b3 == 1 && b4 == 0) sendColor(255, 192, 203); // Pink
//   else if (b1 == 1 && b2 == 0 && b3 == 1 && b4 == 1) sendColor(0, 128, 128); // Teal
//   else if (b1 == 1 && b2 == 1 && b3 == 0 && b4 == 0) sendColor(165, 42, 42); // Brown
//   else if (b1 == 1 && b2 == 1 && b3 == 0 && b4 == 1) sendColor(211, 211, 211); // Light Gray
//   else if (b1 == 1 && b2 == 1 && b3 == 1 && b4 == 0) sendColor(105, 105, 105); // Dark Gray
//   else if (b1 == 1 && b2 == 1 && b3 == 1 && b4 == 1) sendColor(255, 255, 255); // Bright White
// }

// void loop() {
//   // Transmit each 4-bit group as a color
//   for (int i = 0; i < sizeof(binaryData) / sizeof(binaryData[0]); i += 4) {
//     bool b1 = binaryData[i];
//     bool b2 = binaryData[i + 1];
//     bool b3 = binaryData[i + 2];
//     bool b4 = binaryData[i + 3];
//     transmitSymbol(b1, b2, b3, b4);
//   }
}
