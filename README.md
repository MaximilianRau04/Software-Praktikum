# Exchange Days Management

Welcome to the Exchange Days Management Web App! This application helps you participate in "Exchange Days" by viewing workshops and training events, registering easily, and providing Feedback to continuously improve your experience. You can find it here: http://193.196.54.172:8000

## How to Use the App

1. Clone this repository.
2. Install a JDK in at least version 21. Make sure Maven is installed. Install Node.js and ensure that the root folder of its installation is added to your PATH.
3. Start the API by navigating into the backend and executing `mvn spring-boot:run`
5. Now start the frontend by navigating into its folder and executing
    - `npm install`
    - `npm run dev`

## How the App Works

The app does not use any authentication. After using the registration-formular, you will be redirected to the main page, where the application fetches all saved Exchange Days and their associated workshops from the database.


## Developer Contacts

Deniz Altunkapan(3721629): st187784@stud.uni-stuttgart.de  
Maximilian Peresunchak(3232875): st152466@stud.uni-stuttgart.de  
Maximilian Rau(3731389): st188391@stud.uni-stuttgart.de  
