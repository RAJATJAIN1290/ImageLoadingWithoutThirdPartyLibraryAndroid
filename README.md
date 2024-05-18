  Image Load without Third Party Library Android Kotlin
  
1. This project demonstrates how to load images in an Android application without relying on third-party libraries. The implementation leverages Android's built-in components to efficiently load and display images.


   ## Features

- Load images from URLs
- Display images in an `ImageView`
- Simple and lightweight implementation

    ## Installation

To get a local copy up and running follow these simple steps:

1. **Clone the repository:**
   
    git clone https://github.com/RAJATJAIN1290/ImageLoadingWithoutThirdPartyLibraryAndroid.git
  

2. **Open the project in Android Studio:**
    - Open Android Studio.
    - Select "Open an existing Android Studio project".
    - Navigate to the cloned repository and select it.

3. **Build the project:**
    - Click on the "Build" menu.
    - Select "Make Project" to build the project.


    ## Usage

1. **Ensure you have internet permission in your `AndroidManifest.xml`:**
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    ```

2. **Use the provided utility class to load images:**
    - Example usage in an `Activity` or `Fragment`:
    ```java
    ImageView imageView = findViewById(R.id.imageView);
    String imageUrl = "https://example.com/image.jpg";
    ImageLoader.loadImage(imageView, imageUrl);




    
