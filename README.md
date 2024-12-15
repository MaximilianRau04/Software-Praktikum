
# Exchange Days Management

Welcome to the Exchange Days Management Web App! This application helps you participate in Exchange Days by viewing workshops and training events, registering easily, and providing feedback to continuously improve your experience. You can find it here: http://193.196.54.172:8000.

---

## How to Use the App

1. Clone this repository:

```
git clone <https://github.tik.uni-stuttgart.de/iste-sopra-2024-itestra/team-20>
cd team-20
```

2. Install the required software:

- JDK 21 or higher

- Maven

- Node.js (ensure the installation path is added to your PATH).

3. Start the backend:

```
cd ./backend
mvn spring-boot:run
```

4. Start the frontend:

```
cd ./frontend
npm install
npm run dev
```


## How to Test the App

Follow the steps in [How to Use the App](How-to-Use-the-App) to set up the environment.

1. Open a terminal and navigate to the /backend folder:
```
cd ./backend
```

2. Run the tests:

```
mvn test
```
---

## How the App Works

**Registration**: After registering, you are redirected to the main page, where the app fetches all saved Exchange Days and their workshops from the database.

**Navigation**: The sidebar on the left allows seamless switching between components.

**Creating Events**: Use the Create button to add a new Exchange Day or Workshop. Newly created entries appear on the main page automatically. This feature is only available for admins.

**Attendance**: Workshop Organizers get a QR-Code that is uniquely generated for the respective event. Scanning this QR-Code confirms the attendance of the logged in user and allows them to provide feedback.

**Feedback**: Collected feedback is automatically analyzed and can be displayed under *Meine Events* as the organizer of the workshop.


## Features

View and register for Exchange Days and workshops.

Create new Exchange Days and workshops using an intuitive interface.

Provide feedback to improve future events.

Responsive design for a seamless experience across devices.

Display Feedback Summaries for finished workshops.

---

## Documentation

Check out the [API Documentation](docs/API.md) for implemented API endpoints.

## Developer Contacts

Deniz Altunkapan (3721629) [st187784@stud.uni-stuttgart.de](st187784@stud.uni-stuttgart.de)
</br></br>
Maximilian Peresunchak (3232875) [st152466@stud.uni-stuttgart.de](st152466@stud.uni-stuttgart.de)
</br></br>
Maximilian Rau (3731389) [st188391@stud.uni-stuttgart.de](st188391@stud.uni-stuttgart.de)