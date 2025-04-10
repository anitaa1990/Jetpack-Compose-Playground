# Jetpack-Compose-Playground

This repository contains a collection of concise write ups and demos on building some Jetpack Compose layouts and components that I have personally used in some of my android app projects. Each component can be added to any android app by simply copying the file to your app. Some are demos I found online and others are components that I have used in the past.

<img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/1.png" width="300" style="max-width:100%;">  <img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/2.png" width="300" style="max-width:100%;">

---

### Custom Text Editor with basic editing features
* [ComposeTextEditor](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/ComposeTextEditor.kt) is a `composable` file that supports rich text formatting. Currently supported formats includes: Title, Subtitle, Bold, Italics, Underline, Strikethrough, Highlight.
* Take a look at [TextEditorScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/TextEditorScreen.kt) file on how to use this component in any app.
* Currently being used in the [NotesApp](https://github.com/anitaa1990/NotesApp).

<img src="https://github.com/anitaa1990/NotesApp/blob/main/media/1.png" width="200" style="max-width:100%;">  <img src="https://github.com/anitaa1990/NotesApp/blob/main/media/2.png" width="200" style="max-width:100%;">   <img src="https://github.com/anitaa1990/NotesApp/blob/main/media/3.png" width="200" style="max-width:100%;">

---

### Network Observer
* [NetworkObserver](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/network/NetworkObserver.kt) is a util class that observes the network changes in the device. This can be observed by the UI classes to display network status to the end user. The [NetworkStatusBar](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/network/NetworkStatusBar.kt) class is a `composable` file that displays a status bar evertime the network connection changes.
* Take a look at [NetworkStatusScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/NetworkStatusScreen.kt) file on how to use this component in any app.

<img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/7.gif" width="300" style="max-width:100%;">

---

### Runtime Permissions
* [PermissionManager](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/permission/PermissionManager.kt) is a utility class for managing runtime permissions. [PermissionHandler](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/permission/PermissionHandler.kt) is a `composable` class to handle runtime permission requests in a Jetpack Compose environment.
* Take a look at [RuntimePermissionScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/RuntimePermissionScreen.kt) file on how to use this component in any app.

<img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/8.gif" width="300" style="max-width:100%;">

---

### Biometric Login
* Login using [Android's Biometric API](https://developer.android.com/identity/sign-in/biometric-auth). Supports fingerprint login, facial login or PIN login.
* [BiometricAuthUtil](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/BiometricAuthUtil.kt) is a utility class to handle biometric (fingerprint and facial recognition) authentication functionality.
* Take a look at [BiometricAuthScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/BiometricAuthScreen.kt) file on how to use this component in any app.
* You would need to add the `androidx.biometric:biometric:1.2.0-alpha05` dependency to the app.

<img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/9.gif" width="300" style="max-width:100%;">

---

### PaginatedLazyColumn
* [PaginatedLazyColumn](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/PaginatedLazyColumn.kt) is a `composable` for displaying a paginated list using LazyColumn in Jetpack Compose. This solution is robust for smaller-scale apps. For more complex scenarios with large data sets or multiple data sources, you should consider using the Paging library.
* This solution uses `PersistentList` because it ensures immutability, making state updates safe and predictable. Modifications like `add` or `remove` return a new list, preserving the original. Persistent lists are optimized for minimal copying, enhancing performance in this scenario where the list is updated frequently.
* In order to use `PersistentList`, you would need to add the [Kotlin Collections dependency](https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-collections-immutable/0.3.5) to the app. 
* Take a look at [PaginatedLazyColumnScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/PaginatedLazyColumnScreen.kt) file on how to use this component in any app.

<img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/10.gif" width="300" style="max-width:100%;">

---

### SlideToBook
* [SlideToBook](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/SlideToBook.kt) is a `composable`  that allows the user to drag a slider thumb horizontally to confirm an action. Once dragged past a threshold, the button triggers an action, plays a subtle animation, and displays a loading indicator.
* Take a look at [SlideToBookScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/SlideToBookScreen.kt) file on how to use this component in any app.
  
<img src="https://github.com/user-attachments/assets/0fd7340c-7947-4b54-828d-d3714101240b" style="max-width:100%;">

---

### GoogleMapsView
* [GoogleMapsView](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/GoogleMapsView.kt)  is a `composable` that demonstrates how Google Maps can work with Jetpack Compose.
* Take a look at [GoogleMapViewScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/GoogleMapViewScreen.kt) file on how to use this component in any app.
  
<img src="https://github.com/user-attachments/assets/660cc790-772a-4bef-89ad-740440d18159" width="300">

---

### BookPager
* [BookPager](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/BookPager.kt) is a `composable` file that creates a flip-style pager component with horizontal or vertical orientations. It simulates the effect of flipping through pages in a book, with additional features like overscroll effects. It is pretty much a replica from [Sinasamaki's Page Flip animation in Jetpack Compose](https://www.sinasamaki.com/page-flip-3d-animation-in-jetpack-compose/).
* Take a look at [BookPagerScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/BookPagerScreen.kt) file on how to use this component in any app.
* Currently being used in the [NewsApp](https://github.com/anitaa1990/PagingLib3-Sample).

<img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/3.gif" width="300" style="max-width:100%;">  <img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/4.gif" width="300" style="max-width:100%;">

---

### ParallaxPager
* [ParallaxPager](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/ParallaxPager.kt) is a `composable` file that creates a parallax-style pager component that takes places when scrolling horizontally between pages. It is pretty much an adaptation from [this article](https://canopas.com/how-to-create-a-parallax-movie-pager-in-jetpack-compose-ab9c1e19d2cb).
* Take a look at [ParallaxPagerScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/ParallaxPagerScreen.kt) file on how to use this component in any app.

<img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/5.gif" width="300" style="max-width:100%;">

---

### CircleRevealPager
* [CircleRevealPager](/app/src/main/java/com/an/jetpack_compose_playground/ui/component/CircleRevealPager.kt) is a `composable` file that creates a circular reveal animation that will clip the incoming page in a growing circle, until it fills the screen. It is pretty much a replica from [Sinasamaki's Pager animations in Jetpack Compose](https://www.sinasamaki.com/pager-animations/).
* Take a look at [CircleRevealPagerScreen](/app/src/main/java/com/an/jetpack_compose_playground/ui/screen/CircleRevealPagerScreen.kt) file on how to use this component in any app.
* Currently being used in the [TrailersApp](https://github.com/anitaa1990/Trailers-Compose).

<img src="https://github.com/anitaa1990/Jetpack-Compose-Playground/blob/main/media/6.gif" width="300" style="max-width:100%;">
