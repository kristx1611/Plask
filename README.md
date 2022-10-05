The original repository is a private repository made for a course project.
This project lasted throughout the whole semester, approximately March - May 2021.
The base requirement was to make an android application where the user could see the live temperature of different Norwegian beaches.
Features I worked on were: Weather and water temperature, map location of beaches, distance to beaches, favorite beaches, search and filter, information about the specific beach, live map and user location and the ui elements to represent these features.
The application was developed using Kotlin, SQL, XML and Android Studio.
Below is the original README.

# IN2000 software engineering project
Our android application is based on [Case 1 – Badetemperatur](https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/case/in2000-cases-v21.pdf).
### Team 21
| name                           | username                                   | study                                                                  | role  |
|--------------------------------|--------------------------------------------|------------------------------------------------------------------------|-------|
| Hallvard Aske                  | [hallvaae](https://github.uio.no/hallvaae) | [prosa](https://www.uio.no/studier/program/informatikk-programmering/) | 👨‍💻|
| Hanna Fuxiang Skaalvik Tronsen | [hftronse](https://github.uio.no/hftronse) | [prosa](https://www.uio.no/studier/program/informatikk-programmering/) | 👩‍💻|
| Kristian Xin-Ti Yang           | [kxyang](https://github.uio.no/kxyang)     | [prosa](https://www.uio.no/studier/program/informatikk-programmering/) | 👨‍💻|
| Christian Fredrik Eggesbø      | [chrisfeg](https://github.uio.no/chrisfeg) | [digøk](https://www.uio.no/studier/program/informatikk-ledelse/)       | 👨‍💻|
| Joanna Sol Vestad              | [joannasv](https://github.uio.no/joannasv) | [digøk](https://www.uio.no/studier/program/informatikk-ledelse/)       | 👩‍💻|
| Kim Isak Olsen                 | [kimio](https://github.uio.no/kimio)       | [digøk](https://www.uio.no/studier/program/informatikk-ledelse/)       | 👨‍💻|
| Stian Grimsrud                 | [stiangri](mailto:stiangri@ifi.uio.no)     | [design](https://www.uio.no/studier/program/inf-design-master/)        | 👨‍🏫|

# Getting started
## Connect Android Studio to GitHub
1. Open Settings in Android Studio
2. Choose 'Version Control' → 'GitHub'
3. Click on (+)
4. Change server to 'github.uio.no'
5. Login with UiO-username and password

## Clone the project
1. Choose 'File' → 'New' → 'Project from Version Control'
2. Choose 'Git' as version control
3. Enter the repository's URL (https://github.uio.no/kimio/team21)
4. Then Clone


## Project APIs
### Room Database Library
implementation "androidx.room:room-runtime:2.2.6"
kapt "androidx.room:room-compiler:2.2.6"
implementation "androidx.room:room-ktx:2.2.6"

### Navigation library
implementation 'androidx.navigation:navigation-fragment-ktx:2.3.4'
implementation 'androidx.navigation:navigation-ui-ktx:2.3.4'

### Retrofit library (HTTP/HTTPS)
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.retrofit2:converter-gson:2.9.0"

implementation "com.google.code.gson:gson:2.8.6"

### Coroutines library
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2"
