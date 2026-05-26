# Java Traditional Image Processing - Cartoon Filter App 🎨

This is a Java Object-Oriented Programming (OOP) challenge project that converts regular photos into a cartoon/cell-shading style using **pure traditional image processing algorithms (No AI / Deep Learning)**.

## 🚀 Features
- **Edge-Preserving Smoothing**: Modified Gaussian blur that preserves strong edges while smoothing skin/surfaces.
- **Sobel Edge Detection**: Extracts distinct contours for the cartoon outlines.
- **Dynamic Color Quantization**: Reduces color depth to create a cell-shading/comic book effect.
- **Interactive Control Panel**: Real-time Java Swing GUI sliders to adjust Blur Passes, Edge Sensitivity, and Color Levels.

## 📁 Project Structure
- `CartoonApp.java`: The main single-file application containing all OOP components.
- `test_images/`: A folder containing sample images for quick testing.

## 🛠️ How to Run (Command Line)
Make sure you have Java (JDK) installed on your system.

1. Open your terminal or command prompt.
2. Navigate to the project directory.
3. Compile the Java file:
   ```bash
   javac CartoonApp.java